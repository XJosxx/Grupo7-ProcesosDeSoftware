
import 'package:flutter/material.dart';
import 'package:tienda_interfaz/pages/login_page.dart';

void main() {
  runApp(const StitchApp());
}

class StitchApp extends StatelessWidget {
  const StitchApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Stitch Design',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        fontFamily: 'Inter',
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF4A90E2), // Un azul más brillante y moderno
          primary: const Color(0xFF4A90E2),
          secondary: const Color(0xFF50E3C2), // Un verde azulado como color secundario
          background: const Color(0xFFF9FAFB),
          surface: Colors.white,
          error: const Color(0xFFD0021B),
        ),
        useMaterial3: true,
      ),
      home: const LoginPage(), // Set LoginPage as the home screen
    );
  }
}
