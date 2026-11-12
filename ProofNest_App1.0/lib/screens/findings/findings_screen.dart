import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/format.dart';
import '../../core/theme.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';
import '../../widgets/app_drawer.dart';
import '../../widgets/loading_builder.dart';
import '../comparison/comparison_picker_screen.dart';
import 'finding_detail_screen.dart';

class FindingsScreen extends StatefulWidget {
  const FindingsScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  State<FindingsScreen> createState() => _FindingsScreenState();
}

class _FindingsScreenState extends State<FindingsScreen> {
  String? _status;

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return Scaffold(
      appBar: AppBar(title: const Text('Findings')),
      drawer: AppDrawer(onOpenTab: widget.onOpenTab),
      body: Column(
        children: [
          SizedBox(
            height: 44,
            child: ListView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              children: [
                _Chip(
                    label: 'All',
                    selected: _status == null,
                    onTap: () => setState(() => _status = null)),
                for (final choice in kFindingStatuses)
                  _Chip(
                    label: choice.label,
                    selected: _status == choice.value,
                    onTap: () => setState(() => _status = choice.value),
                  ),
              ],
            ),
          ),
          Expanded(
            child: LoadingBuilder<List<Finding>>(
              refreshKey: '$_status|${auth.dataVersion}',
              loader: () => auth.api.findings(status: _status),
              builder: (context, findings, reload) {
                if (findings.isEmpty) {
                  return RefreshIndicator(
                    onRefresh: reload,
                    child: ListView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      children: [
                        SizedBox(
                          height: 380,
                          child: EmptyState(
                            icon: Icons.flag_outlined,
                            title: 'No findings',
                            message:
                                'Findings are filed from a comparison of a move-in and a move-out inspection.',
                            action: OutlinedButton(
                              onPressed: () => Navigator.of(context).push(
                                MaterialPageRoute<void>(
                                    builder: (_) =>
                                        const ComparisonPickerScreen()),
                              ),
                              child: const Text('Open a comparison'),
                            ),
                          ),
                        ),
                      ],
                    ),
                  );
                }
                return RefreshIndicator(
                  onRefresh: reload,
                  child: ListView.separated(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
                    itemCount: findings.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 12),
                    itemBuilder: (context, index) =>
                        _FindingCard(finding: findings[index]),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

class _Chip extends StatelessWidget {
  const _Chip(
      {required this.label, required this.selected, required this.onTap});

  final String label;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: ChoiceChip(
        label: Text(label),
        selected: selected,
        onSelected: (_) => onTap(),
        selectedColor: AppColors.redTint,
        backgroundColor: AppColors.panel,
        side: BorderSide(color: selected ? AppColors.red : AppColors.lineSoft),
        labelStyle:
            TextStyle(color: selected ? AppColors.text : AppColors.muted),
        showCheckmark: false,
      ),
    );
  }
}

class _FindingCard extends StatelessWidget {
  const _FindingCard({required this.finding});

  final Finding finding;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute<void>(
            builder: (_) => FindingDetailScreen(finding: finding)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                  child: Text(finding.title,
                      style: AppText.heading,
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis)),
              const SizedBox(width: 8),
              StatusPill(
                  label: finding.statusLabel,
                  color: findingStatusColor(finding.status)),
            ],
          ),
          const SizedBox(height: 6),
          Text('${finding.propertyName} · ${finding.areaName}',
              style: AppText.muted),
          const SizedBox(height: 4),
          Text(
              'Filed by ${finding.createdByName} · ${formatDate(finding.createdAt?.toLocal())}',
              style: AppText.small),
        ],
      ),
    );
  }
}
