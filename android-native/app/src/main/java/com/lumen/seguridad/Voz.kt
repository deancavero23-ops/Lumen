package com.lumen.seguridad

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.Locale

object Voz {
    private var tts: TextToSpeech? = null
    private var reproductor: MediaPlayer? = null
    /** Texto pendiente mientras el motor TTS termina de iniciarse (último recurso). */
    private var pendiente: Pair<String, String>? = null
    private var creandoTts = false
    private var generacion = 0

    /**
     * Reproduce el audio femenino incluido. Usa un único MediaPlayer y preparación asíncrona
     * para no bloquear la interfaz ni acumular reproductores al hablar varias veces.
     */
    fun hablar(context: Context, clave: String, texto: String) {
        val lang = Textos.idioma(context)
        val mp = reproductor ?: MediaPlayer().also { reproductor = it }
        val afd = try {
            context.assets.openFd("audio/$lang-$clave.mp3")
        } catch (e: Exception) {
            // Solo las frases incluidas tienen audio. Si falta, se usa TTS como último recurso.
            hablarTts(context, texto, lang)
            return
        }
        try {
            mp.reset()
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.setOnPreparedListener { it.start() }
            mp.setOnErrorListener { _, _, _ ->
                hablarTts(context, texto, lang)
                true
            }
            mp.prepareAsync()
        } catch (e: Exception) {
            try { afd.close() } catch (e2: Exception) { }
            hablarTts(context, texto, lang)
        }
    }

    /**
     * Respuestas del chat: si el texto coincide con una frase incluida se reproduce su audio
     * femenino; solo se usa el TTS del sistema para texto libre sin audio preparado.
     */
    fun decir(context: Context, texto: String) {
        val clave = Textos.claveDe(context, texto)
        if (clave != null) hablar(context, clave, texto)
        else hablarTts(context, texto, Textos.idioma(context))
    }

    fun detener() {
        synchronized(this) {
            generacion++
            pendiente = null
            creandoTts = false
        }
        reproductor?.let {
            try {
                if (it.isPlaying) it.stop()
                it.release()
            } catch (e: Exception) { }
        }
        reproductor = null
        tts?.let {
            try {
                it.stop()
                it.shutdown()
            } catch (e: Exception) { }
        }
        tts = null
    }

    /** Quita emoji, guillemets y signos que los motores TTS suelen leer en voz alta. */
    private fun limpio(texto: String): String =
        texto.replace(Regex("[^\\p{L}\\p{N}\\s¿?¡!.,;:-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun esFemenina(nombre: String): Boolean {
        val n = nombre.lowercase(Locale.ROOT)
        return n.contains("female") || n.contains("femin") || n.contains("mujer") ||
            n.contains("sabina") || n.contains("zira") || n.contains("serena") ||
            n.contains("samantha") || n.contains("victoria") || n.contains("karen") ||
            n.contains("lucia") || n.contains("paola") || n.contains("chiara") || n.contains("ava")
    }

    private fun configurarYHablar(motor: TextToSpeech, texto: String, lang: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val delIdioma = motor.voices?.filter { voz ->
                    voz.locale?.language?.equals(lang, ignoreCase = true) == true
                }.orEmpty()
                val elegida = delIdioma.firstOrNull { esFemenina(it.name) } ?: delIdioma.firstOrNull()
                if (elegida != null) {
                    motor.setVoice(elegida)
                } else {
                    motor.setLanguage(Locale(lang))
                }
            } else {
                @Suppress("DEPRECATION")
                motor.setLanguage(Locale(lang))
            }
            motor.setPitch(1.05f)
            motor.setSpeechRate(0.98f)
            motor.speak(limpio(texto), TextToSpeech.QUEUE_FLUSH, null, "lumi")
        } catch (e: Exception) {
            // El texto ya se muestra en pantalla.
        }
    }

    private fun hablarTts(context: Context, texto: String, lang: String) {
        val existente = tts
        if (existente != null) {
            configurarYHablar(existente, texto, lang)
            return
        }
        synchronized(this) {
            if (tts != null) {
                configurarYHablar(tts!!, texto, lang)
                return
            }
            // Guardar la última frase y no marcar el motor como listo hasta onInit:
            // hablar sobre un TTS sin inicializar era otra causa de voz "rara".
            pendiente = texto to lang
            if (creandoTts) return
            creandoTts = true
            val mia = generacion
            val referencia = arrayOfNulls<TextToSpeech>(1)
            referencia[0] = TextToSpeech(context.applicationContext) { estado ->
                val frase = synchronized(this) {
                    if (mia != generacion) null else {
                        creandoTts = false
                        val guardada = if (estado == TextToSpeech.SUCCESS) pendiente else null
                        pendiente = null
                        guardada
                    }
                }
                val creado = referencia[0]
                if (estado == TextToSpeech.SUCCESS && creado != null && frase != null) {
                    tts = creado
                    configurarYHablar(creado, frase.first, frase.second)
                } else if (mia == generacion) {
                    try { creado?.shutdown() } catch (e: Exception) { }
                }
            }
        }
    }
}
