import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'app.dart';
import 'core/secure_store.dart';
import 'state/auth_controller.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    ChangeNotifierProvider<AuthController>(
      create: (_) => AuthController(SecureStore())..bootstrap(),
      child: const ProofnestApp(),
    ),
  );
}
