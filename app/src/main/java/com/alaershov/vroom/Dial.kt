package com.alaershov.vroom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF

class Dial(
    val strokeWidth: Float,
    val color: Int
) {

    private val center: PointF = PointF()
    private var size: Float = 0f
    private val radius: Float
        get() = (size - strokeWidth) / 2

    private val circlePaint: Paint = Paint()

    init {
        circlePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@Dial.strokeWidth
            isAntiAlias = true
            color = this@Dial.color
        }
    }

    fun update(center: PointF, size: Float) {
        this.center.set(center)
        this.size = size
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, circlePaint)
    }
}
