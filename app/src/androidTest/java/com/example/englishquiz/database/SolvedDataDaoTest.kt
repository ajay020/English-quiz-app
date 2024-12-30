package com.example.englishquiz.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.englishquiz.data.database.AppDatabase
import com.example.englishquiz.data.database.SolvedDate
import com.example.englishquiz.data.database.SolvedDateDao
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SolvedDataDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var solvedDateDao: SolvedDateDao

    @Inject
    lateinit var database: AppDatabase

    @Before
    fun setup() {
        hiltRule.inject()
        solvedDateDao = database.solvedDateDao()
    }

    @Test
    fun testInsertSolvedDate() =
        runTest {
            val solvedDate = SolvedDate("2025-12-2")
            solvedDateDao.insertSolvedDate(solvedDate)
            val allSolvedDates = solvedDateDao.getAllSolvedDates()
            allSolvedDates.test {
                assert(awaitItem().contains(solvedDate))
            }
        }

    @Test
    fun testGetAllSolvedDates() =
        runTest {
            val solvedDate1 = SolvedDate("2025-12-2")
            val solvedDate2 = SolvedDate("2025-12-3")

            solvedDateDao.insertSolvedDate(solvedDate1)
            solvedDateDao.insertSolvedDate(solvedDate2)

            // collect flow after insertions
            val allSolvedDates = solvedDateDao.getAllSolvedDates()

            allSolvedDates.test {
                val emittedList = awaitItem()
                assertEquals(2, emittedList.size)
                assertEquals(listOf(solvedDate1, solvedDate2), emittedList)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @After
    fun tearDown() {
        database.close()
    }
}
