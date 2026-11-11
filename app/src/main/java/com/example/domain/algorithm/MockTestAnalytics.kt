package com.example.domain.algorithm

import com.example.data.model.MistakeEntity
import com.example.data.model.MockTestEntity

data class MockTestSummary(
    val accuracyPct: Float,
    val attemptPct: Float,
    val negativeMarksLost: Int,
    val physicsPercentage: Float,
    val chemistryPercentage: Float,
    val mathsPercentage: Float,
    val overallPercentage: Float
)

data class MistakeAnalysis(
    val totalMistakes: Int,
    val categoryCounts: Map<String, Int>, // "Conceptual" -> 4, etc.
    val mostCommonMistakeType: String,
    val repeatMistakeChapters: List<String>,
    val highRiskAdvice: String
)

object MockTestAnalytics {

    fun computeTestSummary(test: MockTestEntity): MockTestSummary {
        val totalQ = if (test.totalMarks == 300) 75 else 75
        val marksPerQ = 4

        val accuracy = if (test.attemptedQuestions > 0) {
            (test.correctQuestions.toFloat() / test.attemptedQuestions) * 100f
        } else 0f

        val attemptPct = if (totalQ > 0) {
            (test.attemptedQuestions.toFloat() / totalQ) * 100f
        } else 0f

        // Typically in JEE Main, -1 for every incorrect MCQ
        val negativeMarks = test.incorrectQuestions * 1

        val maxSubjectMarks = test.totalMarks / 3
        val phyPct = if (maxSubjectMarks > 0) (test.physicsMarks.toFloat() / maxSubjectMarks) * 100f else 0f
        val chemPct = if (maxSubjectMarks > 0) (test.chemistryMarks.toFloat() / maxSubjectMarks) * 100f else 0f
        val mathPct = if (maxSubjectMarks > 0) (test.mathsMarks.toFloat() / maxSubjectMarks) * 100f else 0f
        val overallPct = if (test.totalMarks > 0) (test.marksObtained.toFloat() / test.totalMarks) * 100f else 0f

        return MockTestSummary(
            accuracyPct = accuracy,
            attemptPct = attemptPct,
            negativeMarksLost = negativeMarks,
            physicsPercentage = phyPct,
            chemistryPercentage = chemPct,
            mathsPercentage = mathPct,
            overallPercentage = overallPct
        )
    }

    fun analyzeMistakes(mistakes: List<MistakeEntity>): MistakeAnalysis {
        if (mistakes.isEmpty()) {
            return MistakeAnalysis(
                totalMistakes = 0,
                categoryCounts = emptyMap(),
                mostCommonMistakeType = "None",
                repeatMistakeChapters = emptyList(),
                highRiskAdvice = "Log your mock test mistakes into the Mistake Notebook to pinpoint marks leaks!"
            )
        }

        val counts = mistakes.groupingBy { it.mistakeType }.eachCount()
        val topCategory = counts.maxByOrNull { it.value }?.key ?: "Conceptual"

        val chapterMistakes = mistakes.groupingBy { it.chapterName }.eachCount()
        val repeatChapters = chapterMistakes.filter { it.value >= 2 }.keys.toList()

        val advice = when (topCategory) {
            "Conceptual" -> "Major marks leak is in Core Theory and Formulas. Spend 40% more study time on foundational lecture revision and DPPs."
            "Calculation" -> "Silly arithmetic and algebraic errors detected. Practice solving numericals without calculators and cross-check boundary values."
            "Time Management" -> "Time crunch in test hall. Implement 3-round exam strategy: Round 1 (easy <2 min), Round 2 (moderate), Round 3 (tough)."
            "Interpretation" -> "Misreading question conditions or units. Underline keywords like 'INCORRECT', 'NOT TRUE', and dimensional units."
            "Silly" -> "Losing guaranteed marks to haste. Slow down during the last 30 seconds of each question bubble marking."
            else -> "Review weak chapters regularly to eliminate repeated errors."
        }

        return MistakeAnalysis(
            totalMistakes = mistakes.size,
            categoryCounts = counts,
            mostCommonMistakeType = topCategory,
            repeatMistakeChapters = repeatChapters,
            highRiskAdvice = advice
        )
    }
}
