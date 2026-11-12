import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/api_exception.dart';
import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../state/auth_controller.dart';
import '../../widgets/brand.dart';
import '../../widgets/common.dart';
import 'register_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _email = TextEditingController();
  final _password = TextEditingController();
  bool _hidePassword = true;
  bool _busy = false;

  @override
  void dispose() {
    _email.dispose();
    _password.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!(_formKey.currentState?.validate() ?? false)) {
      return;
    }
    final auth = context.read<AuthController>();
    setState(() => _busy = true);
    try {
      await auth.login(_email.text, _password.text);
      // On success the AuthGate swaps this screen for the app.
    } catch (e) {
      if (mounted) {
        showMessage(context, errorText(e), error: true);
      }
    }
    if (mounted) {
      setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(24, 24, 24, 32),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 440),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Center(child: BrandLogo(height: 56)),
                    const SizedBox(height: 36),
                    const Eyebrow('Welcome back'),
                    const SizedBox(height: 6),
                    const Text('Log in to ProofNest', style: AppText.title),
                    const SizedBox(height: 6),
                    const Text('Every detail. Every proof. One place.',
                        style: AppText.muted),
                    const SizedBox(height: 22),
                    if (auth.notice != null)
                      _Banner(text: auth.notice!, color: AppColors.statusWarn),
                    if (auth.startupError != null)
                      _Banner(
                        text: auth.startupError!,
                        color: AppColors.redBright,
                        action: TextButton(
                            onPressed: auth.bootstrap,
                            child: const Text('Retry')),
                      ),
                    TextFormField(
                      controller: _email,
                      keyboardType: TextInputType.emailAddress,
                      autocorrect: false,
                      autofillHints: const [AutofillHints.email],
                      textInputAction: TextInputAction.next,
                      decoration: fieldDecoration('Email'),
                      validator: (value) {
                        final text = (value ?? '').trim();
                        if (text.isEmpty || !text.contains('@')) {
                          return 'Enter your email address';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 14),
                    TextFormField(
                      controller: _password,
                      obscureText: _hidePassword,
                      autofillHints: const [AutofillHints.password],
                      textInputAction: TextInputAction.done,
                      onFieldSubmitted: (_) => _submit(),
                      decoration: fieldDecoration(
                        'Password',
                        suffix: IconButton(
                          tooltip:
                              _hidePassword ? 'Show password' : 'Hide password',
                          icon: Icon(_hidePassword
                              ? Icons.visibility_outlined
                              : Icons.visibility_off_outlined),
                          onPressed: () =>
                              setState(() => _hidePassword = !_hidePassword),
                        ),
                      ),
                      validator: (value) =>
                          (value ?? '').isEmpty ? 'Enter your password' : null,
                    ),
                    const SizedBox(height: 22),
                    PrimaryButton(
                        label: 'Log in', loading: _busy, onPressed: _submit),
                    const SizedBox(height: 14),
                    TextButton(
                      onPressed: () => Navigator.of(context).push(
                        MaterialPageRoute<void>(
                            builder: (_) => const RegisterScreen()),
                      ),
                      child: const Text('New to ProofNest? Create an account'),
                    ),
                    const SizedBox(height: 18),
                    const SizedBox(height: 8),
                    const Text(
                      'Accounts are saved only on this device. Shared data will be added with Firebase.',
                      textAlign: TextAlign.center,
                      style: AppText.small,
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _Banner extends StatelessWidget {
  const _Banner({required this.text, required this.color, this.action});

  final String text;
  final Color color;
  final Widget? action;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 14),
      child: Container(
        padding: const EdgeInsets.fromLTRB(14, 10, 8, 10),
        decoration: BoxDecoration(
          color: AppColors.panelRaised,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: color),
        ),
        child: Row(
          children: [
            Expanded(child: Text(text, style: AppText.body)),
            if (action != null) action!,
          ],
        ),
      ),
    );
  }
}
