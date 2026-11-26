package com.lumen.seguridad

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Escudo de Lumen dibujado en código: rojo con la L negra y la chispa blanca.
 * No depende de que el móvil tenga fuente de emoji 🛡️ instalada.
 */
class EscudoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val rojo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E11D2E") }
    private val negro = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0B0B0C") }
    private val blanco = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val s = minOf(width, height) / 100f
        val cx = (width - 100f * s) / 2f
        val cy = (height - 100f * s) / 2f
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(s, s)

        val escudo = Path().apply {
            moveTo(50f, 3f)
            lineTo(93f, 18f)
            lineTo(93f, 52f)
            quadTo(93f, 79f, 50f, 97f)
            quadTo(7f, 79f, 7f, 52f)
            lineTo(7f, 18f)
            close()
        }
        canvas.drawPath(escudo, rojo)

        // L negra estilizada.
        canvas.drawRect(31f, 29f, 41f, 72f, negro)
        canvas.drawRect(31f, 63f, 68f, 72f, negro)

        // Chispa blanca.
        val chispa = Path().apply {
            moveTo(67f, 22f)
            lineTo(70f, 31f)
            lineTo(79f, 34f)
            lineTo(70f, 37f)
            lineTo(67f, 46f)
            lineTo(64f, 37f)
            lineTo(55f, 34f)
            lineTo(64f, 31f)
            close()
        }
        canvas.drawPath(chispa, blanco)
        canvas.restore()
    }
}
