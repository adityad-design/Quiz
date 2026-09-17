package com.example.data.model

enum class QuizMode(val displayName: String, val emoji: String, val tagline: String) {
    KIDS("Kids", "👦", "Fun, colorful & friendly trivia for curious young minds"),
    ADULTS("Adults", "🧑", "Deep logic, history, science & intellectual challenges")
}

enum class Difficulty(
    val title: String,
    val badge: String,
    val basePoints: Int,
    val multiplier: Float,
    val description: String
) {
    EASY("Easy", "🟢", 100, 1.0f, "Great for warm-ups and quick confidence boosts"),
    MEDIUM("Medium", "🟡", 150, 1.25f, "Balanced challenge testing core knowledge"),
    HARD("Hard", "🔴", 200, 1.5f, "Intense brain-teasers and master-level trivia"),
    DAILY("Daily", "🌟", 250, 2.0f, "Today's special curated 10-question challenge")
}

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val correctAnswer: String,
    val category: String,
    val difficulty: String,
    val explanation: String
)

data class QuizQuestionState(
    val question: Question,
    val shuffledOptions: List<String>,
    val correctShuffledIndex: Int,
    var selectedIndex: Int? = null,
    var isTimedOut: Boolean = false,
    var timeSpentSeconds: Int = 0
) {
    val isAnswered: Boolean
        get() = selectedIndex != null || isTimedOut

    val isCorrect: Boolean
        get() = selectedIndex == correctShuffledIndex
}

data class QuizResult(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val timedOutCount: Int,
    val score: Int,
    val accuracy: Float,
    val maxStreak: Int,
    val xpEarned: Int,
    val mode: QuizMode,
    val difficulty: Difficulty,
    val timestamp: Long = System.currentTimeMillis(),
    val isHighscore: Boolean = false,
    val isVictory: Boolean = true,
    val questionStates: List<QuizQuestionState> = emptyList()
)

data class UserStats(
    val totalQuizzes: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val totalXp: Int = 0,
    val bestStreak: Int = 0,
    val kidsQuizzes: Int = 0,
    val adultsQuizzes: Int = 0,
    val bestScores: Map<String, Int> = emptyMap(),
    val dailyChallengeStreak: Int = 0,
    val lastDailyDate: String = "",
    val consecutiveDaysStreak: Int = 0,
    val lastPlayedDate: String = "",
    val playedDates: Set<String> = emptySet()
) {
    val overallAccuracy: Float
        get() = if (totalQuestionsAnswered > 0) {
            (totalCorrect.toFloat() / totalQuestionsAnswered) * 100f
        } else 0f

    val effectiveDailyStreak: Int
        get() = maxOf(consecutiveDaysStreak, dailyChallengeStreak)
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val targetValue: Int,
    val currentValue: Int,
    val isUnlocked: Boolean,
    val xpReward: Int
) {
    val progress: Float
        get() = (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)
}
