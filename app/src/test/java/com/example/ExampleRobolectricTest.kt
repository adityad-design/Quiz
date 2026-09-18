package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.QuizPreferences
import com.example.data.model.CompletedGameHistory
import com.example.data.model.Difficulty
import com.example.data.model.QuizMode
import com.example.data.repository.QuizRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("QuizRush", appName)
    }

    @Test
    fun `verify questions loaded from assets for Kids and Adults`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = QuizRepository(context)

        val kidsQuestions = repository.getQuestions(QuizMode.KIDS)
        val adultsQuestions = repository.getQuestions(QuizMode.ADULTS)

        assertTrue("Kids questions should be between 150 and 200", kidsQuestions.size in 150..200)
        assertTrue("Adults questions should be between 150 and 200", adultsQuestions.size in 150..200)

        // Verify every question has 4 multiple-choice options and a valid correct index
        kidsQuestions.forEach { q ->
            assertEquals("Each kids question must have exactly 4 options", 4, q.options.size)
            assertTrue("Correct index must be in 0..3", q.correctIndex in 0..3)
            assertNotNull("Question text should not be null", q.text)
            assertNotNull("Explanation should not be null", q.explanation)
        }

        adultsQuestions.forEach { q ->
            assertEquals("Each adult question must have exactly 4 options", 4, q.options.size)
            assertTrue("Correct index must be in 0..3", q.correctIndex in 0..3)
            assertNotNull("Question text should not be null", q.text)
            assertNotNull("Explanation should not be null", q.explanation)
        }
    }

    @Test
    fun `verify quiz creation selects exactly 10 unique questions with shuffled options`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = QuizRepository(context)

        val quiz = repository.createQuiz(QuizMode.KIDS, Difficulty.EASY)
        assertEquals("Quiz must select exactly 10 questions", 10, quiz.size)

        // Check uniqueness of questions in the quiz
        val uniqueIds = quiz.map { it.question.id }.toSet()
        assertEquals("All 10 questions in a quiz must be unique", 10, uniqueIds.size)

        // Check options are shuffled with recalculated correct index
        quiz.forEach { qState ->
            assertEquals(4, qState.shuffledOptions.size)
            assertTrue(qState.correctShuffledIndex in 0..3)
            assertEquals(
                qState.question.correctAnswer,
                qState.shuffledOptions[qState.correctShuffledIndex]
            )
        }
    }

    @Test
    fun `verify preferences persistence and stats tracking`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = QuizPreferences(context)
        prefs.resetAllData()

        val initialStats = prefs.getUserStats()
        assertEquals(0, initialStats.totalQuizzes)
        assertEquals(0, initialStats.totalXp)

        val isHighscore = prefs.recordQuizCompletion(
            mode = QuizMode.ADULTS,
            difficulty = Difficulty.MEDIUM,
            score = 850,
            correctCount = 8,
            totalQuestions = 10,
            streak = 5,
            xpEarned = 400,
            todayDateStr = "20260917"
        )

        assertTrue("First score should be a high score", isHighscore)

        val updatedStats = prefs.getUserStats()
        assertEquals(1, updatedStats.totalQuizzes)
        assertEquals(1, updatedStats.adultsQuizzes)
        assertEquals(0, updatedStats.kidsQuizzes)
        assertEquals(400, updatedStats.totalXp)
        assertEquals(5, updatedStats.bestStreak)
        assertEquals(850, prefs.getBestScore(QuizMode.ADULTS, Difficulty.MEDIUM))
    }

    @Test
    fun `verify consecutive days streak tracking`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = QuizPreferences(context)
        prefs.resetAllData()

        // Day 1
        prefs.recordQuizCompletion(
            mode = QuizMode.KIDS,
            difficulty = Difficulty.EASY,
            score = 100,
            correctCount = 10,
            totalQuestions = 10,
            streak = 10,
            xpEarned = 100,
            todayDateStr = "20260915"
        )
        var stats = prefs.getUserStats("20260915")
        assertEquals(1, stats.consecutiveDaysStreak)
        assertTrue(stats.playedDates.contains("20260915"))

        // Day 2 (Consecutive!)
        prefs.recordQuizCompletion(
            mode = QuizMode.KIDS,
            difficulty = Difficulty.EASY,
            score = 100,
            correctCount = 10,
            totalQuestions = 10,
            streak = 10,
            xpEarned = 100,
            todayDateStr = "20260916"
        )
        stats = prefs.getUserStats("20260916")
        assertEquals(2, stats.consecutiveDaysStreak)
        assertTrue(stats.playedDates.contains("20260916"))

        // Another quiz played on Day 2 (Same day does not artificially increase streak)
        prefs.recordQuizCompletion(
            mode = QuizMode.ADULTS,
            difficulty = Difficulty.MEDIUM,
            score = 150,
            correctCount = 9,
            totalQuestions = 10,
            streak = 5,
            xpEarned = 120,
            todayDateStr = "20260916"
        )
        stats = prefs.getUserStats("20260916")
        assertEquals(2, stats.consecutiveDaysStreak)

        // Day 5 (Break in streak - missed days 17 and 18)
        prefs.recordQuizCompletion(
            mode = QuizMode.KIDS,
            difficulty = Difficulty.HARD,
            score = 200,
            correctCount = 10,
            totalQuestions = 10,
            streak = 10,
            xpEarned = 250,
            todayDateStr = "20260919"
        )
        stats = prefs.getUserStats("20260919")
        assertEquals(1, stats.consecutiveDaysStreak)
        assertTrue(stats.playedDates.contains("20260919"))
    }

    @Test
    fun `verify week streak calculation produces 7 days with today and played status`() {
        val playedDates = setOf("20260915", "20260916")
        val week = com.example.ui.components.calculateCurrentWeekDays(playedDates)

        assertEquals("Week must always contain 7 days", 7, week.size)
        val todayCount = week.count { it.isToday }
        assertEquals("Exactly one day in the current week should be marked today", 1, todayCount)
    }

    @Test
    fun `verify sound effects toggle in preferences and audio controller`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = QuizPreferences(context)
        prefs.isSoundEnabled = true
        assertTrue(prefs.isSoundEnabled)

        val audioFeedback = com.example.service.AudioFeedback(context, prefs)
        // Should execute smoothly without crashing
        audioFeedback.playCorrect()
        audioFeedback.playWrong()

        // Toggle sound off
        prefs.isSoundEnabled = false
        assertFalse(prefs.isSoundEnabled)

        // When disabled, playback calls are gracefully ignored
        audioFeedback.playCorrect()
        audioFeedback.playWrong()

        audioFeedback.release()
    }

    @Test
    fun `verify achievement system tracks milestones and unlocks Quiz Master and Fast Learner`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = QuizPreferences(context)
        prefs.resetAllData()

        val initialAchievements = prefs.getAchievements()
        val quizMasterInitial = initialAchievements.find { it.id == "quiz_master" }
        val fastLearnerInitial = initialAchievements.find { it.id == "fast_learner" }

        assertNotNull("Quiz Master badge must exist", quizMasterInitial)
        assertNotNull("Fast Learner badge must exist", fastLearnerInitial)
        assertEquals("Quiz Master", quizMasterInitial?.title)
        assertEquals("Fast Learner", fastLearnerInitial?.title)
        assertFalse("Quiz Master should be locked initially", quizMasterInitial?.isUnlocked ?: true)
        assertFalse("Fast Learner should be locked initially", fastLearnerInitial?.isUnlocked ?: true)
        assertEquals(0, quizMasterInitial?.currentValue)
        assertEquals(5, quizMasterInitial?.targetValue)
        assertEquals(0, fastLearnerInitial?.currentValue)
        assertEquals(10, fastLearnerInitial?.targetValue)

        // Record 1st quiz with 6 fast answers and 8/10 correct (80% accuracy - qualifies as master quiz)
        prefs.recordQuizCompletion(
            mode = QuizMode.ADULTS,
            difficulty = Difficulty.HARD,
            score = 700,
            correctCount = 8,
            totalQuestions = 10,
            streak = 4,
            xpEarned = 350,
            todayDateStr = "20260917",
            fastAnswersInQuiz = 6
        )

        var currentAchievements = prefs.getAchievements()
        var fastLearner = currentAchievements.first { it.id == "fast_learner" }
        var quizMaster = currentAchievements.first { it.id == "quiz_master" }
        assertEquals(6, fastLearner.currentValue)
        assertFalse("Fast Learner not yet unlocked (6/10)", fastLearner.isUnlocked)
        assertEquals(1, quizMaster.currentValue)
        assertFalse("Quiz Master not yet unlocked (1/5)", quizMaster.isUnlocked)

        // Record 2nd quiz with 5 fast answers (total fast answers = 11 >= 10 -> unlocks Fast Learner!)
        prefs.recordQuizCompletion(
            mode = QuizMode.ADULTS,
            difficulty = Difficulty.MEDIUM,
            score = 900,
            correctCount = 9,
            totalQuestions = 10,
            streak = 6,
            xpEarned = 450,
            todayDateStr = "20260917",
            fastAnswersInQuiz = 5
        )

        currentAchievements = prefs.getAchievements()
        fastLearner = currentAchievements.first { it.id == "fast_learner" }
        quizMaster = currentAchievements.first { it.id == "quiz_master" }
        assertTrue("Fast Learner must be unlocked after 11 fast answers", fastLearner.isUnlocked)
        assertEquals(10, fastLearner.currentValue) // Coerced to target
        assertEquals(2, quizMaster.currentValue)

        // Complete 3 more master quizzes (reaching 5 total) to unlock Quiz Master
        for (i in 3..5) {
            prefs.recordQuizCompletion(
                mode = QuizMode.KIDS,
                difficulty = Difficulty.EASY,
                score = 800,
                correctCount = 8,
                totalQuestions = 10,
                streak = 5,
                xpEarned = 400,
                todayDateStr = "20260917",
                fastAnswersInQuiz = 2
            )
        }

        currentAchievements = prefs.getAchievements()
        quizMaster = currentAchievements.first { it.id == "quiz_master" }
        assertTrue("Quiz Master must be unlocked after 5 master quizzes", quizMaster.isUnlocked)
        assertEquals(5, quizMaster.currentValue)
        assertEquals(500, quizMaster.xpReward)
    }

    @Test
    fun `verify quiz history records and retrieves last 5 completed games using local storage`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = QuizPreferences(context)
        prefs.clearQuizHistory()

        // Initially empty
        assertTrue("Initial history must be empty", prefs.getRecentGames(5).isEmpty())

        // Record 7 completed games
        for (i in 1..7) {
            val game = CompletedGameHistory(
                mode = if (i % 2 == 0) QuizMode.KIDS else QuizMode.ADULTS,
                difficulty = Difficulty.MEDIUM,
                score = i * 100,
                correctCount = i,
                totalQuestions = 10,
                accuracy = i * 10f,
                maxStreak = i,
                xpEarned = i * 50,
                isVictory = i >= 4,
                timestamp = System.currentTimeMillis() + i * 1000,
                dateFormatted = "Sep 17, 2026 • 0${i}:00 AM"
            )
            prefs.recordCompletedGame(game)
        }

        val recentGames = prefs.getRecentGames(limit = 5)
        assertEquals("History must return exactly the last 5 games", 5, recentGames.size)

        // The most recently recorded game (i=7) should be at index 0
        val latest = recentGames[0]
        assertEquals(700, latest.score)
        assertEquals("Sep 17, 2026 • 07:00 AM", latest.dateFormatted)
        assertEquals(QuizMode.ADULTS, latest.mode)
        assertEquals(70f, latest.accuracy, 0.01f)
        assertEquals(7, latest.maxStreak)
        assertTrue(latest.isVictory)

        // Game 6 should be at index 1
        val second = recentGames[1]
        assertEquals(600, second.score)
        assertEquals(QuizMode.KIDS, second.mode)

        // Clear history test
        prefs.clearQuizHistory()
        assertTrue("History must be empty after clearing", prefs.getRecentGames(5).isEmpty())
    }
}

