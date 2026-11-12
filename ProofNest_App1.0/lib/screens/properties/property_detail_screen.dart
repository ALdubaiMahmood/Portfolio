import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/auth_image.dart';
import '../../widgets/common.dart';
import '../../widgets/loading_builder.dart';
import '../comparison/comparison_picker_screen.dart';
import '../inspections/inspection_detail_screen.dart';
import '../inspections/inspection_form_screen.dart';
import 'property_form_screen.dart';

class _PropertyPage {
  const _PropertyPage(this.property, this.inspections);

  final Property property;
  final List<InspectionSummary> inspections;
}

class PropertyDetailScreen extends StatelessWidget {
  const PropertyDetailScreen({super.key, required this.propertyId});

  final int propertyId;

  Future<_PropertyPage> _load(AuthController auth) async {
    final results = await Future.wait<Object>([
      auth.api.property(propertyId),
      auth.api.propertyInspections(propertyId),
    ]);
    return _PropertyPage(
        results[0] as Property, results[1] as List<InspectionSummary>);
  }

  Future<void> _addTenant(BuildContext context) async {
    final api = context.read<AuthController>().api;
    final email = await promptText(
      context,
      title: 'Add a tenant',
      label: 'Tenant email',
      confirmLabel: 'Add',
      keyboard: TextInputType.emailAddress,
    );
    if (email == null || email.isEmpty || !context.mounted) {
      return;
    }
    await runAction(context, () => api.addTenant(propertyId, email),
        success: 'Tenant added to the property.');
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return LoadingBuilder<_PropertyPage>(
      refreshKey: auth.dataVersion,
      frame: (body) => Scaffold(appBar: AppBar(), body: body),
      loader: () => _load(auth),
      builder: (context, page, reload) {
        final property = page.property;
        final hasMoveIn = page.inspections.any((i) => i.isMoveIn);
        final hasMoveOut = page.inspections.any((i) => i.isMoveOut);
        return Scaffold(
          appBar: AppBar(
            title: Text(property.name),
            actions: [
              if (property.owner)
                IconButton(
                  tooltip: 'Edit property',
                  icon: const Icon(Icons.edit_outlined),
                  onPressed: () => Navigator.of(context).push(
                    MaterialPageRoute<void>(
                        builder: (_) => PropertyFormScreen(existing: property)),
                  ),
                ),
            ],
          ),
          body: RefreshIndicator(
            onRefresh: reload,
            child: ListView(
              physics: const AlwaysScrollableScrollPhysics(),
              padding: const EdgeInsets.fromLTRB(16, 4, 16, 32),
              children: [
                if (property.hasImage) ...[
                  AspectRatio(
                    aspectRatio: 16 / 9,
                    child: AuthImage(
                      url: auth.api.propertyImageUrl(property.id),
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),
                  const SizedBox(height: 14),
                ],
                AppCard(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          const Expanded(
                              child: Text('Details', style: AppText.heading)),
                          StatusPill(
                              label: property.statusLabel,
                              color: propertyStatusColor(property.status)),
                        ],
                      ),
                      const SizedBox(height: 8),
                      InfoRow(
                          'Address',
                          (property.address ?? '').isEmpty
                              ? 'Not provided'
                              : property.address!),
                      InfoRow('Type', property.propertyTypeLabel),
                      InfoRow('Units', '${property.unitCount}'),
                      InfoRow('Owner', property.ownerName),
                    ],
                  ),
                ),
                if (property.owner) ...[
                  const SizedBox(height: 14),
                  AppCard(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          children: [
                            const Expanded(
                                child: Text('Tenants with access',
                                    style: AppText.heading)),
                            TextButton.icon(
                              onPressed: () => _addTenant(context),
                              icon: const Icon(Icons.person_add_alt_1_outlined,
                                  size: 18),
                              label: const Text('Add'),
                            ),
                          ],
                        ),
                        if (property.tenants.isEmpty)
                          const Text(
                            'No tenants yet. A tenant must already have a ProofNest account; add them by email.',
                            style: AppText.muted,
                          )
                        else
                          for (final tenant in property.tenants)
                            ListTile(
                              contentPadding: EdgeInsets.zero,
                              dense: true,
                              leading: const Icon(Icons.person_outline,
                                  color: AppColors.muted),
                              title: Text(tenant.fullName),
                              subtitle:
                                  Text(tenant.email, style: AppText.small),
                            ),
                      ],
                    ),
                  ),
                ],
                const SizedBox(height: 14),
                AppCard(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          const Expanded(
                              child:
                                  Text('Inspections', style: AppText.heading)),
                          if (property.owner)
                            TextButton.icon(
                              onPressed: () => Navigator.of(context).push(
                                MaterialPageRoute<void>(
                                  builder: (_) => InspectionFormScreen(
                                      propertyId: property.id),
                                ),
                              ),
                              icon: const Icon(Icons.add, size: 18),
                              label: const Text('New'),
                            ),
                        ],
                      ),
                      if (page.inspections.isEmpty)
                        const Text(
                            'No inspections recorded for this property yet.',
                            style: AppText.muted)
                      else
                        for (final inspection in page.inspections)
                          ListTile(
                            contentPadding: EdgeInsets.zero,
                            title: Text(
                                '${inspection.typeLabel} · ${inspection.dateLabel}'),
                            subtitle: Text('${inspection.areaCount} rooms',
                                style: AppText.small),
                            trailing: StatusPill(
                              label: inspection.statusLabel,
                              color: inspectionStatusColor(inspection.status),
                            ),
                            onTap: () => Navigator.of(context).push(
                              MaterialPageRoute<void>(
                                builder: (_) => InspectionDetailScreen(
                                    inspectionId: inspection.id),
                              ),
                            ),
                          ),
                    ],
                  ),
                ),
                const SizedBox(height: 14),
                OutlinedButton.icon(
                  onPressed: (hasMoveIn && hasMoveOut)
                      ? () => Navigator.of(context).push(
                            MaterialPageRoute<void>(
                              builder: (_) => ComparisonPickerScreen(
                                  initialPropertyId: property.id),
                            ),
                          )
                      : null,
                  icon: const Icon(Icons.compare_outlined),
                  label: const Text('Compare move-in and move-out'),
                  style: OutlinedButton.styleFrom(
                      minimumSize: const Size.fromHeight(50)),
                ),
                if (!(hasMoveIn && hasMoveOut))
                  const Padding(
                    padding: EdgeInsets.only(top: 6),
                    child: Text(
                        'Comparison needs one move-in and one move-out inspection.',
                        style: AppText.small),
                  ),
              ],
            ),
          ),
        );
      },
    );
  }
}
