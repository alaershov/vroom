package com.alaershov.vroom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

        hand.update(center, (size / 2).toFloat(), minAngle)
        hand.draw(canvas)
    }
}
