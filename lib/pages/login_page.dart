
import 'package:flutter/material.dart';
import 'package:tienda_interfaz/layout/main_layout.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    )..forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _loginAs(String role) {
    Navigator.of(context).pushReplacement(
      MaterialPageRoute(builder: (context) => MainLayout(userRole: role)),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              Theme.of(context).colorScheme.primary,
              Theme.of(context).colorScheme.secondary,
            ],
          ),
        ),
        child: Center(
          child: FadeTransition(
            opacity: _controller,
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(32.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  const Icon(Icons.storefront, size: 80, color: Colors.white),
                  const SizedBox(height: 20),
                  Text(
                    'Iniciar Sesión Como',
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(color: Colors.white, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 50),
                  _buildLoginButton(
                    context,
                    title: 'Administrador',
                    icon: Icons.admin_panel_settings,
                    onPressed: () => _loginAs('admin'),
                  ),
                  const SizedBox(height: 20),
                  _buildLoginButton(
                    context,
                    title: 'Usuario 1',
                    icon: Icons.person,
                    onPressed: () => _loginAs('user'),
                  ),
                  const SizedBox(height: 20),
                  _buildLoginButton(
                    context,
                    title: 'Usuario 2',
                    icon: Icons.person_outline,
                    onPressed: () => _loginAs('user'),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLoginButton(BuildContext context, {required String title, required IconData icon, required VoidCallback onPressed}) {
    return ElevatedButton.icon(
      icon: Icon(icon, size: 24),
      label: Text(title, style: const TextStyle(fontSize: 18)),
      style: ElevatedButton.styleFrom(
        foregroundColor: Theme.of(context).colorScheme.primary,
        backgroundColor: Colors.white,
        minimumSize: const Size(double.infinity, 55),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(30.0),
        ),
        padding: const EdgeInsets.symmetric(horizontal: 30, vertical: 15),
      ),
      onPressed: onPressed,
    );
  }
}
