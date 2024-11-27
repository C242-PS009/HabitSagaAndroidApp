package com.c242_ps009.habitsaga.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.updatePadding
import com.c242_ps009.habitsaga.R

class Card3dView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val shadow: Float
    private val radius: Float
    private val border: Float

    private val paint: Paint
    private val shadowPaint: Paint
    private val borderPaint: Paint

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.Card3dView)

        shadow = attr.getDimension(R.styleable.Card3dView_shadowHeight, rDimenF(R.dimen.card_shadow))
        radius = attr.getDimension(R.styleable.Card3dView_radius, rDimenF(R.dimen.card_radius))
        border = attr.getDimension(R.styleable.Card3dView_border, rDimenF(R.dimen.card_border))

        paint = Paint().apply {
            color = attr.getColor(R.styleable.Card3dView_color, rColor(R.color.card_color))
        }

        shadowPaint = Paint().apply {
            color = attr.getColor(R.styleable.Card3dView_shadowColor, rColor(R.color.card_shadow))
        }

        borderPaint = Paint().apply {
            color = attr.getColor(R.styleable.Card3dView_shadowColor, rColor(R.color.card_shadow))
            style = Paint.Style.STROKE
            strokeWidth = border
        }

        val s = shadow.toInt()
        val b = border.toInt()
        updatePadding(paddingLeft + b, paddingTop + b, paddingRight + b, paddingBottom + s + b)

        attr.recycle()
        background = Background()
    }

    inner class Background : Drawable() {

        override fun draw(canvas: Canvas) {
            val sh = shadow
            val r = radius
            val w = width.toFloat()
            val h = height.toFloat()

            canvas.drawRoundRect(0f, 0f, w, h, r, r, shadowPaint)

            val b = border / 2
            val br = w - b
            val bb = h - sh - b
            canvas.drawRoundRect(b, b, br, bb, r, r, paint)
            canvas.drawRoundRect(b, b, br, bb, r, r, borderPaint)
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