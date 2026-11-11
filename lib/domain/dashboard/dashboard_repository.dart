abstract class DashboardRepository {
  Future<int> getTodayStudyMinutes();
  Future<int> getCurrentStreak();
  Future<Map<String, dynamic>> getSyllabusSummary();
}
