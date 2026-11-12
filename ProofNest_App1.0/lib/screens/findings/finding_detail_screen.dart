import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/format.dart';
import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';

/// One finding. "Dispute" and "Resolve" are shown only when the server reports the signed-in user
/// may use them (tenant who did not file it / the property's landlord).
class FindingDetailScreen extends StatefulWidget {
  const FindingDetailScreen({super.key, required this.finding});

  final Finding finding;

  @override
  State<FindingDetailScreen> createState() => _FindingDetailScreenState();
}

class _FindingDetailScreenState extends State<FindingDetailScreen> {
  late Finding _finding;
  bool _busy = false;

  @override
  void initState() {
    super.initState();
    _finding = widget.finding;
  }

  Future<void> _setStatus(String status, String confirmTitle,
      String confirmMessage, String confirmLabel) async {
    final confirmed = await confirmDialog(
      context,
      title: confirmTitle,
      message: confirmMessage,
      confirmLabel: confirmLabel,
    );
    if (!confirmed || !mounted) {
      return;
    }
    final api = context.read<AuthController>().api;
    Finding? updated;
    setState(() => _busy = true);
    await runAction(
      context,
      () async {
        updated = await api.setFindingStatus(_finding.id, status);
      },
      success: 'Finding updated.',
    );
    if (!mounted) {
      return;
    }
    setState(() {
      _busy = false;
      final latest = updated;
      if (latest != null) {
        _finding = latest;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final finding = _finding;
    return Scaffold(
      appBar: AppBar(title: const Text('Finding')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 4, 16, 32),
          children: [
            AppCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                          child: Text(finding.title,
                              style: AppText.title.copyWith(fontSize: 21))),
                      const SizedBox(width: 8),
                      StatusPill(
                          label: finding.statusLabel,
                          color: findingStatusColor(finding.status)),
                    ],
                  ),
                  const SizedBox(height: 14),
                  InfoRow('Property', finding.propertyName),
                  InfoRow('Room', finding.areaName),
                  InfoRow('Filed by', finding.createdByName),
                  InfoRow('Filed on', formatDateTime(finding.createdAt)),
                ],
              ),
            ),
            const SizedBox(height: 14),
            AppCard(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('What changed', style: AppText.heading),
                  const SizedBox(height: 8),
                  Text(finding.description, style: AppText.body),
                ],
              ),
            ),
            const SizedBox(height: 18),
            if (finding.canDispute)
              PrimaryButton(
                label: 'Dispute this finding',
                icon: Icons.gavel_outlined,
                loading: _busy,
                onPressed: () => _setStatus(
                  'DISPUTED',
                  'Dispute this finding?',
                  'The landlord will see that you disagree with it.',
                  'Dispute',
                ),
              ),
            if (finding.canResolve) ...[
              if (finding.canDispute) const SizedBox(height: 12),
              PrimaryButton(
                label: 'Mark as resolved',
                icon: Icons.check_circle_outline,
                loading: _busy,
                onPressed: () => _setStatus(
                  'RESOLVED',
                  'Mark as resolved?',
                  'Use this once the matter has been settled. Resolved findings cannot be reopened in this version.',
                  'Resolve',
                ),
              ),
            ],
            if (!finding.canDispute && !finding.canResolve)
              const Text(
                'No actions are available for you on this finding right now.',
                style: AppText.small,
                textAlign: TextAlign.center,
              ),
          ],
        ),
      ),
    );
  }
}
