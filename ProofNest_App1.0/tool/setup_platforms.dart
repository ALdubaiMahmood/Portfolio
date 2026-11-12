// Applies the platform settings ProofNest needs after Flutter generates android/ (and optionally ios/).
// Run from the project root: dart tool/setup_platforms.dart
// Safe to run more than once.
import 'dart:io';

void main() {
  var changed = false;
  changed |= _patchAndroid();
  changed |= _patchIos();
  stdout.writeln(changed
      ? 'Done. ProofNest platform files updated.'
      : 'Nothing to change; platform files are already configured.');
}

bool _patchAndroid() {
  final file = File('android/app/src/main/AndroidManifest.xml');
  if (!file.existsSync()) {
    stderr.writeln(
        'Android platform files are missing. Run setup.bat or flutter create --platforms=android . first.');
    return false;
  }

  var text = file.readAsStringSync();
  final original = text;
  final applicationStart = text.indexOf('<application');
  if (applicationStart < 0) {
    stderr.writeln('Could not find the <application> element in ${file.path}.');
    return false;
  }

  // Internet permission will be needed by a future Firebase integration.
  if (!text.contains('android.permission.INTERNET')) {
    text = '${text.substring(0, applicationStart)}'
        '<uses-permission android:name="android.permission.INTERNET" />\n    '
        '${text.substring(applicationStart)}';
  }

  final appStart = text.indexOf('<application');
  final appEnd = text.indexOf('>', appStart);
  if (appEnd < 0) {
    stderr
        .writeln('Could not parse the <application> element in ${file.path}.');
    return false;
  }
  var appTag = text.substring(appStart, appEnd + 1);
  appTag = appTag.replaceAll(
      RegExp(r'\s+android:usesCleartextTraffic\s*=\s*"[^"]*"'), '');
  text = '${text.substring(0, appStart)}$appTag${text.substring(appEnd + 1)}';

  // Consistent application name in the Android launcher.
  final updatedAppStart = text.indexOf('<application');
  final updatedAppEnd = text.indexOf('>', updatedAppStart);
  appTag = text.substring(updatedAppStart, updatedAppEnd + 1);
  if (RegExp(r'android:label\s*=').hasMatch(appTag)) {
    appTag = appTag.replaceAll(
        RegExp(r'android:label\s*=\s*"[^"]*"'), 'android:label="ProofNest"');
  } else {
    appTag =
        '${appTag.substring(0, appTag.length - 1)}\n        android:label="ProofNest">';
  }
  text =
      '${text.substring(0, updatedAppStart)}$appTag${text.substring(updatedAppEnd + 1)}';

  if (text != original) {
    file.writeAsStringSync(text);
    stdout.writeln('Patched ${file.path}');
    return true;
  }
  return false;
}

bool _patchIos() {
  final file = File('ios/Runner/Info.plist');
  if (!file.existsSync()) {
    return false; // iOS is optional for this Android-first project.
  }
  var text = file.readAsStringSync();
  final original = text;
  const additions = <String, String>{
    'NSCameraUsageDescription':
        '<key>NSCameraUsageDescription</key>\n\t<string>ProofNest uses the camera to photograph property conditions.</string>',
    'NSPhotoLibraryUsageDescription':
        '<key>NSPhotoLibraryUsageDescription</key>\n\t<string>ProofNest lets you attach photos to an inspection.</string>',
    'NSPhotoLibraryAddUsageDescription':
        '<key>NSPhotoLibraryAddUsageDescription</key>\n\t<string>ProofNest can save inspection images when requested.</string>',
  };

  final closing = text.lastIndexOf('</dict>');
  if (closing == -1) {
    stderr.writeln('Unexpected Info.plist format in ${file.path}.');
    return false;
  }
  final missing = additions.entries
      .where((entry) => !text.contains(entry.key))
      .map((entry) => entry.value)
      .toList();
  if (missing.isEmpty) {
    return false;
  }
  text =
      '${text.substring(0, closing)}\t${missing.join('\n\t')}\n${text.substring(closing)}';
  file.writeAsStringSync(text);
  stdout.writeln('Patched ${file.path}');
  return text != original;
}
