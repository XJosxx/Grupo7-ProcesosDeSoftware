// ventas_page.dart (Modificado para funcionalidad)

import 'package:flutter/material.dart';

class VentasPage extends StatelessWidget {
  const VentasPage({super.key});

  // Función para mostrar el diálogo de nueva venta
  void _showNewSaleDialog(BuildContext context, bool isQuickSale) {
    final TextEditingController productController = TextEditingController();
    final TextEditingController priceController = TextEditingController();
    final TextEditingController quantityController = TextEditingController(text: '1');
    final GlobalKey<FormState> formKey = GlobalKey<FormState>();

    // Mostrar un diálogo
    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: Text(isQuickSale ? 'Venta Rápida' : 'Nueva Venta'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                TextFormField(
                  controller: productController,
                  decoration: const InputDecoration(labelText: 'Nombre del Producto'),
                  validator: (value) => value!.isEmpty ? 'Ingrese un nombre' : null,
                ),
                TextFormField(
                  controller: priceController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: 'Precio del Producto (sin IGV)'),
                  validator: (value) => (value == null || double.tryParse(value) == null) ? 'Ingrese un precio válido' : null,
                ),
                TextFormField(
                  controller: quantityController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: 'Cantidad'),
                  validator: (value) => (value == null || int.tryParse(value) == null) ? 'Ingrese una cantidad válida' : null,
                ),
              ],
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: const Text('Cancelar'),
              onPressed: () => Navigator.of(dialogContext).pop(),
            ),
            ElevatedButton(
              child: const Text('Generar Boleta'),
              onPressed: () {
                if (formKey.currentState!.validate()) {
                  Navigator.of(dialogContext).pop();
                  _generateInvoice(
                    context,
                    productController.text,
                    double.parse(priceController.text),
                    int.parse(quantityController.text),
                  );
                }
              },
            ),
          ],
        );
      },
    );
  }

  // Función para generar la boleta y mostrar el resumen
  void _generateInvoice(BuildContext context, String product, double basePrice, int quantity) {
    const double igvRate = 0.18; // 18%

    // Cálculos
    final double subtotal = basePrice * quantity;
    final double igbAmount = subtotal * igvRate;
    final double total = subtotal + igbAmount;

    // TODO: La boleta se genera y se guarda en Reportes
    // (Esta es la lógica de negocio simulada)

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Boleta Electrónica Generada! 🧾'),
          content: SingleChildScrollView(
            child: ListBody(
              children: <Widget>[
                Text('Producto: $product x$quantity'),
                const Divider(),
                Text('Subtotal: \$${subtotal.toStringAsFixed(2)}'),
                Text('IGV (${(igvRate * 100).toStringAsFixed(0)}%): \$${igbAmount.toStringAsFixed(2)}'),
                const Divider(),
                Text(
                  'TOTAL: \$${total.toStringAsFixed(2)}',
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18),
                ),
                const SizedBox(height: 10),
                Text('Guardado en Reportes con la venta: $product', style: const TextStyle(fontStyle: FontStyle.italic)),
              ],
            ),
          ),
          actions: <Widget>[
            TextButton(
              child: const Text('Cerrar'),
              onPressed: () => Navigator.of(context).pop(),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Ventas'),
        backgroundColor: Theme.of(context).colorScheme.primary, // Usa el color primario
        foregroundColor: Colors.white,
        automaticallyImplyLeading: false,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton.icon(
              icon: const Icon(Icons.add_shopping_cart, size: 28),
              label: const Text('Nueva Venta'),
              onPressed: () => _showNewSaleDialog(context, false),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(220, 60),
                textStyle: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                backgroundColor: Theme.of(context).colorScheme.primary,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                elevation: 4,
              ),
            ),
            const SizedBox(height: 30),
            ElevatedButton.icon(
              icon: const Icon(Icons.flash_on, size: 28),
              label: const Text('Venta Rápida'),
              onPressed: () => _showNewSaleDialog(context, true),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(220, 60),
                textStyle: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                backgroundColor: Theme.of(context).colorScheme.secondary,
                foregroundColor: Colors.black87,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                elevation: 4,
              ),
            ),
          ],
        ),
      ),
    );
  }
}