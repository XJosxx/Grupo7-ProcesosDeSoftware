import 'package:flutter/material.dart';
import 'package:tienda_interfazw/screens/LoginScreen.dart';

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
          seedColor: const Color(0xFF1E40AF), // Nuevo Azul Primario
          primary: const Color(0xFF1E40AF),
          secondary: const Color(0xFF84CC16), // Nuevo Verde Lima Secundario
          background: const Color(0xFFF9FAFB),
          surface: Colors.white,
          error: const Color(0xFFD0021B),
        ),
        useMaterial3: true,
      ),
      home: const LoginScreen(), // Set LoginScreen as the home screen
    );
  }
}