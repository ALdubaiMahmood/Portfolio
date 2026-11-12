import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/api_exception.dart';
import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _name = TextEditingController();
  final _email = TextEditingController();
  final _password = TextEditingController();
  final _confirm = TextEditingController();
  String _role = 'LANDLORD';
  bool _hidePassword = true;
  bool _busy = false;

  @override
  void dispose() {
    _name.dispose();
    _email.dispose();
    _password.dispose();
    _confirm.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!(_formKey.currentState?.validate() ?? false)) {
      return;
    }
    final auth = context.read<AuthController>();
    final navigator = Navigator.of(context);
    setState(() => _busy = true);
    try {
      await auth.register(_name.text, _email.text, _password.text, _role);
      // Signed in: close this screen so the app underneath (swapped in by the AuthGate) is visible.
      navigator.popUntil((route) => route.isFirst);
      return;
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
    return Scaffold(
      appBar: AppBar(),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(24, 0, 24, 32),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 440),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    const Eyebrow('Get started'),
                    const SizedBox(height: 6),
                    const Text('Create your account', style: AppText.title),
                    const SizedBox(height: 20),
                    const Text('I am a', style: AppText.muted),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        Expanded(
                          child: _RoleCard(
                            icon: Icons.home_work_outlined,
                            title: 'Landlord',
                            subtitle: 'Manage properties and inspections',
                            selected: _role == 'LANDLORD',
                            onTap: () => setState(() => _role = 'LANDLORD'),
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: _RoleCard(
                            icon: Icons.person_outline,
                            title: 'Tenant',
                            subtitle: 'Review and add evidence',
                            selected: _role == 'TENANT',
                            onTap: () => setState(() => _role = 'TENANT'),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 18),
                    TextFormField(
                      controller: _name,
                      textCapitalization: TextCapitalization.words,
                      textInputAction: TextInputAction.next,
                      decoration: fieldDecoration('Full name'),
                      validator: (value) {
                        final text = (value ?? '').trim();
                        if (text.isEmpty) {
                          return 'Enter your full name';
                        }
                        return text.length > 100
                            ? 'Use 100 characters or fewer'
                            : null;
                      },
                    ),
                    const SizedBox(height: 14),
                    TextFormField(
                      controller: _email,
                      keyboardType: TextInputType.emailAddress,
                      autocorrect: false,
                      textInputAction: TextInputAction.next,
                      decoration: fieldDecoration('Email'),
                      validator: (value) {
                        final text = (value ?? '').trim();
                        if (text.isEmpty ||
                            !text.contains('@') ||
                            !text.contains('.')) {
                          return 'Enter a valid email address';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 14),
                    TextFormField(
                      controller: _password,
                      obscureText: _hidePassword,
                      textInputAction: TextInputAction.next,
                      decoration: fieldDecoration(
                        'Password (8 to 72 characters)',
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
                      validator: (value) {
                        final text = value ?? '';
                        if (text.length < 8 || text.length > 72) {
                          return 'Password must be 8 to 72 characters';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 14),
                    TextFormField(
                      controller: _confirm,
                      obscureText: _hidePassword,
                      textInputAction: TextInputAction.done,
                      onFieldSubmitted: (_) => _submit(),
                      decoration: fieldDecoration('Confirm password'),
                      validator: (value) => value != _password.text
                          ? 'Passwords do not match'
                          : null,
                    ),
                    const SizedBox(height: 22),
                    PrimaryButton(
                        label: 'Create account',
                        loading: _busy,
                        onPressed: _submit),
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

class _RoleCard extends StatelessWidget {
  const _RoleCard({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.selected,
    required this.onTap,
  });

  final IconData icon;
  final String title;
  final String subtitle;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(14),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: selected ? AppColors.redTint : AppColors.panel,
          borderRadius: BorderRadius.circular(14),
          border: Border.all(
              color: selected ? AppColors.red : AppColors.lineSoft,
              width: selected ? 1.5 : 1),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: selected ? AppColors.redBright : AppColors.muted),
            const SizedBox(height: 10),
            Text(title, style: AppText.heading),
            const SizedBox(height: 2),
            Text(subtitle, style: AppText.small),
          ],
        ),
      ),
    );
  }
}
