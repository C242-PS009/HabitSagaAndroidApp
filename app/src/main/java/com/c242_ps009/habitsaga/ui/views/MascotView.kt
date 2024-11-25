package com.c242_ps009.habitsaga.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import coil3.load
import com.github.jimcoven.view.JCropImageView
import com.github.jimcoven.view.JCropImageView.CropAlign
import com.github.jimcoven.view.JCropImageView.CropType

class MascotView : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    fun init(firstLayer: String, vararg layers: String) {
        removeAllViews()

        addLayer(firstLayer)
        for (layer in layers) addLayer(layer)
    }

    private fun addLayer(layer: String) {
        val image = JCropImageView(context)

        addView(image)
        image.apply {
            load(layer)
            layoutParams.width = LayoutParams.MATCH_PARENT
            layoutParams.height = LayoutParams.MATCH_PARENT
            setCropType(CropType.FIT_WIDTH)
            setCropAlign(CropAlign.ALIGN_TOP)
        }
    }

}
