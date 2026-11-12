import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Keeps local account records and the signed-in session in the phone's encrypted storage
/// (Keychain on iOS, Keystore-backed storage on Android).
class SecureStore {
  SecureStore() : _storage = const FlutterSecureStorage();

  static const String _tokenKey = 'proofnest_token';
  static const String _userKey = 'proofnest_current_user';
  static const String _accountsKey = 'proofnest_local_accounts';

  final FlutterSecureStorage _storage;

  Future<String?> readToken() => _read(_tokenKey);
  Future<void> writeToken(String token) =>
      _storage.write(key: _tokenKey, value: token);
  Future<void> clearToken() => _delete(_tokenKey);

  Future<String?> readCurrentUser() => _read(_userKey);
  Future<void> writeCurrentUser(String user) =>
      _storage.write(key: _userKey, value: user);
  Future<void> clearCurrentUser() => _delete(_userKey);

  Future<String?> readAccounts() => _read(_accountsKey);
  Future<void> writeAccounts(String accounts) =>
      _storage.write(key: _accountsKey, value: accounts);

  // A damaged keystore (for example after restoring a phone backup) can throw on read.
  // Treat that as "nothing stored" instead of crashing the app at start-up.
  Future<String?> _read(String key) async {
    try {
      return await _storage.read(key: key);
    } catch (_) {
      await _delete(key);
      return null;
    }
  }

  Future<void> _delete(String key) async {
    try {
      await _storage.delete(key: key);
    } catch (_) {
      // Nothing more can be done.
    }
  }
}
