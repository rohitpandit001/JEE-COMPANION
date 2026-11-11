import 'package:flutter_riverpod/flutter_riverpod.dart';

// Dashboard
import '../../domain/dashboard/dashboard_repository.dart';
import '../../data/dashboard/dashboard_local_data_source.dart';

// Planner
import '../../domain/planner/study_session.dart';
import '../../data/planner/planner_repository_impl.dart';

// Syllabus
import '../../domain/syllabus/chapter.dart';
import '../../data/syllabus/syllabus_repository_impl.dart';

// Analytics
import '../../domain/analytics/mock_test.dart';
import '../../data/analytics/analytics_repository_impl.dart';

// Auth
import '../../domain/auth/user_model.dart';
import '../../data/auth/auth_repository_impl.dart';

// --- Repository Layer Providers ---

final dashboardRepositoryProvider = Provider<DashboardRepository>((ref) {
  return DashboardLocalDataSource();
});

final plannerRepositoryProvider = Provider<PlannerRepositoryImpl>((ref) {
  return PlannerRepositoryImpl();
});

final syllabusRepositoryProvider = Provider<SyllabusRepositoryImpl>((ref) {
  return SyllabusRepositoryImpl();
});

final analyticsRepositoryProvider = Provider<AnalyticsRepositoryImpl>((ref) {
  return AnalyticsRepositoryImpl();
});

final authRepositoryProvider = Provider<AuthRepositoryImpl>((ref) {
  return AuthRepositoryImpl();
});

// --- Feature State Providers ---

final todayStudyMinutesProvider = FutureProvider<int>((ref) async {
  final repository = ref.watch(dashboardRepositoryProvider);
  return repository.getTodayStudyMinutes();
});

final currentStreakProvider = FutureProvider<int>((ref) async {
  final repository = ref.watch(dashboardRepositoryProvider);
  return repository.getCurrentStreak();
});

final syllabusSummaryProvider = FutureProvider<Map<String, dynamic>>((ref) async {
  final repository = ref.watch(dashboardRepositoryProvider);
  return repository.getSyllabusSummary();
});

final allChaptersProvider = FutureProvider<List<Chapter>>((ref) async {
  final repository = ref.watch(syllabusRepositoryProvider);
  return repository.getAllChapters();
});

final allMockTestsProvider = FutureProvider<List<MockTest>>((ref) async {
  final repository = ref.watch(analyticsRepositoryProvider);
  return repository.getAllMockTests();
});

final currentUserProvider = FutureProvider<UserProfile?>((ref) async {
  final repository = ref.watch(authRepositoryProvider);
  return repository.getCurrentUser();
});
