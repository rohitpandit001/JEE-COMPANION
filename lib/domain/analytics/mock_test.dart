class MockTest {
  final int id;
  final String testName;
  final String testDate;
  final int totalMarks;
  final int marksObtained;
  final int physicsMarks;
  final int chemistryMarks;
  final int mathsMarks;
  final int attemptedQuestions;
  final int correctQuestions;
  final int incorrectQuestions;
  final int unattemptedQuestions;

  const MockTest({
    required this.id,
    required this.testName,
    required this.testDate,
    required this.totalMarks,
    required this.marksObtained,
    required this.physicsMarks,
    required this.chemistryMarks,
    required this.mathsMarks,
    required this.attemptedQuestions,
    required this.correctQuestions,
    required this.incorrectQuestions,
    required this.unattemptedQuestions,
  });

  double get accuracy => attemptedQuestions > 0 ? (correctQuestions / attemptedQuestions) * 100 : 0.0;
  int get negativeMarksLost => incorrectQuestions * 1;
}
