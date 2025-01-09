package com.example.englishquiz.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AnimationUtility {
    fun animateButtonColor(
        context: Context,
        button: MaterialButton,
        startColorRes: Int,
        endColorRes: Int,
    ) {
        val startColor = ContextCompat.getColor(context, startColorRes)
        val endColor = ContextCompat.getColor(context, endColorRes)

        val colorAnimator = ObjectAnimator.ofArgb(button, "backgroundColor", startColor, endColor)

        colorAnimator.duration = 400 // Animation duration in milliseconds
        colorAnimator.start()
    }

    fun animateTextChangeAnimator(
        textView: TextView,
        startCoins: Int,
        targetCoins: Int,
        duration: Long = 1000L,
    ) {
        val animator = ValueAnimator.ofInt(startCoins, targetCoins)
        animator.duration = duration

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            textView.text = "Coins: $animatedValue"
        }

        animator.start()
    }

    fun animateCoinIncreaseCoroutine(
        textView: TextView,
        startCoins: Int,
        targetCoins: Int,
        duration: Long = 1000L,
        lifecycleScope: CoroutineScope,
    ) {
        val steps = targetCoins - startCoins
        if (steps <= 0) return // No animation needed if coins decrease or stay the same

        val interval = duration / steps

        lifecycleScope.launch {
            var currentCoins = startCoins
            while (currentCoins < targetCoins) {
                currentCoins++
                textView.text = "Coins: $currentCoins"
                delay(interval)
            }
        }
    }
}
