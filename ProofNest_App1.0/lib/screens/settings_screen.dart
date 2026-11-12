import 'package:flutter/material.dart';

import '../core/theme.dart';
import '../widgets/app_drawer.dart';
import '../widgets/common.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Settings')),
      drawer: AppDrawer(onOpenTab: onOpenTab),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          AppCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Account', style: AppText.heading),
                const SizedBox(height: 8),
                const Text(
                  'Manage your local profile. This prototype stores accounts on this device only.',
                  style: AppText.muted,
                ),
                const SizedBox(height: 12),
                Align(
                  alignment: Alignment.centerLeft,
                  child: OutlinedButton.icon(
                    onPressed: () {
                      Navigator.of(context).pop();
                      onOpenTab(4);
                    },
                    icon: const Icon(Icons.person_outline),
                    label: const Text('Open profile'),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 12),
          const AppCard(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Data and sync', style: AppText.heading),
                SizedBox(height: 8),
                Text(
                  'Cloud sync, property sharing, and chat are not connected yet. Firebase setup is required before '
                  'these features can share data between users.',
                  style: AppText.muted,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
