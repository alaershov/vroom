package com.alaershov.vroom

import android.graphics.*

class Arrow(
    val startWidth: Float,
    val endWidth: Float,
    val color: Int
) {
    private var length: Float = 0f
    private var angle: Float = 0f

    val pivot: PointF = PointF()
    val path: Path = Path()
    val matrix: Matrix = Matrix()
    private val paint: Paint = Paint()

    init {
        paint.apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            color = this@Arrow.color
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
            val startX = pivot.x
            val y = pivot.y
            val endX = pivot.x + length
            moveTo(startX, y + startWidthShift)
            lineTo(endX, y + endWidthShift)
            lineTo(endX, y - endWidthShift)
            lineTo(startX, y - startWidthShift)
            lineTo(startX, y + startWidthShift)
            setLastPoint(startX, y + startWidthShift)
        }
        matrix.apply {
            reset()
            setRotate(angle, pivot.x, pivot.y)
        }
        path.transform(matrix)
        canvas.drawPath(path, paint)
    }
}
