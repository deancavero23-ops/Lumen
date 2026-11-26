package com.lumen.seguridad

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/** Mascota de Lumen dibujada sin imágenes externas: cuadrado rojo, L negra y chispa blanca. */
class LumiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val rojo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E11D2E") }
    private val negro = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0B0B0C")
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
    private val blanco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val lado = min(width, height).toFloat()
        val m = lado * 0.06f
        val r = RectF(m, m, lado - m, lado - m)
        canvas.drawRoundRect(r, lado * 0.18f, lado * 0.18f, rojo)

        negro.textSize = lado * 0.56f
        canvas.drawText("L", lado * 0.47f, lado * 0.75f, negro)

        val cx = lado * 0.76f
        val cy = lado * 0.27f
        val s = lado * 0.11f
        val p = Path().apply {
            moveTo(cx, cy - s)
            lineTo(cx + s * 0.28f, cy - s * 0.28f)
            lineTo(cx + s, cy)
            lineTo(cx + s * 0.28f, cy + s * 0.28f)
            lineTo(cx, cy + s)
            lineTo(cx - s * 0.28f, cy + s * 0.28f)
            lineTo(cx - s, cy)
            lineTo(cx - s * 0.28f, cy - s * 0.28f)
            close()
        }
        canvas.drawPath(p, blanco)
    }
}
