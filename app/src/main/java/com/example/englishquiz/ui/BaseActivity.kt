package com.example.englishquiz.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.englishquiz.data.preferences.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager.saveFirstLaunchDate()
    }
}
