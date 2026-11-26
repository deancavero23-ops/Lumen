package com.lumen.seguridad

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import org.json.JSONObject
import java.io.File

data class AppRiesgo(
    val paquete: String,
    val nombre: String,
    val version: String,
    val sha: String,
    val firmante: String,
    val instalador: String,
    val permisos: List<String>,
    val accesibilidad: Boolean,
    val puntuacion: Int,
    val nivel: String,
    val conocida: Boolean = false,
    /** Último uso en milisegundos; 0 si el usuario no ha dado acceso al uso de apps. */
    val ultimoUso: Long = 0L
)

object Analizador {

    private const val CONOCIDAS = "apps_conocidas"

    /** Lista local de paquetes que el usuario marca como conocidos. Nada sale del móvil. */
    private fun conocidas(context: Context): MutableSet<String> =
        context.getSharedPreferences(Textos.PREFS, Context.MODE_PRIVATE)
            .getStringSet(CONOCIDAS, emptySet())?.toMutableSet() ?: mutableSetOf()

    fun esConocida(context: Context, paquete: String): Boolean = paquete in conocidas(context)

    /** El acceso al uso de apps es opt-in y se concede en Ajustes del sistema. */
    fun tieneUso(context: Context): Boolean = try {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        @Suppress("DEPRECATION")
        val modo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ops.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
            )
        } else {
            ops.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
            )
        }
        modo == AppOpsManager.MODE_ALLOWED
    } catch (e: Exception) { false }

    /** Consulta única de uso: evita recorrer las estadísticas una vez por app. */
    private fun usosRecientes(context: Context): Map<String, Long> {
        if (!tieneUso(context)) return emptyMap()
        return try {
            val gestor = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val fin = System.currentTimeMillis()
            val inicio = fin - 90L * 86_400_000L
            gestor.queryUsageStats(UsageStatsManager.INTERVAL_BEST, inicio, fin)
                .groupBy { it.packageName }
                .mapValues { (_, lista) -> lista.maxOf { it.lastTimeUsed } }
        } catch (e: Exception) { emptyMap() }
    }

    /** Añade o quita una app de la lista local de confianza y devuelve true si quedó marcada. */
    fun alternarConocida(context: Context, paquete: String): Boolean {
        val actuales = conocidas(context)
        val marcada = !actuales.contains(paquete)
        if (marcada) actuales.add(paquete) else actuales.remove(paquete)
        context.getSharedPreferences(Textos.PREFS, Context.MODE_PRIVATE)
            .edit().putStringSet(CONOCIDAS, actuales).apply()
        return marcada
    }

    private data class Conjunto(
        val hashes: JSONObject,
        val firmantes: JSONObject,
        val sensibles: Set<String>
    )

    private fun reglas(context: Context): Conjunto {
        val json = try {
            JSONObject(Reglas.texto(context))
        } catch (e: Exception) {
            JSONObject()
        }
        val hashes = json.optJSONObject("hashes") ?: JSONObject()
        val firmantes = json.optJSONObject("firmantes") ?: JSONObject()
        val sensibles = mutableSetOf<String>()
        json.optJSONArray("permisosSensibles")?.let { arr ->
            for (i in 0 until arr.length()) sensibles.add(arr.getString(i))
        }
        return Conjunto(hashes, firmantes, sensibles)
    }

    private fun firmante(pi: PackageInfo): String {
        val firmas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.signingInfo?.apkContentsSigners ?: pi.signatures
        } else {
            pi.signatures
        }
        val bytes = firmas?.firstOrNull()?.toByteArray() ?: return "—"
        return Utilidades.sha256(bytes).take(16)
    }

    private fun construir(context: Context, pi: PackageInfo, r: Conjunto, usos: Map<String, Long>): AppRiesgo? {
        val ai = pi.applicationInfo ?: return null
        val paquete = pi.packageName
        if (paquete == context.packageName) return null

        val pm = context.packageManager
        val nombre = try { pm.getApplicationLabel(ai).toString() } catch (e: Exception) { paquete }
        val version = pi.versionName ?: "—"
        val sha = try { Utilidades.sha256(File(ai.sourceDir)) } catch (e: Exception) { "" }
        val firma = firmante(pi)
        val permisos = (pi.requestedPermissions ?: emptyArray()).filter { it in r.sensibles }
        val instalador = try { pm.getInstallerPackageName(paquete) } catch (e: Exception) { null }
            ?.takeIf { it.isNotBlank() } ?: "desconocida"
        val fuenteDesconocida = instalador == "desconocida"

        var puntos = 0
        if (sha.isNotEmpty() && r.hashes.has(sha)) puntos += 4
        if (firma != "—" && r.firmantes.has(firma)) puntos += 3
        puntos += minOf(permisos.size, 3)
        val accesibilidad = try {
            pm.queryIntentServices(Intent("android.accessibilityservice.AccessibilityService"), 0)
                .any { it.serviceInfo.packageName == paquete }
        } catch (e: Exception) { false }
        if (accesibilidad) puntos += 2
        if (fuenteDesconocida && permisos.any { it.endsWith("REQUEST_INSTALL_PACKAGES") }) puntos += 2

        val conocida = paquete in conocidas(context)
        val nivel = when {
            conocida -> "risk_low"
            puntos >= 5 -> "risk_high"
            puntos >= 3 -> "risk_medium"
            else -> "risk_low"
        }
        return AppRiesgo(
            paquete = paquete,
            nombre = nombre,
            version = version,
            sha = sha,
            firmante = firma,
            instalador = instalador,
            permisos = permisos,
            accesibilidad = accesibilidad,
            puntuacion = if (conocida) 0 else puntos,
            nivel = nivel,
            conocida = conocida,
            ultimoUso = usos[paquete] ?: 0L
        )
    }

    fun analizar(context: Context): List<AppRiesgo> {
        val pm = context.packageManager
        val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_SIGNATURES
        val r = reglas(context)
        val usos = usosRecientes(context)
        return pm.getInstalledPackages(flags)
            .mapNotNull { construir(context, it, r, usos) }
            .sortedWith(compareByDescending<AppRiesgo> { it.puntuacion }.thenBy { it.nombre.lowercase() })
    }

    fun analizarUno(context: Context, paquete: String): AppRiesgo? =
        analizar(context).firstOrNull { it.paquete == paquete }
}
