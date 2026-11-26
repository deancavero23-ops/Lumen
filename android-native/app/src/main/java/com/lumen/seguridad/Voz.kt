package com.lumen.seguridad

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.Locale

object Voz {
    private var tts: TextToSpeech? = null

    /** Reproduce el audio femenino incluido; si falta, usa TTS con voz femenina preferida. */
    fun hablar(context: Context, clave: String, texto: String) {
        val lang = Textos.idioma(context)
        try {
            val afd = context.assets.openFd("audio/$lang-$clave.wav")
            val mp = MediaPlayer()
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.prepare()
            mp.setOnCompletionListener { it.release() }
            mp.start()
            return
        } catch (e: Exception) {
            // Sin audio incluido: se usa la voz del sistema.
        }
        hablarTts(context, texto, lang)
    }

    private fun configurarYHablar(t: TextToSpeech, texto: String, lang: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val femenina = t.voices?.firstOrNull { v ->
                    val n = v.name.lowercase(Locale.ROOT)
                    n.contains("female") || n.contains("femin") ||
                        n.contains("sabina") || n.contains("zira") || n.contains("serena")
                }
                if (femenina != null) t.setVoice(femenina) else t.setLanguage(Locale(lang))
            } else {
                @Suppress("DEPRECATION")
                t.setLanguage(Locale(lang))
            }
            t.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "lumi")
        } catch (e: Exception) {
            // El texto ya se muestra en pantalla.
        }
    }

    private fun hablarTts(context: Context, texto: String, lang: String) {
        tts?.let { configurarYHablar(it, texto, lang); return }
        var instancia: TextToSpeech? = null
        instancia = TextToSpeech(context.applicationContext) { estado ->
            if (estado == TextToSpeech.SUCCESS && instancia != null) {
                tts = instancia
                configurarYHablar(instancia!!, texto, lang)
            }
        }
        if (instancia == null) tts = instancia
    }
}
