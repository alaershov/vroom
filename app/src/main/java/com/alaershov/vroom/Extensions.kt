package com.alaershov.vroom

import android.view.View
import kotlin.math.roundToInt

fun View.dpToPxInt(dp: Int): Int = (resources.displayMetrics.density * dp).roundToInt()

fun View.dpToPxFloat(dp: Int): Float = resources.displayMetrics.density * dp
