import '../../domain/planner/study_session.dart';

class PlannerRepositoryImpl {
  final List<StudySession> _sessions = [];

  Future<List<StudySession>> getSessionsForDate(String date) async {
    return _sessions.where((s) => s.scheduledDate == date).toList();
  }

  Future<void> addSession(StudySession session) async {
    _sessions.add(session);
  }

  Future<void> deleteSession(int id) async {
    _sessions.removeWhere((s) => s.id == id);
  }
}
