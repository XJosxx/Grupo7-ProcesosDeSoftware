
import 'package:flutter/material.dart';
import '../pages/home_page.dart';
import '../pages/productos_page.dart';
import '../pages/ventas_page.dart';
import '../pages/compras_page.dart';
import '../pages/reportes_page.dart';

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
      // Removed 'const' from this list
      _pages = [
        const HomePage(),
        const ProductosPage(),
        const VentasPage(),
        const ComprasPage(),
        const ReportesPage(),
      ];
      _navBarItems = const [
        BottomNavigationBarItem(icon: Icon(Icons.home), label: "Inicio"),
        BottomNavigationBarItem(icon: Icon(Icons.inventory_2), label: "Productos"),
        BottomNavigationBarItem(icon: Icon(Icons.receipt_long), label: "Ventas"),
        BottomNavigationBarItem(icon: Icon(Icons.local_shipping), label: "Compras"),
        BottomNavigationBarItem(icon: Icon(Icons.bar_chart), label: "Reportes"),
      ];
    } else { // Regular user
      // Removed 'const' from this list
      _pages = [
        const HomePage(),
        const ProductosPage(),
        const VentasPage(),
      ];
      _navBarItems = const [
        BottomNavigationBarItem(icon: Icon(Icons.home), label: "Inicio"),
        BottomNavigationBarItem(icon: Icon(Icons.inventory_2), label: "Productos"),
        BottomNavigationBarItem(icon: Icon(Icons.receipt_long), label: "Ventas"),
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
      duration: const Duration(milliseconds: 400),
      curve: Curves.easeInOut,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
        selectedItemColor: Theme.of(context).colorScheme.primary,
        unselectedItemColor: Colors.grey,
        type: BottomNavigationBarType.fixed,
        items: _navBarItems,
      ),
    );
  }
}
