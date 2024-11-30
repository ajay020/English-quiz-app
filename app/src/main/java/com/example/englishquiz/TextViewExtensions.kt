package com.example.englishquiz

import android.animation.ValueAnimator
import android.widget.TextView

fun TextView.animateNumberChange(
    startValue: Int,
    endValue: Int,
    duration: Long = 1000L,
    prefix: String = "",
    suffix: String = "",
) {
    val animator = ValueAnimator.ofInt(startValue, endValue)
    animator.duration = duration

    animator.addUpdateListener { animation ->
        val animatedValue = animation.animatedValue as Int
        this.text = "$prefix$animatedValue$suffix"
    }

    animator.start()
}
