import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/format.dart';
import '../../core/theme.dart';
import '../../core/ui_helpers.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/auth_image.dart';
import '../../widgets/common.dart';
import '../../widgets/loading_builder.dart';
import '../findings/finding_form_screen.dart';
import '../inspections/photo_viewer_screen.dart';

/// Move-in and move-out photos side by side, room by room, with comments and a way to file a finding.
class ComparisonScreen extends StatelessWidget {
  const ComparisonScreen({
    super.key,
    required this.propertyId,
    required this.moveInId,
    required this.moveOutId,
  });

  final int propertyId;
  final int moveInId;
  final int moveOutId;

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthController>();
    return LoadingBuilder<Comparison>(
      refreshKey: auth.dataVersion,
      frame: (body) =>
          Scaffold(appBar: AppBar(title: const Text('Comparison')), body: body),
      loader: () => auth.api.comparison(propertyId, moveInId, moveOutId),
      builder: (context, comparison, reload) {
        return Scaffold(
          appBar: AppBar(title: Text(comparison.first.propertyName)),
          body: RefreshIndicator(
            onRefresh: reload,
            child: ListView(
              physics: const AlwaysScrollableScrollPhysics(),
              padding: const EdgeInsets.fromLTRB(16, 4, 16, 40),
              children: [
                Row(
                  children: [
                    Expanded(
                        child: _SideHeader(
                            title: 'Move-in', inspection: comparison.first)),
                    const SizedBox(width: 12),
                    Expanded(
                        child: _SideHeader(
                            title: 'Move-out', inspection: comparison.second)),
                  ],
                ),
                const SizedBox(height: 16),
                if (comparison.pairs.isEmpty)
                  const AppCard(
                    child: Text(
                      'Neither inspection has any rooms yet. Add rooms and photos to the inspections first.',
                      style: AppText.muted,
                    ),
                  )
                else
                  for (final pair in comparison.pairs) ...[
                    _PairCard(
                        key: ValueKey(pair.name),
                        comparison: comparison,
                        pair: pair),
                    const SizedBox(height: 14),
                  ],
              ],
            ),
          ),
        );
      },
    );
  }
}

class _SideHeader extends StatelessWidget {
  const _SideHeader({required this.title, required this.inspection});

  final String title;
  final InspectionSummary inspection;

  @override
  Widget build(BuildContext context) {
    return AppCard(
      padding: const EdgeInsets.all(12),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Eyebrow(title),
          const SizedBox(height: 4),
          Text(inspection.dateLabel, style: AppText.heading),
          const SizedBox(height: 2),
          StatusPill(
              label: inspection.statusLabel,
              color: inspectionStatusColor(inspection.status)),
        ],
      ),
    );
  }
}

class _PairCard extends StatefulWidget {
  const _PairCard({super.key, required this.comparison, required this.pair});

  final Comparison comparison;
  final AreaPair pair;

  @override
  State<_PairCard> createState() => _PairCardState();
}

class _PairCardState extends State<_PairCard> {
  final _comment = TextEditingController();
  bool _posting = false;

  @override
  void dispose() {
    _comment.dispose();
    super.dispose();
  }

  Future<void> _post() async {
    final text = _comment.text.trim();
    if (text.isEmpty) {
      return;
    }
    final api = context.read<AuthController>().api;
    final comparison = widget.comparison;
    setState(() => _posting = true);
    final ok = await runAction(
      context,
      () => api.addComment(
        propertyId: comparison.propertyId,
        moveInId: comparison.first.id,
        moveOutId: comparison.second.id,
        areaName: widget.pair.name,
        message: text,
      ),
    );
    if (!mounted) {
      return;
    }
    if (ok) {
      _comment.clear();
    }
    setState(() => _posting = false);
  }

  void _openPhoto(int inspectionId, Evidence evidence) {
    Navigator.of(context).push(
      MaterialPageRoute<void>(
        builder: (_) => PhotoViewerScreen(
          inspectionId: inspectionId,
          evidence: evidence,
          roomName: widget.pair.name,
        ),
      ),
    );
  }

