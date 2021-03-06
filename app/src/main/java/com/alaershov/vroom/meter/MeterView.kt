package com.alaershov.vroom.meter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.alaershov.vroom.R
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

    private var config: Config = Config()

    private var handColor: Int = Color.WHITE
    private var dialColor: Int = Color.WHITE
    private var dialBackgroundColor: Int = Color.BLACK
    private var unitText: String = ""
    private var unitTextSize: Float = 0f
    private var unitTextPadding: Float = 0f
    private var valueTextSize: Float = 0f
    private var valueTextPadding: Float = 0f

    private val center: PointF = PointF()

    private var dial: Dial
    private var hand: Hand

    private var minAngle: Float = 150f
    private var maxAngle: Float = 390f
    private val angleRange: Float
        get() = maxAngle - minAngle

    private var circlePadding = dpToPxFloat(6)

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
                dialBackgroundColor = getColor(R.styleable.MeterView_meter_dialBackgroundColor, dialBackgroundColor)
                unitText = getString(R.styleable.MeterView_meter_unitText) ?: unitText
                unitTextSize = getDimension(R.styleable.MeterView_meter_unitTextSize, unitTextSize)
                unitTextPadding = getDimension(R.styleable.MeterView_meter_unitTextPadding, unitTextPadding)
                valueTextSize = getDimension(R.styleable.MeterView_meter_valueTextSize, valueTextSize)
                valueTextPadding = getDimension(R.styleable.MeterView_meter_valueTextPadding, valueTextPadding)
            } finally {
                recycle()
            }
        }

        dial = Dial(
            circleStrokeWidth = dpToPxFloat(2),
            circlePadding = circlePadding,
            minorTickConfig = TickConfig(
                length = dpToPxFloat(15),
                strokeWidth = dpToPxFloat(3),
                color = dialColor
            ),
            majorTickConfig = TickConfig(
                length = dpToPxFloat(30),
                strokeWidth = dpToPxFloat(6),
                color = dialColor
            ),
            color = dialColor,
            backgroundColor = dialBackgroundColor,
            unitTextSize = unitTextSize,
            unitTextPadding = unitTextPadding,
            valueTextSize = valueTextSize,
            valueTextPadding = valueTextPadding
        )

        hand = Hand(
            startWidth = dpToPxFloat(6),
            endWidth = dpToPxFloat(3),
            color = handColor
        )
    }

    fun configure(config: Config) {
        this.config = config
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        center.x = width.toFloat() / 2
        center.y = height.toFloat() / 2

        val size = minOf(width, height)

        drawSpeedometer(canvas, center, size)
    }

    private fun drawSpeedometer(
        canvas: Canvas,
        center: PointF,
        size: Int
    ) {
        drawDial(canvas, center, size)
        drawHand(canvas, center, size)
    }

    private fun drawDial(
        canvas: Canvas,
        center: PointF,
        size: Int
    ) {
        dial.update(
            center = center,
            size = size.toFloat(),
            minorTickAmount = config.minorTickAmount,
            majorTickAmount = config.majorTickAmount,
            minAngle = minAngle,
            maxAngle = maxAngle,
            unitText = unitText,
            majorTickValueList = config.majorTickValueList
        )
        dial.draw(canvas)
    }

    private fun drawHand(
        canvas: Canvas,
        center: PointF,
        size: Int
    ) {
        val length = (size / 2).toFloat() - circlePadding

        val valuePercent = (value / abs(config.valueRange)).coerceIn(0.0, 1.0)
        val angle = (minAngle + (angleRange * valuePercent)).toFloat()

        hand.update(center, length, angle)
        hand.draw(canvas)
    }

    class Config(
        val valueMin: Double = 0.0,
        val valueMax: Double = 1.0,
        val minorTickValue: Double = 1.0,
        val majorTickValue: Double = 1.0
    ) {

        val valueRange: Double = abs(valueMax - valueMin)

        val minorTickAmount: Int = (valueRange / minorTickValue).toInt()
        val majorTickAmount: Int = (valueRange / majorTickValue).toInt()

        val majorTickValueList: List<Double>

        init {
            val list = mutableListOf<Double>()

            for (i in 0..majorTickAmount) {
                list.add(valueMin + majorTickValue * i)
            }

            majorTickValueList = list.toList()
        }
    }
}
