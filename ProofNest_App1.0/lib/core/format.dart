const List<String> _months = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
];

/// "Oct 10, 2026". Returns an empty string for null.
String formatDate(DateTime? date) {
  if (date == null) {
    return '';
  }
  return '${_months[date.month - 1]} ${date.day}, ${date.year}';
}

/// Converts a server timestamp (which carries a UTC offset) to the phone's own time zone,
/// e.g. "Oct 10, 2026 · 17:34".
String formatDateTime(DateTime? value) {
  if (value == null) {
    return '';
  }
  final local = value.toLocal();
  final hh = local.hour.toString().padLeft(2, '0');
  final mm = local.minute.toString().padLeft(2, '0');
  return '${formatDate(local)} · $hh:$mm';
}

/// Parses a yyyy-MM-dd inspection date; null when the text is not a date.
DateTime? parseDay(String? value) {
  if (value == null || value.isEmpty) {
    return null;
  }
  return DateTime.tryParse(value);
}

/// yyyy-MM-dd for sending a chosen day to the server.
String dayToIso(DateTime day) {
  final mm = day.month.toString().padLeft(2, '0');
  final dd = day.day.toString().padLeft(2, '0');
  return '${day.year}-$mm-$dd';
}

String formatBytes(int bytes) {
  if (bytes < 1024) {
    return '$bytes B';
  }
  if (bytes < 1024 * 1024) {
    return '${(bytes / 1024).toStringAsFixed(0)} KB';
  }
  return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
}

/// Adds http:// when missing and removes trailing slashes, so "192.168.1.20:8080" works.
String normalizeBaseUrl(String input) {
  var value = input.trim();
  if (value.isEmpty) {
    return value;
  }
  if (!value.startsWith('http://') && !value.startsWith('https://')) {
    value = 'http://$value';
  }
  while (value.endsWith('/')) {
    value = value.substring(0, value.length - 1);
  }
  return value;
}

String firstName(String fullName) {
  final trimmed = fullName.trim();
  if (trimmed.isEmpty) {
    return '';
  }
  return trimmed.split(RegExp(r'\s+')).first;
}

String initials(String fullName) {
  final parts =
      fullName.trim().split(RegExp(r'\s+')).where((p) => p.isNotEmpty).toList();
  if (parts.isEmpty) {
    return '?';
  }
  if (parts.length == 1) {
    return parts.first.substring(0, 1).toUpperCase();
  }
  return (parts.first.substring(0, 1) + parts.last.substring(0, 1))
      .toUpperCase();
}
