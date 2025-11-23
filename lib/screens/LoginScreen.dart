import '../../layout/main_layout.dart';
import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  bool _showButtons = true;
  String _selectedRole = '';

  final _userController = TextEditingController();
  final _passController = TextEditingController();

  @override
  void dispose() {
    _userController.dispose();
    _passController.dispose();
    super.dispose();
  }

  void _selectRole(String role) {
    setState(() {
      _showButtons = false;
      _selectedRole = role;
    });
  }

  void _login() {
    if (_userController.text.toLowerCase() == _selectedRole &&
        _passController.text.isNotEmpty) {
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(
          builder: (context) => MainLayout(userRole: _selectedRole),
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Usuario o contraseña incorrectos.'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          color: Theme.of(context).colorScheme.background,
        ),
        child: Center(
          child: AnimatedSwitcher(
            duration: const Duration(milliseconds: 400),
            transitionBuilder: (child, animation) {
              return FadeTransition(opacity: animation, child: child);
            },
            child: _showButtons ? _buildButtonSelection() : _buildLoginForm(),
          ),
        ),
      ),
    );
  }

  Widget _buildButtonSelection() {
    return SingleChildScrollView(
      key: const ValueKey('buttons'),
      padding: const EdgeInsets.all(32.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[

          _buildLogo(context)
              .animate()
              .fadeIn(duration: 500.ms)
              .scale(delay: 200.ms),
          const SizedBox(height: 20),

          // ❌ "Bienvenido a NeliShop" SE HA QUITADO DE AQUÍ

          Text(
            'Iniciar Sesión Como',
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(color: Theme.of(context).colorScheme.primary, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 50),
          _buildRoleButton(
            context,
            title: 'Administrador',
            icon: Icons.admin_panel_settings,
            onPressed: () => _selectRole('admin'),
          ),
          const SizedBox(height: 20),
          _buildRoleButton(
            context,
            title: 'Usuario',
            icon: Icons.person,
            onPressed: () => _selectRole('user'),
          ),
        ],
      ).animate().slideY(begin: 0.1, end: 0, duration: 400.ms),
    );
  }

  Widget _buildLoginForm() {
    final theme = Theme.of(context);

    // ⭐️ AÑADIDO: Stack para poner la flecha encima
    return Stack(
      children: [
        SingleChildScrollView(
          key: const ValueKey('loginForm'),
          padding: const EdgeInsets.all(32.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // ⭐️ LOGO DE NELISHOP (TEXTO DENTRO DEL ICONO)
              _buildLogo(context)
                  .animate()
                  .fadeIn(duration: 500.ms)
                  .slideY(begin: -0.2, end: 0),

              // ✅ "Bienvenido a NeliShop" SE HA AÑADIDO AQUÍ
              const SizedBox(height: 20),
              Text(
                'Bienvenido a NeliShop',
                style: Theme.of(context)
                    .textTheme
                    .headlineSmall
                    ?.copyWith(color: Colors.grey[700]),
              ).animate().fadeIn(delay: 200.ms, duration: 400.ms),

              const SizedBox(height: 30),
              _buildTextField(
                controller: _userController,
                labelText: 'Usuario',
                hintText: "Escribe '$_selectedRole'",
              ),
              const SizedBox(height: 16),
              _buildTextField(
                controller: _passController,
                labelText: 'Contraseña',
                hintText: 'Tu contraseña',
                obscureText: true,
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size(double.infinity, 50),
                ),
                onPressed: _login,
                child: const Text('Login', style: TextStyle(fontSize: 18)),
              ),
            ],
          ).animate().fadeIn(duration: 300.ms),
        ),
        // ⭐️ AÑADIDO: Flecha de regreso
        Positioned(
          // Posición: Arriba a la izquierda, con padding
          top: MediaQuery.of(context).padding.top + 10,
          left: 16,
          child: IconButton(
            icon: Icon(Icons.arrow_back_ios_new, color: theme.colorScheme.primary),
            onPressed: () {
              setState(() {
                _showButtons = true; // Vuelve a la selección de rol
              });
            },
          ),
        ),
      ],
    );
  }

  // ⭐️ NUEVO WIDGET DE LOGO (NS DENTRO DE LA BOLSA)
  Widget _buildLogo(BuildContext context) {
    final theme = Theme.of(context);
    final primaryColor = theme.colorScheme.primary;

    return Stack(
      alignment: Alignment.center,
      children: [
        // La cartera (bolsa)
        Icon(
          Icons.shopping_bag_outlined,
          color: primaryColor,
          size: 100,
        ),

        // Las letras "NS", movidas 10px hacia abajo para centrarse
        Transform.translate(
          offset: const Offset(0, 10.0), // Mueve el texto 10px hacia abajo
          child: Text(
            "NS",
            style: TextStyle(
              color: primaryColor,
              fontSize: 32, // Tamaño para que quepa dentro
              fontWeight: FontWeight.w900,
              letterSpacing: -1,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildTextField({
    required TextEditingController controller,
    required String labelText,
    required String hintText,
    bool obscureText = false,
  }) {
    final theme = Theme.of(context);
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: TextField(
        controller: controller,
        obscureText: obscureText,
        style: TextStyle(color: theme.colorScheme.onSurface),
        decoration: InputDecoration(
          labelText: labelText,
          hintText: hintText,
          hintStyle: TextStyle(color: Colors.grey[400]),
          labelStyle: TextStyle(color: theme.colorScheme.primary),
          border: InputBorder.none,
          contentPadding:
          const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        ),
      ),
    );
  }

  Widget _buildRoleButton(BuildContext context,
      {required String title,
        required IconData icon,
        required VoidCallback onPressed}) {
    final theme = Theme.of(context);
    return OutlinedButton.icon(
      icon: Icon(icon, size: 24),
      label: Text(title, style: const TextStyle(fontSize: 18)),
      style: OutlinedButton.styleFrom(
        foregroundColor: theme.colorScheme.primary,
        minimumSize: const Size(double.infinity, 55),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(30.0),
        ),
        padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 15),
        side: BorderSide(color: theme.colorScheme.primary, width: 1.5),
      ),
      onPressed: onPressed,
    );
  }
}