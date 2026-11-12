import 'package:flutter/material.dart';

import '../widgets/app_drawer.dart';
import '../widgets/common.dart';

class TenantsScreen extends StatelessWidget {
  const TenantsScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Tenants')),
      drawer: AppDrawer(onOpenTab: onOpenTab),
      body: const EmptyState(
        icon: Icons.people_outline,
        title: 'No tenants to show',
        message:
            'Tenant lists need saved properties and shared accounts. Those features will be available after Firebase is connected.',
      ),
    );
  }
}
