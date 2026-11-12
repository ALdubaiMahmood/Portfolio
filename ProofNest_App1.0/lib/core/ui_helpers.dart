import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

import '../state/auth_controller.dart';
import 'api_exception.dart';
import 'config.dart';
import 'theme.dart';

void showMessage(BuildContext context, String text, {bool error = false}) {
  final messenger = ScaffoldMessenger.of(context);
  messenger.hideCurrentSnackBar();
  messenger.showSnackBar(
    SnackBar(
      content: Text(text),
      backgroundColor: error ? AppColors.redDeep : AppColors.panelRaised,
    ),
  );
}

Future<bool> confirmDialog(
  BuildContext context, {
  required String title,
  required String message,
  required String confirmLabel,
}) async {
  final result = await showDialog<bool>(
    context: context,
    builder: (dialogContext) => AlertDialog(
      backgroundColor: AppColors.panelRaised,
      title: Text(title),
      content: Text(message, style: AppText.muted),
      actions: [
        TextButton(
            onPressed: () => Navigator.of(dialogContext).pop(false),
            child: const Text('Cancel')),
        TextButton(
          onPressed: () => Navigator.of(dialogContext).pop(true),
          child: Text(confirmLabel,
              style: const TextStyle(color: AppColors.redBright)),
        ),
      ],
    ),
  );
  return result ?? false;
}

/// Runs a server action, shows the server's message when it fails, and tells every list to reload
/// when it succeeds. Returns true on success.
Future<bool> runAction(
  BuildContext context,
  Future<void> Function() action, {
  String? success,
}) async {
  final auth = context.read<AuthController>();
  try {
    await action();
  } catch (e) {
    if (context.mounted) {
      showMessage(context, errorText(e), error: true);
    }
    return false;
  }
  auth.markDataChanged();
  if (success != null && context.mounted) {
    showMessage(context, success);
  }
  return true;
}

/// Asks for camera or gallery, returns the chosen photo's path (null when cancelled or refused).
/// Photos are scaled down and saved as JPEG, which keeps them under the server's 10 MB limit.
Future<String?> pickPhoto(BuildContext context) async {
  final source = await showModalBottomSheet<ImageSource>(
    context: context,
    backgroundColor: AppColors.panelRaised,
    builder: (sheetContext) => SafeArea(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
            leading: const Icon(Icons.photo_camera_outlined),
            title: const Text('Take a photo'),
            onTap: () => Navigator.of(sheetContext).pop(ImageSource.camera),
          ),
          ListTile(
            leading: const Icon(Icons.photo_library_outlined),
            title: const Text('Choose from gallery'),
            onTap: () => Navigator.of(sheetContext).pop(ImageSource.gallery),
          ),
        ],
      ),
    ),
  );
  if (source == null) {
    return null;
  }
  try {
    final picked = await ImagePicker().pickImage(
        source: source, imageQuality: 85, maxWidth: 2560, maxHeight: 2560);
    if (picked == null) {
      return null;
    }
    final size = await picked.length();
    if (size > maxPhotoBytes) {
      if (context.mounted) {
        showMessage(context, 'Choose a photo smaller than 10 MB.', error: true);
      }
      return null;
    }
    return picked.path;
  } catch (_) {
    if (context.mounted) {
      showMessage(context,
          'The photo could not be opened. Check the app has permission to use the camera or photos.',
          error: true);
    }
    return null;
  }
}

/// Small dialog with one text field; returns the entered text or null when cancelled.
Future<String?> promptText(
  BuildContext context, {
  required String title,
  required String label,
  String initial = '',
  String confirmLabel = 'Save',
  TextInputType keyboard = TextInputType.text,
}) {
  final controller = TextEditingController(text: initial);
  return showDialog<String>(
    context: context,
    builder: (dialogContext) => AlertDialog(
      backgroundColor: AppColors.panelRaised,
      title: Text(title),
      content: TextField(
        controller: controller,
        autofocus: true,
        keyboardType: keyboard,
        decoration: InputDecoration(labelText: label),
      ),
      actions: [
        TextButton(
            onPressed: () => Navigator.of(dialogContext).pop(),
            child: const Text('Cancel')),
        TextButton(
          onPressed: () =>
              Navigator.of(dialogContext).pop(controller.text.trim()),
          child: Text(confirmLabel),
        ),
      ],
    ),
  );
}
