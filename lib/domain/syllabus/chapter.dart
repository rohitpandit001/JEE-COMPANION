class Chapter {
  final int id;
  final String subjectId;
  final String name;
  final int classLevel;
  final String subCategory;
  final String importance;
  final String status;
  final int lectureCompletedPct;
  final int conceptRating;
  final int dppSolvedPct;
  final int pyqSolvedPct;
  final int revisionCount;

  const Chapter({
    required this.id,
    required this.subjectId,
    required this.name,
    required this.classLevel,
    required this.subCategory,
    required this.importance,
    required this.status,
    this.lectureCompletedPct = 0,
    this.conceptRating = 3,
    this.dppSolvedPct = 0,
    this.pyqSolvedPct = 0,
    this.revisionCount = 0,
  });
}
