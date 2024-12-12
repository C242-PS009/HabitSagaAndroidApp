package com.c242_ps009.habitsaga.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import coil3.load
import com.c242_ps009.habitsaga.R
import com.github.jimcoven.view.JCropImageView
import com.github.jimcoven.view.JCropImageView.CropAlign
import com.github.jimcoven.view.JCropImageView.CropType

class MascotView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private var _layer1: String? = null
    private var _layer2: String? = null

    var layer1: String?
        get() = _layer1
        set(value) {
            _layer1 = value
            init()
        }

    var layer2: String?
        get() = _layer2
        set(value) {
            _layer2 = value
            init()
        }

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.MascotView)

        _layer1 = attr.getString(R.styleable.MascotView_layer1)
        _layer2 = attr.getString(R.styleable.MascotView_layer2)

        attr.recycle()
        init()
    }

    private fun init() {
        removeAllViews()
        addLayer(layer1)
        addLayer(layer2)
    }

    private fun addLayer(layer: String?) {
        if (layer == null) return

        val image = JCropImageView(context)
        addView(image)
        image.apply {
            load(layer)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setCropType(CropType.FIT_WIDTH)
            setCropAlign(CropAlign.ALIGN_TOP)
        }
    }
}