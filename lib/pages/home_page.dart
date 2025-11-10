// home_page.dart (Modificado)

import 'package:flutter/material.dart';
import '../widgets/summary_card.dart';
import '../widgets/activity_card.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    // Definimos el nuevo color primario para usar en AppBar
    final Color primaryColor = Theme.of(context).colorScheme.primary;
    final Color secondaryColor = Theme.of(context).colorScheme.secondary;

    return Scaffold(
      appBar: AppBar(
        title: const Text("Hola, Nely!"),
        // Nuevo: Usa el color principal del tema
        backgroundColor: primaryColor,
        // Nuevo: Texto e íconos blancos para contrastar
        foregroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.menu),
          onPressed: () { /* TODO: Implementar menú lateral */ },
        ),
        actions: [
          // Funcionalidad: Círculo para ver perfil
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: GestureDetector(
              onTap: () {
                // TODO: Implementar navegación a la pantalla de Perfil
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Navegar a Perfil...')),
                );
              },
              child: CircleAvatar(
                backgroundColor: Colors.white.withOpacity(0.8),
                radius: 18,
                child: Icon(Icons.person, color: primaryColor, size: 20),
              ),
            ),
          ),
        ],
      ),
      // ... (el resto del cuerpo del SingleChildScrollView permanece igual)
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              "Resumen del negocio",
              style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            GridView.count(
              crossAxisCount: 2,
              crossAxisSpacing: 12,
              mainAxisSpacing: 12,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              children: [
                // Uso de colores del tema para las tarjetas
                SummaryCard(
                  icon: Icons.attach_money,
                  title: "Ventas totales",
                  value: "\$12,500",
                  color: primaryColor.withOpacity(0.1),
                  iconColor: primaryColor,
                ),
                SummaryCard(
                  icon: Icons.inventory_2,
                  title: "Productos",
                  value: "350",
                  color: secondaryColor.withOpacity(0.1),
                  iconColor: secondaryColor,
                ),
                SummaryCard(
                  icon: Icons.group,
                  title: "Clientes",
                  value: "120",
                  color: Colors.orange.withOpacity(0.1),
                  iconColor: Colors.orange,
                  fullWidth: true,
                ),
              ],
            ),
            // ... (el resto de "Actividad reciente" permanece igual)
            const SizedBox(height: 24),
            const Text(
              "Actividad reciente",
              style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            const ActivityCard(
              icon: Icons.shopping_cart,
              title: "Venta de producto",
              subtitle: "Producto vendido",
              time: "Hace 2 horas",
              color: Colors.green,
            ),
            const ActivityCard(
              icon: Icons.add,
              title: "Producto agregado",
              subtitle: "Nuevo producto agregado",
              time: "Hace 4 horas",
              color: Colors.blue,
            ),
            const ActivityCard(
              icon: Icons.local_shipping,
              title: "Compra de suministros",
              subtitle: "Compra realizada",
              time: "Ayer",
              color: Colors.purple,
            ),
          ],
        ),
      ),
    );
  }
}