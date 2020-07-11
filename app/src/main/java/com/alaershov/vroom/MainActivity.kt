package com.alaershov.vroom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val valueMin = 0.0
    private val valueMax = 220.0

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
            speedometerView.value = Random.Default.nextDouble(valueMin, valueMax)
            postValue()
        }, 500)
    }
}
