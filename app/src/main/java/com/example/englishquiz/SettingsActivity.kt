package com.example.englishquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.englishquiz.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
