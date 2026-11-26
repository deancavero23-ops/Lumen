package com.lumen.seguridad

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

/**
 * Actualizaciones automáticas sin servidor propio: solo se consulta el repositorio público
 * de GitHub. Las reglas se verifican con SHA-256 antes de aplicarse. La app no se instala
 * sola en Android: avisa y abre la descarga (en Play, Google Play ya actualiza por sí mismo).
 */
object AutoActualizacion {
    private const val API = "https://api.github.com/repos/deancavero23-ops/Lumen/releases?per_page=20"
    private const val INTERVALO = 24L * 60 * 60 * 1000
    private const val ULTIMA_REVISION = "auto_ultima_revision"
    private const val REGLAS_AUTO = "auto_reglas"
    private const val APP = "auto_app"
    private const val ULTIMAS_REGLAS = "auto_version_reglas"

    fun reglasActiva(context: Context) = prefs(context).getBoolean(REGLAS_AUTO, true)
    fun appActiva(context: Context) = prefs(context).getBoolean(APP, true)

    fun alternarReglas(context: Context): Boolean {
        val valor = !reglasActiva(context)
        prefs(context).edit().putBoolean(REGLAS_AUTO, valor).apply()
        return valor
    }

    fun alternarApp(context: Context): Boolean {
        val valor = !appActiva(context)
        prefs(context).edit().putBoolean(APP, valor).apply()
        return valor
    }

    fun ultimaRevision(context: Context): Long = prefs(context).getLong(ULTIMA_REVISION, 0L)

    fun ultimaVersionReglas(context: Context): String =
        prefs(context).getString(ULTIMAS_REGLAS, "").orEmpty()

    private fun prefs(context: Context) =
        context.getSharedPreferences(Textos.PREFS, Context.MODE_PRIVATE)

    /** Se llama al abrir la app. Como máximo una revisión al día. */
    fun revisar(
        context: Context,
        alActualizarReglas: (String) -> Unit,
        alEncontrarApp: (String, String) -> Unit
    ) {
        val ahora = System.currentTimeMillis()
        val anterior = ultimaRevision(context)
        if (ahora - anterior < INTERVALO) return
        prefs(context).edit().putLong(ULTIMA_REVISION, ahora).apply()

        if (reglasActiva(context)) {
            Thread {
                val antes = Reglas.info(context).version
                val resultado = runCatching { Reglas.actualizar(context) }
                resultado.onSuccess { info ->
                    if (info.version != antes) {
                        prefs(context).edit().putString(ULTIMAS_REGLAS, info.version).apply()
                        alActualizarReglas(info.version)
                    }
                }
            }.start()
        }

        if (appActiva(context)) {
            Thread {
                runCatching { buscarApp(context) }.onSuccess { encontrada ->
                    if (encontrada != null) alEncontrarApp(encontrada.first, encontrada.second)
                }
            }.start()
        }
    }

    private fun esPlay(context: Context): Boolean = try {
        context.packageManager.getInstallerPackageName(context.packageName) == "com.android.vending"
    } catch (e: Exception) { false }

    private fun buscarApp(context: Context): Pair<String, String>? {
        if (esPlay(context)) return null
        val bytes = descargar(API, 256 * 1024)
        val releases = JSONArray(String(bytes, Charsets.UTF_8))
        var mejorVersion: String? = null
        var mejorUrl: String? = null
        for (i in 0 until releases.length()) {
            val r = releases.optJSONObject(i) ?: continue
            val tag = r.optString("tag_name", "")
            if (!tag.startsWith("seguridad-v")) continue
            val version = tag.removePrefix("seguridad-v")
            if (!esMayor(version, BuildConfig.VERSION_NAME)) continue
            if (mejorVersion == null || esMayor(version, mejorVersion!!)) {
                val assets = r.optJSONArray("assets") ?: continue
                for (j in 0 until assets.length()) {
                    val a = assets.optJSONObject(j) ?: continue
                    val nombre = a.optString("name", "")
                    val enlace = a.optString("browser_download_url", "")
                    if (nombre.endsWith(".apk") && enlace.startsWith("https://github.com/")) {
                        mejorVersion = version
                        mejorUrl = enlace
                        break
                    }
                }
            }
        }
        return if (mejorVersion != null && mejorUrl != null) mejorVersion!! to mejorUrl!! else null
    }

    /** Compara 1.2.3, 1.3 y 2.0 sin depender de librerías externas. */
    private fun esMayor(candidata: String, actual: String): Boolean {
        fun partes(v: String) = Regex("\\d+").findAll(v).map { it.value.toInt() }
            .toList().let { it + List(3 - it.size.coerceAtMost(3)) { 0 } }
        val a = partes(candidata)
        val b = partes(actual)
        for (i in 0 until minOf(a.size, b.size)) {
            if (a[i] != b[i]) return a[i] > b[i]
        }
        return false
    }

    private fun descargar(enlace: String, maximo: Int): ByteArray {
        val conexion = URL(enlace).openConnection() as HttpURLConnection
        conexion.connectTimeout = 10_000
        conexion.readTimeout = 15_000
        conexion.instanceFollowRedirects = true
        conexion.setRequestProperty("User-Agent", "Lumen-Seguridad/${BuildConfig.VERSION_NAME}")
        conexion.setRequestProperty("Accept", "application/vnd.github+json")
        try {
            val codigo = conexion.responseCode
            if (codigo !in 200..299) throw Exception("HTTP $codigo")
            val host = conexion.url.host.lowercase(Locale.ROOT)
            if (host != "api.github.com" && host != "github.com" &&
                !host.endsWith(".githubusercontent.com")
            ) throw Exception("Host no permitido")
            val salida = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            var total = 0
            conexion.inputStream.use { entrada ->
                while (true) {
                    val n = entrada.read(buffer)
                    if (n < 0) break
                    total += n
                    if (total > maximo) throw Exception("Respuesta demasiado grande")
                    salida.write(buffer, 0, n)
                }
            }
            return salida.toByteArray()
        } finally {
            conexion.disconnect()
        }
    }

    fun urlLanzador() = Uri.parse("https://github.com/deancavero23-ops/Lumen/releases")
}
