package com.alaershov.vroom

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import kotlin.math.abs

class ScrollCoordinator(
    private val container: ViewGroup,
    firstView: View,
    secondView: View,
    private val onViewActive: (id: Int, isActive: Boolean) -> Unit,
    private val nextPageThreshold: Float = 0.3f,
    private val snapDuration: Long = 300,
    private val snapInterpolator: TimeInterpolator = DecelerateInterpolator()
) {
    private var currentView: View = firstView
    private var nextView: View = secondView

    private var isCurrentViewActive: Boolean = false
        set(value) {
            if (field != value) {
                onViewActive.invoke(currentView.id, value)
            }
            field = value
        }
    private var isNextViewActive: Boolean = false
        set(value) {
            if (field != value) {
                onViewActive.invoke(nextView.id, value)
            }
            field = value
        }

    private var valueAnimator: ValueAnimator? = null

    private var currentScrollValue: Float = 0f

    private val currentScrollPercent: Float
        get() = currentScrollValue / container.width

    init {
        currentView.z = 0f
        nextView.z = 1f
        isCurrentViewActive = true
        isNextViewActive = false
        container.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                container.viewTreeObserver.removeOnGlobalLayoutListener(this)
                applyScrollState(0f)
            }
        })
    }


    fun onScroll(scrollValue: Float) {
        valueAnimator?.cancel()
        currentScrollValue = scrollValue
        applyScrollState(currentScrollPercent)
        isCurrentViewActive = true
        isNextViewActive = true
    }

    fun onScrollFinished() {
        val scrollPercent = abs(currentScrollPercent)
        if (scrollPercent > nextPageThreshold) {
            animateTransitionForward()
        } else {
            animateTransitionBack()
        }
    }

    private fun animateTransitionForward() {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(currentScrollPercent, -1f).also {
            it.duration = snapDuration
            it.interpolator = snapInterpolator
            it.addUpdateListener { animator ->
                val animatedFloat = animator.animatedValue as Float
                applyScrollState(animatedFloat)
            }
            it.doOnEnd {
                swapViews()
                applyScrollState(0f)
            }
            it.start()
        }
    }

    private fun swapViews() {
        val tempView: View = currentView
        currentView = nextView
        nextView = tempView
        currentView.z = 0f
        nextView.z = 1f
        currentView.alpha = 1f
        nextView.alpha = 1f
        isCurrentViewActive = true
        isNextViewActive = false
    }

    private fun animateTransitionBack() {
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(currentScrollPercent, 0f).also {
            it.duration = snapDuration
            it.interpolator = snapInterpolator
            it.addUpdateListener { animator ->
                val animatedFloat = animator.animatedValue as Float
                applyScrollState(animatedFloat)
            }
            it.doOnEnd {
                isCurrentViewActive = true
                isNextViewActive = false
            }
            it.start()
        }
    }

    private fun applyScrollState(percent: Float) {
        currentView.alpha = 1 - abs(percent)
        currentView.x = 0 + (container.width * percent) / 2
        nextView.x = container.width + (container.width * percent)
    }
}
