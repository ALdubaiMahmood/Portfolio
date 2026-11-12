import 'package:flutter/material.dart';

/// Design tokens copied from the website (static/css/proofnest.css) so both clients look alike.
class AppColors {
  AppColors._();

  static const Color bg = Color(0xFF06090D);
  static const Color bgSoft = Color(0xFF091019);
  static const Color panel = Color(0xFF08121B);
  static const Color panelRaised = Color(0xFF0A1520);
  static const Color red = Color(0xFFE20E35);
  static const Color redBright = Color(0xFFFF1744);
  static const Color redDeep = Color(0xFF8B0D1A);
  static const Color redTint = Color(0x33E20E35);
  static const Color text = Color(0xFFF5F2ED);
  static const Color muted = Color(0xFFA7B6C8);
  static const Color dim = Color(0xFF708398);
  static const Color line = Color(0x8A23698B);
  static const Color lineSoft = Color(0x4D3C5E78);

  // Status colours, same as the website's status badges.
  static const Color statusDraft = Color(0xFFA7B6C8);
  static const Color statusProgress = Color(0xFF71B8EF);
  static const Color statusDone = Color(0xFF57C9A0);
  static const Color statusWarn = Color(0xFFE7AE4A);
  static const Color statusArchived = Color(0xFF9AA8B7);
}

class AppText {
  AppText._();

  static const TextStyle eyebrow = TextStyle(
    color: AppColors.redBright,
    fontSize: 11,
    fontWeight: FontWeight.w700,
    letterSpacing: 1.4,
  );

  static const TextStyle title = TextStyle(
    color: AppColors.text,
    fontSize: 26,
    fontWeight: FontWeight.w800,
    height: 1.15,
  );

  static const TextStyle heading = TextStyle(
    color: AppColors.text,
    fontSize: 17,
    fontWeight: FontWeight.w700,
  );

  static const TextStyle body =
      TextStyle(color: AppColors.text, fontSize: 14, height: 1.4);
  static const TextStyle muted =
      TextStyle(color: AppColors.muted, fontSize: 13, height: 1.4);
  static const TextStyle small =
      TextStyle(color: AppColors.dim, fontSize: 12, height: 1.3);
}

ThemeData buildAppTheme() {
  const scheme = ColorScheme.dark(
    primary: AppColors.red,
    onPrimary: Colors.white,
    secondary: AppColors.redBright,
    onSecondary: Colors.white,
    surface: AppColors.panel,
    onSurface: AppColors.text,
    error: AppColors.redBright,
    onError: Colors.white,
  );

  final base = ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    colorScheme: scheme,
    scaffoldBackgroundColor: AppColors.bg,
    canvasColor: AppColors.bg,
    dividerColor: AppColors.lineSoft,
  );

  return base.copyWith(
    textTheme: base.textTheme
        .apply(bodyColor: AppColors.text, displayColor: AppColors.text),
    appBarTheme: const AppBarTheme(
      backgroundColor: AppColors.bg,
      foregroundColor: AppColors.text,
      elevation: 0,
      scrolledUnderElevation: 0,
      centerTitle: false,
    ),
    navigationBarTheme: const NavigationBarThemeData(
      backgroundColor: AppColors.bgSoft,
      indicatorColor: AppColors.redTint,
    ),
    snackBarTheme: const SnackBarThemeData(
      behavior: SnackBarBehavior.floating,
      backgroundColor: AppColors.panelRaised,
      contentTextStyle: TextStyle(color: AppColors.text),
    ),
  );
}
