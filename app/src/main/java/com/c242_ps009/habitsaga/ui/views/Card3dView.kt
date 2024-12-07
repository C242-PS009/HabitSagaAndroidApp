package com.c242_ps009.habitsaga.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.postDelayed
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

    private val actualPaddingTop: Int
    private val actualPaddingBottom: Int

    private var down = false

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

        actualPaddingTop = paddingTop
        actualPaddingBottom = paddingBottom

        val b = border.toInt()
        updatePadding(left = paddingLeft + b, right = paddingRight + b)
        setDown(false)

        attr.recycle()
        background = Background()

        setOnTouchListener r@{ v, event ->
            if (!hasOnClickListeners()) return@r false

            if (event.action == MotionEvent.ACTION_DOWN) {
                setDown(true)
            } else if (event.action == MotionEvent.ACTION_UP) {
                setDown(false)
                v.performClick()
            }
            return@r true
        }
    }

    private fun setDown(down: Boolean) {
        this.down = down

        val s = shadow.toInt()
        val b = border.toInt()
        if (down) {
            updatePadding(top = actualPaddingTop + s + b, bottom = actualPaddingBottom + b)
        } else {
            updatePadding(top = actualPaddingTop + b, bottom = actualPaddingBottom + s + b)
        }
    }

    inner class Background : Drawable() {

        override fun draw(canvas: Canvas) {
            val w = width.toFloat()
            val h = height.toFloat()

            val l = border / 2
            var t = l
            val r = w - l
            var b = h - t

            if (down) {
                t += shadow
            } else {
                canvas.drawRoundRect(0f, 0f, w, h, radius, radius, shadowPaint)
                b -= shadow
            }

            canvas.drawRoundRect(l, t, r, b, radius, radius, paint)
            canvas.drawRoundRect(l, t, r, b, radius, radius, borderPaint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
            shadowPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
            shadowPaint.colorFilter = colorFilter
        }

        @Deprecated("Deprecated in Java",
            ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
        )
        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

    }

}