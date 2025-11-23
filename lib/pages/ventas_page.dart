import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:dropdown_search/dropdown_search.dart'; // Importa el dropdown
// Importa servicios y modelos
import '../modelos/product_model.dart'; // Ruta corregida
import '../servicios/product_service.dart'; // Ruta corregida
import '../modelos/report_model.dart'; // Ruta corregida
import '../servicios/ReportService.dart'; // Ruta corregida (usando tu nombre de archivo)
import '../servicios/activity_service.dart'; // 👈 NUEVA IMPORTACIÓN
import '../modelos/activity_event_model.dart'; // 👈 NUEVA IMPORTACIÓN
import 'dart:io'; // Para mostrar la imagen
import 'package:intl/intl.dart'; // 👈 NUEVA IMPORTACIÓN (para la fecha)

// 🛍️ Convertido a DefaultTabController para las pestañas
class VentasPage extends StatelessWidget {
  const VentasPage({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3, // Catálogo, Promociones, Venta Rápida
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Ventas'),
          automaticallyImplyLeading: false,
          bottom: const TabBar(
            tabs: [
              Tab(icon: Icon(Icons.store), text: 'Catálogo'),
              Tab(icon: Icon(Icons.star), text: 'Promociones'),
              Tab(icon: Icon(Icons.flash_on), text: 'Venta Rápida'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            // Pestaña 1: Catálogo
            _buildCatalogTab(context, isPromosOnly: false),
            // Pestaña 2: Promociones
            _buildCatalogTab(context, isPromosOnly: true),
            // Pestaña 3: Venta Rápida
            _buildQuickSaleTab(context),
          ],
        ),
      ),
    );
  }

