package com.alaershov.vroom

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val valueMin = 0.0
    private val valueMax = 220.0

    private var valueAnimator: ValueAnimator? = null

    private lateinit var speedometerView: MeterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speedometerView = findViewById(R.id.view_speedometer)
        speedometerView.setup(
            valueMin = valueMin,
            valueMax = valueMax,
            minorTickValue = 10.0,
            majorTickValue = 20.0
        )
        postValue()
    }

    private fun postValue() {
        handler.postDelayed({
            val previousValue = speedometerView.value
            val newValue = currentSpeed(System.currentTimeMillis())

            animateSpeed(previousValue, newValue)

            postValue()
        }, 50)
    }

    private fun animateSpeed(previousValue: Double, newValue: Double) {
        val valueDifference: Double = newValue - previousValue

        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 100
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                val animatedFloat = valueAnimator.animatedValue as Float
                speedometerView.value = previousValue + (animatedFloat * valueDifference)
            }
            start()
        }
    }

    private fun currentSpeed(time: Long): Double {
        val bigPeriod: Double = 10.0 * 1000
        val smallPeriod: Double = bigPeriod / 30

        val big = speedPart(time, bigPeriod) * 0.8
        val small = speedPart(time, smallPeriod) * 0.2

        val range = abs(valueMax - valueMin)
        return valueMin + range * (big + small)
    }

    private fun speedPart(time: Long, period: Double): Double {
        val fullPeriod = Math.PI * 2
        val partOfPeriod = time.rem(period) / period
        return (sin(partOfPeriod * fullPeriod - Math.PI / 2) + 1) / 2
    }
}
