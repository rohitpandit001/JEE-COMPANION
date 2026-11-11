class UserProfile {
  final String uid;
  final String displayName;
  final String email;
  final String targetExamYear;
  final double dailyTargetHours;

  const UserProfile({
    required this.uid,
    required this.displayName,
    required this.email,
    this.targetExamYear = '2027',
    this.dailyTargetHours = 6.0,
  });
}
