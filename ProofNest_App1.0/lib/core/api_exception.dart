/// A service/data error with a message that is safe to show to the user.
class ApiException implements Exception {
  ApiException(this.message, {this.statusCode});

  final String message;
  final int? statusCode;

  bool get isUnauthorized => statusCode == 401;

  @override
  String toString() => message;
}

/// Text to show for any error caught in the UI.
String errorText(Object error) {
  if (error is ApiException) {
    return error.message;
  }
  return 'Something went wrong. Please try again.';
}
