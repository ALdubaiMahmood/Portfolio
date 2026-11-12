import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:proofnest/core/secure_store.dart';
import 'package:proofnest/core/theme.dart';
import 'package:proofnest/screens/auth/login_screen.dart';
import 'package:proofnest/screens/home_shell.dart';
import 'package:proofnest/state/auth_controller.dart';

void main() {
  testWidgets('login form validates email and password', (tester) async {
    await tester.pumpWidget(
      ChangeNotifierProvider<AuthController>(
        create: (_) => AuthController(SecureStore()),
        child: MaterialApp(theme: buildAppTheme(), home: const LoginScreen()),
      ),
    );

    expect(find.text('Log in to ProofNest'), findsOneWidget);

    await tester.ensureVisible(find.text('Log in'));
    await tester.tap(find.text('Log in'));
    await tester.pump();

    expect(find.text('Enter your email address'), findsOneWidget);
    expect(find.text('Enter your password'), findsOneWidget);
  });

  testWidgets('drawer opens chat, tenants, settings, and sign out destinations',
      (tester) async {
    await tester.pumpWidget(
      ChangeNotifierProvider<AuthController>(
        create: (_) => AuthController(SecureStore()),
        child: MaterialApp(theme: buildAppTheme(), home: const HomeShell()),
      ),
    );

    await tester.tap(find.byTooltip('Open navigation menu'));
    await tester.pumpAndSettle();

    expect(find.text('Home'), findsNWidgets(2));
    expect(find.text('Chat'), findsOneWidget);
    expect(find.text('Tenants'), findsOneWidget);
    expect(find.text('Settings'), findsOneWidget);
    expect(find.text('Sign out'), findsOneWidget);

    await tester.tap(find.text('Chat'));
    await tester.pumpAndSettle();
    expect(find.text('No conversations yet'), findsOneWidget);
  });
}
