import '../screens/LoginScreen.dart'; // Ruta corregida
import 'package:flutter/material.dart';

void main() {
  runApp(const StitchApp());
}

class StitchApp extends StatelessWidget {
  const StitchApp({super.key});

  @override
  Widget build(BuildContext context) {
    // 🎨 NUEVO TEMA: Colores más vivos y femeninos
    final Color primaryColor = const Color(0xFF00695C); // Verde Teal
    final Color secondaryColor = const Color(0xFFFF7043); // Coral Vívido
    final Color backgroundColor = const Color(0xFFF5F5F5); // Gris muy claro

    return MaterialApp(
      title: 'Stitch Design',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        fontFamily: 'Inter',
        colorScheme: ColorScheme.fromSeed(
          seedColor: primaryColor,
          primary: primaryColor,
          secondary: secondaryColor,
          background: backgroundColor,
          surface: Colors.white,
          onPrimary: Colors.white,
          onSecondary: Colors.white,
          onBackground: const Color(0xFF212121), // Texto oscuro
          onSurface: const Color(0xFF212121),
        ),
        useMaterial3: true,

        // Estilo de AppBar
        appBarTheme: AppBarTheme(
          backgroundColor: Colors.white, // Fondo blanco para limpieza
          foregroundColor: primaryColor, // Título e íconos en color primario
          elevation: 0.5, // Sombra sutil
          iconTheme: IconThemeData(color: primaryColor),
          titleTextStyle: TextStyle(
            fontFamily: 'Inter',
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: primaryColor,
          ),
        ),

        // Estilo de BottomNavigationBar
        bottomNavigationBarTheme: BottomNavigationBarThemeData(
          selectedItemColor: primaryColor,
          unselectedItemColor: Colors.grey[600],
          backgroundColor: Colors.white,
          type: BottomNavigationBarType.fixed,
          elevation: 2,
        ),

        // SE ELIMINARON cardTheme Y elevatedButtonTheme
        // para evitar errores de caché del editor.

      ),
      home: const LoginScreen(),
    );
  }
}