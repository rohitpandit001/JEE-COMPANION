import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/theme/theme_data.dart';
import 'presentation/dashboard/dashboard_screen.dart';

void main() {
  runApp(
    const ProviderScope(
      child: JeeCompanionApp(),
    ),
  );
}

class JeeCompanionApp extends ConsumerWidget {
  const JeeCompanionApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return MaterialApp(
      title: 'JEE Companion',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.darkTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.dark,
      home: const DashboardScreen(),
    );
  }
}
