import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:rive/rive.dart' hide LinearGradient;
import 'package:tienda_interfazw/layout/main_layout.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  // State to toggle between button selection and login form
  bool _showButtons = true;
  String _selectedRole = '';

  // Rive animation controllers
  Artboard? _artboard;
  SMITrigger? _failTrigger, _successTrigger;
  SMIBool? _isHandsUp, _isChecking;
  SMINumber? _lookNum;
  StateMachineController? _stateMachineController;

  // Text field controllers
  final _userController = TextEditingController();
  final _passController = TextEditingController();

  @override
  void initState() {
    super.initState();

    rootBundle.load('Imagenes/login-bear.riv').then((data) {
      final file = RiveFile.import(data);
      final art = file.mainArtboard;
      _stateMachineController =
          StateMachineController.fromArtboard(art, 'Login Machine');
      if (_stateMachineController != null) {
        art.addController(_stateMachineController!);
        _isChecking = _stateMachineController!.findInput<SMIBool>('isChecking') as SMIBool?;
        _isHandsUp = _stateMachineController!.findInput<SMIBool>('isHandsUp') as SMIBool?;
        _successTrigger =
            _stateMachineController!.findInput<SMITrigger>('trigSuccess') as SMITrigger?;
        _failTrigger = _stateMachineController!.findInput<SMITrigger>('trigFail') as SMITrigger?;
        _lookNum = _stateMachineController!.findInput<SMINumber>('numLook') as SMINumber?;
      }
      if (mounted) {
        setState(() => _artboard = art);
      }
    });
  }

  @override
  void dispose() {
    _stateMachineController?.dispose();
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

  void _lookAround() {
    _isChecking?.change(true);
    _isHandsUp?.change(false);
    _lookNum?.change(0);
  }

  void _moveEyes(String value) {
    _lookNum?.change(value.length.toDouble());
  }

  void _handsUpOnEyes() {
    _isHandsUp?.change(true);
    _isChecking?.change(false);
  }

  void _login() {
    _isChecking?.change(false);
    _isHandsUp?.change(false);

    if (_userController.text.toLowerCase() == _selectedRole &&
        _passController.text.isNotEmpty) {
      _successTrigger?.fire();
      Future.delayed(const Duration(milliseconds: 1200), () {
        if (mounted) {
          Navigator.of(context).pushReplacement(
            MaterialPageRoute(
                builder: (context) => MainLayout(userRole: _selectedRole)),
          );
        }
      });
    } else {
      _failTrigger?.fire();
    }
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
          const Icon(Icons.storefront, size: 80, color: Colors.white),
          const SizedBox(height: 20),
          Text(
            'Iniciar Sesión Como',
            style: Theme.of(context)
                .textTheme
                .headlineMedium
                ?.copyWith(color: Colors.white, fontWeight: FontWeight.bold),
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
      ),
    );
  }

  Widget _buildLoginForm() {
    return SingleChildScrollView(
      key: const ValueKey('loginForm'),
      padding: const EdgeInsets.all(32.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          if (_artboard != null)
            SizedBox(
              width: 300,
              height: 250,
              child: Rive(artboard: _artboard!),
            )
          else
            const SizedBox(height: 250),
          Container(
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(8),
            ),
            child: TextField(
              onTap: _lookAround,
              onChanged: _moveEyes,
              controller: _userController,
              style: const TextStyle(color: Colors.white),
              decoration: InputDecoration(
                labelText: 'Usuario',
                hintText: "Escribe '$_selectedRole'",
                hintStyle: TextStyle(color: Colors.white.withOpacity(0.6)),
                labelStyle: const TextStyle(color: Colors.white),
                border: InputBorder.none,
                contentPadding:
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              ),
            ),
          ),
          const SizedBox(height: 16),
          Container(
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              borderRadius: BorderRadius.circular(8),
            ),
            child: TextField(
              onTap: _handsUpOnEyes,
              controller: _passController,
              obscureText: true,
              style: const TextStyle(color: Colors.white),
              decoration: const InputDecoration(
                labelText: 'Contraseña',
                labelStyle: TextStyle(color: Colors.white),
                border: InputBorder.none,
                contentPadding:
                    EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              ),
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              foregroundColor: Theme.of(context).colorScheme.primary,
              backgroundColor: Colors.white,
              minimumSize: const Size(double.infinity, 50),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(30.0),
              ),
            ),
            onPressed: _login,
            child: const Text('Login', style: TextStyle(fontSize: 18)),
          ),
        ],
      ),
    );
  }

  Widget _buildRoleButton(BuildContext context,
      {required String title,
      required IconData icon,
      required VoidCallback onPressed}) {
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
