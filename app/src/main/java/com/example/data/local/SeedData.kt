package com.example.data.local

import com.example.data.model.BacklogEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.DailyStudyLogEntity
import com.example.data.model.MistakeEntity
import com.example.data.model.MockTestEntity
import com.example.data.model.StudySessionEntity

object SeedData {

    // Clean syllabus for a new user: all chapters start at "Not Started" with 0 records
    fun getInitialChapters(): List<ChapterEntity> {
        return listOf(
            // --- PHYSICS CLASS 11 ---
            ChapterEntity(subjectId = "physics", name = "Units & Measurements", classLevel = 11, subCategory = "Mechanics", importance = "Low"),
            ChapterEntity(subjectId = "physics", name = "Kinematics 1D & 2D", classLevel = 11, subCategory = "Mechanics", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Laws of Motion & Friction", classLevel = 11, subCategory = "Mechanics", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Work, Energy & Power", classLevel = 11, subCategory = "Mechanics", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Rotational Dynamics", classLevel = 11, subCategory = "Mechanics", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Gravitation", classLevel = 11, subCategory = "Mechanics", importance = "Medium"),
            ChapterEntity(subjectId = "physics", name = "Mechanical Properties of Solids & Fluids", classLevel = 11, subCategory = "General Physics", importance = "Medium"),
            ChapterEntity(subjectId = "physics", name = "Thermodynamics & KTG", classLevel = 11, subCategory = "Thermal", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Oscillations (SHM)", classLevel = 11, subCategory = "Waves", importance = "Medium"),
            ChapterEntity(subjectId = "physics", name = "Waves & Sound", classLevel = 11, subCategory = "Waves", importance = "Medium"),

            // --- PHYSICS CLASS 12 ---
            ChapterEntity(subjectId = "physics", name = "Electrostatics & Gauss Law", classLevel = 12, subCategory = "Electromagnetism", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Current Electricity", classLevel = 12, subCategory = "Electromagnetism", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Moving Charges & Magnetism", classLevel = 12, subCategory = "Electromagnetism", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Electromagnetic Induction & AC", classLevel = 12, subCategory = "Electromagnetism", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Ray Optics & Optical Instruments", classLevel = 12, subCategory = "Optics", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Wave Optics", classLevel = 12, subCategory = "Optics", importance = "Medium"),
            ChapterEntity(subjectId = "physics", name = "Modern Physics (Photoelectric & Atom)", classLevel = 12, subCategory = "Modern Physics", importance = "High"),
            ChapterEntity(subjectId = "physics", name = "Semiconductors & Logic Gates", classLevel = 12, subCategory = "Modern Physics", importance = "Medium"),

            // --- CHEMISTRY CLASS 11 ---
            ChapterEntity(subjectId = "chemistry", name = "Mole Concept & Stoichiometry", classLevel = 11, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Atomic Structure", classLevel = 11, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Periodic Table & Periodicity", classLevel = 11, subCategory = "Inorganic", importance = "Medium"),
            ChapterEntity(subjectId = "chemistry", name = "Chemical Bonding & VSEPR", classLevel = 11, subCategory = "Inorganic", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Chemical Thermodynamics", classLevel = 11, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Chemical & Ionic Equilibrium", classLevel = 11, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "General Organic Chemistry (GOC)", classLevel = 11, subCategory = "Organic", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Hydrocarbons", classLevel = 11, subCategory = "Organic", importance = "High"),

            // --- CHEMISTRY CLASS 12 ---
            ChapterEntity(subjectId = "chemistry", name = "Solutions & Colligative Properties", classLevel = 12, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Electrochemistry", classLevel = 12, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Chemical Kinetics", classLevel = 12, subCategory = "Physical", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Coordination Compounds", classLevel = 12, subCategory = "Inorganic", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "d & f Block Elements", classLevel = 12, subCategory = "Inorganic", importance = "Medium"),
            ChapterEntity(subjectId = "chemistry", name = "Haloalkanes & Haloarenes", classLevel = 12, subCategory = "Organic", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Alcohols, Phenols & Ethers", classLevel = 12, subCategory = "Organic", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Aldehydes, Ketones & Carboxylic Acids", classLevel = 12, subCategory = "Organic", importance = "High"),
            ChapterEntity(subjectId = "chemistry", name = "Amines & Biomolecules", classLevel = 12, subCategory = "Organic", importance = "Medium"),

            // --- MATHEMATICS CLASS 11 ---
            ChapterEntity(subjectId = "mathematics", name = "Sets, Relations & Functions", classLevel = 11, subCategory = "Algebra", importance = "Medium"),
            ChapterEntity(subjectId = "mathematics", name = "Quadratic Equations", classLevel = 11, subCategory = "Algebra", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Complex Numbers", classLevel = 11, subCategory = "Algebra", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Sequences & Series", classLevel = 11, subCategory = "Algebra", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Permutations & Combinations", classLevel = 11, subCategory = "Algebra", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Binomial Theorem", classLevel = 11, subCategory = "Algebra", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Straight Lines & Circles", classLevel = 11, subCategory = "Coordinate", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Conic Sections (Parabola, Ellipse, Hyperbola)", classLevel = 11, subCategory = "Coordinate", importance = "High"),

            // --- MATHEMATICS CLASS 12 ---
            ChapterEntity(subjectId = "mathematics", name = "Matrices & Determinants", classLevel = 12, subCategory = "Algebra", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Limits, Continuity & Differentiability", classLevel = 12, subCategory = "Calculus", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Application of Derivatives (AOD)", classLevel = 12, subCategory = "Calculus", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Indefinite & Definite Integration", classLevel = 12, subCategory = "Calculus", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Differential Equations", classLevel = 12, subCategory = "Calculus", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Vectors & 3D Geometry", classLevel = 12, subCategory = "Vectors & 3D", importance = "High"),
            ChapterEntity(subjectId = "mathematics", name = "Probability", classLevel = 12, subCategory = "Probability", importance = "High")
        )
    }

    // For new users, sessions start empty
    fun getInitialSessions(): List<StudySessionEntity> = emptyList()

    // For new users, backlogs start empty
    fun getInitialBacklogs(): List<BacklogEntity> = emptyList()

    // For new users, mock tests start empty
    fun getInitialMockTests(): List<MockTestEntity> = emptyList()

    // For new users, mistakes start empty
    fun getInitialMistakes(): List<MistakeEntity> = emptyList()

    // For new users, study logs start empty
    fun getInitialLogs(): List<DailyStudyLogEntity> = emptyList()

    // Sample data loader for demo testing if needed
    fun getSampleDemoSessions(today: String): List<StudySessionEntity> {
        return listOf(
            StudySessionEntity(
                subjectId = "physics",
                chapterName = "Rotational Dynamics",
                title = "Moment of Inertia Practice",
                scheduledDate = today,
                scheduledTime = "09:00",
                durationMinutes = 90,
                sessionType = "DPP / Practice",
                isCompleted = true,
                actualDurationMinutes = 90
            ),
            StudySessionEntity(
                subjectId = "chemistry",
                chapterName = "Chemical Thermodynamics",
                title = "Enthalpy & Entropy Problems",
                scheduledDate = today,
                scheduledTime = "14:00",
                durationMinutes = 90,
                sessionType = "Theory",
                isCompleted = false
            )
        )
    }

    fun getSampleDemoBacklogs(): List<BacklogEntity> {
        return listOf(
            BacklogEntity(
                chapterId = 5,
                chapterName = "Rotational Dynamics",
                subjectId = "physics",
                estimatedHours = 8,
                priority = "Critical",
                understandingLevel = "Low"
            ),
            BacklogEntity(
                chapterId = 23,
                chapterName = "Chemical Thermodynamics",
                subjectId = "chemistry",
                estimatedHours = 6,
                priority = "High",
                understandingLevel = "Medium"
            )
        )
    }

    fun getSampleDemoMockTests(): List<MockTestEntity> {
        return listOf(
            MockTestEntity(
                testName = "All India Mock Test 1",
                testDate = "2026-11-05",
                totalMarks = 300,
                marksObtained = 194,
                physicsMarks = 68,
                chemistryMarks = 72,
                mathsMarks = 54,
                attemptedQuestions = 56,
                correctQuestions = 50,
                incorrectQuestions = 6,
                unattemptedQuestions = 19
            )
        )
    }
}
