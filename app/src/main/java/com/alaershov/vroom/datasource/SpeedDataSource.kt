package com.alaershov.vroom.datasource

import kotlin.math.abs
import kotlin.math.sin

class SpeedDataSource(
    private val valueMin: Double,
    private val valueMax: Double
) {

    fun getSpeed(time: Long): Double {
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
