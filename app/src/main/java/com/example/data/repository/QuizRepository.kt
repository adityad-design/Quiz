package com.example.data.repository

import android.content.Context
import com.example.data.model.Difficulty
import com.example.data.model.Question
import com.example.data.model.QuizMode
import com.example.data.model.QuizQuestionState
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class QuizRepository(private val context: Context) {

    private var cachedKidsQuestions: List<Question>? = null
    private var cachedAdultsQuestions: List<Question>? = null

    @Synchronized
    fun getQuestions(mode: QuizMode): List<Question> {
        return when (mode) {
            QuizMode.KIDS -> {
                if (cachedKidsQuestions == null) {
                    cachedKidsQuestions = loadQuestionsFromAsset("questions_kids.json")
                }
                cachedKidsQuestions ?: emptyList()
            }
            QuizMode.ADULTS -> {
                if (cachedAdultsQuestions == null) {
                    cachedAdultsQuestions = loadQuestionsFromAsset("questions_adults.json")
                }
                cachedAdultsQuestions ?: emptyList()
            }
        }
    }

    private fun loadQuestionsFromAsset(fileName: String): List<Question> {
        val questions = mutableListOf<Question>()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optString("id", "q_$i")
                val text = obj.getString("text")
                val optsArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optsArray.length()) {
                    options.add(optsArray.getString(j))
                }
                val correctIndex = obj.getInt("correctIndex")
                val correctAnswer = obj.optString("correctAnswer", options.getOrNull(correctIndex) ?: "")
                val category = obj.optString("category", "General")
                val difficulty = obj.optString("difficulty", "Medium")
                val explanation = obj.optString("explanation", "Correct answer is: $correctAnswer")

                questions.add(
                    Question(
                        id = id,
                        text = text,
                        options = options,
                        correctIndex = correctIndex,
                        correctAnswer = correctAnswer,
                        category = category,
                        difficulty = difficulty,
                        explanation = explanation
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return questions
    }

    /**
     * Generates a 10-question quiz attempt with no repeats and randomized option positions.
     */
    fun createQuiz(mode: QuizMode, difficulty: Difficulty): List<QuizQuestionState> {
        val pool = getQuestions(mode)
        if (pool.isEmpty()) return emptyList()

        val selectedQuestions: List<Question> = if (difficulty == Difficulty.DAILY) {
            // Seeded selection based on today's date YYYYMMDD
            val dateKey = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val seed = dateKey.toLongOrNull() ?: 20260917L
            val rng = Random(seed)

            val easyPool = pool.filter { it.difficulty.equals("Easy", ignoreCase = true) }
            val medPool = pool.filter { it.difficulty.equals("Medium", ignoreCase = true) }
            val hardPool = pool.filter { it.difficulty.equals("Hard", ignoreCase = true) }

            val chosen = mutableListOf<Question>()
            chosen.addAll(easyPool.shuffled(rng).take(4))
            chosen.addAll(medPool.shuffled(rng).take(3))
            chosen.addAll(hardPool.shuffled(rng).take(3))

            // Fallback if difficulty buckets are smaller than requested
            if (chosen.size < 10) {
                val remaining = pool.filterNot { chosen.contains(it) }.shuffled(rng)
                chosen.addAll(remaining.take(10 - chosen.size))
            }
            chosen.shuffled(rng).take(10)
        } else {
            // Filter by requested difficulty
            val diffTarget = difficulty.title
            val filtered = pool.filter { it.difficulty.equals(diffTarget, ignoreCase = true) }
            val basePool = if (filtered.size >= 10) filtered else pool
            basePool.shuffled().take(10)
        }

        // Randomize answer option positions for each question
        return selectedQuestions.map { q ->
            val shuffled = q.options.shuffled()
            val newCorrectIdx = shuffled.indexOf(q.correctAnswer).let { if (it >= 0) it else 0 }
            QuizQuestionState(
                question = q,
                shuffledOptions = shuffled,
                correctShuffledIndex = newCorrectIdx
            )
        }
    }

    fun getTotalQuestionCount(mode: QuizMode): Int = getQuestions(mode).size

    fun getCategoryCounts(mode: QuizMode): Map<String, Int> {
        return getQuestions(mode).groupingBy { it.category }.eachCount()
    }
}
