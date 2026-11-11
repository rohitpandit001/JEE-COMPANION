import 'package:flutter/material.dart';

/// JEE Companion Dark Mode Theme Constants & Material 3 ThemeData
/// Incorporates deep navy and charcoal backgrounds with electric blue and violet accents.

class AppColors {
  // Deep Navy & Charcoal Backgrounds
  static const Color bgDark = Color(0xFF080C14);
  static const Color surfaceDark = Color(0xFF0F172A);
  static const Color surfaceCard = Color(0xFF131D31);
  static const Color surfaceElevated = Color(0xFF1A2640);
  static const Color surfaceBorder = Color(0xFF233554);

  // Electric Blue & Violet Accents
  static const Color electricBlue = Color(0xFF3B82F6);
  static const Color electricBlueLight = Color(0xFF60A5FA);
  static const Color electricViolet = Color(0xFF8B5CF6);
  static const Color electricVioletLight = Color(0xFFA78BFA);
  static const Color electricCyan = Color(0xFF06B6D4);

  // Subject Signatures
  static const Color physicsColor = Color(0xFF38BDF8);
  static const Color chemistryColor = Color(0xFF34D399);
  static const Color mathsColor = Color(0xFFF472B6);

  // Semantic Status Colors
  static const Color accentGold = Color(0xFFFBBF24);
  static const Color accentEmerald = Color(0xFF10B981);
  static const Color accentRose = Color(0xFFF43F5E);
  static const Color accentOrange = Color(0xFFFB923C);

  // Neutrals & Text
  static const Color textPrimary = Color(0xFFF1F5F9);
  static const Color textSecondary = Color(0xFF94A3B8);
  static const Color textTertiary = Color(0xFF64748B);
  static const Color textDisabled = Color(0xFF475569);
}

class AppTheme {
  static final ThemeData darkTheme = ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    scaffoldBackgroundColor: AppColors.bgDark,
    colorScheme: const ColorScheme(
      brightness: Brightness.dark,
      primary: AppColors.electricBlue,
      onPrimary: AppColors.textPrimary,
      primaryContainer: AppColors.surfaceElevated,
      onPrimaryContainer: AppColors.electricBlueLight,
      secondary: AppColors.electricViolet,
      onSecondary: AppColors.textPrimary,
      secondaryContainer: AppColors.surfaceCard,
      onSecondaryContainer: AppColors.electricVioletLight,
      tertiary: AppColors.electricCyan,
      onTertiary: AppColors.textPrimary,
      error: AppColors.accentRose,
      onError: AppColors.textPrimary,
      surface: AppColors.surfaceDark,
      onSurface: AppColors.textPrimary,
      surfaceContainerHighest: AppColors.surfaceElevated,
      outline: AppColors.surfaceBorder,
    ),
    cardTheme: CardTheme(
      color: AppColors.surfaceCard,
      elevation: 0,
      shape: RoundedCornerShape(16.0),
      margin: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: AppColors.bgDark,
      foregroundColor: AppColors.textPrimary,
      elevation: 0,
      centerTitle: false,
    ),
    bottomNavigationBarTheme: const BottomNavigationBarThemeData(
      backgroundColor: AppColors.surfaceCard,
      selectedItemColor: AppColors.electricBlueLight,
      unselectedItemColor: AppColors.textSecondary,
      type: BottomNavigationBarType.fixed,
    ),
  );
}
