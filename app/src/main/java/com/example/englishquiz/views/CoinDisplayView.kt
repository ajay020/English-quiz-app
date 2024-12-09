package com.example.englishquiz.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.englishquiz.R
import com.example.englishquiz.utils.extensions.animateNumberChange

class CoinDisplayView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr) {
        private val coinImage: ImageView
        private val coinCount: TextView

        init {
            LayoutInflater.from(context).inflate(R.layout.layout_coin_display, this, true)
            coinImage = findViewById(R.id.coinImage)
            coinCount = findViewById(R.id.coinCount)
        }

        fun setCoinCount(count: Int) {
            coinCount.text = count.toString()
        }

        fun setAnimatedCoinCount(
            startValue: Int,
            endValue: Int,
            duration: Long = 1000L,
        ) {
            coinCount.animateNumberChange(
                startValue = startValue,
                endValue = endValue,
                duration = duration,
                prefix = "",
            )
        }

        fun setCoinImage(imageResId: Int) {
            coinImage.setImageResource(imageResId)
        }
    }
