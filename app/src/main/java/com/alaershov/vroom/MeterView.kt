package com.alaershov.vroom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class MeterView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var arrowColor: Int = Color.WHITE
    private var circleColor: Int = Color.WHITE

    private val center: PointF = PointF()

    private var arrow: Arrow
    private var minAngle: Float = 150f
    private var maxAngle: Float = 390f

    private val circlePaint: Paint = Paint()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MeterView,
            0,
            0
        ).apply {
            try {
                arrowColor = getColor(R.styleable.MeterView_meter_arrowColor, arrowColor)
                circleColor = getColor(R.styleable.MeterView_meter_circleColor, circleColor)
            } finally {
                recycle()
            }
        }

        circlePaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = dpToPxInt(2).toFloat()
            isAntiAlias = true
            color = circleColor
        }

        arrow = Arrow(
            startWidth = dpToPxFloat(4),
            endWidth = dpToPxFloat(2),
            color = arrowColor
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        center.x = width.toFloat() / 2
        center.y = height.toFloat() / 2
        val size = minOf(width, height)

        drawSpeedometer(canvas, center, size)
    }

    private fun drawSpeedometer(canvas: Canvas, center: PointF, size: Int) {
        val radius = size / 2 - circlePaint.strokeWidth

        drawOuterCircle(canvas, center, radius)

        arrow.update(center, radius, minAngle)
        arrow.draw(canvas)
    }

    private fun drawOuterCircle(canvas: Canvas, center: PointF, radius: Float) {
        canvas.drawCircle(center.x, center.y, radius, circlePaint)
    }
}
