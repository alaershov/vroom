package com.alaershov.vroom

import android.graphics.*

class Hand(
    val startWidth: Float,
    val endWidth: Float,
    val color: Int
) {
    private var length: Float = 0f
    private var angle: Float = 0f

    private val pivot: PointF = PointF()
    private val path: Path = Path()
    private val matrix: Matrix = Matrix()
    private val paint: Paint = Paint()

    init {
        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            color = this@Hand.color
        }
    }

    fun update(center: PointF, length: Float, angle: Float) {
        this.pivot.set(center)
        this.length = length
        this.angle = angle
    }

    fun draw(canvas: Canvas) {
        path.apply {
            reset()
            val startWidthShift = startWidth / 2
            val endWidthShift = endWidth / 2
            val startX = 0f
            val y = 0f
            val endX = startX + length
            moveTo(startX, y + startWidthShift)
            lineTo(endX, y + endWidthShift)
            lineTo(endX, y - endWidthShift)
            lineTo(startX, y - startWidthShift)
            lineTo(startX, y + startWidthShift)
            setLastPoint(startX, y + startWidthShift)
        }
        matrix.apply {
            reset()
            setRotate(angle)
            postTranslate(pivot.x, pivot.y)
        }
        path.transform(matrix)
        canvas.drawPath(path, paint)
    }
}
