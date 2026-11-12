import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/api_exception.dart';
import '../../core/theme.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/common.dart';
import 'comparison_screen.dart';

/// Step before the comparison: choose a property, one move-in and one move-out inspection.
class ComparisonPickerScreen extends StatefulWidget {
  const ComparisonPickerScreen({super.key, this.initialPropertyId});

  final int? initialPropertyId;

  @override
  State<ComparisonPickerScreen> createState() => _ComparisonPickerScreenState();
}

class _ComparisonPickerScreenState extends State<ComparisonPickerScreen> {
  List<Property>? _properties;
  List<InspectionSummary> _inspections = const [];
  int? _propertyId;
  int? _moveInId;
  int? _moveOutId;
  bool _loadingInspections = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadProperties();
  }

  Future<void> _loadProperties() async {
    try {
      final list = await context.read<AuthController>().api.properties();
      if (!mounted) {
        return;
      }
      setState(() {
        _properties = list;
        _error = null;
      });
      final preset =
          widget.initialPropertyId ?? (list.length == 1 ? list.first.id : null);
      if (preset != null) {
        await _selectProperty(preset);
      }
    } catch (e) {
      if (mounted) {
        setState(() => _error = errorText(e));
      }
    }
  }

  Future<void> _selectProperty(int id) async {
    setState(() {
      _propertyId = id;
      _moveInId = null;
      _moveOutId = null;
      _inspections = const [];
      _loadingInspections = true;
    });
    try {
      final list =
          await context.read<AuthController>().api.propertyInspections(id);
      if (!mounted) {
        return;
      }
      final moveIns = list.where((i) => i.isMoveIn).toList();
      final moveOuts = list.where((i) => i.isMoveOut).toList();
      setState(() {
        _inspections = list;
        _loadingInspections = false;
        _error = null;
        // Nothing to decide when there is only one of each.
        _moveInId = moveIns.length == 1 ? moveIns.first.id : null;
        _moveOutId = moveOuts.length == 1 ? moveOuts.first.id : null;
      });
    } catch (e) {
      if (mounted) {
        setState(() {
          _loadingInspections = false;
          _error = errorText(e);
        });
      }
    }
  }

  List<MapEntry<String, String>> _options(bool moveIn) => [
        for (final i
            in _inspections.where((i) => moveIn ? i.isMoveIn : i.isMoveOut))
          MapEntry('${i.id}', '${i.dateLabel} · ${i.statusLabel}'),
      ];

  @override
  Widget build(BuildContext context) {
    final moveIns = _options(true);
    final moveOuts = _options(false);
    final ready =
        _propertyId != null && _moveInId != null && _moveOutId != null;
    return Scaffold(
      appBar: AppBar(title: const Text('Compare inspections')),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 32),
          children: [
            const Text(
              'Rooms are matched by name, so name the same room the same way in both inspections.',
              style: AppText.muted,
            ),
            const SizedBox(height: 18),
            if (_error != null)
              Text(_error!, style: const TextStyle(color: AppColors.redBright))
            else if (_properties == null)
              const Center(
                  child: Padding(
                      padding: EdgeInsets.all(24),
                      child: CircularProgressIndicator()))
            else if (_properties!.isEmpty)
              const Text('You do not have any properties to compare yet.',
                  style: AppText.muted)
            else ...[
              ChoiceRow(
                label: 'Property',
                options: [
                  for (final p in _properties!) MapEntry('${p.id}', p.name)
                ],
                value: _propertyId == null ? null : '$_propertyId',
                onChanged: (value) => _selectProperty(int.parse(value)),
              ),
              const SizedBox(height: 22),
              if (_loadingInspections)
                const Center(child: CircularProgressIndicator())
              else if (_propertyId != null) ...[
                if (moveIns.isEmpty)
                  const Text('This property has no move-in inspection yet.',
                      style: AppText.muted)
                else
                  ChoiceRow(
                    label: 'Move-in inspection',
                    options: moveIns,
                    value: _moveInId == null ? null : '$_moveInId',
                    onChanged: (value) =>
                        setState(() => _moveInId = int.parse(value)),
                  ),
                const SizedBox(height: 18),
                if (moveOuts.isEmpty)
                  const Text('This property has no move-out inspection yet.',
                      style: AppText.muted)
                else
                  ChoiceRow(
                    label: 'Move-out inspection',
                    options: moveOuts,
                    value: _moveOutId == null ? null : '$_moveOutId',
                    onChanged: (value) =>
                        setState(() => _moveOutId = int.parse(value)),
                  ),
              ],
            ],
            const SizedBox(height: 28),
            PrimaryButton(
              label: 'Compare',
              icon: Icons.compare_outlined,
              onPressed: ready
                  ? () => Navigator.of(context).push(
                        MaterialPageRoute<void>(
                          builder: (_) => ComparisonScreen(
                            propertyId: _propertyId!,
                            moveInId: _moveInId!,
                            moveOutId: _moveOutId!,
                          ),
                        ),
                      )
                  : null,
            ),
          ],
        ),
      ),
    );
  }
}
