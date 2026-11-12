import 'dart:convert';
import 'dart:math';

import 'package:crypto/crypto.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/painting.dart';

import '../core/api_exception.dart';
import '../core/secure_store.dart';
import '../data/proofnest_api.dart';
import '../models/models.dart';

enum AuthStatus { starting, signedOut, signedIn }

/// Local-only account/session state. Replace this with Firebase Authentication before release.
class AuthController extends ChangeNotifier {
  AuthController(this._store) {
    api = ProofnestApi(isLandlord: () => isLandlord);
  }

  final SecureStore _store;
  late final ProofnestApi api;

  AuthStatus status = AuthStatus.starting;
  AppUser? user;
  String? notice;
  String? startupError;

  int dataVersion = 0;
  bool get isLandlord => user?.isLandlord ?? false;

  void markDataChanged() {
    dataVersion++;
    notifyListeners();
  }

  Future<void> bootstrap() async {
    status = AuthStatus.starting;
    startupError = null;
    notifyListeners();
    try {
      final token = await _store.readToken();
      final savedUser = await _store.readCurrentUser();
      if (token == null || savedUser == null) {
        status = AuthStatus.signedOut;
      } else {
        user = AppUser.fromJson(jsonDecode(savedUser) as Map<String, dynamic>);
        status = AuthStatus.signedIn;
      }
    } catch (error) {
      startupError = 'Could not restore your local session: $error';
      status = AuthStatus.signedOut;
    }
    notifyListeners();
  }

  Future<void> login(String email, String password) async {
    final normalizedEmail = email.trim().toLowerCase();
    final accounts = await _readAccounts();
    Map<String, dynamic>? account;
    for (final candidate in accounts) {
      if (candidate['email'] == normalizedEmail) {
        account = candidate;
        break;
      }
    }
    if (account == null) {
      throw ApiException(
          'No local account was found for this email. Create an account on this device first.');
    }
    final hash = await _passwordHash(password, account['salt'] as String);
    if (hash != account['passwordHash']) {
      throw ApiException('Invalid email or password.');
    }
    final appUser = _userFromAccount(account);
    await _startSession(appUser);
  }

  Future<void> register(
      String fullName, String email, String password, String role) async {
    final normalizedEmail = email.trim().toLowerCase();
    final accounts = await _readAccounts();
    if (accounts.any((account) => account['email'] == normalizedEmail)) {
      throw ApiException(
          'An account with this email already exists on this device.');
    }

    final salt = _randomToken();
    final appUser = AppUser(
      id: accounts.fold<int>(
              0, (maxId, account) => max(maxId, account['id'] as int)) +
          1,
      fullName: fullName.trim(),
      email: normalizedEmail,
      role: role,
    );
    accounts.add({
      'id': appUser.id,
      'fullName': appUser.fullName,
      'email': appUser.email,
      'role': appUser.role,
      'salt': salt,
      'passwordHash': await _passwordHash(password, salt),
    });
    await _writeAccounts(accounts);
    await _startSession(appUser);
  }

  Future<void> logout() async {
    await _store.clearToken();
    await _store.clearCurrentUser();
    user = null;
    status = AuthStatus.signedOut;
    notice = null;
    _clearImageCache();
    notifyListeners();
  }

  Future<void> updateName(String fullName) async {
    final current = user;
    if (current == null) {
      throw ApiException('Sign in before updating your profile.');
    }
    final updated = AppUser(
      id: current.id,
      fullName: fullName.trim(),
      email: current.email,
      role: current.role,
    );
    final accounts = await _readAccounts();
    final index = accounts.indexWhere((account) => account['id'] == current.id);
    if (index < 0) {
      throw ApiException(
          'This local account is unavailable. Please register again.');
    }
    accounts[index]['fullName'] = updated.fullName;
    await _writeAccounts(accounts);
    await _startSession(updated);
    dataVersion++;
  }

  Future<void> _startSession(AppUser appUser) async {
    user = appUser;
    notice = null;
    startupError = null;
    await _store.writeToken(_randomToken());
    await _store.writeCurrentUser(jsonEncode(_userJson(appUser)));
    status = AuthStatus.signedIn;
    dataVersion++;
    notifyListeners();
  }

  Future<List<Map<String, dynamic>>> _readAccounts() async {
    final serialized = await _store.readAccounts();
    if (serialized == null || serialized.isEmpty) {
      return [];
    }
    final decoded = jsonDecode(serialized);
    if (decoded is! List) {
      throw const FormatException('Stored local account data is invalid.');
    }
    return decoded.map((entry) {
      if (entry is! Map<String, dynamic>) {
        throw const FormatException('Stored local account data is invalid.');
      }
      return entry;
    }).toList();
  }

  Future<void> _writeAccounts(List<Map<String, dynamic>> accounts) =>
      _store.writeAccounts(jsonEncode(accounts));

  AppUser _userFromAccount(Map<String, dynamic> account) => AppUser(
        id: account['id'] as int,
        fullName: account['fullName'] as String,
        email: account['email'] as String,
        role: account['role'] as String,
      );

  Map<String, dynamic> _userJson(AppUser appUser) => {
        'id': appUser.id,
        'fullName': appUser.fullName,
        'email': appUser.email,
        'role': appUser.role,
      };

  Future<String> _passwordHash(String password, String salt) async {
    final hmac = Hmac(sha256, utf8.encode(password));
    var block = hmac.convert([...utf8.encode(salt), 0, 0, 0, 1]).bytes;
    final result = List<int>.from(block);
    for (var round = 1; round < 60000; round++) {
      block = hmac.convert(block).bytes;
      for (var index = 0; index < result.length; index++) {
        result[index] ^= block[index];
      }
      if (round % 2000 == 0) {
        await Future<void>.delayed(Duration.zero);
      }
    }
    return base64Url.encode(result);
  }

  String _randomToken() {
    final random = Random.secure();
    final bytes = List<int>.generate(32, (_) => random.nextInt(256));
    return base64Url.encode(bytes).replaceAll('=', '');
  }

  void _clearImageCache() {
    PaintingBinding.instance.imageCache.clear();
    PaintingBinding.instance.imageCache.clearLiveImages();
  }
}
