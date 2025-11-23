import 'package:flutter/material.dart';
import '../modelos/product_model.dart'; // Ruta corregida
import '../servicios/product_service.dart'; // Ruta corregida
import 'package:dropdown_search/dropdown_search.dart'; // Importa el dropdown

class PromocionesPage extends StatefulWidget {
  const PromocionesPage({super.key});

  @override
  State<PromocionesPage> createState() => _PromocionesPageState();
}

class _PromocionesPageState extends State<PromocionesPage> {
  final ProductService _productService = ProductService.instance;

  // Diálogo para crear o editar una promoción
  void _showPromotionDialog({Product? productToEdit}) {
    Product? selectedProduct = productToEdit;
    final salePriceController = TextEditingController(
      text: productToEdit?.salePrice?.toString() ?? '',
    );
    final formKey = GlobalKey<FormState>();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text(productToEdit == null ? 'Crear Promoción' : 'Editar Promoción'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // Selector de producto (usa dropdown_search)
                DropdownSearch<Product>(
                  // Deshabilita si estamos editando
                  enabled: productToEdit == null,

                  // items: _productService.getProducts(),
                  asyncItems: (filter) async {
                    // Simula una búsqueda
                    return _productService.getProducts()
                        .where((p) => p.name.toLowerCase().contains(filter.toLowerCase()))
                        .toList();
                  },

                  itemAsString: (Product p) => p.name, // Muestra el nombre

                  selectedItem: selectedProduct,

                  popupProps: const PopupProps.menu(
                    showSearchBox: true,
                    searchFieldProps: TextFieldProps(
                      decoration: InputDecoration(labelText: 'Buscar producto...'),
                    ),
                  ),

                  dropdownDecoratorProps: const DropDownDecoratorProps(
                    dropdownSearchDecoration: InputDecoration(
                      labelText: "Seleccionar Producto",
                    ),
                  ),

                  onChanged: (Product? product) {
                    selectedProduct = product;
                  },

                  validator: (p) => p == null ? 'Debe seleccionar un producto' : null,
                ),

                const SizedBox(height: 16),

                // Campo de precio de oferta
                TextFormField(
                  controller: salePriceController,
                  decoration: const InputDecoration(labelText: 'Precio de Oferta (S/)'), // 👈 CAMBIO
                  keyboardType: TextInputType.number,
                  validator: (v) {
                    if (v == null || v.isEmpty) return 'Requerido';
                    final price = double.tryParse(v);
                    if (price == null) return 'Precio inválido';
                    if (selectedProduct != null && price >= selectedProduct!.price) {
                      return 'El precio de oferta debe ser menor al original';
                    }
                    return null;
                  },
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Cancelar'),
            ),
            ElevatedButton(
              onPressed: () {
                if (formKey.currentState!.validate() && selectedProduct != null) {
                  final salePrice = double.parse(salePriceController.text);
                  // Llama al servicio para crear la promoción
                  _productService.setPromotion(selectedProduct!.id, salePrice);
                  Navigator.of(context).pop();
                }
              },
              child: const Text('Guardar'),
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
        title: const Text('Gestionar Promociones'),
        automaticallyImplyLeading: false,
      ),
      // Escucha los cambios para mostrar la lista de promociones
      body: ValueListenableBuilder<List<Product>>(
        valueListenable: _productService.productsNotifier,
        builder: (context, products, child) {
          final promos = _productService.getPromotions();

          if (promos.isEmpty) {
            return const Center(
              child: Text("No hay promociones activas."),
            );
          }

          // Lista de promociones activas
          return ListView.builder(
            itemCount: promos.length,
            itemBuilder: (context, index) {
              final product = promos[index];
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                child: ListTile(
                  title: Text(product.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Text(
                    "Precio Original: S/${product.price.toStringAsFixed(2)}", // 👈 CAMBIO
                    style: const TextStyle(decoration: TextDecoration.lineThrough),
                  ),
                  trailing: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        "Oferta: S/${product.salePrice!.toStringAsFixed(2)}", // 👈 CAMBIO
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.secondary,
                          fontSize: 16,
                        ),
                      ),
                    ],
                  ),
                  onTap: () {
                    // Permite editar la promoción
                    _showPromotionDialog(productToEdit: product);
                  },
                  // Botón para eliminar promoción
                  leading: IconButton(
                    icon: const Icon(Icons.delete, color: Colors.red),
                    onPressed: () {
                      _productService.removePromotion(product.id);
                    },
                  ),
                ),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showPromotionDialog,
        tooltip: 'Crear Promoción',
        child: const Icon(Icons.add),
      ),
    );
  }
}