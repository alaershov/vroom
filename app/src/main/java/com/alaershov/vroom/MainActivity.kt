package com.alaershov.vroom

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

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
            val newValue = Random.Default.nextDouble(valueMin, valueMax)
            val valueDifference: Double = newValue - previousValue
            valueAnimator?.cancel()
            valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 300
                interpolator = LinearInterpolator()
                addUpdateListener { valueAnimator ->
                    val animatedFloat = valueAnimator.animatedValue as Float
                    speedometerView.value = previousValue + (animatedFloat * valueDifference)
                }
                start()
            }

            postValue()
        }, 500)
    }
}
