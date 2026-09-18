package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Difficulty
import com.example.data.model.Question
import com.example.data.model.QuizMode
import com.example.data.model.QuizQuestionState
import com.example.data.repository.QuizRepository
import com.example.service.AudioFeedback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable representation of the Quiz Game State.
 */
data class GameState(
    val questions: List<QuizQuestionState> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val livesRemaining: Int = 3,
    val secondsRemaining: Int = 60,
    val totalSecondsPerQuestion: Int = 60,
    val currentScore: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val isAnswerRevealed: Boolean = false,
    val isQuizPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isQuizCompleted: Boolean = false,
    val selectedOptionIndex: Int? = null,
    val difficulty: Difficulty = Difficulty.EASY,
    val mode: QuizMode = QuizMode.KIDS
) {
    // Convenient property aliases for test suites and UI components
    val lives: Int get() = livesRemaining
    val timeLeft: Int get() = secondsRemaining
    val score: Int get() = currentScore
    val streak: Int get() = currentStreak
    val totalQuestions: Int get() = questions.size
    val currentQuestion: Question? get() = questions.getOrNull(currentQuestionIndex)?.question
    val currentQuestionState: QuizQuestionState? get() = questions.getOrNull(currentQuestionIndex)
    val progress: Float get() = if (questions.isNotEmpty()) currentQuestionIndex.toFloat() / questions.size else 0f
    val timerProgress: Float get() = if (totalSecondsPerQuestion > 0) secondsRemaining.toFloat() / totalSecondsPerQuestion else 0f
}

/**
 * ViewModel that handles the core game state, including countdown timer for each question
 * and logic for reducing lives upon incorrect answers or timeouts.
 */
