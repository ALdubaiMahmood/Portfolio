import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/theme.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';
import '../../widgets/app_drawer.dart';
import '../../widgets/loading_builder.dart';
import '../comparison/comparison_picker_screen.dart';
import 'inspection_detail_screen.dart';
import 'inspection_form_screen.dart';

class InspectionsScreen extends StatefulWidget {
  const InspectionsScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  State<InspectionsScreen> createState() => _InspectionsScreenState();
}

class _InspectionsScreenState extends State<InspectionsScreen> {
  final _search = TextEditingController();
  String _query = '';
  String? _status;

  @override
  void dispose() {
    _search.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return Scaffold(
      appBar: AppBar(
        title: const Text('Inspections'),
        actions: [
          IconButton(
            tooltip: 'Compare move-in and move-out',
            icon: const Icon(Icons.compare_outlined),
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute<void>(
                  builder: (_) => const ComparisonPickerScreen()),
            ),
          ),
        ],
      ),
      drawer: AppDrawer(onOpenTab: widget.onOpenTab),
      floatingActionButton: auth.isLandlord
          ? FloatingActionButton.extended(
              backgroundColor: AppColors.red,
              foregroundColor: Colors.white,
              onPressed: () => Navigator.of(context).push(
                MaterialPageRoute<void>(
                    builder: (_) => const InspectionFormScreen()),
              ),
              icon: const Icon(Icons.add),
              label: const Text('New inspection'),
            )
          : null,
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 4, 16, 8),
            child: TextField(
              controller: _search,
              textInputAction: TextInputAction.search,
              onSubmitted: (value) => setState(() => _query = value.trim()),
              decoration: fieldDecoration(
                'Search property or type',
                suffix: _query.isEmpty
                    ? const Icon(Icons.search)
                    : IconButton(
                        tooltip: 'Clear search',
                        icon: const Icon(Icons.close),
                        onPressed: () {
                          _search.clear();
                          setState(() => _query = '');
                        },
                      ),
              ),
            ),
          ),
          SizedBox(
            height: 44,
            child: ListView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.symmetric(horizontal: 16),
              children: [
                _StatusChip(
                    label: 'All',
                    selected: _status == null,
                    onTap: () => setState(() => _status = null)),
                for (final choice in kInspectionStatuses)
                  _StatusChip(
                    label: choice.label,
                    selected: _status == choice.value,
                    onTap: () => setState(() => _status = choice.value),
                  ),
              ],
            ),
          ),
          Expanded(
            child: LoadingBuilder<List<InspectionSummary>>(
              refreshKey: '$_query|$_status|${auth.dataVersion}',
              loader: () =>
                  auth.api.inspections(query: _query, status: _status),
              builder: (context, inspections, reload) {
                if (inspections.isEmpty) {
                  return RefreshIndicator(
                    onRefresh: reload,
                    child: ListView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      children: [
                        SizedBox(
                          height: 360,
                          child: EmptyState(
                            icon: Icons.fact_check_outlined,
                            title: 'No inspections found',
                            message: auth.isLandlord
                                ? 'Create a move-in inspection to start recording the condition of a property.'
                                : 'Inspections shared with you will appear here.',
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
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 96),
                    itemCount: inspections.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 12),
                    itemBuilder: (context, index) =>
                        _InspectionCard(inspection: inspections[index]),
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

class _StatusChip extends StatelessWidget {
  const _StatusChip(
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

class _InspectionCard extends StatelessWidget {
  const _InspectionCard({required this.inspection});

  final InspectionSummary inspection;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute<void>(
            builder: (_) =>
                InspectionDetailScreen(inspectionId: inspection.id)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(inspection.propertyName,
                    style: AppText.heading,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis),
              ),
              StatusPill(
                  label: inspection.statusLabel,
                  color: inspectionStatusColor(inspection.status)),
            ],
          ),
          const SizedBox(height: 6),
          Text('${inspection.typeLabel} · ${inspection.dateLabel}',
              style: AppText.muted),
          const SizedBox(height: 4),
          Text('${inspection.areaCount} rooms · by ${inspection.createdByName}',
              style: AppText.small),
        ],
      ),
    );
  }
}
