import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'core/theme.dart';
import 'screens/auth/login_screen.dart';
import 'screens/home_shell.dart';
import 'state/auth_controller.dart';
import 'widgets/brand.dart';

class ProofnestApp extends StatefulWidget {
  const ProofnestApp({super.key});

  @override
  State<ProofnestApp> createState() => _ProofnestAppState();
}

class _ProofnestAppState extends State<ProofnestApp> {
  final GlobalKey<NavigatorState> _navigatorKey = GlobalKey<NavigatorState>();
  late final AuthController _auth;

  @override
  void initState() {
    super.initState();
    _auth = context.read<AuthController>();
    _auth.addListener(_returnToRootWhenSignedOut);
  }

  @override
  void dispose() {
    _auth.removeListener(_returnToRootWhenSignedOut);
    super.dispose();
  }

  /// If the session ends (sign-out or expired token) while the user is deep inside the app,
  /// close every open screen so the sign-in page is what they see.
  void _returnToRootWhenSignedOut() {
    if (_auth.status == AuthStatus.signedOut) {
      _navigatorKey.currentState?.popUntil((route) => route.isFirst);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ProofNest',
      debugShowCheckedModeBanner: false,
      theme: buildAppTheme(),
      navigatorKey: _navigatorKey,
      home: const _AuthGate(),
    );
  }
}

class _AuthGate extends StatelessWidget {
  const _AuthGate();

  @override
  Widget build(BuildContext context) {
    final status =
        context.select<AuthController, AuthStatus>((auth) => auth.status);
    switch (status) {
      case AuthStatus.starting:
        return const _SplashScreen();
      case AuthStatus.signedIn:
        return const HomeShell();
      case AuthStatus.signedOut:
        return const LoginScreen();
    }
  }
}

class _SplashScreen extends StatelessWidget {
  const _SplashScreen();

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      backgroundColor: AppColors.bg,
      body: Center(child: BrandLogo(height: 64)),
    );
  }
}
