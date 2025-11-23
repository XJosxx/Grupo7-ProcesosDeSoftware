import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'dart:io'; // 👈 Importar dart:io para File
// Importa el servicio y el modelo
import '../modelos/product_model.dart'; // Ruta corregida
import '../servicios/product_service.dart'; // Ruta corregida

// 🛍️ 2. Convertido a StatefulWidget para manejar el estado de los filtros
class ProductosPage extends StatefulWidget {
  const ProductosPage({super.key});

  @override
  State<ProductosPage> createState() => _ProductosPageState();
}

class _ProductosPageState extends State<ProductosPage> {
  // Servicio de productos
  final ProductService _productService = ProductService.instance;
  late List<Product> _allProducts;

  // Estado de los filtros
  String _searchQuery = '';
  String? _selectedCategory;
  RangeValues _priceRange = const RangeValues(0, 500);

  // Lista de categorías (se genera dinámicamente)
  List<String> _categories = [];

  @override
  void initState() {
    super.initState();
    // Carga inicial de productos y categorías
    _allProducts = _productService.getProducts();
    _categories = _allProducts.map((p) => p.category).toSet().toList();

    // Actualiza el rango de precios máximo
    if (_allProducts.isNotEmpty) {
      double maxPrice = _allProducts.map((p) => p.price).reduce((a, b) => a > b ? a : b);
      _priceRange = RangeValues(0, maxPrice.ceilToDouble());
    }
  }

  // 🛍️ Método para mostrar el diálogo de filtros
  void _showFilterDialog() {
    showModalBottomSheet(
      context: context,
      builder: (context) {
        // StatefulBuilder para que el modal actualice su estado
        return StatefulBuilder(
          builder: (context, setStateInModal) {
            return Container(
              padding: const EdgeInsets.all(20),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Filtrar Productos',
                      style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 16),

                  // Filtro por Categoría
                  DropdownButtonFormField<String>(
                    value: _selectedCategory,
                    hint: const Text('Todas las categorías'),
                    onChanged: (String? newValue) {
                      setStateInModal(() {
                        _selectedCategory = newValue;
                      });
                    },
                    items: [
                      // Opción para limpiar filtro
                      const DropdownMenuItem<String>(
                        value: null,
                        child: Text('Todas las categorías'),
                      ),
                      ..._categories.map<DropdownMenuItem<String>>((String category) {
                        return DropdownMenuItem<String>(
                          value: category,
                          child: Text(category),
                        );
                      }).toList(),
                    ],
                  ),
                  const SizedBox(height: 16),

                  // Filtro por Precio
                  Text('Rango de Precio: S/${_priceRange.start.toInt()} - S/${_priceRange.end.toInt()}'),
                  RangeSlider(
                    values: _priceRange,
                    min: 0,
                    max: 500, // TODO: Hacer dinámico
                    divisions: 10,
                    labels: RangeLabels(
                      'S/${_priceRange.start.round()}',
                      'S/${_priceRange.end.round()}',
                    ),
                    onChanged: (RangeValues values) {
                      setStateInModal(() {
                        _priceRange = values;
                      });
                    },
                  ),
                  const SizedBox(height: 16),

                  // Botón para aplicar
                  Center(
                    child: ElevatedButton(
                      onPressed: () {
                        // Actualiza el estado de la página principal
                        setState(() {});
                        Navigator.pop(context);
                      },
                      child: const Text('Aplicar Filtros'),
                    ),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  // Tarjeta de producto
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
              // Imagen
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
              // Detalles
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
                    // Lógica de precio (normal o con oferta)
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
          // ⭐️ Icono de Oferta
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Productos"),
        automaticallyImplyLeading: false,
        actions: [
          // 🛍️ 2. Botón de Filtros
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: _showFilterDialog,
          ),
          // 🛍️ 2. Quitado el botón "+"
        ],
      ),
      // Escucha los cambios del servicio para redibujar si se añade un producto
      body: ValueListenableBuilder<List<Product>>(
        valueListenable: _productService.productsNotifier,
        builder: (context, products, child) {
          // Actualiza la lista local cuando el notificador cambia
          _allProducts = products;


          final List<Product> filteredList = _allProducts.where((product) {
            final nameMatch =
            product.name.toLowerCase().contains(_searchQuery.toLowerCase());
            final categoryMatch =
                _selectedCategory == null || product.category == _selectedCategory;
            final priceMatch =
                product.price >= _priceRange.start && product.price <= _priceRange.end;
            return nameMatch && categoryMatch && priceMatch;
          }).toList();

          if (filteredList.isEmpty) {
            return const Center(child: Text("No se encontraron productos."));
          }

          // 🛍️ 2. Cambiado a GridView
          return GridView.builder(
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2, // 2 columnas
              mainAxisSpacing: 8,
              crossAxisSpacing: 8,
              childAspectRatio: 0.70, // 👈 CAMBIO AQUÍ (antes era 0.8)
            ),
            itemCount: filteredList.length, // Usa la lista filtrada
            itemBuilder: (context, index) {
              final product = filteredList[index]; // Usa la lista filtrada
              return _buildProductCard(context, product)
                  .animate()
                  .fadeIn(duration: 300.ms, delay: (index % 10 * 50).ms); // Animación
            },
          );
        },
      ),
    );
  }
}