package com.alaershov.vroom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.abs

class MeterView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var value: Double = 0.0
        set(value) {
            field = value
            invalidate()
        }

    private var valueMin: Double = 0.0
    private var valueMax: Double = 0.0
    private var minorTickValue: Double = 0.0
    private var majorTickValue: Double = 0.0

    private var handColor: Int = Color.WHITE
    private var dialColor: Int = Color.WHITE

    private val center: PointF = PointF()

    private var dial: Dial
    private var hand: Hand

    private var minAngle: Float = 150f
    private var maxAngle: Float = 390f
    private val angleRange: Float
        get() = maxAngle - minAngle

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MeterView,
            0,
            0
        ).apply {
            try {
                handColor = getColor(R.styleable.MeterView_meter_handColor, handColor)
                dialColor = getColor(R.styleable.MeterView_meter_dialColor, dialColor)
            } finally {
                recycle()
            }
        }

        dial = Dial(
            strokeWidth = dpToPxFloat(2),
            color = dialColor
        )

        hand = Hand(
            startWidth = dpToPxFloat(6),
            endWidth = dpToPxFloat(2),
            color = handColor
        )
    }

    fun setup(
        valueMin: Double,
        valueMax: Double,
        minorTickValue: Double,
        majorTickValue: Double
    ) {
        this.valueMin = valueMin
        this.valueMax = valueMax
        this.minorTickValue = minorTickValue
        this.majorTickValue = majorTickValue
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        center.x = width.toFloat() / 2
        center.y = height.toFloat() / 2

        val size = minOf(width, height)

        drawSpeedometer(canvas, center, size)
    }

    private fun drawSpeedometer(canvas: Canvas, center: PointF, size: Int) {
        dial.update(center, size.toFloat())
        dial.draw(canvas)

        drawHand(center, size, canvas)
    }

    private fun drawHand(
        center: PointF,
        size: Int,
        canvas: Canvas
    ) {
        val length = (size / 2).toFloat()

        val valuePercent = (value / abs(valueMax - valueMin)).coerceIn(0.0, 1.0)
        val angle = (minAngle + (angleRange * valuePercent)).toFloat()
        Log.d("MeterView", "value=$value valuePercent=$valuePercent angle=$angle")

        hand.update(center, length, angle)
        hand.draw(canvas)
    }
}
