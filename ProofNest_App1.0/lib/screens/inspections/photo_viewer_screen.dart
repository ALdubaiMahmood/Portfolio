import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/format.dart';
import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/auth_image.dart';

/// Full-screen, zoomable photo with its evidence details. The delete button appears only when the
/// server says this user may delete it (property owner or the uploader).
class PhotoViewerScreen extends StatelessWidget {
  const PhotoViewerScreen({
    super.key,
    required this.inspectionId,
    required this.evidence,
    required this.roomName,
  });

  final int inspectionId;
  final Evidence evidence;
  final String roomName;

  Future<void> _delete(BuildContext context) async {
    final api = context.read<AuthController>().api;
    final navigator = Navigator.of(context);
    final confirmed = await confirmDialog(
      context,
      title: 'Delete this photo?',
      message: 'The photo is removed from the inspection for everyone.',
      confirmLabel: 'Delete',
    );
    if (!confirmed || !context.mounted) {
      return;
    }
    final ok = await runAction(
      context,
      () => api.deleteEvidence(inspectionId, evidence.id),
      success: 'Photo deleted.',
    );
    if (ok) {
      navigator.pop();
    }
  }

  @override
  Widget build(BuildContext context) {
    final api = context.read<AuthController>().api;
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        backgroundColor: Colors.black,
        title: Text(roomName),
        actions: [
          if (evidence.canDelete)
            IconButton(
              tooltip: 'Delete photo',
              icon: const Icon(Icons.delete_outline),
              onPressed: () => _delete(context),
            ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: InteractiveViewer(
              minScale: 1,
              maxScale: 5,
              child: Center(
                child: AuthImage(
                    url: api.evidenceUrl(evidence.id), fit: BoxFit.contain),
              ),
            ),
          ),
          SafeArea(
            top: false,
            child: Container(
              width: double.infinity,
              color: AppColors.panelRaised,
              padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(evidence.originalFileName,
                      style: AppText.body,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis),
                  const SizedBox(height: 2),
                  Text(
                    '${evidence.uploadedByName} · ${formatDateTime(evidence.uploadedAt)} · ${formatBytes(evidence.fileSize)}',
                    style: AppText.muted,
                  ),
                  const SizedBox(height: 6),
                  SelectableText('SHA-256  ${evidence.sha256}',
                      style: const TextStyle(
                          color: AppColors.dim,
                          fontSize: 11,
                          fontFamily: 'monospace')),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
