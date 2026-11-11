import '../../domain/analytics/mock_test.dart';

class AnalyticsRepositoryImpl {
  final List<MockTest> _mockTests = [];

  Future<List<MockTest>> getAllMockTests() async {
    return List.unmodifiable(_mockTests);
  }

  Future<void> addMockTest(MockTest test) async {
    _mockTests.add(test);
  }
}
