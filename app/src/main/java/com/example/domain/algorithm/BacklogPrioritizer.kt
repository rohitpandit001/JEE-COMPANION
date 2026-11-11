package com.example.domain.algorithm

import com.example.data.model.BacklogEntity
import com.example.data.model.ChapterEntity
import kotlin.math.roundToInt

data class RecoverySlot(
    val dayOffset: Int,
    val dayLabel: String,
    val chapterName: String,
    val subjectId: String,
    val allocatedMinutes: Int,
    val focusArea: String,
    val priorityScore: Float
)

data class RecoveryPlan(
    val totalBacklogHours: Int,
    val recommendedDailyExtraMinutes: Int,
    val estimatedDaysToClear: Int,
    val dailySchedule: List<RecoverySlot>,
    val highYieldAlerts: List<String>
)

object BacklogPrioritizer {

    /**
     * Calculates a composite priority score for each backlog item.
     * Score = (Weightage Multiplier * 3.5) + (Understanding Deficit * 2.5) + Workload Factor
     */
    fun computePriorityScore(backlog: BacklogEntity, chapter: ChapterEntity?): Float {
        val importanceWeight = when (chapter?.importance) {
            "High" -> 4.0f
            "Medium" -> 2.5f
            "Low" -> 1.0f
            else -> 2.0f
        }

        val understandingDeficit = when (backlog.understandingLevel) {
            "Low" -> 3.0f
            "Medium" -> 1.8f
            "Good" -> 0.8f
            else -> 2.0f
        }

        val userPriorityBoost = when (backlog.priority) {
            "Critical" -> 3.0f
            "High" -> 2.0f
            "Medium" -> 1.0f
            "Low" -> 0.5f
            else -> 1.0f
        }

        val workloadPenalty = if (backlog.estimatedHours > 8) 1.5f else 1.0f

        return (importanceWeight * 2.5f) + (understandingDeficit * 2.0f) + (userPriorityBoost * 1.5f) + workloadPenalty
    }

    /**
     * Generates a realistic recovery schedule allocating 60-90 minutes daily
     * without burning out the student or exceeding daily targets.
     */
    fun generateRecoveryPlan(
        backlogs: List<BacklogEntity>,
        chaptersMap: Map<Long, ChapterEntity>,
        availableExtraMinutesDaily: Int = 75
    ): RecoveryPlan {
        val active = backlogs.filter { !it.isResolved }
        if (active.isEmpty()) {
            return RecoveryPlan(
                totalBacklogHours = 0,
                recommendedDailyExtraMinutes = 0,
                estimatedDaysToClear = 0,
                dailySchedule = emptyList(),
                highYieldAlerts = listOf("Congratulations! No active backlogs detected.")
            )
        }

        val totalHours = active.sumOf { it.estimatedHours }
        val scoredList = active.map { b ->
            val chap = chaptersMap[b.chapterId]
            val score = computePriorityScore(b, chap)
            Pair(b, score)
        }.sortedByDescending { it.second }

        val slots = mutableListOf<RecoverySlot>()
        var currentDay = 1
        val alerts = mutableListOf<String>()

        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        for ((backlog, score) in scoredList) {
            val chap = chaptersMap[backlog.chapterId]
            if (chap?.importance == "High") {
                alerts.add("High-yield JEE chapter: ${backlog.chapterName} has priority rank #${slots.size + 1}")
            }

            // Break down backlog hours into manageable 60-90 min chunks
            var remainingMins = backlog.estimatedHours * 60
            while (remainingMins > 0 && currentDay <= 14) {
                val sessionMins = minOf(remainingMins, availableExtraMinutesDaily)
                val focus = when {
                    !backlog.lectureDone -> "Concept Theory & Notes"
                    !backlog.questionsDone -> "Standard DPP & Level 1 Problems"
                    !backlog.revisionDone -> "PYQ Drill (Last 5 Years)"
                    else -> "Formula Revision & Summary"
                }

                val dayIndex = (currentDay - 1) % 7
                slots.add(
                    RecoverySlot(
                        dayOffset = currentDay,
                        dayLabel = "Day $currentDay (${daysOfWeek[dayIndex]})",
                        chapterName = backlog.chapterName,
                        subjectId = backlog.subjectId,
                        allocatedMinutes = sessionMins,
                        focusArea = focus,
                        priorityScore = score
                    )
                )
                remainingMins -= sessionMins
                currentDay++
            }
        }

        val totalMinutesNeeded = totalHours * 60
        val estDays = (totalMinutesNeeded.toFloat() / availableExtraMinutesDaily).roundToInt().coerceAtLeast(1)

        return RecoveryPlan(
            totalBacklogHours = totalHours,
            recommendedDailyExtraMinutes = availableExtraMinutesDaily,
            estimatedDaysToClear = estDays,
            dailySchedule = slots,
            highYieldAlerts = alerts.take(3)
        )
    }
}
