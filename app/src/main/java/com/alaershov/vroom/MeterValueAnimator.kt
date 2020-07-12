package com.alaershov.vroom

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.alaershov.vroom.meter.MeterView

class MeterValueAnimator(
    private val meterView: MeterView
) {

    private var valueAnimator: ValueAnimator? = null

    fun animateValue(value: Double) {
        val previousValue = meterView.value
        val valueDifference: Double = value - previousValue

        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).also {
            it.duration = 100
            it.interpolator = LinearInterpolator()
            it.addUpdateListener { valueAnimator ->
                val animatedFloat = valueAnimator.animatedValue as Float
                meterView.value = previousValue + (animatedFloat * valueDifference)
            }
            it.start()
        }
    }
}
