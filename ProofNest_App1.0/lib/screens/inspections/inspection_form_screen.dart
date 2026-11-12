import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/api_exception.dart';
import '../../core/format.dart';
import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';
import 'inspection_detail_screen.dart';

/// Creates an inspection (for [propertyId], or a property chosen on the form), or edits [existing].
class InspectionFormScreen extends StatefulWidget {
  const InspectionFormScreen({super.key, this.propertyId, this.existing});

  final int? propertyId;
  final InspectionSummary? existing;

  @override
  State<InspectionFormScreen> createState() => _InspectionFormScreenState();
}

class _InspectionFormScreenState extends State<InspectionFormScreen> {
  late final TextEditingController _notes;
  late String _type;
  late DateTime _date;
  int? _propertyId;
  List<Property>? _properties;
  String? _loadError;
  bool _busy = false;

  bool get _editing => widget.existing != null;

  @override
  void initState() {
    super.initState();
    final e = widget.existing;
    _notes = TextEditingController(text: e?.notes ?? '');
    _type = e?.type ?? 'MOVE_IN';
    _date = e?.date ?? DateTime.now();
    _propertyId = widget.propertyId;
    if (!_editing && _propertyId == null) {
      _loadProperties();
    }
  }

  @override
  void dispose() {
    _notes.dispose();
    super.dispose();
  }

  Future<void> _loadProperties() async {
    try {
      final list = await context.read<AuthController>().api.properties();
      if (!mounted) {
        return;
      }
      setState(() {
        _properties = list.where((p) => p.owner).toList();
        _loadError = null;
      });
    } catch (e) {
      if (mounted) {
        setState(() => _loadError = errorText(e));
      }
    }
  }

  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _date,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );
    if (picked != null && mounted) {
      setState(() => _date = picked);
    }
  }

  Future<void> _save() async {
    if (_propertyId == null && !_editing) {
      showMessage(context, 'Choose a property first.', error: true);
      return;
    }
    final api = context.read<AuthController>().api;
    final navigator = Navigator.of(context);
    setState(() => _busy = true);
    int? createdId;
    final ok = await runAction(
      context,
      () async {
        final existing = widget.existing;
        if (existing != null) {
          await api.updateInspection(
            existing.id,
            type: _type,
            inspectionDate: dayToIso(_date),
            notes: _notes.text.trim(),
          );
        } else {
          final created = await api.createInspection(
            propertyId: _propertyId!,
            type: _type,
            inspectionDate: dayToIso(_date),
            notes: _notes.text.trim(),
          );
          createdId = created.inspection.id;
        }
      },
      success: _editing
          ? 'Inspection updated.'
          : 'Inspection created. Add rooms and photos next.',
    );
    if (!mounted) {
      return;
    }
    if (!ok) {
      setState(() => _busy = false);
      return;
    }
    if (createdId != null) {
      final id = createdId!;
      navigator.pushReplacement(
        MaterialPageRoute<void>(
            builder: (_) => InspectionDetailScreen(inspectionId: id)),
      );
    } else {
      navigator.pop(true);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar:
          AppBar(title: Text(_editing ? 'Edit inspection' : 'New inspection')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              if (!_editing && widget.propertyId == null) ...[
                if (_loadError != null)
                  Text(_loadError!,
                      style: const TextStyle(color: AppColors.redBright))
                else if (_properties == null)
                  const Center(
                      child: Padding(
                          padding: EdgeInsets.all(12),
                          child: CircularProgressIndicator()))
                else if (_properties!.isEmpty)
                  const Text(
                      'Add a property first, then create an inspection for it.',
                      style: AppText.muted)
                else
                  ChoiceRow(
                    label: 'Property',
                    options: [
                      for (final p in _properties!) MapEntry('${p.id}', p.name)
                    ],
                    value: _propertyId == null ? null : '$_propertyId',
                    onChanged: (value) =>
                        setState(() => _propertyId = int.parse(value)),
                  ),
                const SizedBox(height: 18),
              ],
              ChoiceRow(
                label: 'Inspection type',
                options: [
                  for (final c in kInspectionTypes) MapEntry(c.value, c.label)
                ],
                value: _type,
                onChanged: (value) => setState(() => _type = value),
              ),
              const SizedBox(height: 18),
              const Text('Inspection date', style: AppText.muted),
              const SizedBox(height: 8),
              OutlinedButton.icon(
                onPressed: _pickDate,
                icon: const Icon(Icons.event_outlined),
                label: Text(formatDate(_date)),
                style: OutlinedButton.styleFrom(
                    minimumSize: const Size.fromHeight(50)),
              ),
              const SizedBox(height: 18),
              TextField(
                controller: _notes,
                maxLines: 4,
                maxLength: 1000,
                decoration: fieldDecoration('General notes (optional)'),
              ),
              const SizedBox(height: 14),
              PrimaryButton(
                label: _editing ? 'Save changes' : 'Create inspection',
                loading: _busy,
                onPressed: _save,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
