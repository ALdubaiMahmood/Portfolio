import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';

/// Files a finding for one room of a move-in / move-out pair. It starts as "Open".
class FindingFormScreen extends StatefulWidget {
  const FindingFormScreen({
    super.key,
    required this.propertyId,
    required this.propertyName,
    required this.moveInId,
    required this.moveOutId,
    required this.areaName,
  });

  final int propertyId;
  final String propertyName;
  final int moveInId;
  final int moveOutId;
  final String areaName;

  @override
  State<FindingFormScreen> createState() => _FindingFormScreenState();
}

class _FindingFormScreenState extends State<FindingFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _title = TextEditingController();
  final _description = TextEditingController();
  bool _busy = false;

  @override
  void dispose() {
    _title.dispose();
    _description.dispose();
    super.dispose();
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
      () => api.createFinding(
        propertyId: widget.propertyId,
        moveInId: widget.moveInId,
        moveOutId: widget.moveOutId,
        areaName: widget.areaName,
        title: _title.text.trim(),
        description: _description.text.trim(),
      ),
      success: 'Finding filed for review.',
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('File a finding')),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                AppCard(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      InfoRow('Property', widget.propertyName),
                      InfoRow('Room', widget.areaName),
                    ],
                  ),
                ),
                const SizedBox(height: 18),
                TextFormField(
                  controller: _title,
                  textCapitalization: TextCapitalization.sentences,
                  maxLength: 100,
                  decoration: fieldDecoration('Title',
                      hint: 'e.g. Scratch on kitchen counter'),
                  validator: (value) => (value ?? '').trim().isEmpty
                      ? 'Enter a short title'
                      : null,
                ),
                const SizedBox(height: 10),
                TextFormField(
                  controller: _description,
                  textCapitalization: TextCapitalization.sentences,
                  minLines: 4,
                  maxLines: 8,
                  maxLength: 2000,
                  decoration: fieldDecoration('What changed?',
                      hint: 'Describe the difference you see'),
                  validator: (value) => (value ?? '').trim().isEmpty
                      ? 'Describe the finding'
                      : null,
                ),
                const SizedBox(height: 6),
                const Text(
                  'A finding records a difference for review. The other party can dispute it, and the landlord can mark it resolved.',
                  style: AppText.small,
                ),
                const SizedBox(height: 22),
                PrimaryButton(
                    label: 'File finding', loading: _busy, onPressed: _save),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
