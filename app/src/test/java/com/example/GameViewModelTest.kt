package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Difficulty
import com.example.data.model.Question
import com.example.data.model.QuizMode
import com.example.data.model.QuizQuestionState
import com.example.ui.viewmodel.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GameViewModelTest {

    private lateinit var application: Application
    private lateinit var viewModel: GameViewModel

    private fun createSampleQuestions(): List<QuizQuestionState> {
        val q1 = Question(
            id = "q1",
            text = "What is 2 + 2?",
            options = listOf("3", "4", "5", "6"),
            correctIndex = 1,
            correctAnswer = "4",
            category = "Math",
            difficulty = "Easy",
            explanation = "2 + 2 = 4"
        )
        val q2 = Question(
            id = "q2",
            text = "What is the capital of France?",
            options = listOf("Berlin", "Madrid", "Paris", "Rome"),
            correctIndex = 2,
            correctAnswer = "Paris",
            category = "Geography",
            difficulty = "Easy",
            explanation = "Paris is the capital of France"
        )
        return listOf(
            QuizQuestionState(
                question = q1,
                shuffledOptions = listOf("3", "4", "5", "6"),
                correctShuffledIndex = 1 // Option index 1 is correct ("4")
            ),
            QuizQuestionState(
                question = q2,
                shuffledOptions = listOf("Berlin", "Madrid", "Paris", "Rome"),
                correctShuffledIndex = 2 // Option index 2 is correct ("Paris")
            )
        )
    }

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        viewModel = GameViewModel(application)
    }

    @Test
    fun `initial game state has 3 lives and is not game over`() {
        val state = viewModel.gameState.value
        assertEquals(3, state.lives)
        assertEquals(3, state.livesRemaining)
        assertFalse(state.isGameOver)
        assertEquals(0, state.score)
    }

    @Test
    fun `startQuiz initializes game state and timer per question`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(
            difficulty = Difficulty.EASY,
            mode = QuizMode.KIDS,
            customQuestions = questions,
            timePerQuestion = 30
        )

        val state = viewModel.gameState.value
        assertEquals(2, state.totalQuestions)
        assertEquals(0, state.currentQuestionIndex)
        assertEquals(3, state.livesRemaining)
        assertEquals(30, state.secondsRemaining)
        assertEquals(30, state.totalSecondsPerQuestion)
        assertFalse(state.isGameOver)
        assertEquals("What is 2 + 2?", state.currentQuestion?.text)
    }

    @Test
    fun `correct answer increases score and preserves lives`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(
            difficulty = Difficulty.EASY,
            mode = QuizMode.KIDS,
            customQuestions = questions,
            timePerQuestion = 30
        )

        // Select correct answer (index 1 is "4")
        viewModel.selectAnswer(1, autoAdvance = false)

        val state = viewModel.gameState.value
        assertTrue("Answer should be revealed", state.isAnswerRevealed)
        assertEquals("Lives should remain 3 on correct answer", 3, state.livesRemaining)
        assertTrue("Score should increase above 0", state.score > 0)
        assertEquals(1, state.streak)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `incorrect answer reduces lives by 1 and resets streak`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(
            difficulty = Difficulty.EASY,
            mode = QuizMode.KIDS,
            customQuestions = questions,
            timePerQuestion = 30
        )

        // Select wrong answer (index 0 is "3", correct is 1)
        viewModel.selectAnswer(0, autoAdvance = false)

        val state = viewModel.gameState.value
        assertTrue("Answer should be revealed", state.isAnswerRevealed)
        assertEquals("Lives should be reduced from 3 to 2 on incorrect answer", 2, state.livesRemaining)
        assertEquals("Streak should be reset to 0", 0, state.streak)
        assertFalse("Game should not be over with 2 lives left", state.isGameOver)
    }

    @Test
    fun `multiple incorrect answers deplete lives and trigger game over`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(
            difficulty = Difficulty.EASY,
            mode = QuizMode.KIDS,
            customQuestions = questions,
            timePerQuestion = 30
        )

        // First incorrect answer: 3 -> 2
        viewModel.handleIncorrectAnswer()
        assertEquals(2, viewModel.gameState.value.livesRemaining)
        assertFalse(viewModel.gameState.value.isGameOver)

        // Second incorrect answer: 2 -> 1
        viewModel.handleIncorrectAnswer()
        assertEquals(1, viewModel.gameState.value.livesRemaining)
        assertFalse(viewModel.gameState.value.isGameOver)

        // Third incorrect answer: 1 -> 0
        viewModel.handleIncorrectAnswer()
        assertEquals(0, viewModel.gameState.value.livesRemaining)
        assertTrue("Game over should be triggered when lives reach 0", viewModel.gameState.value.isGameOver)
    }

    @Test
    fun `reduceLife method reduces lives directly and triggers game over at zero`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(customQuestions = questions)

        assertEquals(3, viewModel.gameState.value.lives)
        viewModel.reduceLife()
        assertEquals(2, viewModel.gameState.value.lives)
        viewModel.reduceLife()
        assertEquals(1, viewModel.gameState.value.lives)
        viewModel.reduceLife()
        assertEquals(0, viewModel.gameState.value.lives)
        assertTrue(viewModel.gameState.value.isGameOver)
    }

    @Test
    fun `timeout reduces lives by 1`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(customQuestions = questions)

        viewModel.handleTimeOut()
        val state = viewModel.gameState.value
        assertEquals("Timeout should reduce lives by 1", 2, state.livesRemaining)
        assertTrue(state.isAnswerRevealed)
    }

    @Test
    fun `pauseTimer and resumeTimer modify paused state`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(customQuestions = questions)

        assertFalse(viewModel.gameState.value.isQuizPaused)
        viewModel.pauseTimer()
        assertTrue(viewModel.gameState.value.isQuizPaused)
        viewModel.resumeTimer()
        assertFalse(viewModel.gameState.value.isQuizPaused)
    }

    @Test
    fun `advanceToNextQuestion resets countdown timer for next question`() {
        val questions = createSampleQuestions()
        viewModel.startQuiz(customQuestions = questions, timePerQuestion = 45)

        assertEquals(0, viewModel.gameState.value.currentQuestionIndex)
        assertEquals(45, viewModel.gameState.value.secondsRemaining)

        viewModel.advanceToNextQuestion()

        val nextState = viewModel.gameState.value
        assertEquals(1, nextState.currentQuestionIndex)
        assertEquals("Seconds remaining should reset to total seconds per question", 45, nextState.secondsRemaining)
        assertEquals("What is the capital of France?", nextState.currentQuestion?.text)
        assertFalse(nextState.isAnswerRevealed)
    }
}
