import 'package:flutter/material.dart';

import '../widgets/app_drawer.dart';
import '../widgets/common.dart';

class ChatScreen extends StatelessWidget {
  const ChatScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Chat')),
      drawer: AppDrawer(onOpenTab: onOpenTab),
      body: const EmptyState(
        icon: Icons.chat_bubble_outline,
        title: 'No conversations yet',
        message:
            'Property chat will appear here when shared tenant accounts and Firebase messaging are connected.',
      ),
    );
  }
}
