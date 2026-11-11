class StudySession {
  final int id;
  final String subjectId;
  final String chapterName;
  final String title;
  final String scheduledDate;
  final String scheduledTime;
  final int durationMinutes;
  final String sessionType;
  final bool isCompleted;

  const StudySession({
    required this.id,
    required this.subjectId,
    required this.chapterName,
    required this.title,
    required this.scheduledDate,
    required this.scheduledTime,
    required this.durationMinutes,
    required this.sessionType,
    this.isCompleted = false,
  });
}
