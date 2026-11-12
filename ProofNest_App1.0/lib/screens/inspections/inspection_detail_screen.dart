import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/auth_image.dart';
import '../../widgets/common.dart';
import '../../widgets/loading_builder.dart';
import '../properties/property_detail_screen.dart';
import 'inspection_form_screen.dart';
import 'photo_viewer_screen.dart';

const List<String> _suggestedRooms = [
  'Living room',
  'Kitchen',
  'Bedroom',
  'Bathroom',
  'Hallway'
];

class InspectionDetailScreen extends StatelessWidget {
  const InspectionDetailScreen({super.key, required this.inspectionId});

  final int inspectionId;

  Future<void> _addRoom(BuildContext context, {String? suggestedName}) async {
    final api = context.read<AuthController>().api;
    final input = suggestedName != null
        ? AreaInput(suggestedName, '')
        : await showAreaDialog(context, title: 'Add a room or area');
    if (input == null || !context.mounted) {
      return;
    }
    await runAction(
        context, () => api.addArea(inspectionId, input.name, input.notes),
        success: 'Room added.');
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return LoadingBuilder<InspectionDetail>(
      refreshKey: auth.dataVersion,
      frame: (body) => Scaffold(appBar: AppBar(), body: body),
      loader: () => auth.api.inspection(inspectionId),
      builder: (context, detail, reload) {
        final inspection = detail.inspection;
        return Scaffold(
          appBar: AppBar(
            title: Text(inspection.propertyName),
            actions: [
              if (detail.owner)
                IconButton(
                  tooltip: 'Edit inspection',
                  icon: const Icon(Icons.edit_outlined),
                  onPressed: () => Navigator.of(context).push(
                    MaterialPageRoute<void>(
                        builder: (_) =>
                            InspectionFormScreen(existing: inspection)),
                  ),
                ),
            ],
          ),
          body: RefreshIndicator(
            onRefresh: reload,
            child: ListView(
              physics: const AlwaysScrollableScrollPhysics(),
              padding: const EdgeInsets.fromLTRB(16, 4, 16, 40),
              children: [
                AppCard(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Expanded(
                            child: Text('${inspection.typeLabel} inspection',
                                style: AppText.heading),
                          ),
                          StatusPill(
                              label: inspection.statusLabel,
                              color: inspectionStatusColor(inspection.status)),
                        ],
                      ),
                      const SizedBox(height: 8),
                      InfoRow('Date', inspection.dateLabel),
                      InfoRow('Created by', inspection.createdByName),
                      InfoRow('Rooms', '${inspection.areaCount}'),
                      InfoRow('Photos', '${detail.evidenceCount}'),
                      if ((inspection.notes ?? '').isNotEmpty)
                        InfoRow('Notes', inspection.notes!),
                      const SizedBox(height: 6),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: TextButton(
                          onPressed: () => Navigator.of(context).push(
                            MaterialPageRoute<void>(
                              builder: (_) => PropertyDetailScreen(
                                  propertyId: inspection.propertyId),
                            ),
                          ),
                          child: const Text('View property'),
                        ),
                      ),
                      if (detail.owner) ...[
                        const Divider(height: 24),
                        ChoiceRow(
                          label: 'Status (landlord only)',
                          options: [
                            for (final c in kInspectionStatuses)
                              MapEntry(c.value, c.label)
                          ],
                          value: inspection.status,
                          onChanged: (value) {
                            if (value != inspection.status) {
                              runAction(
                                context,
                                () => auth.api
                                    .setInspectionStatus(inspectionId, value),
                                success: 'Status updated.',
                              );
                            }
                          },
                        ),
                      ],
                    ],
                  ),
                ),
                const SizedBox(height: 22),
                SectionHeader(
                  eyebrow: 'Evidence',
                  title: 'Rooms and photos',
                  trailing: TextButton.icon(
                    onPressed: () => _addRoom(context),
                    icon: const Icon(Icons.add, size: 18),
                    label: const Text('Add room'),
                  ),
                ),
                const SizedBox(height: 10),
                if (detail.areas.isEmpty) ...[
                  const AppCard(
                    child: Text(
                      'No rooms yet. Add the rooms or areas you want to document, then attach photos to each.',
                      style: AppText.muted,
                    ),
                  ),
                  const SizedBox(height: 12),
                  Wrap(
                    spacing: 8,
                    runSpacing: 8,
                    children: [
                      for (final name in _suggestedRooms)
                        ActionChip(
                          label: Text('+ $name'),
                          backgroundColor: AppColors.panel,
                          side: const BorderSide(color: AppColors.lineSoft),
                          onPressed: () =>
                              _addRoom(context, suggestedName: name),
                        ),
                    ],
                  ),
                ] else
                  for (final area in detail.areas) ...[
                    _AreaCard(inspectionId: inspectionId, area: area),
                    const SizedBox(height: 12),
                  ],
              ],
            ),
          ),
        );
      },
    );
  }
}

