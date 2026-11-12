import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../core/format.dart';
import '../core/theme.dart';
import '../core/ui_helpers.dart';
import '../state/auth_controller.dart';
import '../widgets/common.dart';
import '../widgets/app_drawer.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  Future<void> _editName(BuildContext context) async {
    final auth = context.read<AuthController>();
    final name = await promptText(
      context,
      title: 'Edit your name',
      label: 'Full name',
      initial: auth.user?.fullName ?? '',
    );
    if (name == null || !context.mounted) {
      return;
    }
    await runAction(context, () => auth.updateName(name),
        success: 'Your name was updated.');
  }

  Future<void> _signOut(BuildContext context) async {
    final auth = context.read<AuthController>();
    final confirmed = await confirmDialog(
      context,
      title: 'Sign out?',
      message: 'You will need to sign in again to see your properties.',
      confirmLabel: 'Sign out',
    );
    if (confirmed) {
      await auth.logout();
    }
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    final user = auth.user;
    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      drawer: AppDrawer(onOpenTab: onOpenTab),
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
        children: [
          AppCard(
            child: Row(
              children: [
                CircleAvatar(
                  radius: 28,
                  backgroundColor: AppColors.red,
                  child: Text(
                    initials(user?.fullName ?? ''),
                    style: const TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.w800,
                        fontSize: 18),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(user?.fullName ?? '', style: AppText.heading),
                      const SizedBox(height: 2),
                      Text(user?.email ?? '', style: AppText.muted),
                      const SizedBox(height: 6),
                      StatusPill(
                          label: user?.roleLabel ?? '',
                          color: AppColors.statusProgress),
                    ],
                  ),
                ),
                IconButton(
                  tooltip: 'Edit name',
                  icon: const Icon(Icons.edit_outlined),
                  onPressed: () => _editName(context),
                ),
              ],
            ),
          ),
          const SizedBox(height: 14),
          const AppCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Local account', style: AppText.heading),
                SizedBox(height: 8),
                Text(
                  'This account is stored on this device only. It is not a production sign-in and will not sync '
                  'with other devices. Firebase authentication can replace this later.',
                  style: AppText.muted,
                ),
              ],
            ),
          ),
          const SizedBox(height: 14),
          const AppCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('About evidence hashes', style: AppText.heading),
                SizedBox(height: 8),
                Text(
                  'Each photo gets a SHA-256 fingerprint when it is uploaded. A fingerprint can show whether the '
                  'file changed after that moment. It does not prove who took the photo, or when or where.',
                  style: AppText.muted,
                ),
              ],
            ),
          ),
          const SizedBox(height: 22),
          OutlinedButton.icon(
            onPressed: () => _signOut(context),
            icon: const Icon(Icons.logout),
            label: const Text('Sign out'),
            style: OutlinedButton.styleFrom(
              foregroundColor: AppColors.redBright,
              side: const BorderSide(color: AppColors.redDeep),
              minimumSize: const Size.fromHeight(50),
            ),
          ),
        ],
      ),
    );
  }
}
