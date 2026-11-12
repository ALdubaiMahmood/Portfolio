import 'package:flutter/material.dart';

import '../core/theme.dart';

/// Dark rounded panel used for every block of content (the website's "dashboard-panel").
class AppCard extends StatelessWidget {
  const AppCard(
      {super.key,
      required this.child,
      this.padding = const EdgeInsets.all(16),
      this.onTap});

  final Widget child;
  final EdgeInsetsGeometry padding;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    const radius = BorderRadius.all(Radius.circular(14));
    return Material(
      color: AppColors.panel,
      shape: const RoundedRectangleBorder(
        borderRadius: radius,
        side: BorderSide(color: AppColors.lineSoft),
      ),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Padding(padding: padding, child: child),
      ),
    );
  }
}

class Eyebrow extends StatelessWidget {
  const Eyebrow(this.text, {super.key});

  final String text;

  @override
  Widget build(BuildContext context) =>
      Text(text.toUpperCase(), style: AppText.eyebrow);
}

/// Coloured dot + label, like the website's status badges.
class StatusPill extends StatelessWidget {
  const StatusPill({super.key, required this.label, required this.color});

  final String label;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(color: color, shape: BoxShape.circle)),
        const SizedBox(width: 6),
        Text(label,
            style: TextStyle(
                color: color, fontSize: 12.5, fontWeight: FontWeight.w600)),
      ],
    );
  }
}

Color inspectionStatusColor(String status) {
  switch (status) {
    case 'COMPLETED':
      return AppColors.statusDone;
    case 'IN_PROGRESS':
      return AppColors.statusProgress;
    default:
      return AppColors.statusDraft;
  }
}

Color propertyStatusColor(String status) {
  switch (status) {
    case 'ACTIVE':
      return AppColors.statusDone;
    case 'MAINTENANCE':
      return AppColors.statusWarn;
    default:
      return AppColors.statusArchived;
  }
}

Color findingStatusColor(String status) {
  switch (status) {
    case 'RESOLVED':
      return AppColors.statusDone;
    case 'DISPUTED':
      return AppColors.redBright;
    default:
      return AppColors.statusWarn;
  }
}

class SectionHeader extends StatelessWidget {
  const SectionHeader(
      {super.key, required this.title, this.eyebrow, this.trailing});

  final String title;
  final String? eyebrow;
  final Widget? trailing;

  @override
  Widget build(BuildContext context) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (eyebrow != null) ...[
                Eyebrow(eyebrow!),
                const SizedBox(height: 4)
              ],
              Text(title, style: AppText.heading),
            ],
          ),
        ),
        if (trailing != null) trailing!,
      ],
    );
  }
}

/// "Label    value" row used inside detail cards.
class InfoRow extends StatelessWidget {
  const InfoRow(this.label, this.value, {super.key});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(width: 104, child: Text(label, style: AppText.small)),
          Expanded(child: Text(value, style: AppText.body)),
        ],
      ),
    );
  }
}

class EmptyState extends StatelessWidget {
  const EmptyState(
      {super.key,
      required this.icon,
      required this.title,
      required this.message,
      this.action});

  final IconData icon;
  final String title;
  final String message;
  final Widget? action;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 44, color: AppColors.dim),
            const SizedBox(height: 14),
            Text(title, style: AppText.heading, textAlign: TextAlign.center),
            const SizedBox(height: 6),
            Text(message, style: AppText.muted, textAlign: TextAlign.center),
            if (action != null) ...[const SizedBox(height: 18), action!],
          ],
        ),
      ),
    );
  }
}

class ErrorState extends StatelessWidget {
  const ErrorState({super.key, required this.message, required this.onRetry});

  final String message;
  final VoidCallback onRetry;

  @override
  Widget build(BuildContext context) {
    return EmptyState(
      icon: Icons.cloud_off_outlined,
      title: 'Could not load this',
      message: message,
      action:
          OutlinedButton(onPressed: onRetry, child: const Text('Try again')),
    );
  }
}

/// Red filled button that shows a spinner while [loading].
class PrimaryButton extends StatelessWidget {
  const PrimaryButton(
      {super.key,
      required this.label,
      required this.onPressed,
      this.loading = false,
      this.icon});

  final String label;
  final VoidCallback? onPressed;
  final bool loading;
  final IconData? icon;

  @override
  Widget build(BuildContext context) {
    final child = loading
        ? const SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(
                strokeWidth: 2.4, color: Colors.white),
          )
        : Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (icon != null) ...[
                Icon(icon, size: 18),
                const SizedBox(width: 8)
              ],
              Text(label, style: const TextStyle(fontWeight: FontWeight.w700)),
            ],
          );
    return SizedBox(
      height: 50,
      child: FilledButton(
        onPressed: loading ? null : onPressed,
        style: FilledButton.styleFrom(
          backgroundColor: AppColors.red,
          foregroundColor: Colors.white,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        ),
        child: child,
      ),
    );
  }
}

/// A label followed by single-choice chips (used instead of dropdowns for short lists).
class ChoiceRow extends StatelessWidget {
  const ChoiceRow({
    super.key,
    required this.label,
    required this.options,
    required this.value,
    required this.onChanged,
  });

  final String label;
  final List<MapEntry<String, String>> options;
  final String? value;
  final ValueChanged<String> onChanged;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: AppText.muted),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            for (final option in options)
              ChoiceChip(
                label: Text(option.value),
                selected: option.key == value,
                onSelected: (_) => onChanged(option.key),
                selectedColor: AppColors.redTint,
                backgroundColor: AppColors.panel,
                side: BorderSide(
                    color: option.key == value
                        ? AppColors.red
                        : AppColors.lineSoft),
                labelStyle: TextStyle(
                    color:
                        option.key == value ? AppColors.text : AppColors.muted),
                showCheckmark: false,
              ),
          ],
        ),
      ],
    );
  }
}

/// Text-field look shared by every form, so the theme does not depend on a Flutter-version-specific InputDecorationTheme.
InputDecoration fieldDecoration(String label, {String? hint, Widget? suffix}) {
  const border = OutlineInputBorder(
    borderRadius: BorderRadius.all(Radius.circular(12)),
    borderSide: BorderSide(color: AppColors.lineSoft),
  );
  return InputDecoration(
    labelText: label,
    hintText: hint,
    suffixIcon: suffix,
    filled: true,
    fillColor: AppColors.panel,
    labelStyle: const TextStyle(color: AppColors.muted),
    hintStyle: const TextStyle(color: AppColors.dim),
    enabledBorder: border,
    border: border,
    focusedBorder: const OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12)),
      borderSide: BorderSide(color: AppColors.red, width: 1.5),
    ),
    errorBorder: const OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12)),
      borderSide: BorderSide(color: AppColors.redBright),
    ),
    focusedErrorBorder: const OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12)),
      borderSide: BorderSide(color: AppColors.redBright, width: 1.5),
    ),
  );
}
