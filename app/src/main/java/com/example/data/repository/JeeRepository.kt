package com.example.data.repository

import com.example.data.local.JeeDao
import com.example.data.local.SeedData
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JeeRepository(private val dao: JeeDao) {

    val userProfile: Flow<UserAccountEntity?> = dao.getUserFlow()
    val allChapters: Flow<List<ChapterEntity>> = dao.getAllChapters()
    val allSessions: Flow<List<StudySessionEntity>> = dao.getAllSessions()
    val activeBacklogs: Flow<List<BacklogEntity>> = dao.getActiveBacklogs()
    val allBacklogs: Flow<List<BacklogEntity>> = dao.getAllBacklogs()
    val allMockTests: Flow<List<MockTestEntity>> = dao.getAllMockTests()
    val allMistakes: Flow<List<MistakeEntity>> = dao.getAllMistakes()
    val recentDailyLogs: Flow<List<DailyStudyLogEntity>> = dao.getRecentDailyLogs()

    suspend fun getChaptersBySubject(subjectId: String): Flow<List<ChapterEntity>> {
        return dao.getChaptersBySubject(subjectId)
    }

    suspend fun updateChapter(chapter: ChapterEntity) {
        dao.updateChapter(chapter)
    }

    suspend fun insertSession(session: StudySessionEntity): Long {
        return dao.insertSession(session)
    }

    suspend fun updateSession(session: StudySessionEntity) {
        dao.updateSession(session)
    }

    suspend fun toggleSessionComplete(session: StudySessionEntity) {
        val newStatus = !session.isCompleted
        val actualMins = if (newStatus) session.durationMinutes else 0
        dao.updateSession(
            session.copy(
                isCompleted = newStatus,
                actualDurationMinutes = actualMins,
                completedAt = if (newStatus) System.currentTimeMillis() else 0L
            )
        )
        if (newStatus) {
            logStudyMinutes(session.durationMinutes)
        }
    }

    suspend fun deleteSession(id: Long) {
        dao.deleteSessionById(id)
    }

    suspend fun insertBacklog(backlog: BacklogEntity): Long {
        return dao.insertBacklog(backlog)
    }

    suspend fun updateBacklog(backlog: BacklogEntity) {
        dao.updateBacklog(backlog)
    }

    suspend fun resolveBacklog(backlog: BacklogEntity, resolved: Boolean) {
        dao.updateBacklog(backlog.copy(isResolved = resolved))
    }

    suspend fun deleteBacklog(id: Long) {
        dao.deleteBacklogById(id)
    }

    suspend fun insertMockTest(test: MockTestEntity): Long {
        return dao.insertMockTest(test)
    }

    suspend fun deleteMockTest(test: MockTestEntity) {
        dao.deleteMockTest(test)
    }

    suspend fun insertMistake(mistake: MistakeEntity): Long {
        return dao.insertMistake(mistake)
    }

    suspend fun deleteMistake(mistake: MistakeEntity) {
        dao.deleteMistake(mistake)
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    suspend fun logStudyMinutes(minutes: Int) {
        val today = getTodayDateString()
        val existing = dao.getLogForDate(today)
        if (existing != null) {
            dao.insertOrUpdateDailyLog(
                existing.copy(
                    totalMinutesStudied = existing.totalMinutesStudied + minutes,
                    sessionsCompleted = existing.sessionsCompleted + 1
                )
            )
        } else {
            dao.insertOrUpdateDailyLog(
                DailyStudyLogEntity(
                    dateString = today,
                    totalMinutesStudied = minutes,
                    sessionsCompleted = 1,
                    targetMinutes = 360
                )
            )
        }
    }

    // --- User Management ---
    suspend fun saveUser(user: UserAccountEntity) {
        dao.insertOrUpdateUser(user)
    }

    suspend fun getCurrentUser(): UserAccountEntity? {
        return dao.getUser()
    }

    suspend fun loginOrSignup(name: String, email: String, avatarType: String, examName: String) {
        val existing = dao.getUser()
        val updated = (existing ?: UserAccountEntity()).copy(
            name = name.ifBlank { existing?.name ?: "Aspirant" },
            email = email.ifBlank { existing?.email ?: "aspirant@jee.prep" },
            avatarType = if (avatarType.isNotBlank()) avatarType else (existing?.avatarType ?: "rocket"),
            examName = examName.ifBlank { existing?.examName ?: "JEE Main 2027 & Advanced" },
            isLoggedIn = true,
            isTutorialCompleted = existing?.isTutorialCompleted ?: true
        )
        dao.insertOrUpdateUser(updated)
    }

    suspend fun logout() {
        val current = dao.getUser()
        if (current != null) {
            dao.insertOrUpdateUser(current.copy(isLoggedIn = false))
        }
    }

    suspend fun setTutorialCompleted(completed: Boolean) {
        val current = dao.getUser() ?: UserAccountEntity()
        dao.insertOrUpdateUser(current.copy(isTutorialCompleted = completed))
    }

    // New user starts clean: syllabus seeded as Not Started, 0 sessions, 0 backlogs, 0 mock tests
    suspend fun ensureSeeded() {
        if (dao.getChapterCount() == 0) {
            dao.insertChapters(SeedData.getInitialChapters())
        }
    }

    // Reset all records to a clean new-user slate
    suspend fun resetToNewUser(name: String, email: String, avatarType: String, examName: String) {
        dao.clearAllSessions()
        dao.clearAllBacklogs()
        dao.clearAllMockTests()
        dao.clearAllMistakes()
        dao.clearAllLogs()
        dao.insertChapters(SeedData.getInitialChapters())
        dao.insertOrUpdateUser(
            UserAccountEntity(
                name = name,
                email = email,
                avatarType = avatarType,
                examName = examName,
                isLoggedIn = true,
                isTutorialCompleted = false
            )
        )
    }

    suspend fun loadDemoSampleData() {
        val today = getTodayDateString()
        SeedData.getSampleDemoSessions(today).forEach { dao.insertSession(it) }
        SeedData.getSampleDemoBacklogs().forEach { dao.insertBacklog(it) }
        SeedData.getSampleDemoMockTests().forEach { dao.insertMockTest(it) }
    }
}
