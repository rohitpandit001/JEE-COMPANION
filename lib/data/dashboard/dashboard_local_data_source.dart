import '../../domain/dashboard/dashboard_repository.dart';

class DashboardLocalDataSource implements DashboardRepository {
  @override
  Future<int> getTodayStudyMinutes() async {
    return 180;
  }

  @override
  Future<int> getCurrentStreak() async {
    return 5;
  }

  @override
  Future<Map<String, dynamic>> getSyllabusSummary() async {
    return {
      'overallPercentage': 48,
      'physicsPercentage': 55,
      'chemistryPercentage': 50,
      'mathsPercentage': 40,
    };
  }
}
