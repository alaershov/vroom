package com.alaershov.vroom

import android.graphics.*
import android.util.Log

class Dial(
    val circleStrokeWidth: Float,
    val circlePadding: Float,
    val minorTickConfig: TickConfig,
    val majorTickConfig: TickConfig,
    val color: Int
) {

    private var minorTickAmount: Int = 0
    private var majorTickAmount: Int = 0

    private var minAngle: Float = 0f
    private var maxAngle: Float = 0f
    private val angleRange: Float
        get() = maxAngle - minAngle

    private val center: PointF = PointF()
    private var size: Float = 0f
    private val radius: Float
        get() = (size - circleStrokeWidth) / 2

    private val path: Path = Path()
    private val matrix: Matrix = Matrix()
    private val circlePaint: Paint = Paint()
    private val minorTickPaint: Paint = Paint()
    private val majorTickPaint: Paint = Paint()

    init {
        circlePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@Dial.circleStrokeWidth
            isAntiAlias = true
            color = this@Dial.color
        }

        minorTickPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@Dial.circleStrokeWidth
            isAntiAlias = true
            color = this@Dial.color
        }

        majorTickPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@Dial.circleStrokeWidth * 2
            isAntiAlias = true
            color = this@Dial.color
        }
    }

    fun update(
        center: PointF,
        size: Float,
        minorTickAmount: Int,
        majorTickAmount: Int,
        minAngle: Float,
        maxAngle: Float
    ) {
        this.center.set(center)
        this.size = size
        this.minorTickAmount = minorTickAmount
        this.majorTickAmount = majorTickAmount
        this.minAngle = minAngle
        this.maxAngle = maxAngle
    }

    fun draw(canvas: Canvas) {
        drawCircle(canvas)
        drawTicks(canvas)
        drawTickValues(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, circlePaint)
    }

    private fun drawTicks(canvas: Canvas) {
        drawMinorTicks(canvas)
        drawMajorTicks(canvas)
    }

    private fun drawMinorTicks(canvas: Canvas) {
        Log.d("HandView", "minorTickAmount=$minorTickAmount")
        val tickAngleValue = angleRange / minorTickAmount
        for (i in 0..minorTickAmount) {
            drawTick(canvas, minAngle + tickAngleValue * i, minorTickConfig)
        }
    }

    private fun drawMajorTicks(canvas: Canvas) {
        Log.d("HandView", "majorTickAmount=$majorTickAmount")
        val tickAngleValue = angleRange / majorTickAmount
        for (i in 0..majorTickAmount) {
            drawTick(canvas, minAngle + tickAngleValue * i, majorTickConfig)
        }
    }

    private fun drawTick(
        canvas: Canvas,
        angle: Float,
        tickConfig: TickConfig
    ) {
        matrix.apply {
            reset()
            setRotate(angle)
            postTranslate(center.x, center.y)
        }

        path.apply {
            reset()
            val outerRadius = radius - circlePadding
            moveTo(outerRadius - tickConfig.length, 0f)
            lineTo(outerRadius, 0f)
            transform(matrix)
        }

        canvas.drawPath(path, tickConfig.paint)
    }

    private fun drawTickValues(canvas: Canvas) {

    }
}
