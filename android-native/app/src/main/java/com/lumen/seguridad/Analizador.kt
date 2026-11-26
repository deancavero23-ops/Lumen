package com.lumen.seguridad

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
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
    val nivel: String
)

object Analizador {

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

    private fun construir(context: Context, pi: PackageInfo, r: Conjunto): AppRiesgo? {
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

        val nivel = when {
            puntos >= 5 -> "risk_high"
            puntos >= 3 -> "risk_medium"
            else -> "risk_low"
        }
        return AppRiesgo(
            paquete, nombre, version, sha, firma, instalador, permisos,
            accesibilidad, puntos, nivel
        )
    }

    fun analizar(context: Context): List<AppRiesgo> {
        val pm = context.packageManager
        val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_SIGNATURES
        val r = reglas(context)
        return pm.getInstalledPackages(flags)
            .mapNotNull { construir(context, it, r) }
            .sortedWith(compareByDescending<AppRiesgo> { it.puntuacion }.thenBy { it.nombre.lowercase() })
    }

    fun analizarUno(context: Context, paquete: String): AppRiesgo? =
        analizar(context).firstOrNull { it.paquete == paquete }
}