  // 🛍️ PESTAÑA 1 y 2: CATÁLOGO Y PROMOCIONES
  Widget _buildCatalogTab(BuildContext context, {required bool isPromosOnly}) {
    final productService = ProductService.instance;

    return ValueListenableBuilder<List<Product>>(
      valueListenable: productService.productsNotifier,
      builder: (context, products, child) {
        // Filtra por promos o muestra todos
        final productList = isPromosOnly
            ? productService.getPromotions()
            : products;

        if (productList.isEmpty) {
          return Center(
            child: Text(isPromosOnly
                ? "No hay promociones activas."
                : "No hay productos en el catálogo."),
          );
        }

        // Grid similar a la página de productos
        return GridView.builder(
          padding: const EdgeInsets.all(8),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            mainAxisSpacing: 8,
            crossAxisSpacing: 8,
            childAspectRatio: 0.70, // 👈 CAMBIO AQUÍ (antes era 0.8)
          ),
          itemCount: productList.length,
          itemBuilder: (context, index) {
            final product = productList[index];
            return InkWell(
              onTap: () {
                _showCatalogSaleDialog(context, product);
              },
              child: _buildProductCard(context, product),
            );
          },
        );
      },
    );
  }

  // Tarjeta de producto (copiada de 'productos_page' para esta vista)
  Widget _buildProductCard(BuildContext context, Product product) {
    // 💥 CORRECCIÓN: Definir 'theme'
    final theme = Theme.of(context);
    bool onSale = product.onSale;

    return Card(
      clipBehavior: Clip.antiAlias,
      child: Stack(
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              AspectRatio(
                aspectRatio: 1.2,
                child: Container(
                  color: Colors.grey[200],
                  child: product.imagePath != null
                      ? Image.file(
                    File(product.imagePath!),
                    fit: BoxFit.cover,
                  )
                      : const Center(
                      child: Icon(Icons.image, color: Colors.grey, size: 40)),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      product.name,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      product.category,
                      style: const TextStyle(color: Colors.grey, fontSize: 12),
                    ),
                    const SizedBox(height: 4),
                    if (onSale)
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            "S/${product.salePrice!.toStringAsFixed(2)}", // 👈 CAMBIO S/
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                              color: theme.colorScheme.secondary,
                              fontSize: 16,
                            ),
                          ),
                          Text(
                            "S/${product.price.toStringAsFixed(2)}", // 👈 CAMBIO S/
                            style: TextStyle(
                              color: Colors.grey,
                              decoration: TextDecoration.lineThrough,
                              fontSize: 12,
                            ),
                          ),
                        ],
                      )
                    else
                      Text(
                        "S/${product.price.toStringAsFixed(2)}", // 👈 CAMBIO S/
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          color: theme.colorScheme.primary,
                          fontSize: 16,
                        ),
                      ),
                  ],
                ),
              ),
            ],
          ),
          if (onSale)
            Positioned(
              top: 8,
              right: 8,
              child: Container(
                padding: const EdgeInsets.all(4),
                decoration: BoxDecoration(
                  color: theme.colorScheme.secondary,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: const Text(
                  "OFERTA",
                  style: TextStyle(
                      color: Colors.white,
                      fontSize: 10,
                      fontWeight: FontWeight.bold),
                ),
              ),
            ),
        ],
      ),
    );
  }

  // 🛍️ PESTAÑA 3: VENTA RÁPIDA (Tu lógica anterior)
  Widget _buildQuickSaleTab(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          ElevatedButton.icon(
            icon: const Icon(Icons.flash_on, size: 28),
            label: const Text('Venta Rápida Manual'),
            onPressed: () => _showQuickSaleDialog(context),
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(220, 60),
              textStyle: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              elevation: 4,
            ),
          ),
        ],
      ),
    );
  }

  // --- LÓGICA DE DIÁLOGOS Y VENTAS ---

  // 🛍️ Diálogo para Venta Rápida (Manual)
  void _showQuickSaleDialog(BuildContext context) {
    final productController = TextEditingController();
    final priceController = TextEditingController();
    final quantityController = TextEditingController(text: '1');
    final formKey = GlobalKey<FormState>();

    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: const Text('Venta Rápida'),
          // 💥 CORRECCIÓN OVERFLOW:
          // Se envuelve en SingleChildScrollView para evitar las rayas amarillas
          content: SingleChildScrollView(
            child: Form(
              key: formKey,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  TextFormField(
                    controller: productController,
                    decoration: const InputDecoration(labelText: 'Nombre del Producto'),
                    validator: (v) => v!.isEmpty ? 'Ingrese un nombre' : null,
                  ),
                  TextFormField(
                    controller: priceController,
                    keyboardType: TextInputType.number,
                    // 💸 LÓGICA IGV: Se pide el precio FINAL
                    decoration: const InputDecoration(labelText: 'Precio Final del Producto (S/)'),
                    validator: (v) => (v == null || double.tryParse(v) == null)
                        ? 'Ingrese un precio'
                        : null,
                  ),
                  TextFormField(
                    controller: quantityController,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: 'Cantidad'),
                    validator: (v) => (v == null || int.tryParse(v) == null)
                        ? 'Ingrese una cantidad'
                        : null,
                  ),
                ],
              ),
            ),
          ),
          actions: [
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

  // 🛍️ Diálogo para Venta por Catálogo
  void _showCatalogSaleDialog(BuildContext context, Product product) {
    final quantityController = TextEditingController(text: '1');
    final formKey = GlobalKey<FormState>();
    // El precio de venta es el de oferta si existe, si no, el normal
    final double salePrice = product.onSale ? product.salePrice! : product.price;

    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: Text(product.name),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Text(
                  "Precio: S/${salePrice.toStringAsFixed(2)}", // 👈 CAMBIO S/
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                TextFormField(
                  controller: quantityController,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(labelText: 'Cantidad'),
                  validator: (v) => (v == null || int.tryParse(v) == null || int.parse(v) <= 0)
                      ? 'Cantidad inválida'
                      : null,
                ),
              ],
            ),
          ),
          actions: [
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
                    product.name,
                    salePrice,
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

  // ⭐️⭐️⭐️ ¡BOLETA BONITA! ⭐️⭐️⭐️
  // Función para generar la boleta (Lógica de Reportes)
  // 💸 LÓGICA IGV: 'basePrice' ahora es 'finalPricePerUnit'
  void _generateInvoice(BuildContext context, String product, double finalPricePerUnit, int quantity) {

    // 💸 LÓGICA IGV: El cálculo ahora se basa en el Precio Final
    // 1. Calcular el Total
    final double total = finalPricePerUnit * quantity;
    // 2. Calcular el Subtotal (dividiendo entre 1.18)
    final double subtotal = total / 1.18;
    // 3. Calcular el IGV (restando)
    final double igbAmount = total - subtotal;

    final DateTime now = DateTime.now();

    // 💸 4. Crea el reporte con los valores correctos
    final newReport = Report(
      id: now.millisecondsSinceEpoch.toString(), // Usa el timestamp como ID
      productName: product,
      quantity: quantity,
      subtotal: subtotal, // 👈 Guarda el subtotal calculado
      igv: igbAmount, // 👈 Guarda el IGV calculado
      total: total, // 👈 Guarda el total
      date: now,
    );
    ReportService.instance.addReport(newReport);

    // ⭐️ YAPE: Añade a la actividad reciente
    ActivityService.instance.addActivity(
      ActivityEvent(
        title: 'Venta registrada',
        subtitle: 'S/${total.toStringAsFixed(2)} - $product',
        icon: Icons.shopping_cart,
        color: Colors.green,
        timestamp: now,
      ),
    );

    // ⭐️ Muestra el nuevo diálogo de boleta "Bonita"
    showDialog(
      context: context,
      builder: (context) {
        final theme = Theme.of(context);
        return AlertDialog(
          // Bordes redondeados
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          // Quitamos el padding del título para poner el ícono
          titlePadding: const EdgeInsets.all(0),
          title: Container(
            padding: const EdgeInsets.only(top: 24, bottom: 16),
            width: double.infinity,
            decoration: BoxDecoration(
              color: Colors.green[50], // Verde claro de éxito
              borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
            ),
            child: const Column(
              children: [
                Icon(Icons.check_circle_outline, color: Colors.green, size: 60),
                SizedBox(height: 12),
                Text('¡Boleta Generada!', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
              ],
            ),
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min, // Para que se ajuste al contenido
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              const Text(
                "TOTAL PAGADO",
                style: TextStyle(color: Colors.grey, fontSize: 12),
              ),
              // Total grande y visible
              Text(
                'S/${total.toStringAsFixed(2)}',
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 36,
                  color: theme.colorScheme.primary,
                ),
              ),
              const SizedBox(height: 16),
              const Divider(),
              const SizedBox(height: 16),
              // Detalles de la boleta con formato
              _buildDetailRow("Producto:", "$product (x$quantity)"),
              const SizedBox(height: 8),
              // 💸 LÓGICA IGV: Muestra los valores calculados
              _buildDetailRow("Subtotal:", "S/${subtotal.toStringAsFixed(2)}"),
              const SizedBox(height: 8),
              _buildDetailRow("IGV (18%):", "S/${igbAmount.toStringAsFixed(2)}"),
              const SizedBox(height: 8),
              _buildDetailRow("ID Transacción:", newReport.id),
              const SizedBox(height: 8),
              _buildDetailRow("Fecha:", DateFormat('dd/MM/yy hh:mma').format(now)),
              const SizedBox(height: 24),
              const Center(
                child: Text(
                  '¡Gracias por tu compra!',
                  style: TextStyle(fontStyle: FontStyle.italic, color: Colors.grey),
                ),
              ),
            ],
          ),
          actionsPadding: const EdgeInsets.all(16),
          actions: [
            // Botón de cerrar más moderno
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30),
                    )
                ),
                child: const Text('Cerrar', style: TextStyle(fontSize: 16)),
                onPressed: () => Navigator.of(context).pop(),
              ),
            ),
          ],
        );
      },
    );
  }

  // ⭐️ NUEVO: Widget helper para las filas de detalles de la boleta
  Widget _buildDetailRow(String title, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(color: Colors.grey, fontSize: 14),
        ),
        const SizedBox(width: 10),
        // Flexible para que el texto largo (como el ID) se ajuste
        Flexible(
          child: Text(
            value,
            textAlign: TextAlign.end,
            style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
          ),
        ),
      ],
    );
  }
}