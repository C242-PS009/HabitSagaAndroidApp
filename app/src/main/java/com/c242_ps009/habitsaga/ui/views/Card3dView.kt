package com.c242_ps009.habitsaga.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.c242_ps009.habitsaga.R

class Card3dView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val paint: Paint
    private val shadowPaint: Paint
    private val borderPaint: Paint

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.Card3dView)

        paint = Paint().apply {
            color = attr.getColor(R.styleable.Card3dView_color, 0)
        }

        shadowPaint = Paint().apply {
            color = attr.getColor(R.styleable.Card3dView_shadowColor, 0)
        }

        borderPaint = Paint().apply {
            color = attr.getColor(R.styleable.Card3dView_shadowColor, 0)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            strokeWidth = 10f
        }

        attr.recycle()

        background = Background()
    }

    inner class Background : Drawable() {

        override fun draw(canvas: Canvas) {
            val r = 20f

            val w = width.toFloat()
            val h = height.toFloat()

            canvas.drawRoundRect(0f, 0f, w, h, r, r, shadowPaint) // shadow
            canvas.drawRoundRect(0f, 0f, w, h - r, r, r, paint) // main
            canvas.drawRoundRect(0f, 0f, w, h - r, r, r, borderPaint) // border
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
            shadowPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
            shadowPaint.colorFilter = colorFilter
        }

        @Deprecated("Deprecated in Java")
        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

    }

}