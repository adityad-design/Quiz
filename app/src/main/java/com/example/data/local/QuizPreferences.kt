package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Achievement
import com.example.data.model.Difficulty
import com.example.data.model.QuizMode
import com.example.data.model.UserStats
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class QuizPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("quizrush_prefs", Context.MODE_PRIVATE)

    fun getTodayDateStr(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    }

    fun getYesterdayDateStr(referenceDate: Date = Date()): String {
        val cal = Calendar.getInstance(Locale.US)
        cal.time = referenceDate
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyyMMdd", Locale.US).format(cal.time)
    }

    fun getPreviousDayDateStr(dateStr: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
            val date = sdf.parse(dateStr) ?: return ""
            val cal = Calendar.getInstance(Locale.US)
            cal.time = date
            cal.add(Calendar.DAY_OF_YEAR, -1)
            sdf.format(cal.time)
        } catch (e: Exception) {
            ""
        }
    }

    fun getPlayedDates(): Set<String> {
        val playedJson = prefs.getString("stats_played_dates", "[]") ?: "[]"
        val set = mutableSetOf<String>()
        try {
            val arr = org.json.JSONArray(playedJson)
            for (i in 0 until arr.length()) {
                set.add(arr.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return set
    }

    // Settings
    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("pref_sound", true)
        set(value) = prefs.edit().putBoolean("pref_sound", value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean("pref_vibration", true)
        set(value) = prefs.edit().putBoolean("pref_vibration", value).apply()

    var themeSetting: String
        get() = prefs.getString("pref_theme", "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString("pref_theme", value).apply()

    var defaultMode: QuizMode
        get() {
            val modeStr = prefs.getString("pref_default_mode", QuizMode.KIDS.name)
            return try {
                QuizMode.valueOf(modeStr ?: QuizMode.KIDS.name)
            } catch (e: Exception) {
                QuizMode.KIDS
            }
        }
        set(value) = prefs.edit().putString("pref_default_mode", value.name).apply()

    var isAdMobTestMode: Boolean
        get() = prefs.getBoolean("pref_admob_test_mode", true)
        set(value) = prefs.edit().putBoolean("pref_admob_test_mode", value).apply()

    // Stats
    fun getUserStats(referenceDateStr: String = getTodayDateStr()): UserStats {
        val totalQuizzes = prefs.getInt("stats_total_quizzes", 0)
        val totalQuestionsAnswered = prefs.getInt("stats_total_questions", 0)
        val totalCorrect = prefs.getInt("stats_total_correct", 0)
        val totalXp = prefs.getInt("stats_total_xp", 0)
        val bestStreak = prefs.getInt("stats_best_streak", 0)
        val kidsQuizzes = prefs.getInt("stats_kids_quizzes", 0)
        val adultsQuizzes = prefs.getInt("stats_adults_quizzes", 0)
        val dailyChallengeStreak = prefs.getInt("stats_daily_streak", 0)
        val lastDailyDate = prefs.getString("stats_last_daily_date", "") ?: ""

        val rawConsecutiveStreak = prefs.getInt("stats_consecutive_days_streak", 0)
        val lastPlayedDate = prefs.getString("stats_last_played_date", "") ?: ""
        val yesterdayStr = getPreviousDayDateStr(referenceDateStr)

        val consecutiveDaysStreak = when {
            lastPlayedDate == referenceDateStr -> rawConsecutiveStreak
            lastPlayedDate == yesterdayStr -> rawConsecutiveStreak
            lastPlayedDate.isEmpty() -> 0
            else -> 0 // Missed a day
        }

        val playedDates = getPlayedDates().toMutableSet()
        if (lastDailyDate.isNotEmpty()) playedDates.add(lastDailyDate)
        if (lastPlayedDate.isNotEmpty()) playedDates.add(lastPlayedDate)

        val bestScores = mutableMapOf<String, Int>()
        val scoresJson = prefs.getString("stats_best_scores", "{}") ?: "{}"
        try {
            val json = JSONObject(scoresJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                bestScores[key] = json.getInt(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return UserStats(
            totalQuizzes = totalQuizzes,
            totalQuestionsAnswered = totalQuestionsAnswered,
            totalCorrect = totalCorrect,
            totalXp = totalXp,
            bestStreak = bestStreak,
            kidsQuizzes = kidsQuizzes,
            adultsQuizzes = adultsQuizzes,
            bestScores = bestScores,
            dailyChallengeStreak = dailyChallengeStreak,
            lastDailyDate = lastDailyDate,
            consecutiveDaysStreak = consecutiveDaysStreak,
            lastPlayedDate = lastPlayedDate,
            playedDates = playedDates
        )
    }

    fun recordQuizCompletion(
        mode: QuizMode,
        difficulty: Difficulty,
        score: Int,
        correctCount: Int,
        totalQuestions: Int,
        streak: Int,
        xpEarned: Int,
        todayDateStr: String
    ): Boolean {
        val currentStats = getUserStats(todayDateStr)
        val key = "${mode.name}_${difficulty.name}"
        val previousBest = currentStats.bestScores[key] ?: 0
        val isNewHighscore = score > previousBest

        val updatedBestScores = currentStats.bestScores.toMutableMap()
        if (score > previousBest) {
            updatedBestScores[key] = score
        }

        val json = JSONObject()
        for ((k, v) in updatedBestScores) {
            json.put(k, v)
        }

        val newBestStreak = maxOf(currentStats.bestStreak, streak)

        var newDailyStreak = currentStats.dailyChallengeStreak
        var newLastDailyDate = currentStats.lastDailyDate
        if (difficulty == Difficulty.DAILY && todayDateStr != currentStats.lastDailyDate) {
            newDailyStreak += 1
            newLastDailyDate = todayDateStr
        }

        val previousLastPlayedDate = prefs.getString("stats_last_played_date", "") ?: ""
        val storedConsecutiveStreak = prefs.getInt("stats_consecutive_days_streak", 0)
        val previousDayStr = getPreviousDayDateStr(todayDateStr)

        val newConsecutiveStreak = when {
            previousLastPlayedDate == todayDateStr -> maxOf(1, storedConsecutiveStreak)
            previousLastPlayedDate == previousDayStr -> storedConsecutiveStreak + 1
            else -> 1 // Started or restarted streak today
        }

        val updatedPlayedDates = getPlayedDates().toMutableSet()
        updatedPlayedDates.add(todayDateStr)
        val playedArr = org.json.JSONArray()
        updatedPlayedDates.sorted().takeLast(60).forEach { playedArr.put(it) }

        prefs.edit()
            .putInt("stats_total_quizzes", currentStats.totalQuizzes + 1)
            .putInt("stats_total_questions", currentStats.totalQuestionsAnswered + totalQuestions)
            .putInt("stats_total_correct", currentStats.totalCorrect + correctCount)
            .putInt("stats_total_xp", currentStats.totalXp + xpEarned)
            .putInt("stats_best_streak", newBestStreak)
            .putInt(
                if (mode == QuizMode.KIDS) "stats_kids_quizzes" else "stats_adults_quizzes",
                if (mode == QuizMode.KIDS) currentStats.kidsQuizzes + 1 else currentStats.adultsQuizzes + 1
            )
            .putInt("stats_daily_streak", newDailyStreak)
            .putString("stats_last_daily_date", newLastDailyDate)
            .putInt("stats_consecutive_days_streak", newConsecutiveStreak)
            .putString("stats_last_played_date", todayDateStr)
            .putString("stats_played_dates", playedArr.toString())
            .putString("stats_best_scores", json.toString())
            .apply()

        return isNewHighscore
    }

    fun getBestScore(mode: QuizMode, difficulty: Difficulty): Int {
        val key = "${mode.name}_${difficulty.name}"
        return getUserStats().bestScores[key] ?: 0
    }

    // Achievements
    fun getAchievements(): List<Achievement> {
        val stats = getUserStats()
        val unlockedJson = prefs.getString("achievements_unlocked", "[]") ?: "[]"
        val unlockedSet = mutableSetOf<String>()
        try {
            val arr = org.json.JSONArray(unlockedJson)
            for (i in 0 until arr.length()) {
                unlockedSet.add(arr.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val list = listOf(
            Achievement(
                id = "first_quiz",
                title = "First Steps",
                description = "Complete your very first quiz",
                emoji = "🚀",
                targetValue = 1,
                currentValue = stats.totalQuizzes,
                isUnlocked = unlockedSet.contains("first_quiz") || stats.totalQuizzes >= 1,
                xpReward = 100
            ),
            Achievement(
                id = "perfectionist",
                title = "Perfectionist",
                description = "Score 10 out of 10 in any quiz level",
                emoji = "🎯",
                targetValue = 10,
                currentValue = if (unlockedSet.contains("perfectionist")) 10 else 0,
                isUnlocked = unlockedSet.contains("perfectionist"),
                xpReward = 250
            ),
            Achievement(
                id = "streak_master",
                title = "On Fire!",
                description = "Achieve a streak of 5 correct answers in a row",
                emoji = "🔥",
                targetValue = 5,
                currentValue = stats.bestStreak.coerceAtMost(5),
                isUnlocked = unlockedSet.contains("streak_master") || stats.bestStreak >= 5,
                xpReward = 200
            ),
            Achievement(
                id = "streak_legend",
                title = "Unstoppable",
                description = "Achieve a flawless 10 streak in a single quiz",
                emoji = "⚡",
                targetValue = 10,
                currentValue = stats.bestStreak.coerceAtMost(10),
                isUnlocked = unlockedSet.contains("streak_legend") || stats.bestStreak >= 10,
                xpReward = 500
            ),
            Achievement(
                id = "quiz_veteran",
                title = "Trivia Veteran",
                description = "Complete 10 quiz attempts",
                emoji = "🏆",
                targetValue = 10,
                currentValue = stats.totalQuizzes.coerceAtMost(10),
                isUnlocked = unlockedSet.contains("quiz_veteran") || stats.totalQuizzes >= 10,
                xpReward = 300
            ),
            Achievement(
                id = "century_answers",
                title = "Century Club",
                description = "Answer 100 questions correctly",
                emoji = "💯",
                targetValue = 100,
                currentValue = stats.totalCorrect.coerceAtMost(100),
                isUnlocked = unlockedSet.contains("century_answers") || stats.totalCorrect >= 100,
                xpReward = 400
            ),
            Achievement(
                id = "daily_champion",
                title = "Daily Champion",
                description = "Maintain a 3-day streak in Daily Challenges",
                emoji = "📅",
                targetValue = 3,
                currentValue = stats.dailyChallengeStreak.coerceAtMost(3),
                isUnlocked = unlockedSet.contains("daily_champion") || stats.dailyChallengeStreak >= 3,
                xpReward = 350
            ),
            Achievement(
                id = "knowledge_master",
                title = "Knowledge Master",
                description = "Earn a total of 5,000 XP",
                emoji = "👑",
                targetValue = 5000,
                currentValue = stats.totalXp.coerceAtMost(5000),
                isUnlocked = unlockedSet.contains("knowledge_master") || stats.totalXp >= 5000,
                xpReward = 1000
            )
        )

        // Persist newly unlocked achievements
        val newlyUnlocked = list.filter { it.isUnlocked }.map { it.id }.toSet()
        if (newlyUnlocked.size > unlockedSet.size) {
            val arr = org.json.JSONArray()
            newlyUnlocked.forEach { arr.put(it) }
            prefs.edit().putString("achievements_unlocked", arr.toString()).apply()
        }

        return list
    }

    fun markAchievementUnlocked(id: String) {
        val unlockedJson = prefs.getString("achievements_unlocked", "[]") ?: "[]"
        val unlockedSet = mutableSetOf<String>()
        try {
            val arr = org.json.JSONArray(unlockedJson)
            for (i in 0 until arr.length()) {
                unlockedSet.add(arr.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        unlockedSet.add(id)
        val arr = org.json.JSONArray()
        unlockedSet.forEach { arr.put(it) }
        prefs.edit().putString("achievements_unlocked", arr.toString()).apply()
    }

    fun resetAllData() {
        prefs.edit().clear().apply()
    }
}