class GameViewModel(
    application: Application,
    private val repository: QuizRepository = QuizRepository(application),
    private val audioFeedback: AudioFeedback? = null
) : AndroidViewModel(application) {

    // Secondary constructor allowing zero-arg creation for tests or default factory
    constructor() : this(getApplicationFallback())

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Alias for uiState naming convention
    val uiState: StateFlow<GameState> = gameState

    private var timerJob: Job? = null

    /**
     * Starts a new game session with loaded questions and initializes question timer.
     */
    fun startQuiz(
        difficulty: Difficulty = Difficulty.EASY,
        mode: QuizMode = QuizMode.KIDS,
        customQuestions: List<QuizQuestionState>? = null,
        timePerQuestion: Int = 60
    ) {
        timerJob?.cancel()
        val loadedQuestions = customQuestions ?: repository.createQuiz(mode, difficulty)
        _gameState.value = GameState(
            questions = loadedQuestions,
            currentQuestionIndex = 0,
            livesRemaining = 3,
            secondsRemaining = timePerQuestion,
            totalSecondsPerQuestion = timePerQuestion,
            currentScore = 0,
            currentStreak = 0,
            maxStreak = 0,
            isAnswerRevealed = false,
            isQuizPaused = false,
            isGameOver = false,
            isVictory = false,
            isQuizCompleted = false,
            selectedOptionIndex = null,
            difficulty = difficulty,
            mode = mode
        )

        if (loadedQuestions.isNotEmpty()) {
            startQuestionTimer()
        }
    }

    /**
     * Starts game session (alias for startQuiz).
     */
    fun startGame(
        difficulty: Difficulty = Difficulty.EASY,
        mode: QuizMode = QuizMode.KIDS,
        customQuestions: List<QuizQuestionState>? = null
    ) = startQuiz(difficulty, mode, customQuestions)

    /**
     * Starts or restarts the countdown timer for the current question.
     */
    fun startQuestionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameState.value.secondsRemaining > 0 &&
                !_gameState.value.isQuizPaused &&
                !_gameState.value.isAnswerRevealed &&
                !_gameState.value.isGameOver
            ) {
                delay(1000)
                if (!_gameState.value.isQuizPaused &&
                    !_gameState.value.isAnswerRevealed &&
                    !_gameState.value.isGameOver
                ) {
                    val remaining = _gameState.value.secondsRemaining - 1
                    if (remaining in 1..5) {
                        audioFeedback?.playTick()
                    }
                    _gameState.update { it.copy(secondsRemaining = remaining) }
                    if (remaining <= 0) {
                        handleTimeOut()
                    }
                }
            }
        }
    }

    fun startTimer() = startQuestionTimer()

    /**
     * Pauses the countdown timer and marks game paused.
     */
    fun pauseTimer() {
        timerJob?.cancel()
        _gameState.update { it.copy(isQuizPaused = true) }
    }

    fun pauseQuiz() = pauseTimer()

    /**
     * Resumes the countdown timer and marks game active.
     */
    fun resumeTimer() {
        _gameState.update { it.copy(isQuizPaused = false) }
        startQuestionTimer()
    }

    fun resumeQuiz() = resumeTimer()

    /**
     * Selects an answer option for the current question and applies score or life penalties.
     */
    fun selectAnswer(optionIndex: Int, autoAdvance: Boolean = true) {
        val state = _gameState.value
        if (state.isAnswerRevealed || state.isQuizPaused || state.isGameOver) return

        timerJob?.cancel()

        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        currentQ.selectedIndex = optionIndex
        currentQ.timeSpentSeconds = state.totalSecondsPerQuestion - state.secondsRemaining

        val isCorrect = optionIndex == currentQ.correctShuffledIndex

        if (isCorrect) {
            handleCorrectAnswer(optionIndex)
        } else {
            handleIncorrectAnswer(optionIndex)
        }

        if (autoAdvance && !_gameState.value.isGameOver) {
            viewModelScope.launch {
                delay(1200)
                if (_gameState.value.livesRemaining > 0 && !_gameState.value.isGameOver) {
                    advanceToNextQuestion()
                }
            }
        }
    }

    /**
     * Submits an answer (alias for selectAnswer).
     */
    fun submitAnswer(optionIndex: Int) = selectAnswer(optionIndex)

    /**
     * Handles logic when user submits an incorrect answer:
     * - Decrements lives by 1
     * - Resets streak to 0
     * - Checks if lives reached 0 (triggers Game Over)
     */
    fun handleIncorrectAnswer(optionIndex: Int? = null) {
        val state = _gameState.value
        val newLives = (state.livesRemaining - 1).coerceAtLeast(0)
        val isGameOver = newLives <= 0

        audioFeedback?.playWrong()

        _gameState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                livesRemaining = newLives,
                currentStreak = 0,
                isGameOver = isGameOver
            )
        }
    }

    /**
     * Directly reduces lives by 1 and updates game over state if lives reach 0.
     */
    fun reduceLife() {
        val state = _gameState.value
        val newLives = (state.livesRemaining - 1).coerceAtLeast(0)
        val isGameOver = newLives <= 0

        audioFeedback?.playWrong()

        _gameState.update {
            it.copy(
                livesRemaining = newLives,
                currentStreak = 0,
                isGameOver = isGameOver
            )
        }
    }

    /**
     * Handles logic when user submits a correct answer:
     * - Preserves lives
     * - Calculates score with base points, time bonus, and streak multiplier
     * - Increments current streak and tracks max streak
     */
    fun handleCorrectAnswer(optionIndex: Int) {
        val state = _gameState.value
        val newStreak = state.currentStreak + 1
        val newMaxStreak = maxOf(state.maxStreak, newStreak)

        val basePoints = state.difficulty.basePoints
        val timeBonus = (state.secondsRemaining * 1.5f).toInt()
        val streakMultiplier = when {
            newStreak >= 5 -> 1.5f
            newStreak >= 3 -> 1.25f
            else -> 1.0f
        }
        val pointsEarned = ((basePoints + timeBonus) * streakMultiplier).toInt()

        audioFeedback?.playCorrect()

        _gameState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isAnswerRevealed = true,
                currentScore = it.currentScore + pointsEarned,
                currentStreak = newStreak,
                maxStreak = newMaxStreak
            )
        }
    }

    /**
     * Handles timer expiration:
     * - Marks current question as timed out
     * - Reduces lives by 1
     * - Resets streak
     * - Triggers game over if lives reach 0, otherwise advances to next question
     */
    fun handleTimeOut() {
        val state = _gameState.value
        if (state.isAnswerRevealed || state.isGameOver) return

        timerJob?.cancel()
        val currentQ = state.questions.getOrNull(state.currentQuestionIndex)
        currentQ?.isTimedOut = true
        currentQ?.timeSpentSeconds = state.totalSecondsPerQuestion

        audioFeedback?.playWrong()

        val newLives = (state.livesRemaining - 1).coerceAtLeast(0)
        val isGameOver = newLives <= 0

        _gameState.update {
            it.copy(
                isAnswerRevealed = true,
                livesRemaining = newLives,
                currentStreak = 0,
                isGameOver = isGameOver
            )
        }

        viewModelScope.launch {
            delay(1200)
            if (newLives > 0 && !isGameOver) {
                advanceToNextQuestion()
            }
        }
    }

    fun onTimeExpired() = handleTimeOut()

    /**
     * Advances to the next question or completes the quiz if all questions were answered.
     */
    fun advanceToNextQuestion() {
        val state = _gameState.value
        if (state.isGameOver) return

        timerJob?.cancel()
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= state.questions.size) {
            audioFeedback?.playVictory()
            _gameState.update {
                it.copy(
                    isVictory = true,
                    isQuizCompleted = true,
                    isAnswerRevealed = true
                )
            }
        } else {
            _gameState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    secondsRemaining = it.totalSecondsPerQuestion,
                    isAnswerRevealed = false,
                    selectedOptionIndex = null
                )
            }
            startQuestionTimer()
        }
    }

    fun nextQuestion() = advanceToNextQuestion()

    /**
     * Grants a revival by restoring 1 life and restarting the question timer.
     */
    fun reviveWithLife(bonusLives: Int = 1) {
        val newLives = bonusLives.coerceAtLeast(1)
        _gameState.update {
            it.copy(
                livesRemaining = newLives,
                isGameOver = false,
                isAnswerRevealed = false,
                secondsRemaining = it.totalSecondsPerQuestion
            )
        }
        startQuestionTimer()
    }

    /**
     * Resets the entire game state.
     */
    fun resetGame() {
        timerJob?.cancel()
        _gameState.value = GameState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    companion object {
        private fun getApplicationFallback(): Application {
            return try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val method = activityThreadClass.getMethod("currentApplication")
                (method.invoke(null) as? Application) ?: Application()
            } catch (e: Throwable) {
                Application()
            }
        }
    }
}
