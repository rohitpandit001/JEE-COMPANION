package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.JeeRepository
import com.example.domain.algorithm.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class TimerMode(val title: String, val defaultMinutes: Int) {
    WORK("Focus Session", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}

data class TimerState(
    val isRunning: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val currentMode: TimerMode = TimerMode.WORK,
    val selectedSubject: String = "physics",
    val selectedChapterName: String = "Rotational Dynamics"
)

data class SyllabusSummary(
    val overallPercentage: Int = 0,
    val physicsPercentage: Int = 0,
    val chemistryPercentage: Int = 0,
    val mathsPercentage: Int = 0,
    val completedCount: Int = 0,
    val totalCount: Int = 0
)

class JeeViewModel(private val repository: JeeRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    val userProfile: StateFlow<UserAccountEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val chapters: StateFlow<List<ChapterEntity>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeBacklogs: StateFlow<List<BacklogEntity>> = repository.activeBacklogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBacklogs: StateFlow<List<BacklogEntity>> = repository.allBacklogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockTests: StateFlow<List<MockTestEntity>> = repository.allMockTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mistakes: StateFlow<List<MistakeEntity>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyLogs: StateFlow<List<DailyStudyLogEntity>> = repository.recentDailyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived syllabus statistics
    val syllabusSummary: StateFlow<SyllabusSummary> = chapters.map { list ->
        if (list.isEmpty()) SyllabusSummary()
        else {
            val phy = list.filter { it.subjectId == "physics" }
            val chem = list.filter { it.subjectId == "chemistry" }
            val math = list.filter { it.subjectId == "mathematics" }

            val phyPct = if (phy.isNotEmpty()) (phy.count { it.status == "Completed" } * 100 / phy.size) else 0
            val chemPct = if (chem.isNotEmpty()) (chem.count { it.status == "Completed" } * 100 / chem.size) else 0
            val mathPct = if (math.isNotEmpty()) (math.count { it.status == "Completed" } * 100 / math.size) else 0
            val overall = (list.count { it.status == "Completed" } * 100 / list.size)

            SyllabusSummary(
                overallPercentage = overall,
                physicsPercentage = phyPct,
                chemistryPercentage = chemPct,
                mathsPercentage = mathPct,
                completedCount = list.count { it.status == "Completed" },
                totalCount = list.size
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SyllabusSummary())

    // Today's study stats
    val todayDate = repository.getTodayDateString()
    val todaySessions: StateFlow<List<StudySessionEntity>> = sessions.map { list ->
        list.filter { it.scheduledDate == todayDate }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMinutesStudied: StateFlow<Int> = dailyLogs.map { logs ->
        logs.find { it.dateString == todayDate }?.totalMinutesStudied ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentStreak: StateFlow<Int> = dailyLogs.map { logs ->
        // Count active consecutive days with studied minutes > 0
        var streak = 0
        for (log in logs) {
            if (log.totalMinutesStudied > 0) streak++
            else break
        }
        streak.coerceAtLeast(1)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    // AI Coach Chat
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(emptyList())
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()

    init {
        viewModelScope.launch {
            _aiMessages.value = listOf(
                AiStudyCoach.generateWelcomeMessage(
                    studentName = "Aspirant",
                    activeBacklogsCount = 3,
                    latestMockScore = 208,
                    overallSyllabusPct = 48
                )
            )
        }
    }

    // Timer State
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var timerJob: Job? = null

    fun setTimerMode(mode: TimerMode) {
        timerJob?.cancel()
        val totalSecs = mode.defaultMinutes * 60
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            currentMode = mode,
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs
        )
    }

    fun setTimerSubject(subject: String, chapterName: String) {
        _timerState.value = _timerState.value.copy(
            selectedSubject = subject,
            selectedChapterName = chapterName
        )
    }

    fun toggleTimer() {
        if (_timerState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _timerState.value = _timerState.value.copy(isRunning = true)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000L)
                val newRemaining = _timerState.value.remainingSeconds - 1
                _timerState.value = _timerState.value.copy(remainingSeconds = newRemaining)
                if (newRemaining <= 0) {
                    onTimerFinished()
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        _timerState.value = _timerState.value.copy(isRunning = false)
        timerJob?.cancel()
    }

    fun resetTimer() {
        timerJob?.cancel()
        val total = _timerState.value.totalSeconds
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            remainingSeconds = total
        )
    }

    private fun onTimerFinished() {
        _timerState.value = _timerState.value.copy(isRunning = false)
        if (_timerState.value.currentMode == TimerMode.WORK) {
            val minutes = _timerState.value.totalSeconds / 60
            viewModelScope.launch {
                repository.logStudyMinutes(minutes)
                // Also create a completed study session in DB
                repository.insertSession(
                    StudySessionEntity(
                        subjectId = _timerState.value.selectedSubject,
                        chapterName = _timerState.value.selectedChapterName,
                        title = "Focus Pomodoro: ${_timerState.value.selectedChapterName}",
                        scheduledDate = todayDate,
                        scheduledTime = "Now",
                        durationMinutes = minutes,
                        sessionType = "Focus Practice",
                        isCompleted = true,
                        actualDurationMinutes = minutes,
                        notes = "Completed via Pomodoro Timer"
                    )
                )
            }
        }
    }

    fun completeTimerEarly() {
        val total = _timerState.value.totalSeconds
        val completedSecs = total - _timerState.value.remainingSeconds
        val completedMins = (completedSecs / 60).coerceAtLeast(1)
        onTimerFinished()
        viewModelScope.launch {
            repository.logStudyMinutes(completedMins)
        }
    }

    // Chapter Operations
    fun updateChapter(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.updateChapter(chapter)
        }
    }

    // Session Operations
    fun addSession(
        subjectId: String,
        chapterName: String,
        title: String,
        scheduledDate: String,
        scheduledTime: String,
        durationMinutes: Int,
        sessionType: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertSession(
                StudySessionEntity(
                    subjectId = subjectId,
                    chapterName = chapterName,
                    title = title,
                    scheduledDate = scheduledDate,
                    scheduledTime = scheduledTime,
                    durationMinutes = durationMinutes,
                    sessionType = sessionType,
                    notes = notes
                )
            )
        }
    }

    fun toggleSessionComplete(session: StudySessionEntity) {
        viewModelScope.launch {
            repository.toggleSessionComplete(session)
        }
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    // Backlog Operations
    fun addBacklog(
        chapterId: Long,
        chapterName: String,
        subjectId: String,
        estimatedHours: Int,
        priority: String,
        understandingLevel: String,
        targetDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertBacklog(
                BacklogEntity(
                    chapterId = chapterId,
                    chapterName = chapterName,
                    subjectId = subjectId,
                    estimatedHours = estimatedHours,
                    priority = priority,
                    understandingLevel = understandingLevel,
                    targetDate = targetDate,
                    notes = notes
                )
            )
        }
    }

    fun toggleBacklogResolved(backlog: BacklogEntity) {
        viewModelScope.launch {
            repository.resolveBacklog(backlog, !backlog.isResolved)
        }
    }

    fun updateBacklog(backlog: BacklogEntity) {
        viewModelScope.launch {
            repository.updateBacklog(backlog)
        }
    }

    fun deleteBacklog(id: Long) {
        viewModelScope.launch {
            repository.deleteBacklog(id)
        }
    }

    // Mock Test Operations
    fun addMockTest(
        testName: String,
        testDate: String,
        totalMarks: Int,
        marksObtained: Int,
        physicsMarks: Int,
        chemistryMarks: Int,
        mathsMarks: Int,
        attemptedQuestions: Int,
        correctQuestions: Int,
        incorrectQuestions: Int,
        unattemptedQuestions: Int,
        testType: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertMockTest(
                MockTestEntity(
                    testName = testName,
                    testDate = testDate,
                    totalMarks = totalMarks,
                    marksObtained = marksObtained,
                    physicsMarks = physicsMarks,
                    chemistryMarks = chemistryMarks,
                    mathsMarks = mathsMarks,
                    attemptedQuestions = attemptedQuestions,
                    correctQuestions = correctQuestions,
                    incorrectQuestions = incorrectQuestions,
                    unattemptedQuestions = unattemptedQuestions,
                    testType = testType,
                    notes = notes
                )
            )
        }
    }

    fun deleteMockTest(test: MockTestEntity) {
        viewModelScope.launch {
            repository.deleteMockTest(test)
        }
    }

    // Mistake Notebook Operations
    fun addMistake(
        mockTestId: Long,
        testName: String,
        subjectId: String,
        chapterName: String,
        questionNumber: Int,
        mistakeType: String,
        description: String,
        correctionNotes: String
    ) {
        viewModelScope.launch {
            repository.insertMistake(
                MistakeEntity(
                    mockTestId = mockTestId,
                    testName = testName,
                    subjectId = subjectId,
                    chapterName = chapterName,
                    questionNumber = questionNumber,
                    mistakeType = mistakeType,
                    description = description,
                    correctionNotes = correctionNotes
                )
            )
        }
    }

    fun deleteMistake(mistake: MistakeEntity) {
        viewModelScope.launch {
            repository.deleteMistake(mistake)
        }
    }

    // AI Coach Interaction
    fun sendAiPrompt(userText: String) {
        val userMsg = AiMessage(sender = "user", text = userText)
        val current = _aiMessages.value.toMutableList()
        current.add(userMsg)
        _aiMessages.value = current

        viewModelScope.launch {
            delay(400L) // subtle conversational response delay
            val reply = AiStudyCoach.answerQuery(
                prompt = userText,
                chapters = chapters.value,
                backlogs = allBacklogs.value,
                mockTests = mockTests.value,
                mistakes = mistakes.value
            )
            val updated = _aiMessages.value.toMutableList()
            updated.add(reply)
            _aiMessages.value = updated
        }
    }

    // User & Account Operations
    fun loginOrSignup(name: String, email: String, avatarType: String, examName: String) {
        viewModelScope.launch {
            repository.loginOrSignup(name, email, avatarType, examName)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            repository.setTutorialCompleted(true)
        }
    }

    fun updateProfile(name: String, examName: String, avatarType: String, dailyHours: Float) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserAccountEntity()
            repository.saveUser(
                current.copy(
                    name = name,
                    examName = examName,
                    avatarType = avatarType,
                    dailyTargetHours = dailyHours
                )
            )
        }
    }

    fun resetToCleanNewUser(name: String, email: String, avatarType: String, examName: String) {
        viewModelScope.launch {
            repository.resetToNewUser(name, email, avatarType, examName)
        }
    }

    fun loadDemoSampleData() {
        viewModelScope.launch {
            repository.loadDemoSampleData()
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetToNewUser("Aspirant", "aspirant@jee.prep", "rocket", "JEE Main 2027 & Advanced")
        }
    }
}

class JeeViewModelFactory(private val repository: JeeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JeeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
