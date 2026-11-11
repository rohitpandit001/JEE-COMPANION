import '../../domain/auth/user_model.dart';

class AuthRepositoryImpl {
  UserProfile? _currentUser;

  Future<UserProfile?> getCurrentUser() async {
    return _currentUser;
  }

  Future<void> signInWithGoogle() async {
    _currentUser = const UserProfile(
      uid: 'jee_aspirant_01',
      displayName: 'JEE Aspirant',
      email: 'aspirant@jeeprep.in',
    );
  }

  Future<void> signOut() async {
    _currentUser = null;
  }
}
