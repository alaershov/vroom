package com.alaershov.vroom.meter

import android.graphics.Paint

class TickConfig(
    val length: Float,
    val strokeWidth: Float,
    val color: Int
) {
    val paint: Paint = Paint()

    init {
        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@TickConfig.strokeWidth
            isAntiAlias = true
            color = this@TickConfig.color
        }
    }
}
