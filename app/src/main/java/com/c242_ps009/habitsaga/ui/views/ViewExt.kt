package com.c242_ps009.habitsaga.ui.views

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes

fun View.rColor(@ColorRes ref: Int) = resources.getColor(ref, null)
fun View.rDimenF(@DimenRes ref: Int) = resources.getDimension(ref)
fun View.rDimenI(@DimenRes ref: Int) = resources.getDimensionPixelSize(ref)
