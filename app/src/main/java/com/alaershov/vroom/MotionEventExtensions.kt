package com.alaershov.vroom

import android.view.MotionEvent

val MotionEvent.focalPoint: Pair<Float, Float>
    get() {
        val pointerUp = action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP
        val skipIndex = if (pointerUp) this.actionIndex else -1

        var sumX = 0f
        var sumY = 0f
        val count: Int = this.pointerCount
        for (i in 0 until count) {
            if (skipIndex == i) continue
            sumX += this.getX(i)
            sumY += this.getY(i)
        }
        val div = if (pointerUp) count - 1 else count
        val focusX = sumX / div
        val focusY = sumY / div
        return Pair(focusX, focusY)
    }
