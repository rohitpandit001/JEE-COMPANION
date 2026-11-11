package com.example

import com.example.data.model.BacklogEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.MockTestEntity
import com.example.domain.algorithm.BacklogPrioritizer
import com.example.domain.algorithm.MockTestAnalytics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testBacklogPriorityCalculation() {
    val chapter = ChapterEntity(
      id = 1,
      subjectId = "physics",
      name = "Rotational Dynamics",
      classLevel = 11,
      subCategory = "Mechanics",
      importance = "High",
      status = "Learning"
    )

    val backlog = BacklogEntity(
      chapterId = 1,
      chapterName = "Rotational Dynamics",
      subjectId = "physics",
      estimatedHours = 10,
      priority = "Critical",
      understandingLevel = "Low"
    )

    val score = BacklogPrioritizer.computePriorityScore(backlog, chapter)
    assertTrue("High weightage and low understanding should have a high priority score", score > 15f)
  }

  @Test
  fun testMockTestSummaryComputation() {
    val test = MockTestEntity(
      testName = "Full Test #1",
      testDate = "2026-11-10",
      totalMarks = 300,
      marksObtained = 210,
      physicsMarks = 75,
      chemistryMarks = 80,
      mathsMarks = 55,
      attemptedQuestions = 60,
      correctQuestions = 54,
      incorrectQuestions = 6,
      unattemptedQuestions = 15
    )

    val summary = MockTestAnalytics.computeTestSummary(test)
    assertEquals(90f, summary.accuracyPct, 0.1f)
    assertEquals(6, summary.negativeMarksLost)
    assertEquals(70f, summary.overallPercentage, 0.1f)
  }

  @Test
  fun testUserAccountDefaults() {
    val defaultUser = com.example.data.model.UserAccountEntity()
    assertEquals("primary_user", defaultUser.id)
    assertEquals(false, defaultUser.isLoggedIn)
    assertEquals(false, defaultUser.isTutorialCompleted)

    val newUserSignup = defaultUser.copy(
      name = "Aman Sharma",
      email = "aman@jeeprep.in",
      examName = "NEET & JEE Main 2027",
      isLoggedIn = true,
      isTutorialCompleted = false
    )
    assertTrue(newUserSignup.isLoggedIn)
    assertEquals(false, newUserSignup.isTutorialCompleted)
    assertEquals("NEET & JEE Main 2027", newUserSignup.examName)
  }
}
