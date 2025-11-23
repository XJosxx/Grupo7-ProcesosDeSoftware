import 'package:flutter/animation.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../pages/compras_page.dart'; // Ruta corregida
import '../pages/home_page.dart'; // Ruta corregida
import '../pages/productos_page.dart'; // Ruta corregida
import '../pages/reportes_page.dart'; // Ruta corregida
import '../pages/ventas_page.dart'; // Ruta corregida
import '../pages/promociones_page.dart'; // Ruta corregida


class MainLayout extends StatefulWidget {
  final String userRole;
  const MainLayout({super.key, required this.userRole});

  @override
  State<MainLayout> createState() => _MainLayoutState();
}

class _MainLayoutState extends State<MainLayout> {
  late final PageController _pageController;
  int _selectedIndex = 0;

  late final List<Widget> _pages;
  late final List<BottomNavigationBarItem> _navBarItems;

  @override
  void initState() {
    super.initState();
    _pageController = PageController();

    if (widget.userRole == 'admin') {
      _pages = [
        HomePage(userRole: widget.userRole),
        const ProductosPage(),
        const VentasPage(),
        const ComprasPage(),
        const PromocionesPage(), // ⭐️ NUEVA PÁGINA PARA ADMIN
        const ReportesPage(),
      ];
      _navBarItems = const [
        BottomNavigationBarItem(icon: Icon(Icons.home), label: "Inicio"),
        BottomNavigationBarItem(
            icon: Icon(Icons.inventory_2), label: "Productos"),
        BottomNavigationBarItem(
            icon: Icon(Icons.receipt_long), label: "Ventas"),
        BottomNavigationBarItem(
            icon: Icon(Icons.local_shipping), label: "Compras"),
        BottomNavigationBarItem(
          // ⭐️ NUEVA PESTAÑA PARA ADMIN
            icon: Icon(Icons.local_offer),
            label: "Promos"),
        BottomNavigationBarItem(
            icon: Icon(Icons.bar_chart), label: "Reportes"),
      ];
    } else {
      // Usuario normal (no ve Compras, Promociones ni Reportes)
      _pages = [
        HomePage(userRole: widget.userRole),
        const ProductosPage(),
        const VentasPage(),
      ];
      _navBarItems = const [
        BottomNavigationBarItem(icon: Icon(Icons.home), label: "Inicio"),
        BottomNavigationBarItem(
            icon: Icon(Icons.inventory_2), label: "Productos"),
        BottomNavigationBarItem(
            icon: Icon(Icons.receipt_long), label: "Ventas"),
      ];
    }
  }

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
    _pageController.animateToPage(
      index,
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.background,
      body: PageView(
        controller: _pageController,
        onPageChanged: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        children: _pages,
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        items: _navBarItems,
      ),
    );
  }
}