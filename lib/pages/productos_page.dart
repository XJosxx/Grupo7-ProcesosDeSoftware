import 'package:flutter/material.dart';

// Modelo simple para un producto
class Product {
  final String name;
  final String category;
  final double price;
  final String imageUrl;

  const Product({
    required this.name,
    required this.category,
    required this.price,
    required this.imageUrl,
  });
}

class ProductosPage extends StatelessWidget {
  const ProductosPage({super.key});

  // Datos de ejemplo
  final List<Product> _products = const [
    Product(name: "Laptop Gamer", category: "Electrónica", price: 1250.00, imageUrl: ""),
    Product(name: "Teclado Mecánico", category: "Accesorios", price: 95.50, imageUrl: ""),
    Product(name: "Monitor 4K", category: "Monitores", price: 750.00, imageUrl: ""),
    Product(name: "Silla Ergonómica", category: "Mobiliario", price: 350.00, imageUrl: ""),
    Product(name: "Mouse Inalámbrico", category: "Accesorios", price: 45.00, imageUrl: ""),
    Product(name: "Auriculares con Micrófono", category: "Audio", price: 120.00, imageUrl: ""),
    Product(name: "Mochila para Laptop", category: "Accesorios", price: 60.00, imageUrl: ""),
    Product(name: "Almohada", category: "Mobiliario", price: 50, imageUrl: ""),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Productos"),
        backgroundColor: Theme.of(context).scaffoldBackgroundColor,
        elevation: 1,
        automaticallyImplyLeading: false, // Oculta el botón de regreso
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () { /* TODO: Implementar búsqueda */ },
          ),
          IconButton(
            icon: const Icon(Icons.add_circle_outline),
            onPressed: () { /* TODO: Implementar añadir producto */ },
          ),
        ],
      ),
      body: ListView.builder(
        itemCount: _products.length,
        padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 10),
        itemBuilder: (context, index) {
          final product = _products[index];
          return Card(
            elevation: 2,
            margin: const EdgeInsets.symmetric(vertical: 8),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: ListTile(
              contentPadding: const EdgeInsets.all(12),
              leading: CircleAvatar(
                radius: 30,
                backgroundColor: Colors.grey[200],
                child: const Icon(Icons.image, size: 30, color: Colors.grey), // Placeholder
              ),
              title: Text(product.name, style: const TextStyle(fontWeight: FontWeight.bold)),
              subtitle: Text(product.category),
              trailing: Text(
                "\$${product.price.toStringAsFixed(2)}",
                style: TextStyle(fontWeight: FontWeight.w600, color: Theme.of(context).colorScheme.primary, fontSize: 15),
              ),
            ),
          );
        },
      ),
    );
  }
}
