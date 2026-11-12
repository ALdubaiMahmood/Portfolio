import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../core/format.dart';
import '../core/theme.dart';
import '../core/ui_helpers.dart';
import '../screens/chat_screen.dart';
import '../screens/settings_screen.dart';
import '../screens/tenants_screen.dart';
import '../state/auth_controller.dart';

class AppDrawer extends StatelessWidget {
  const AppDrawer({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  Widget build(BuildContext context) {
    final user = context.watch<AuthController>().user;
    return Drawer(
      child: SafeArea(
        child: Column(
          children: [
            UserAccountsDrawerHeader(
              decoration: const BoxDecoration(color: AppColors.panelRaised),
              currentAccountPicture: CircleAvatar(
                backgroundColor: AppColors.red,
                child: Text(
                  initials(user?.fullName ?? ''),
                  style: const TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.w800,
                  ),
                ),
              ),
              accountName: Text(user?.fullName ?? 'ProofNest'),
              accountEmail: Text(user?.email ?? ''),
            ),
            ListTile(
              leading: const Icon(Icons.home_outlined),
              title: const Text('Home'),
              onTap: () {
                Navigator.of(context).pop();
                onOpenTab(0);
              },
            ),
            ListTile(
              leading: const Icon(Icons.chat_bubble_outline),
              title: const Text('Chat'),
              onTap: () => _openPage(
                context,
                ChatScreen(onOpenTab: onOpenTab),
              ),
            ),
            ListTile(
              leading: const Icon(Icons.people_outline),
              title: const Text('Tenants'),
              onTap: () => _openPage(
                context,
                TenantsScreen(onOpenTab: onOpenTab),
              ),
            ),
            ListTile(
              leading: const Icon(Icons.settings_outlined),
              title: const Text('Settings'),
              onTap: () => _openPage(
                context,
                SettingsScreen(onOpenTab: onOpenTab),
              ),
            ),
            const Spacer(),
            const Divider(height: 1),
            ListTile(
              leading: const Icon(Icons.logout, color: AppColors.redBright),
              title: const Text('Sign out'),
              onTap: () => _signOut(context),
            ),
          ],
        ),
      ),
    );
  }

  void _openPage(BuildContext context, Widget page) {
    Navigator.of(context).pop();
    Navigator.of(context).push(
      MaterialPageRoute<void>(builder: (_) => page),
    );
  }

  Future<void> _signOut(BuildContext context) async {
    final confirmed = await confirmDialog(
      context,
      title: 'Sign out?',
      message: 'You will need to sign in again to see this device account.',
      confirmLabel: 'Sign out',
    );
    if (!confirmed || !context.mounted) {
      return;
    }
    Navigator.of(context).pop();
    await context.read<AuthController>().logout();
  }
}
