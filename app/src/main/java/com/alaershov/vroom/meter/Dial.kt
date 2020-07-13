package com.alaershov.vroom.meter

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

class Dial(
    val circleStrokeWidth: Float,
    val circlePadding: Float,
    val minorTickConfig: TickConfig,
    val majorTickConfig: TickConfig,
    val color: Int,
    val backgroundColor: Int,
    val unitTextSize: Float
) {

    private var minorTickAmount: Int = 0
    private var majorTickAmount: Int = 0

    private var minAngle: Float = 0f
    private var maxAngle: Float = 0f
    private val angleRange: Float
        get() = maxAngle - minAngle

    private var unitText: String = ""

    private val center: PointF = PointF()
    private var size: Float = 0f
    private val radius: Float
        get() = (size - circleStrokeWidth) / 2

    private val path: Path = Path()
    private val matrix: Matrix = Matrix()
    private val circlePaint: Paint = Paint()
    private val backgroundPaint: Paint = Paint()
    private val minorTickPaint: Paint = Paint()
    private val majorTickPaint: Paint = Paint()
    private val unitTextPaint: Paint = Paint()

    init {
        circlePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@Dial.circleStrokeWidth
            isAntiAlias = true
            color = this@Dial.color
        }
        backgroundPaint.apply {
            style = Paint.Style.FILL
            color = backgroundColor
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

        unitTextPaint.apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = unitTextSize
            color = this@Dial.color
        }
    }

    fun update(
        center: PointF,
        size: Float,
        minorTickAmount: Int,
        majorTickAmount: Int,
        minAngle: Float,
        maxAngle: Float,
        unitText: String
    ) {
        this.center.set(center)
        this.size = size
        this.minorTickAmount = minorTickAmount
        this.majorTickAmount = majorTickAmount
        this.minAngle = minAngle
        this.maxAngle = maxAngle
        this.unitText = unitText
    }

    fun draw(canvas: Canvas) {
        drawCircle(canvas)
        drawTicks(canvas)
        drawTickValues(canvas)
        drawUnitText(canvas)
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, backgroundPaint)
        canvas.drawCircle(center.x, center.y, radius, circlePaint)
    }

    private fun drawTicks(canvas: Canvas) {
        drawMinorTicks(canvas)
        drawMajorTicks(canvas)
    }

    private fun drawMinorTicks(canvas: Canvas) {
        val tickAngleValue = angleRange / minorTickAmount
        for (i in 0..minorTickAmount) {
            drawTick(canvas, minAngle + tickAngleValue * i, minorTickConfig)
        }
    }

    private fun drawMajorTicks(canvas: Canvas) {
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

    private fun drawUnitText(canvas: Canvas) {
        canvas.drawText(unitText, center.x, center.y + size / 4, unitTextPaint)
    }
}
