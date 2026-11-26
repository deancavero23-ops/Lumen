package com.lumen.seguridad

import android.content.Context
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class ErrorReglas(val clave: String) : Exception(clave)

data class InfoReglas(
    val version: String,
    val fecha: String,
    val hashes: Int,
    val firmantes: Int,
    val origen: String
)

/**
 * Reglas de análisis descargables desde una Release pública de GitHub.
 * La descarga es siempre manual, se comprueba el SHA-256 publicado y se guarda dentro de la app.
 * Si algo falla, se siguen usando las reglas incluidas o las ya descargadas.
 */
object Reglas {
    private const val BASE = "https://github.com/deancavero23-ops/Lumen/releases/download/reglas-v1/"
    private const val URL_JSON = BASE + "reglas.json"
    private const val URL_SHA = BASE + "reglas.json.sha256"
    private const val MAX_BYTES = 1024 * 1024

    fun archivo(context: Context): File = File(context.filesDir, "reglas.json")

    fun texto(context: Context): String {
        val local = archivo(context)
        return try {
            if (local.exists()) local.readText()
            else context.assets.open("reglas.json").bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            context.assets.open("reglas.json").bufferedReader().use { it.readText() }
        }
    }

    fun info(context: Context): InfoReglas = try {
        val json = JSONObject(texto(context))
        InfoReglas(
            version = json.optString("version", "?"),
            fecha = json.optString("actualizado", "?"),
            hashes = json.optJSONObject("hashes")?.length() ?: 0,
            firmantes = json.optJSONObject("firmantes")?.length() ?: 0,
            origen = if (archivo(context).exists()) "github" else "app"
        )
    } catch (e: Exception) {
        InfoReglas("?", "?", 0, 0, "app")
    }

    fun restablecer(context: Context): InfoReglas {
        File(context.filesDir, "reglas.json.tmp").delete()
        archivo(context).delete()
        return info(context)
    }

    fun actualizar(context: Context): InfoReglas {
        val bytes = descargar(URL_JSON)
        val contenidoSha = String(descargar(URL_SHA), Charsets.UTF_8).trim()
        val esperado = contenidoSha.split(Regex("\\s+")).firstOrNull().orEmpty().lowercase(Locale.ROOT)
        if (esperado.length != 64 || esperado.any { it !in "0123456789abcdef" }) {
            throw ErrorReglas("rules_err_hash")
        }
        val obtenido = Utilidades.sha256(bytes).lowercase(Locale.ROOT)
        if (obtenido != esperado) throw ErrorReglas("rules_err_hash")

        validar(bytes)
        val temporal = File(context.filesDir, "reglas.json.tmp")
        try {
            temporal.writeBytes(bytes)
            temporal.copyTo(archivo(context), overwrite = true)
        } catch (e: Exception) {
            throw ErrorReglas("rules_err_guardar")
        } finally {
            temporal.delete()
        }
        return info(context)
    }

    private fun validar(bytes: ByteArray) {
        try {
            val json = JSONObject(String(bytes, Charsets.UTF_8))
            if (!json.has("version") || !json.has("permisosSensibles")) throw ErrorReglas("rules_err_json")
            json.optJSONObject("hashes") ?: throw ErrorReglas("rules_err_json")
            json.optJSONObject("firmantes") ?: throw ErrorReglas("rules_err_json")
            json.optJSONArray("permisosSensibles") ?: throw ErrorReglas("rules_err_json")
        } catch (e: ErrorReglas) {
            throw e
        } catch (e: Exception) {
            throw ErrorReglas("rules_err_json")
        }
    }

    private fun descargar(enlace: String): ByteArray {
        val conexion = URL(enlace).openConnection() as HttpURLConnection
        conexion.connectTimeout = 10_000
        conexion.readTimeout = 15_000
        conexion.instanceFollowRedirects = true
        conexion.setRequestProperty("User-Agent", "Lumen-Seguridad/1.2.1")
        try {
            val codigo = conexion.responseCode
            if (codigo !in 200..299) throw ErrorReglas("rules_err_red")
            val host = conexion.url.host.lowercase(Locale.ROOT)
            val permitido = host == "github.com" ||
                host == "objects.githubusercontent.com" ||
                host.endsWith(".githubusercontent.com")
            if (!permitido) throw ErrorReglas("rules_err_red")

            val salida = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            var total = 0
            conexion.inputStream.use { entrada ->
                while (true) {
                    val n = entrada.read(buffer)
                    if (n < 0) break
                    total += n
                    if (total > MAX_BYTES) throw ErrorReglas("rules_err_grande")
                    salida.write(buffer, 0, n)
                }
            }
            if (salida.size() == 0) throw ErrorReglas("rules_err_red")
            return salida.toByteArray()
        } catch (e: ErrorReglas) {
            throw e
        } catch (e: Exception) {
            throw ErrorReglas("rules_err_red")
        } finally {
            conexion.disconnect()
        }
    }
}
