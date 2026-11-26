package com.lumen.seguridad

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.Locale

object Voz {
    private var tts: TextToSpeech? = null
    private var reproductor: MediaPlayer? = null

    /**
     * Reproduce el audio femenino incluido. Usa un único MediaPlayer y preparación asíncrona
     * para no bloquear la interfaz ni acumular reproductores al hablar varias veces.
     */
    fun hablar(context: Context, clave: String, texto: String) {
        val lang = Textos.idioma(context)
        val mp = reproductor ?: MediaPlayer().also { reproductor = it }
        try {
            mp.reset()
            val afd = context.assets.openFd("audio/$lang-$clave.wav")
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.setOnPreparedListener { it.start() }
            mp.setOnErrorListener { _, _, _ ->
                hablarTts(context, texto, lang)
                true
            }
            mp.prepareAsync()
        } catch (e: Exception) {
            hablarTts(context, texto, lang)
        }
    }

    /** Habla texto libre (respuestas del chat) con el TTS del sistema. */
    fun decir(context: Context, texto: String) = hablarTts(context, texto, Textos.idioma(context))

    fun detener() {
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
            } else {
                val referencia = arrayOfNulls<TextToSpeech>(1)
                referencia[0] = TextToSpeech(context.applicationContext) { estado ->
                    val creado = referencia[0]
                    if (estado == TextToSpeech.SUCCESS && creado != null) {
                        tts = creado
                        configurarYHablar(creado, texto, lang)
                    }
                }
                tts = referencia[0]
            }
        }
    }
}
