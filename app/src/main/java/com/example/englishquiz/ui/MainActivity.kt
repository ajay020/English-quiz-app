package com.example.englishquiz.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.example.englishquiz.R
import com.example.englishquiz.databinding.ActivityMainBinding
import com.example.englishquiz.utils.managers.DialogManager
import com.example.englishquiz.utils.managers.SoundManager
import com.example.englishquiz.viewmodel.MainViewModel
import com.example.englishquiz.views.StreakTrackerView
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding

    // Hilt will automatically provide the ViewModel with injected dependencies
    val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var streakTracker: StreakTrackerView

    private var settingsDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState) // Enable dynamic colors
        enableEdgeToEdge()

        // Initialize binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Start music if enabled
        if (viewModel.isMusicEnabled.value == true) {
            viewModel.startMusic()
        }
        setUpToolbar()
        setUpViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        updateCoinCount(viewModel.getCoins())
        return true
    }

    private fun setUpToolbar() {
        // Handle action bar click events
        binding.toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_settings -> {
                    // Open settings dialog
                    openSettingsDialog()
                    true
                }

                R.id.menu_coin_display -> {
                    // Handle coin display click if needed
                    true
                }

                else -> false
            }
        }
    }

    private fun setUpViews() {
        val recyclerView: RecyclerView = binding.streakRecyclerView
        streakTracker.setupStreakTracker(recyclerView)

        // Set up start quiz button
        binding.btnStartQuiz.setOnClickListener {
            viewModel.playClickSound()
            startActivity(Intent(this, QuizActivity::class.java))
        }
    }

    private fun openSettingsDialog() {
        viewModel.playClickSound()

        settingsDialog =
            dialogManager.showSettingsDialog(
                onAudioChanged = { isSoundEnabled ->
                    // Handle sound setting change
                },
                onMusicChanged = { isMusicEnabled ->
                    viewModel.setMusicEnabled(isMusicEnabled)
                },
                onDarkModeChanged = { isDarkModeEnabled ->
                    applyTheme(isDarkModeEnabled)
                },
            )
    }

    private fun applyTheme(isDarkModeEnabled: Boolean) {
        val mode =
            if (isDarkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        AppCompatDelegate.setDefaultNightMode(mode)

        settingsDialog?.dismiss() // Dismiss the dialog before recreating
        settingsDialog = null

        binding.root.postDelayed({
            recreate() // Ensure smooth theme transition
        }, 200) // Delay to avoid flicker
    }

    override fun onResume() {
        super.onResume()
        // Refresh streak data on resumption
        streakTracker.refreshStreakData()

        // Update the coin display
        updateCoinCount(viewModel.getCoins())
    }

    override fun onDestroy() {
        super.onDestroy()
        settingsDialog?.dismiss()
    }

    private fun updateCoinCount(newCount: Int) {
        val toolbar = binding.toolbar
        val coinMenuItem = toolbar.menu.findItem(R.id.menu_coin_display)
        val actionView = coinMenuItem?.actionView

        if (actionView != null) {
            val coinCountTextView = actionView.findViewById<TextView>(R.id.coinCount)
            coinCountTextView?.text = newCount.toString()
        } else {
            Log.e("MainActivity", "Action view is null while updating coin count!")
        }
    }
}
