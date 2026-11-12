import 'package:flutter_test/flutter_test.dart';
import 'package:proofnest/core/api_exception.dart';
import 'package:proofnest/core/secure_store.dart';
import 'package:proofnest/state/auth_controller.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  test('local account can register, sign out, and sign in again', () async {
    final store = _MemorySecureStore();
    final auth = AuthController(store);

    await auth.register(
        'Sam Taylor', 'Sam@example.com', 'strong-password', 'LANDLORD');
    expect(auth.status, AuthStatus.signedIn);
    expect(auth.user?.email, 'sam@example.com');
    expect(auth.isLandlord, isTrue);

    await auth.logout();
    expect(auth.status, AuthStatus.signedOut);

    await auth.login('SAM@example.com', 'strong-password');
    expect(auth.status, AuthStatus.signedIn);
    expect(auth.user?.fullName, 'Sam Taylor');
  });

  test('local account rejects duplicate registration and incorrect passwords',
      () async {
    final auth = AuthController(_MemorySecureStore());
    await auth.register(
        'Sam Taylor', 'sam@example.com', 'strong-password', 'TENANT');
    await auth.logout();

    await expectLater(
      auth.register(
          'Another User', 'SAM@example.com', 'different-password', 'LANDLORD'),
      throwsA(isA<ApiException>()),
    );
    await expectLater(
      auth.login('sam@example.com', 'incorrect-password'),
      throwsA(isA<ApiException>()),
    );
    expect(auth.status, AuthStatus.signedOut);
  });
}

class _MemorySecureStore extends SecureStore {
  String? _token;
  String? _user;
  String? _accounts;

  @override
  Future<String?> readToken() async => _token;

  @override
  Future<void> writeToken(String token) async {
    _token = token;
  }

  @override
  Future<void> clearToken() async {
    _token = null;
  }

  @override
  Future<String?> readCurrentUser() async => _user;

  @override
  Future<void> writeCurrentUser(String user) async {
    _user = user;
  }

  @override
  Future<void> clearCurrentUser() async {
    _user = null;
  }

  @override
  Future<String?> readAccounts() async => _accounts;

  @override
  Future<void> writeAccounts(String accounts) async {
    _accounts = accounts;
  }
}
