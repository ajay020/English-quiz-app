package com.example.quizzybee.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.quizzybee.data.preferences.PreferenceManager
import com.example.quizzybee.utils.managers.DialogManager
import com.example.quizzybee.utils.managers.SoundManager
import com.example.quizzybee.viewmodel.MainViewModel
import com.example.quizzybee.views.StreakTrackerView
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    private lateinit var activity: MainActivity
    private lateinit var mockSoundManager: SoundManager
    private lateinit var mockDialogManager: DialogManager
    private lateinit var mockStreakTracker: StreakTrackerView
    private lateinit var context: Context
    private lateinit var mockPreferenceManager: PreferenceManager
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        // Mock dependencies
        mockSoundManager = mockk(relaxed = true)
        mockDialogManager = mockk(relaxed = true)
        mockStreakTracker = mockk(relaxed = true)
        mockPreferenceManager = mockk(relaxed = true)
        context = ApplicationProvider.getApplicationContext()

        // Mock ViewModel
        viewModel = MainViewModel(mockSoundManager, mockPreferenceManager)

        // Create the activity using Robolectric
        activity =
            Robolectric
                .buildActivity(MainActivity::class.java)
                .create()
                .resume()
                .get()

        // Inject mocks into activity
        activity.dialogManager = mockDialogManager
        activity.streakTracker = mockStreakTracker
    }

    @Test
    fun `onCreate should start music if enabled`() {
        every { activity.viewModel.isMusicEnabled.value } returns true

        every { activity.viewModel.startMusic() } just runs

        verify { activity.viewModel.startMusic() }
    }

//    @Test
//    fun `test onCreate initializes views correctly`() {
//        // Verify StreakTrackerView was set up
//        val recyclerView: RecyclerView = activity.findViewById(R.id.streak_recycler_view)
//        verify { mockStreakTracker.setupStreakTracker(recyclerView) }
//
//        // Verify coin display updates coin count
//        val coinDisplay: TextView = activity.findViewById(R.id.coinDisplay)
//        verify { coinDisplay.text = any() }
//    }

//    @Test
//    fun `test settings button click shows settings dialog`() {
//        val settingsButton: Button = activity.findViewById(R.id.btn_settings)
//
//        // Simulate button click
//        settingsButton.performClick()
//
//        verify {
//            mockDialogManager.showSettingsDialog(
//                any(),
//                any(),
//                any(),
//            )
//        }
//    }

//    @Test
//    fun `test start quiz button navigates to QuizActivity`() {
//        val startQuizButton: Button = activity.findViewById(R.id.btn_start_quiz)
//
//        // Spy on the activity to verify startActivity is called
//        val spyActivity = spyk(activity)
//
//        // Simulate button click
//        startQuizButton.performClick()
//
//        verify {
//            spyActivity.startActivity(
//                match { intent ->
//                    intent.component?.className == QuizActivity::class.java.name
//                },
//            )
//        }
//    }

//    @Test
//    fun `test onResume refreshes streak data and updates coin display`() {
//        // Use Robolectric's lifecycle controller to trigger onResume
//        val controller = Robolectric.buildActivity(MainActivity::class.java)
//        val activity = controller.create().start().resume().get()
//
//        // Verify that streak data is refreshed
//        verify { activity.streakTracker.refreshStreakData() }
//
//        // Verify that coin display is updated
//        verify { activity.binding.coinDisplay.setCoinCount(any()) }
//    }
}
