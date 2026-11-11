package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccountEntity(
    @PrimaryKey val id: String = "primary_user",
    val name: String = "Aspirant",
    val email: String = "aspirant@jee.prep",
    val avatarType: String = "rocket", // "rocket", "atom", "graduate", "fire", "trophy", "brain"
    val examName: String = "JEE Main 2027 & Advanced",
    val targetExamDate: String = "2027-01-24",
    val dailyTargetHours: Float = 6f,
    val isTutorialCompleted: Boolean = false,
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String, // "physics", "chemistry", "mathematics"
    val name: String,
    val iconName: String,
    val totalChapters: Int = 0
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String, // "physics", "chemistry", "mathematics"
    val name: String,
    val classLevel: Int, // 11 or 12
    val subCategory: String, // "Mechanics", "Physical", "Organic", "Calculus", etc.
    val importance: String, // "High", "Medium", "Low"
    val status: String = "Not Started", // "Not Started", "Learning", "Practicing", "Completed", "Revision Required"
    val lectureCompletedPct: Int = 0,
    val conceptRating: Int = 3, // 1 to 5
    val dppSolvedPct: Int = 0,
    val pyqSolvedPct: Int = 0,
    val revisionCount: Int = 0,
    val lastRevisedDate: Long = 0L,
    val notes: String = ""
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val chapterId: Long? = null,
    val chapterName: String,
    val title: String,
    val scheduledDate: String, // "YYYY-MM-DD"
    val scheduledTime: String, // "HH:mm"
    val durationMinutes: Int,
    val sessionType: String, // "Theory", "DPP / Practice", "PYQs", "Revision", "Mock Test"
    val isCompleted: Boolean = false,
    val actualDurationMinutes: Int = 0,
    val completedAt: Long = 0L,
    val notes: String = ""
)

@Entity(tableName = "backlogs")
data class BacklogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: Long,
    val chapterName: String,
    val subjectId: String,
    val estimatedHours: Int,
    val priority: String, // "Critical", "High", "Medium", "Low"
    val understandingLevel: String, // "Low", "Medium", "Good"
    val lectureDone: Boolean = false,
    val questionsDone: Boolean = false,
    val revisionDone: Boolean = false,
    val targetDate: String = "",
    val isResolved: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "mock_tests")
data class MockTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testName: String,
    val testDate: String, // "YYYY-MM-DD"
    val totalMarks: Int = 300,
    val marksObtained: Int,
    val physicsMarks: Int,
    val chemistryMarks: Int,
    val mathsMarks: Int,
    val attemptedQuestions: Int,
    val correctQuestions: Int,
    val incorrectQuestions: Int,
    val unattemptedQuestions: Int,
    val testType: String = "JEE Main", // "JEE Main", "JEE Advanced"
    val notes: String = ""
)

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mockTestId: Long,
    val testName: String,
    val subjectId: String,
    val chapterName: String,
    val questionNumber: Int = 0,
    val mistakeType: String, // "Conceptual", "Calculation", "Interpretation", "Time Management", "Silly"
    val description: String,
    val correctionNotes: String = ""
)

@Entity(tableName = "daily_study_logs")
data class DailyStudyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val totalMinutesStudied: Int = 0,
    val sessionsCompleted: Int = 0,
    val targetMinutes: Int = 360 // default 6 hours
)
