import 'dart:io';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/auth_image.dart';
import '../../widgets/common.dart';

/// Creates a property, or edits [existing] when it is given. Landlords only (the server enforces it).
class PropertyFormScreen extends StatefulWidget {
  const PropertyFormScreen({super.key, this.existing});

  final Property? existing;

  @override
  State<PropertyFormScreen> createState() => _PropertyFormScreenState();
}

class _PropertyFormScreenState extends State<PropertyFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _name;
  late final TextEditingController _address;
  late final TextEditingController _units;
  late String _type;
  late String _status;
  String? _imagePath;
  bool _busy = false;

  bool get _editing => widget.existing != null;

  @override
  void initState() {
    super.initState();
    final p = widget.existing;
    _name = TextEditingController(text: p?.name ?? '');
    _address = TextEditingController(text: p?.address ?? '');
    _units = TextEditingController(text: '${p?.unitCount ?? 1}');
    _type = p?.propertyType ?? 'APARTMENT';
    _status = p?.status ?? 'ACTIVE';
  }

  @override
  void dispose() {
    _name.dispose();
    _address.dispose();
    _units.dispose();
    super.dispose();
  }

  Future<void> _choosePhoto() async {
    final path = await pickPhoto(context);
    if (path != null && mounted) {
      setState(() => _imagePath = path);
    }
  }

  Future<void> _save() async {
    if (!(_formKey.currentState?.validate() ?? false)) {
      return;
    }
    final api = context.read<AuthController>().api;
    final navigator = Navigator.of(context);
    setState(() => _busy = true);
    final ok = await runAction(
      context,
      () => api.saveProperty(
        id: widget.existing?.id,
        name: _name.text.trim(),
        address: _address.text.trim(),
        propertyType: _type,
        unitCount: int.parse(_units.text.trim()),
        status: _status,
        imagePath: _imagePath,
      ),
      success: _editing ? 'Property updated.' : 'Property added.',
    );
    if (!mounted) {
      return;
    }
    if (ok) {
      navigator.pop(true);
    } else {
      setState(() => _busy = false);
    }
  }

  Future<void> _delete() async {
    final existing = widget.existing;
    if (existing == null) {
      return;
    }
    final confirmed = await confirmDialog(
      context,
      title: 'Delete this property?',
      message:
          'This cannot be undone. A property that already has inspections cannot be deleted.',
      confirmLabel: 'Delete',
    );
    if (!confirmed || !mounted) {
      return;
    }
    final api = context.read<AuthController>().api;
    final navigator = Navigator.of(context);
    final ok = await runAction(context, () => api.deleteProperty(existing.id),
        success: 'Property deleted.');
    if (ok) {
      // Close the form and the detail screen underneath it.
      navigator.popUntil((route) => route.isFirst);
    }
  }

  @override
  Widget build(BuildContext context) {
    final existing = widget.existing;
    final api = context.read<AuthController>().api;
    return Scaffold(
      appBar: AppBar(title: Text(_editing ? 'Edit property' : 'Add property')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                GestureDetector(
                  onTap: _choosePhoto,
                  child: AspectRatio(
                    aspectRatio: 16 / 9,
                    child: Container(
                      decoration: BoxDecoration(
                        color: AppColors.panel,
                        borderRadius: BorderRadius.circular(14),
                        border: Border.all(color: AppColors.lineSoft),
                      ),
                      clipBehavior: Clip.antiAlias,
                      child: _imagePath != null
                          ? Image.file(File(_imagePath!), fit: BoxFit.cover)
                          : (existing != null && existing.hasImage)
                              ? AuthImage(
                                  url: api.propertyImageUrl(existing.id))
                              : const Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Icon(Icons.add_a_photo_outlined,
                                        color: AppColors.dim, size: 32),
                                    SizedBox(height: 8),
                                    Text('Add a cover photo (optional)',
                                        style: AppText.muted),
                                  ],
                                ),
                    ),
                  ),
                ),
                if (_imagePath != null || (existing?.hasImage ?? false))
                  Align(
                    alignment: Alignment.centerRight,
                    child: TextButton(
                        onPressed: _choosePhoto,
                        child: const Text('Change photo')),
                  ),
                const SizedBox(height: 14),
                TextFormField(
                  controller: _name,
                  textCapitalization: TextCapitalization.words,
                  textInputAction: TextInputAction.next,
                  decoration: fieldDecoration('Property name'),
                  validator: (value) {
                    final text = (value ?? '').trim();
                    if (text.isEmpty) {
                      return 'Enter a property name';
                    }
                    return text.length > 120
                        ? 'Use 120 characters or fewer'
                        : null;
                  },
                ),
                const SizedBox(height: 14),
                TextFormField(
                  controller: _address,
                  textCapitalization: TextCapitalization.words,
                  textInputAction: TextInputAction.next,
                  decoration: fieldDecoration('Address (optional)'),
                  validator: (value) => (value ?? '').trim().length > 255
                      ? 'Use 255 characters or fewer'
                      : null,
                ),
                const SizedBox(height: 18),
                ChoiceRow(
                  label: 'Property type',
                  options: [
                    for (final c in kPropertyTypes) MapEntry(c.value, c.label)
                  ],
                  value: _type,
                  onChanged: (value) => setState(() => _type = value),
                ),
                const SizedBox(height: 18),
                TextFormField(
                  controller: _units,
                  keyboardType: TextInputType.number,
                  decoration: fieldDecoration('Number of units'),
                  validator: (value) {
                    final number = int.tryParse((value ?? '').trim());
                    if (number == null || number < 1 || number > 100000) {
                      return 'Enter a number from 1 to 100,000';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 18),
                ChoiceRow(
                  label: 'Status',
                  options: [
                    for (final c in kPropertyStatuses)
                      MapEntry(c.value, c.label)
                  ],
                  value: _status,
                  onChanged: (value) => setState(() => _status = value),
                ),
                const SizedBox(height: 26),
                PrimaryButton(
                    label: _editing ? 'Save changes' : 'Add property',
                    loading: _busy,
                    onPressed: _save),
                if (_editing) ...[
                  const SizedBox(height: 12),
                  TextButton(
                    onPressed: _delete,
                    child: const Text('Delete property',
                        style: TextStyle(color: AppColors.redBright)),
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}
