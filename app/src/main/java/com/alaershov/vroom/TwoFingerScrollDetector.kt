package com.alaershov.vroom

import android.view.MotionEvent
import androidx.core.view.MotionEventCompat

class TwoFingerScrollDetector(
    private val listener: Listener
) {

    private var state: State = State.None

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == POINTER_COUNT) {
                    state = State.Scroll(MotionEvent.obtain(event), event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                state.also {
                    when (it) {
                        is State.Scroll -> {
                            if (event.pointerCount != POINTER_COUNT) {
                                state = State.None
                                listener.onScrollFinished(it.from, it.current)
                            } else {
                                listener.onScroll(it.from, it.current)
                                it.current = event
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                state.also {
                    when (it) {
                        is State.Scroll -> listener.onScrollFinished(it.from, it.current)
                    }
                }
                state = State.None
            }
        }
        return true
    }

    private sealed class State {
        object None : State()
        class Scroll(
            val from: MotionEvent,
            var current: MotionEvent
        ) : State()
    }

    interface Listener {

        fun onScroll(
            from: MotionEvent,
            current: MotionEvent
        )

        fun onScrollFinished(
            from: MotionEvent,
            current: MotionEvent
        )
    }

    companion object {

        private const val POINTER_COUNT = 2
    }
}
