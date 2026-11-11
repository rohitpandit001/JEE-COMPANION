package com.example.domain.algorithm

import com.example.data.model.BacklogEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.MistakeEntity
import com.example.data.model.MockTestEntity

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "coach"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedActions: List<String> = emptyList()
)

object AiStudyCoach {

    fun generateWelcomeMessage(
        studentName: String,
        activeBacklogsCount: Int,
        latestMockScore: Int?,
        overallSyllabusPct: Int
    ): AiMessage {
        val scoreText = if (latestMockScore != null) "Your latest mock score stands at $latestMockScore/300." else "No mock tests recorded yet."
        val backlogWarning = if (activeBacklogsCount > 0) "You have $activeBacklogsCount active backlog(s) that require priority attention." else "Your backlogs are completely clear!"

        return AiMessage(
            sender = "coach",
            text = "Namaste $studentName! I am your AI JEE Study Coach. 🎯\n\n• Current Syllabus Progress: $overallSyllabusPct%\n• $scoreText\n• $backlogWarning\n\nHow can I guide your preparation today?",
            suggestedActions = listOf(
                "Analyze My Weak Chapters",
                "Generate Weekly Study Plan",
                "How to Recover Backlogs?",
                "Mock Test Score Strategy",
                "Top High-Yield Chapters for JEE"
            )
        )
    }

