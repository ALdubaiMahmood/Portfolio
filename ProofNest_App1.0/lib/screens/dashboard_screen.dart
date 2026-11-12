import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../core/format.dart';
import '../core/theme.dart';
import '../models/models.dart';
import '../state/auth_controller.dart';
import '../widgets/brand.dart';
import '../widgets/app_drawer.dart';
import '../widgets/common.dart';
import '../widgets/loading_builder.dart';
import 'comparison/comparison_picker_screen.dart';
import 'inspections/inspection_detail_screen.dart';
import 'inspections/inspection_form_screen.dart';
import 'properties/property_form_screen.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key, required this.onOpenTab});

  /// Switches the bottom-navigation tab (0 Home, 1 Properties, 2 Inspections, 3 Findings, 4 Profile).
  final ValueChanged<int> onOpenTab;

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return Scaffold(
      appBar: AppBar(
        toolbarHeight: 64,
        title: const BrandLogo(height: 34),
      ),
      drawer: AppDrawer(onOpenTab: onOpenTab),
      body: LoadingBuilder<Dashboard>(
        refreshKey: auth.dataVersion,
        loader: auth.api.dashboard,
        builder: (context, data, reload) => RefreshIndicator(
          onRefresh: reload,
          child: ListView(
            physics: const AlwaysScrollableScrollPhysics(),
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
            children: [
              const Eyebrow('Your workspace'),
              const SizedBox(height: 4),
              Text('Hello, ${firstName(auth.user?.fullName ?? '')}',
                  style: AppText.title),
              const SizedBox(height: 4),
              const Text(
                  'Live overview of the properties and inspections shared with your account.',
                  style: AppText.muted),
              const SizedBox(height: 18),
              Row(
                children: [
                  Expanded(
                    child: _Stat(
                      icon: Icons.apartment_outlined,
                      color: AppColors.redBright,
                      value: data.propertyCount,
                      label: 'Properties',
                      onTap: () => onOpenTab(1),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _Stat(
                      icon: Icons.event_note_outlined,
                      color: AppColors.statusProgress,
                      value: data.inProgressCount,
                      label: 'In progress',
                      onTap: () => onOpenTab(2),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: _Stat(
                      icon: Icons.edit_note_outlined,
                      color: AppColors.statusWarn,
                      value: data.draftCount,
                      label: 'Draft inspections',
                      onTap: () => onOpenTab(2),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _Stat(
                      icon: Icons.check_circle_outline,
                      color: AppColors.statusDone,
                      value: data.completedCount,
                      label: 'Completed',
                      onTap: () => onOpenTab(2),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              AppCard(
                onTap: () => onOpenTab(3),
                child: Row(
                  children: [
                    const Icon(Icons.flag_outlined,
                        color: AppColors.statusWarn),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        '${data.openFindingCount} open · ${data.disputedFindingCount} disputed findings',
                        style: AppText.body,
                      ),
                    ),
                    const Icon(Icons.chevron_right, color: AppColors.dim),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              SectionHeader(
                eyebrow: 'Latest records',
                title: 'Recent inspections',
                trailing: TextButton(
                    onPressed: () => onOpenTab(2),
                    child: const Text('View all')),
              ),
              const SizedBox(height: 10),
              if (data.recentInspections.isEmpty)
                const AppCard(
                  child: Text(
                      'No inspections yet. Once an inspection is created, it will appear here.',
                      style: AppText.muted),
                )
              else
                for (final inspection in data.recentInspections) ...[
                  _RecentInspection(inspection: inspection),
                  const SizedBox(height: 10),
                ],
              const SizedBox(height: 14),
              const SectionHeader(eyebrow: 'Shortcuts', title: 'Quick actions'),
              const SizedBox(height: 10),
              if (data.landlord) ...[
                _Action(
                  icon: Icons.add_home_work_outlined,
                  label: 'Add property',
                  onTap: () => Navigator.of(context).push(
                    MaterialPageRoute<void>(
                        builder: (_) => const PropertyFormScreen()),
                  ),
                ),
                const SizedBox(height: 10),
                _Action(
                  icon: Icons.post_add_outlined,
                  label: 'Create inspection',
                  onTap: () => Navigator.of(context).push(
                    MaterialPageRoute<void>(
                        builder: (_) => const InspectionFormScreen()),
                  ),
                ),
                const SizedBox(height: 10),
              ],
              _Action(
                icon: Icons.compare_outlined,
                label: 'Compare move-in and move-out',
                onTap: () => Navigator.of(context).push(
                  MaterialPageRoute<void>(
                      builder: (_) => const ComparisonPickerScreen()),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _Stat extends StatelessWidget {
  const _Stat({
    required this.icon,
    required this.color,
    required this.value,
    required this.label,
    required this.onTap,
  });

  final IconData icon;
  final Color color;
  final int value;
  final String label;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: onTap,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: color),
          const SizedBox(height: 12),
          Text('$value',
              style: const TextStyle(
                  fontSize: 30,
                  fontWeight: FontWeight.w800,
                  color: AppColors.text)),
          const SizedBox(height: 2),
          Text(label, style: AppText.muted),
        ],
      ),
    );
  }
}

class _RecentInspection extends StatelessWidget {
  const _RecentInspection({required this.inspection});

  final InspectionSummary inspection;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute<void>(
            builder: (_) =>
                InspectionDetailScreen(inspectionId: inspection.id)),
      ),
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(inspection.propertyName, style: AppText.heading),
                const SizedBox(height: 3),
                Text('${inspection.typeLabel} · ${inspection.dateLabel}',
                    style: AppText.muted),
              ],
            ),
          ),
          StatusPill(
              label: inspection.statusLabel,
              color: inspectionStatusColor(inspection.status)),
          const SizedBox(width: 4),
          const Icon(Icons.chevron_right, color: AppColors.dim),
        ],
      ),
    );
  }
}

class _Action extends StatelessWidget {
  const _Action({required this.icon, required this.label, required this.onTap});

  final IconData icon;
  final String label;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: onTap,
      child: Row(
        children: [
          Icon(icon, color: AppColors.redBright),
          const SizedBox(width: 14),
          Expanded(child: Text(label, style: AppText.body)),
          const Icon(Icons.chevron_right, color: AppColors.dim),
        ],
      ),
    );
  }
}
