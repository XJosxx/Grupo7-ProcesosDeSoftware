import 'package:flutter/material.dart';
import '../widgets/summary_card.dart';
import '../widgets/activity_card.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Hola, Nely!"),
        backgroundColor: Colors.white,
        foregroundColor: Colors.black87,
        elevation: 1,
        leading: IconButton(
          icon: const Icon(Icons.menu),
          onPressed: () {},
        ),
        actions: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: CircleAvatar(
              backgroundColor: Colors.grey[300],
              radius: 16,
            ),
          ),
        ],
      ),
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
              children: const [
                SummaryCard(
                  icon: Icons.attach_money,
                  title: "Ventas totales",
                  value: "\$12,500",
                  color: Color(0xFFF0F8FF),
                  iconColor: Colors.blue,
                ),
                SummaryCard(
                  icon: Icons.inventory_2,
                  title: "Productos",
                  value: "350",
                  color: Color(0xFFF7FEE7),
                  iconColor: Colors.lime,
                ),
                SummaryCard(
                  icon: Icons.group,
                  title: "Clientes",
                  value: "120",
                  color: Color(0xFFFFF7ED),
                  iconColor: Colors.orange,
                  fullWidth: true,
                ),
              ],
            ),
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
