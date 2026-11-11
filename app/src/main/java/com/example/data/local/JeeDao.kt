package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JeeDao {

    // --- User Profile ---
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String = "primary_user"): Flow<UserAccountEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String = "primary_user"): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserAccountEntity)

    @Query("DELETE FROM study_sessions")
    suspend fun clearAllSessions()

    @Query("DELETE FROM backlogs")
    suspend fun clearAllBacklogs()

    @Query("DELETE FROM mock_tests")
    suspend fun clearAllMockTests()

    @Query("DELETE FROM mistakes")
    suspend fun clearAllMistakes()

    @Query("DELETE FROM daily_study_logs")
    suspend fun clearAllLogs()

    // --- Chapters ---
    @Query("SELECT * FROM chapters ORDER BY classLevel ASC, id ASC")
    fun getAllChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY classLevel ASC, id ASC")
    fun getChaptersBySubject(subjectId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :chapterId LIMIT 1")
    suspend fun getChapterById(chapterId: Long): ChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChapterCount(): Int

    // --- Study Sessions ---
    @Query("SELECT * FROM study_sessions ORDER BY scheduledDate DESC, scheduledTime ASC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE scheduledDate = :date ORDER BY scheduledTime ASC")
    fun getSessionsForDate(date: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE scheduledDate >= :startDate AND scheduledDate <= :endDate ORDER BY scheduledDate ASC, scheduledTime ASC")
    fun getSessionsForRange(startDate: String, endDate: String): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Update
    suspend fun updateSession(session: StudySessionEntity)

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    // --- Backlogs ---
    @Query("SELECT * FROM backlogs ORDER BY isResolved ASC, id DESC")
    fun getAllBacklogs(): Flow<List<BacklogEntity>>

    @Query("SELECT * FROM backlogs WHERE isResolved = 0 ORDER BY id DESC")
    fun getActiveBacklogs(): Flow<List<BacklogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBacklog(backlog: BacklogEntity): Long

    @Update
    suspend fun updateBacklog(backlog: BacklogEntity)

    @Delete
    suspend fun deleteBacklog(backlog: BacklogEntity)

    @Query("DELETE FROM backlogs WHERE id = :id")
    suspend fun deleteBacklogById(id: Long)

    // --- Mock Tests ---
    @Query("SELECT * FROM mock_tests ORDER BY testDate DESC, id DESC")
    fun getAllMockTests(): Flow<List<MockTestEntity>>

    @Query("SELECT * FROM mock_tests WHERE id = :id LIMIT 1")
    suspend fun getMockTestById(id: Long): MockTestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockTest(test: MockTestEntity): Long

    @Update
    suspend fun updateMockTest(test: MockTestEntity)

    @Delete
    suspend fun deleteMockTest(test: MockTestEntity)

    // --- Mistakes Notebook ---
    @Query("SELECT * FROM mistakes ORDER BY id DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE mockTestId = :testId ORDER BY id ASC")
    fun getMistakesForTest(testId: Long): Flow<List<MistakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeEntity): Long

    @Delete
    suspend fun deleteMistake(mistake: MistakeEntity)

    // --- Daily Study Logs ---
    @Query("SELECT * FROM daily_study_logs ORDER BY dateString DESC LIMIT 30")
    fun getRecentDailyLogs(): Flow<List<DailyStudyLogEntity>>

    @Query("SELECT * FROM daily_study_logs WHERE dateString = :date LIMIT 1")
    suspend fun getLogForDate(date: String): DailyStudyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyLog(log: DailyStudyLogEntity)
}