/// One room: notes, photo grid, and buttons to add a photo or edit the room.
class _AreaCard extends StatefulWidget {
  const _AreaCard({required this.inspectionId, required this.area});

  final int inspectionId;
  final InspectionArea area;

  @override
  State<_AreaCard> createState() => _AreaCardState();
}

class _AreaCardState extends State<_AreaCard> {
  bool _uploading = false;

  Future<void> _addPhoto() async {
    final api = context.read<AuthController>().api;
    final path = await pickPhoto(context);
    if (path == null || !mounted) {
      return;
    }
    setState(() => _uploading = true);
    await runAction(
      context,
      () => api.uploadEvidence(widget.inspectionId, widget.area.id, path),
      success: 'Photo added.',
    );
    if (mounted) {
      setState(() => _uploading = false);
    }
  }

  Future<void> _edit() async {
    final api = context.read<AuthController>().api;
    final input = await showAreaDialog(
      context,
      title: 'Edit room',
      name: widget.area.name,
      notes: widget.area.notes,
    );
    if (input == null || !mounted) {
      return;
    }
    await runAction(
      context,
      () => api.updateArea(
          widget.inspectionId, widget.area.id, input.name, input.notes),
      success: 'Room saved.',
    );
  }

  @override
  Widget build(BuildContext context) {
    final api = context.read<AuthController>().api;
    final area = widget.area;
    return AppCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(child: Text(area.name, style: AppText.heading)),
              Text('${area.evidence.length} photos', style: AppText.small),
              IconButton(
                tooltip: 'Edit room',
                visualDensity: VisualDensity.compact,
                icon: const Icon(Icons.edit_outlined, size: 20),
                onPressed: _edit,
              ),
            ],
          ),
          if ((area.notes ?? '').isNotEmpty) ...[
            Text(area.notes!, style: AppText.muted),
            const SizedBox(height: 10),
          ],
          if (area.evidence.isNotEmpty)
            GridView.count(
              crossAxisCount: 3,
              mainAxisSpacing: 8,
              crossAxisSpacing: 8,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              children: [
                for (final evidence in area.evidence)
                  GestureDetector(
                    onTap: () => Navigator.of(context).push(
                      MaterialPageRoute<void>(
                        builder: (_) => PhotoViewerScreen(
                          inspectionId: widget.inspectionId,
                          evidence: evidence,
                          roomName: area.name,
                        ),
                      ),
                    ),
                    child: AuthImage(
                      url: api.evidenceUrl(evidence.id),
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
              ],
            ),
          if (_uploading) ...[
            const SizedBox(height: 10),
            const LinearProgressIndicator(),
            const SizedBox(height: 4),
            const Text('Uploading photo…', style: AppText.small),
          ],
          const SizedBox(height: 8),
          OutlinedButton.icon(
            onPressed: _uploading ? null : _addPhoto,
            icon: const Icon(Icons.add_a_photo_outlined, size: 18),
            label: const Text('Add photo'),
          ),
        ],
      ),
    );
  }
}

class AreaInput {
  const AreaInput(this.name, this.notes);

  final String name;
  final String notes;
}

/// Name + notes dialog for adding or editing a room. Returns null when cancelled.
Future<AreaInput?> showAreaDialog(
  BuildContext context, {
  required String title,
  String? name,
  String? notes,
}) {
  final nameController = TextEditingController(text: name ?? '');
  final notesController = TextEditingController(text: notes ?? '');
  String? error;
  return showDialog<AreaInput>(
    context: context,
    builder: (dialogContext) => StatefulBuilder(
      builder: (context, setState) => AlertDialog(
        backgroundColor: AppColors.panelRaised,
        title: Text(title),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                autofocus: true,
                textCapitalization: TextCapitalization.words,
                maxLength: 100,
                decoration:
                    InputDecoration(labelText: 'Room name', errorText: error),
              ),
              TextField(
                controller: notesController,
                maxLines: 3,
                maxLength: 2000,
                decoration:
                    const InputDecoration(labelText: 'Notes (optional)'),
              ),
            ],
          ),
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.of(dialogContext).pop(),
              child: const Text('Cancel')),
          TextButton(
            onPressed: () {
              final trimmed = nameController.text.trim();
              if (trimmed.isEmpty) {
                setState(() => error = 'Enter a room name');
                return;
              }
              Navigator.of(dialogContext)
                  .pop(AreaInput(trimmed, notesController.text.trim()));
            },
            child: const Text('Save'),
          ),
        ],
      ),
    ),
  );
}