    fun answerQuery(
        prompt: String,
        chapters: List<ChapterEntity>,
        backlogs: List<BacklogEntity>,
        mockTests: List<MockTestEntity>,
        mistakes: List<MistakeEntity>
    ): AiMessage {
        val cleanPrompt = prompt.lowercase().trim()

        return when {
            cleanPrompt.contains("weak") || cleanPrompt.contains("weak chapter") -> {
                val weakChaps = chapters.filter { it.conceptRating <= 2 || it.status == "Learning" || it.status == "Revision Required" }
                    .take(4)
                val mistakeChaps = mistakes.groupingBy { it.chapterName }.eachCount().entries.sortedByDescending { it.value }.take(3)

                val response = buildString {
                    append("🔍 **Diagnostic Weak Chapter Report**\n\n")
                    if (weakChaps.isNotEmpty()) {
                        append("Based on your self-assessments:\n")
                        weakChaps.forEach { chap ->
                            append("• **${chap.name}** (${chap.subjectId.replaceFirstChar { it.uppercase() }}) — Concept Rating: ${chap.conceptRating}/5, DPPs: ${chap.dppSolvedPct}%\n")
                        }
                    }
                    if (mistakeChaps.isNotEmpty()) {
                        append("\nChapters leaking marks in recent Mock Tests:\n")
                        mistakeChaps.forEach { (chap, count) ->
                            append("• **$chap**: $count logged mistakes\n")
                        }
                    }
                    append("\n💡 **Recommendation:** Dedicate 2 exclusive 45-minute problem-solving sessions this week specifically on ${weakChaps.firstOrNull()?.name ?: "your lowest DPP chapter"}.")
                }

                AiMessage(
                    sender = "coach",
                    text = response,
                    suggestedActions = listOf("Schedule Weak Chapter Session", "View Backlogs", "Check Mock Tests")
                )
            }

            cleanPrompt.contains("backlog") || cleanPrompt.contains("recover") -> {
                val active = backlogs.filter { !it.isResolved }
                val critical = active.filter { it.priority == "Critical" || it.priority == "High" }
                val response = buildString {
                    append("⚡ **Backlog Recovery Strategy**\n\n")
                    append("You currently have **${active.size} pending backlog(s)** (~${active.sumOf { it.estimatedHours }} total study hours).\n\n")
                    if (critical.isNotEmpty()) {
                        append("🎯 **Immediate Priority Backlogs:**\n")
                        critical.forEach { b ->
                            append("• **${b.chapterName}** (${b.subjectId.uppercase()}): ${b.estimatedHours}h needed | Status: ${if (b.lectureDone) "Practice pending" else "Lectures pending"}\n")
                        }
                    }
                    append("\n🛡️ **The 70-30 Golden Rule:**\n")
                    append("Never stop ongoing class topics! Spend 70% of your daily study time on current class lectures and 30% (~75 minutes) recovering one high-priority backlog at a time.")
                }

                AiMessage(
                    sender = "coach",
                    text = response,
                    suggestedActions = listOf("Open Backlog Recovery Plan", "Schedule 75m Session")
                )
            }

            cleanPrompt.contains("weekly") || cleanPrompt.contains("plan") || cleanPrompt.contains("schedule") -> {
                val response = buildString {
                    append("📅 **Custom Personalized 7-Day JEE Routine**\n\n")
                    append("Balanced daily allocation (Target: 6.5 hours):\n\n")
                    append("• **Morning (6:30 - 8:30 AM):** Mathematics (High-focus problem solving, Conics/Calculus)\n")
                    append("• **Afternoon (2:30 - 4:30 PM):** Physics (Concept consolidation & HC Verma / PYQs)\n")
                    append("• **Evening (6:00 - 8:00 PM):** Chemistry (Inorganic/Organic reaction mechanisms & NCERT line-by-line)\n")
                    append("• **Night Buffer (9:30 - 10:45 PM):** Backlog Recovery or Mistake Notebook Review\n\n")
                    append("✨ *Pro Tip:* Keep Sunday morning for a 3-hour timed Mock Test with strict exam simulation.")
                }

                AiMessage(
                    sender = "coach",
                    text = response,
                    suggestedActions = listOf("Add to Study Planner", "Start Pomodoro Timer")
                )
            }

            cleanPrompt.contains("mock") || cleanPrompt.contains("score") || cleanPrompt.contains("test") -> {
                val latest = mockTests.firstOrNull()
                val score = latest?.marksObtained ?: 190
                val response = buildString {
                    append("📊 **Mock Test Score Optimization Analysis**\n\n")
                    if (latest != null) {
                        append("Latest Test: **${latest.testName}**\n")
                        append("• Score: **${latest.marksObtained}/${latest.totalMarks}**\n")
                        append("• Accuracy: ${(latest.correctQuestions.toFloat() / latest.attemptedQuestions * 100).toInt()}%\n")
                        append("• Negative Marks Penalty: -${latest.incorrectQuestions} marks\n\n")
                    }
                    append("🎯 **Action Steps to Boost Score by +30 Marks:**\n")
                    append("1. **Stop Blind Guesswork:** You lost marks on incorrect attempts. Leaving questionable questions unattempted will instantly boost your net score.\n")
                    append("2. **Chemistry Speedrun:** Complete Chemistry in 40-45 minutes. Save extra time for Mathematics calculations.\n")
                    append("3. **Post-Test 3-Hour Autopsy:** Re-solve every incorrect question on paper within 24 hours.")
                }

                AiMessage(
                    sender = "coach",
                    text = response,
                    suggestedActions = listOf("Analyze Mistake Notebook", "Log New Mock Test")
                )
            }

            cleanPrompt.contains("high yield") || cleanPrompt.contains("important") || cleanPrompt.contains("weightage") -> {
                val highYield = chapters.filter { it.importance == "High" }.shuffled().take(6)
                val response = buildString {
                    append("🔥 **Top High-Yield Chapters for JEE Main & Advanced**\n\n")
                    highYield.forEach { chap ->
                        append("• **${chap.name}** [${chap.subjectId.replaceFirstChar { it.uppercase() }} - Class ${chap.classLevel}]\n")
                    }
                    append("\nMastering these high-weightage chapters guarantees over 60% of the total question paper marks.")
                }

                AiMessage(
                    sender = "coach",
                    text = response,
                    suggestedActions = listOf("View Full Syllabus", "Filter High Weightage")
                )
            }

            else -> {
                AiMessage(
                    sender = "coach",
                    text = "Great question! Success in JEE is driven by 3 fundamental pillars:\n\n1. **Consistency over intensity**: 6 hours every day beats 12 hours once a week.\n2. **Active recall & PYQ mastery**: Don't just re-read notes—solve 2019-2024 JEE Main and Advanced previous year questions under a timer.\n3. **Relentless mistake correction**: Maintain your Mistake Notebook diligently so silly calculation errors are eliminated before exam day.\n\nWould you like me to generate a specific plan or examine a weak topic?",
                    suggestedActions = listOf("Analyze Weak Chapters", "Generate Weekly Plan", "Backlog Recovery")
                )
            }
        }
    }
}
