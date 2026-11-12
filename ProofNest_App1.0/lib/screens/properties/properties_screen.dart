import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/theme.dart';
import '../../models/models.dart';
import '../../state/auth_controller.dart';
import '../../widgets/auth_image.dart';
import '../../widgets/app_drawer.dart';
import '../../widgets/common.dart';
import '../../widgets/loading_builder.dart';
import 'property_detail_screen.dart';
import 'property_form_screen.dart';

class PropertiesScreen extends StatefulWidget {
  const PropertiesScreen({super.key, required this.onOpenTab});

  final ValueChanged<int> onOpenTab;

  @override
  State<PropertiesScreen> createState() => _PropertiesScreenState();
}

class _PropertiesScreenState extends State<PropertiesScreen> {
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
      appBar: AppBar(title: const Text('Properties')),
      drawer: AppDrawer(onOpenTab: widget.onOpenTab),
      floatingActionButton: auth.isLandlord
          ? FloatingActionButton.extended(
              backgroundColor: AppColors.red,
              foregroundColor: Colors.white,
              onPressed: () => Navigator.of(context).push(
                MaterialPageRoute<void>(
                    builder: (_) => const PropertyFormScreen()),
              ),
              icon: const Icon(Icons.add),
              label: const Text('Add property'),
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
                'Search name or address',
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
                _FilterChip(
                    label: 'All',
                    selected: _status == null,
                    onTap: () => setState(() => _status = null)),
                for (final choice in kPropertyStatuses)
                  _FilterChip(
                    label: choice.label,
                    selected: _status == choice.value,
                    onTap: () => setState(() => _status = choice.value),
                  ),
              ],
            ),
          ),
          Expanded(
            child: LoadingBuilder<List<Property>>(
              refreshKey: '$_query|$_status|${auth.dataVersion}',
              loader: () => auth.api.properties(query: _query, status: _status),
              builder: (context, properties, reload) {
                if (properties.isEmpty) {
                  return RefreshIndicator(
                    onRefresh: reload,
                    child: ListView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      children: [
                        SizedBox(
                          height: 360,
                          child: EmptyState(
                            icon: Icons.apartment_outlined,
                            title: 'No properties found',
                            message: auth.isLandlord
                                ? 'Add your first property to start recording inspections.'
                                : 'Properties your landlord shares with you will appear here.',
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
                    itemCount: properties.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 12),
                    itemBuilder: (context, index) =>
                        _PropertyCard(property: properties[index]),
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

class _FilterChip extends StatelessWidget {
  const _FilterChip(
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

class _PropertyCard extends StatelessWidget {
  const _PropertyCard({required this.property});

  final Property property;

  @override
  Widget build(BuildContext context) {
    final api = context.read<AuthController>().api;
    return AppCard(
      padding: EdgeInsets.zero,
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute<void>(
            builder: (_) => PropertyDetailScreen(propertyId: property.id)),
      ),
      child: Row(
        children: [
          SizedBox(
            width: 104,
            height: 104,
            child: property.hasImage
                ? AuthImage(url: api.propertyImageUrl(property.id))
                : Container(
                    color: AppColors.panelRaised,
                    child: const Icon(Icons.apartment,
                        size: 36, color: AppColors.dim),
                  ),
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(14),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(property.name,
                      style: AppText.heading,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis),
                  const SizedBox(height: 2),
                  Text(
                    (property.address ?? '').isEmpty
                        ? property.propertyTypeLabel
                        : property.address!,
                    style: AppText.muted,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      StatusPill(
                          label: property.statusLabel,
                          color: propertyStatusColor(property.status)),
                      const Spacer(),
                      Text('${property.inspectionCount} inspections',
                          style: AppText.small),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
