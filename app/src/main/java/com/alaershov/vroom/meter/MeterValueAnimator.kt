package com.alaershov.vroom.meter

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class MeterValueAnimator(
    private val meterView: MeterView,
    private val duration: Long = 100,
    private val interpolator: TimeInterpolator = LinearInterpolator()
) {

    private var valueAnimator: ValueAnimator? = null

    fun animateValue(value: Double) {
        val previousValue = meterView.value
        val valueDifference: Double = value - previousValue

        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).also {
            it.duration = duration
            it.interpolator = interpolator
            it.addUpdateListener { valueAnimator ->
                val animatedFloat = valueAnimator.animatedValue as Float
                meterView.value = previousValue + (animatedFloat * valueDifference)
            }
            it.start()
        }
    }
}
