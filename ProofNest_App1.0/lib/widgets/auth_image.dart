import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../core/theme.dart';
import '../state/auth_controller.dart';

/// Loads a private image. The server only serves photos to signed-in participants,
/// so the bearer token is sent in the request headers.
class AuthImage extends StatelessWidget {
  const AuthImage({
    super.key,
    required this.url,
    this.fit = BoxFit.cover,
    this.width,
    this.height,
    this.borderRadius = BorderRadius.zero,
  });

  final String url;
  final BoxFit fit;
  final double? width;
  final double? height;
  final BorderRadius borderRadius;

  @override
  Widget build(BuildContext context) {
    final headers = context.read<AuthController>().api.imageHeaders;
    return ClipRRect(
      borderRadius: borderRadius,
      child: Image.network(
        url,
        headers: headers,
        fit: fit,
        width: width,
        height: height,
        loadingBuilder: (context, child, progress) {
          if (progress == null) {
            return child;
          }
          return Container(
            width: width,
            height: height,
            color: AppColors.panelRaised,
            alignment: Alignment.center,
            child: const SizedBox(
                width: 22,
                height: 22,
                child: CircularProgressIndicator(strokeWidth: 2)),
          );
        },
        errorBuilder: (context, error, stack) => Container(
          width: width,
          height: height,
          color: AppColors.panelRaised,
          alignment: Alignment.center,
          child: const Icon(Icons.broken_image_outlined, color: AppColors.dim),
        ),
      ),
    );
  }
}

/// Grey box standing in for a missing photo (for example a room that has no move-out photos yet).
class PhotoPlaceholder extends StatelessWidget {
  const PhotoPlaceholder({super.key, this.label = 'No photo'});

  final String label;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: AppColors.panelRaised,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: AppColors.lineSoft),
      ),
      alignment: Alignment.center,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.photo_outlined, color: AppColors.dim),
          const SizedBox(height: 4),
          Text(label, style: AppText.small),
        ],
      ),
    );
  }
}