  Widget _photoCell(AuthController auth, int inspectionId, Evidence? evidence,
      String emptyLabel) {
    return AspectRatio(
      aspectRatio: 1,
      child: evidence == null
          ? PhotoPlaceholder(label: emptyLabel)
          : GestureDetector(
              onTap: () => _openPhoto(inspectionId, evidence),
              child: AuthImage(
                url: auth.api.evidenceUrl(evidence.id),
                borderRadius: BorderRadius.circular(10),
              ),
            ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.read<AuthController>();
    final comparison = widget.comparison;
    final pair = widget.pair;
    final firstPhotos = pair.first?.evidence ?? const <Evidence>[];
    final secondPhotos = pair.second?.evidence ?? const <Evidence>[];
    final rows = firstPhotos.length > secondPhotos.length
        ? firstPhotos.length
        : secondPhotos.length;
    final missingRoom = pair.first == null
        ? 'This room is only in the move-out inspection.'
        : (pair.second == null
            ? 'This room is only in the move-in inspection.'
            : null);

    return AppCard(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(pair.name, style: AppText.heading),
          if (missingRoom != null) ...[
            const SizedBox(height: 4),
            Text(missingRoom,
                style: const TextStyle(
                    color: AppColors.statusWarn, fontSize: 12.5)),
          ],
          const SizedBox(height: 12),
          if (rows == 0)
            const Text('No photos in either inspection for this room.',
                style: AppText.muted)
          else
            for (var i = 0; i < rows; i++) ...[
              Row(
                children: [
                  Expanded(
                    child: _photoCell(
                        auth,
                        comparison.first.id,
                        i < firstPhotos.length ? firstPhotos[i] : null,
                        'No move-in photo'),
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: _photoCell(
                        auth,
                        comparison.second.id,
                        i < secondPhotos.length ? secondPhotos[i] : null,
                        'No move-out photo'),
                  ),
                ],
              ),
              if (i < rows - 1) const SizedBox(height: 8),
            ],
          if ((pair.first?.notes ?? '').isNotEmpty ||
              (pair.second?.notes ?? '').isNotEmpty) ...[
            const SizedBox(height: 12),
            if ((pair.first?.notes ?? '').isNotEmpty)
              Text('Move-in notes: ${pair.first!.notes}', style: AppText.muted),
            if ((pair.second?.notes ?? '').isNotEmpty)
              Text('Move-out notes: ${pair.second!.notes}',
                  style: AppText.muted),
          ],
          const Divider(height: 28),
          Text('Comments (${pair.comments.length})', style: AppText.heading),
          const SizedBox(height: 8),
          if (pair.comments.isEmpty)
            const Text('No comments on this room yet.', style: AppText.muted)
          else
            for (final comment in pair.comments)
              Padding(
                padding: const EdgeInsets.only(bottom: 10),
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AppColors.panelRaised,
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: AppColors.lineSoft),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                          '${comment.authorName} (${comment.roleLabel}) · ${formatDateTime(comment.createdAt)}',
                          style: AppText.small),
                      const SizedBox(height: 4),
                      Text(comment.message, style: AppText.body),
                    ],
                  ),
                ),
              ),
          const SizedBox(height: 4),
          TextField(
            controller: _comment,
            minLines: 1,
            maxLines: 4,
            maxLength: 2000,
            decoration: fieldDecoration(
              'Add a comment',
              suffix: _posting
                  ? const Padding(
                      padding: EdgeInsets.all(12),
                      child: SizedBox(
                          width: 20,
                          height: 20,
                          child: CircularProgressIndicator(strokeWidth: 2)),
                    )
                  : IconButton(
                      tooltip: 'Post comment',
                      icon: const Icon(Icons.send_rounded,
                          color: AppColors.redBright),
                      onPressed: _post,
                    ),
            ),
          ),
          const SizedBox(height: 4),
          OutlinedButton.icon(
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute<void>(
                builder: (_) => FindingFormScreen(
                  propertyId: comparison.propertyId,
                  propertyName: comparison.first.propertyName,
                  moveInId: comparison.first.id,
                  moveOutId: comparison.second.id,
                  areaName: pair.name,
                ),
              ),
            ),
            icon: const Icon(Icons.flag_outlined, size: 18),
            label: const Text('File a finding for this room'),
          ),
        ],
      ),
    );
  }
}
