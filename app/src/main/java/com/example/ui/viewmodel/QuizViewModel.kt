package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuizPreferences
import com.example.data.model.Achievement
import com.example.data.model.CompletedGameHistory
import com.example.data.model.Difficulty
import com.example.data.model.QuizMode
import com.example.data.model.QuizQuestionState
import com.example.data.model.QuizResult
import com.example.data.model.UserStats
import com.example.data.repository.QuizRepository
import com.example.service.AdMobManager
import com.example.service.AudioFeedback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    SPLASH,
    HOME,
    QUIZ,
    RESULTS,
    REVIEW,
    STATISTICS,
    ACHIEVEMENTS,
    HISTORY,
    SETTINGS
}

data class QuizUiState(
    val currentScreen: AppScreen = AppScreen.SPLASH,
    val activeMode: QuizMode = QuizMode.KIDS,
    val currentDifficulty: Difficulty = Difficulty.EASY,
    val questions: List<QuizQuestionState> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val livesRemaining: Int = 3,
    val secondsRemaining: Int = 60,
    val currentScore: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val isAnswerRevealed: Boolean = false,
    val isQuizPaused: Boolean = false,
    val showRevivalDialog: Boolean = false,
    val hasUsedRevivalAd: Boolean = false,
    val lastResult: QuizResult? = null,
    val stats: UserStats = UserStats(),
    val achievements: List<Achievement> = emptyList(),
    val recentGames: List<CompletedGameHistory> = emptyList(),
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val themeSetting: String = "SYSTEM",
    val isAdMobTestMode: Boolean = true,
    val totalKidsQuestions: Int = 0,
    val totalAdultsQuestions: Int = 0,
    val snackbarMessage: String? = null
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuizRepository(application)
    private val preferences = QuizPreferences(application)
    val audioFeedback = AudioFeedback(application, preferences)
    val adMobManager = AdMobManager(application, preferences)

    private val _uiState = MutableStateFlow(
        QuizUiState(
            activeMode = preferences.defaultMode,
            isSoundEnabled = preferences.isSoundEnabled,
            isVibrationEnabled = preferences.isVibrationEnabled,
            themeSetting = preferences.themeSetting,
            isAdMobTestMode = preferences.isAdMobTestMode,
            stats = preferences.getUserStats(),
            achievements = preferences.getAchievements(),
            recentGames = preferences.getRecentGames(5),
            totalKidsQuestions = repository.getTotalQuestionCount(QuizMode.KIDS),
            totalAdultsQuestions = repository.getTotalQuestionCount(QuizMode.ADULTS)
        )
    )
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        adMobManager.initialize()

        // Splash delay to transition to HOME
        viewModelScope.launch {
            delay(1600)
            if (_uiState.value.currentScreen == AppScreen.SPLASH) {
                _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        audioFeedback.playClick()
        if (screen != AppScreen.QUIZ) {
            pauseTimer()
        }
        _uiState.update {
            it.copy(
                currentScreen = screen,
                stats = preferences.getUserStats(),
                achievements = preferences.getAchievements(),
                recentGames = preferences.getRecentGames(5)
            )
        }
    }

    fun setQuizMode(mode: QuizMode) {
        audioFeedback.playClick()
        preferences.defaultMode = mode
        _uiState.update { it.copy(activeMode = mode) }
    }

    fun startQuiz(difficulty: Difficulty) {
        audioFeedback.playClick()
        val mode = _uiState.value.activeMode
        val questions = repository.createQuiz(mode, difficulty)

        if (questions.isEmpty()) {
            showSnackbar("Failed to load questions. Please check local files.")
            return
        }

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.QUIZ,
                currentDifficulty = difficulty,
                questions = questions,
                currentQuestionIndex = 0,
                livesRemaining = 3,
                secondsRemaining = 60,
                currentScore = 0,
                currentStreak = 0,
                maxStreak = 0,
                isAnswerRevealed = false,
                isQuizPaused = false,
                showRevivalDialog = false,
                hasUsedRevivalAd = false
            )
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.secondsRemaining > 0 && !_uiState.value.isQuizPaused && !_uiState.value.isAnswerRevealed) {
                delay(1000)
                if (!_uiState.value.isQuizPaused && !_uiState.value.isAnswerRevealed) {
                    val remaining = _uiState.value.secondsRemaining - 1
                    if (remaining in 1..5) {
                        audioFeedback.playTick()
                    }
                    _uiState.update { it.copy(secondsRemaining = remaining) }
                    if (remaining <= 0) {
                        handleTimeOut()
                    }
                }
            }
        }
    }

    fun startQuestionTimer() = startTimer()

    fun pauseQuiz() {
        audioFeedback.playClick()
        timerJob?.cancel()
        _uiState.update { it.copy(isQuizPaused = true) }
    }

    fun resumeQuiz() {
        audioFeedback.playClick()
        _uiState.update { it.copy(isQuizPaused = false) }
        startTimer()
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun resumeTimer() {
        resumeQuiz()
    }

    fun reduceLife() {
        val currentLives = _uiState.value.livesRemaining
        val newLives = (currentLives - 1).coerceAtLeast(0)
        _uiState.update {
            it.copy(
                livesRemaining = newLives,
                currentStreak = 0
            )
        }
        if (newLives <= 0) {
            if (!_uiState.value.hasUsedRevivalAd) {
                _uiState.update { it.copy(showRevivalDialog = true) }
            } else {
                finishQuiz(isVictory = false)
            }
        }
    }

    fun submitAnswer(optionIndex: Int) = selectAnswer(optionIndex)

    fun selectAnswer(optionIndex: Int) {
        val state = _uiState.value
        if (state.isAnswerRevealed || state.isQuizPaused) return

        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        currentQ.selectedIndex = optionIndex
        currentQ.timeSpentSeconds = 60 - state.secondsRemaining

        timerJob?.cancel()

        val isCorrect = optionIndex == currentQ.correctShuffledIndex
        val newLives = if (isCorrect) state.livesRemaining else (state.livesRemaining - 1).coerceAtLeast(0)
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newMaxStreak = maxOf(state.maxStreak, newStreak)

        // Points calculation
        val basePoints = state.currentDifficulty.basePoints
        val timeBonus = (state.secondsRemaining * 1.5f).toInt()
        val streakBonus = when {
            newStreak >= 5 -> 1.5f
            newStreak >= 3 -> 1.25f
            else -> 1.0f
        }
        val pointsEarned = if (isCorrect) ((basePoints + timeBonus) * streakBonus).toInt() else 0
        val newScore = state.currentScore + pointsEarned

        if (isCorrect) {
            audioFeedback.playCorrect()
        } else {
            audioFeedback.playWrong()
        }

        _uiState.update {
            it.copy(
                isAnswerRevealed = true,
                livesRemaining = newLives,
                currentStreak = newStreak,
                maxStreak = newMaxStreak,
                currentScore = newScore
            )
        }

        viewModelScope.launch {
            delay(1200)
            if (newLives <= 0) {
                // Out of lives!
                if (!state.hasUsedRevivalAd) {
                    _uiState.update { it.copy(showRevivalDialog = true) }
                } else {
                    finishQuiz(isVictory = false)
                }
            } else {
                advanceToNextQuestion()
            }
        }
    }

    private fun handleTimeOut() {
        val state = _uiState.value
        val currentQ = state.questions.getOrNull(state.currentQuestionIndex) ?: return
        currentQ.isTimedOut = true
        currentQ.timeSpentSeconds = 60

        audioFeedback.playWrong()

        val newLives = (state.livesRemaining - 1).coerceAtLeast(0)

        _uiState.update {
            it.copy(
                isAnswerRevealed = true,
                livesRemaining = newLives,
                currentStreak = 0
            )
        }

        viewModelScope.launch {
            delay(1200)
            if (newLives <= 0) {
                if (!state.hasUsedRevivalAd) {
                    _uiState.update { it.copy(showRevivalDialog = true) }
                } else {
                    finishQuiz(isVictory = false)
                }
            } else {
                advanceToNextQuestion()
            }
        }
    }

    private fun advanceToNextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= state.questions.size) {
            // Quiz completed!
            finishQuiz(isVictory = true)
        } else {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    secondsRemaining = 60,
                    isAnswerRevealed = false
                )
            }
            startTimer()
        }
    }

    fun reviveWithRewardedAd(activity: Activity) {
        adMobManager.showRewardedAd(
            activity = activity,
            onRewardEarned = {
                audioFeedback.playVictory()
                _uiState.update {
                    it.copy(
                        livesRemaining = 1,
                        hasUsedRevivalAd = true,
                        showRevivalDialog = false,
                        isAnswerRevealed = false,
                        secondsRemaining = 60
                    )
                }
                advanceToNextQuestion()
            },
            onFailed = { isOfflineFallback ->
                // Graceful fallback for offline mode
                audioFeedback.playVictory()
                _uiState.update {
                    it.copy(
                        livesRemaining = 1,
                        hasUsedRevivalAd = true,
                        showRevivalDialog = false,
                        isAnswerRevealed = false,
                        secondsRemaining = 60
                    )
                }
                if (isOfflineFallback) {
                    showSnackbar("Offline Mode: 1 extra life granted! ❤️")
                }
                advanceToNextQuestion()
            }
        )
    }

    fun declineRevival() {
        _uiState.update { it.copy(showRevivalDialog = false) }
        finishQuiz(isVictory = false)
    }

    private fun finishQuiz(isVictory: Boolean) {
        timerJob?.cancel()
        val state = _uiState.value
        val total = state.questions.size
        val correctCount = state.questions.count { it.isCorrect }
        val timedOutCount = state.questions.count { it.isTimedOut }
        val wrongCount = total - correctCount

        val accuracy = if (total > 0) (correctCount.toFloat() / total) * 100f else 0f
        val xpEarned = (state.currentScore / 2) + (if (isVictory) 100 else 25)
        val fastAnswersCount = state.questions.count { it.isCorrect && it.timeSpentSeconds <= 15 }

        val todayDate = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
        val isHighscore = preferences.recordQuizCompletion(
            mode = state.activeMode,
            difficulty = state.currentDifficulty,
            score = state.currentScore,
            correctCount = correctCount,
            totalQuestions = total,
            streak = state.maxStreak,
            xpEarned = xpEarned,
            todayDateStr = todayDate,
            fastAnswersInQuiz = fastAnswersCount
        )

        if (isVictory) {
            audioFeedback.playVictory()
            if (correctCount == 10) {
                preferences.markAchievementUnlocked("perfectionist")
            }
        } else {
            audioFeedback.playGameOver()
        }

        val result = QuizResult(
            totalQuestions = total,
            correctCount = correctCount,
            wrongCount = wrongCount,
            timedOutCount = timedOutCount,
            score = state.currentScore,
            accuracy = accuracy,
            maxStreak = state.maxStreak,
            xpEarned = xpEarned,
            mode = state.activeMode,
            difficulty = state.currentDifficulty,
            isHighscore = isHighscore,
            isVictory = isVictory,
            questionStates = state.questions
        )

        val formattedDate = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date())
        val completedGame = CompletedGameHistory(
            mode = state.activeMode,
            difficulty = state.currentDifficulty,
            score = state.currentScore,
            correctCount = correctCount,
            totalQuestions = total,
            accuracy = accuracy,
            maxStreak = state.maxStreak,
            xpEarned = xpEarned,
            isVictory = isVictory,
            timestamp = System.currentTimeMillis(),
            dateFormatted = formattedDate
        )
        preferences.recordCompletedGame(completedGame)

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.RESULTS,
                lastResult = result,
                stats = preferences.getUserStats(),
                achievements = preferences.getAchievements(),
                recentGames = preferences.getRecentGames(5)
            )
        }
    }

    fun clearHistory() {
        audioFeedback.playClick()
        preferences.clearQuizHistory()
        _uiState.update { it.copy(recentGames = emptyList()) }
    }

    fun showCompletionInterstitial(activity: Activity, onDone: () -> Unit = {}) {
        adMobManager.showInterstitial(activity, onDone)
    }

    // Settings actions
    fun toggleSound(enabled: Boolean) {
        preferences.isSoundEnabled = enabled
        _uiState.update { it.copy(isSoundEnabled = enabled) }
    }

    fun toggleVibration(enabled: Boolean) {
        preferences.isVibrationEnabled = enabled
        _uiState.update { it.copy(isVibrationEnabled = enabled) }
    }

    fun setThemeSetting(theme: String) {
        audioFeedback.playClick()
        preferences.themeSetting = theme
        _uiState.update { it.copy(themeSetting = theme) }
    }

    fun toggleAdMobTestMode(enabled: Boolean) {
        preferences.isAdMobTestMode = enabled
        _uiState.update { it.copy(isAdMobTestMode = enabled) }
    }

    fun resetAllData() {
        preferences.resetAllData()
        _uiState.update {
            it.copy(
                stats = preferences.getUserStats(),
                achievements = preferences.getAchievements()
            )
        }
        showSnackbar("All user scores and statistics have been reset.")
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
        viewModelScope.launch {
            delay(3000)
            _uiState.update { it.copy(snackbarMessage = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        audioFeedback.release()
    }
}
