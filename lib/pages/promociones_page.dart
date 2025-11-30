import 'package:flutter/material.dart';
import '../modelos/product_model.dart';
import '../bridge_flutter.dart';
import 'package:dropdown_search/dropdown_search.dart';
import '../widgets/stitch_loader.dart';
import '../widgets/optimized_image.dart'; // Asegúrate de tener este import

class PromocionesPage extends StatefulWidget {
  const PromocionesPage({super.key});

  @override
  State<PromocionesPage> createState() => _PromocionesPageState();
}

class _PromocionesPageState extends State<PromocionesPage> {
  final BridgeFlutter _bridge = BridgeFlutter();
  List<Product> _products = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadProducts();
  }

  Future<void> _loadProducts() async {
    setState(() => _isLoading = true);
    try {
      final List<dynamic> rawProducts = await _bridge.obtenerProductos();
      _products = rawProducts.map((p) {
        return Product(
          id: p['id'].toString(),
          name: p['nombre'] ?? 'Sin nombre',
          category: p['categoriaNombre'] ?? 'General',
          price: (p['precioVenta'] ?? 0).toDouble(),
          stock: p['stock'] ?? 0,
          imagePath: p['imagePath'],
          // Ahora sí mapeamos la oferta desde el backend
          salePrice: p['salePrice'] != null ? (p['salePrice'] as num).toDouble() : null,
        );
      }).toList();
    } catch (e) {
      print("Error loading products: $e");
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  // Actualizar promoción (poner precio oferta)
  Future<void> _setPromotion(Product product, double? promoPrice) async {
    try {
      setState(() => _isLoading = true);

      // Preparamos el mapa completo del producto para actualizar
      final productMap = {
        'id': int.parse(product.id),
        'nombre': product.name,
        'precioCompra': 0.0, // Dato requerido por backend
        'precioVenta': product.price, // Mantenemos el precio regular original
        'cantidad': product.stock,
        'categoriaNombre': product.category,
        'imagePath': product.imagePath,
        'salePrice': promoPrice, // <--- Aquí enviamos la oferta (o null para quitarla)
      };

      final response = await _bridge.actualizarProducto(productMap);

      if (mounted) {
        if (response['status'] == 'ok') {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
                content: Text(promoPrice == null ? 'Promoción eliminada' : '¡Promoción aplicada!'),
                backgroundColor: Colors.green
            ),
          );
          _loadProducts(); // Recargar para ver cambios
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Error: ${response['mensaje']}'), backgroundColor: Colors.red),
          );
          setState(() => _isLoading = false);
        }
      }
    } catch (e) {
      print("Error updating promo: $e");
      setState(() => _isLoading = false);
    }
  }

  void _showPromotionDialog({Product? productToEdit}) {
    Product? selectedProduct = productToEdit;
    final salePriceController = TextEditingController(
      text: productToEdit?.salePrice?.toString() ?? '',
    );
    final formKey = GlobalKey<FormState>();

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(builder: (context, setStateDialog) {
          return AlertDialog(
            title: Text(productToEdit == null ? 'Crear Promoción' : 'Gestionar Promoción'),
            content: Form(
              key: formKey,
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (productToEdit == null)
                    DropdownSearch<Product>(
                      items: _products,
                      itemAsString: (Product p) => p.name,
                      selectedItem: selectedProduct,
                      popupProps: const PopupProps.menu(
                        showSearchBox: true,
                        searchFieldProps: TextFieldProps(decoration: InputDecoration(labelText: 'Buscar producto...')),
                      ),
                      dropdownDecoratorProps: const DropDownDecoratorProps(
                        dropdownSearchDecoration: InputDecoration(labelText: "Seleccionar Producto"),
                      ),
                      onChanged: (Product? product) {
                        setStateDialog(() {
                          selectedProduct = product;
                          // Si ya tiene oferta, ponerla, si no, vacío
                          salePriceController.text = product?.salePrice?.toString() ?? '';
                        });
                      },
                    )
                  else
                    Column(
                      children: [
                        Text("Producto: ${productToEdit.name}", style: const TextStyle(fontWeight: FontWeight.bold)),
                        const SizedBox(height: 5),
                        Text("Precio Regular: S/${productToEdit.price.toStringAsFixed(2)}", style: const TextStyle(color: Colors.grey)),
                      ],
                    ),

                  const SizedBox(height: 16),
                  TextFormField(
                    controller: salePriceController,
                    decoration: const InputDecoration(
                      labelText: 'Precio de Oferta (S/)',
                      hintText: 'Debe ser menor al regular',
                      prefixIcon: Icon(Icons.local_offer),
                      border: OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.number,
                    validator: (v) {
                      if (v == null || v.isEmpty) return null; // Vacío es válido (para borrar)
                      final price = double.tryParse(v);
                      if (price == null || price <= 0) return 'Inválido';
                      if (selectedProduct != null && price >= selectedProduct!.price) {
                        return 'La oferta debe ser menor al precio regular';
                      }
                      return null;
                    },
                  ),
                ],
              ),
            ),
            actions: [
              // Botón para quitar la promoción
              if (selectedProduct?.onSale == true)
                TextButton(
                  onPressed: () {
                    Navigator.pop(context);
                    _setPromotion(selectedProduct!, null); // Enviar null quita la oferta
                  },
                  child: const Text('Quitar Oferta', style: TextStyle(color: Colors.red)),
                ),

              TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancelar')),

              ElevatedButton(
                onPressed: () {
                  if (formKey.currentState!.validate() && selectedProduct != null) {
                    final text = salePriceController.text;
                    final newPrice = text.isEmpty ? null : double.parse(text);
                    Navigator.pop(context);
                    _setPromotion(selectedProduct!, newPrice);
                  }
                },
                child: const Text('Guardar'),
              ),
            ],
          );
        });
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    // Separamos los productos en oferta para mostrarlos primero o destacados
    final productsOnSale = _products.where((p) => p.onSale).toList();
    final productsRegular = _products.where((p) => !p.onSale).toList();

    // Lista combinada para mostrar
    final displayList = [...productsOnSale, ...productsRegular];

    return Scaffold(
      appBar: AppBar(
        title: const Text('Promociones'),
        automaticallyImplyLeading: false,
      ),
      body: _isLoading
          ? const StitchLoader()
          : displayList.isEmpty
          ? const Center(child: Text("No hay productos."))
          : ListView.builder(
        itemCount: displayList.length,
        padding: const EdgeInsets.all(10),
        itemBuilder: (context, index) {
          final product = displayList[index];
          return Card(
            elevation: product.onSale ? 4 : 1,
            color: product.onSale ? Colors.red[50] : Colors.white, // Fondo rojo suave si es oferta
            margin: const EdgeInsets.only(bottom: 10),
            child: ListTile(
              leading: SizedBox(
                width: 50, height: 50,
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(8),
                  child: OptimizedImage(imagePath: product.imagePath),
                ),
              ),
              title: Text(
                product.name,
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  color: product.onSale ? Colors.red[800] : Colors.black,
                ),
              ),
              subtitle: Text("Categoría: ${product.category}"),

              // DISEÑO DE PRECIO (Tachado vs Nuevo)
              trailing: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: [
                  if (product.onSale) ...[
                    Text(
                      "S/${product.price.toStringAsFixed(2)}",
                      style: const TextStyle(
                        decoration: TextDecoration.lineThrough,
                        color: Colors.grey,
                        fontSize: 12,
                      ),
                    ),
                    Text(
                      "S/${product.salePrice!.toStringAsFixed(2)}",
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.red,
                        fontSize: 16,
                      ),
                    ),
                  ] else
                    Text(
                      "S/${product.price.toStringAsFixed(2)}",
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                ],
              ),
              onTap: () => _showPromotionDialog(productToEdit: product),
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showPromotionDialog(),
        tooltip: 'Nueva Promoción',
        backgroundColor: Colors.red,
        child: const Icon(Icons.local_offer, color: Colors.white),
      ),
    );
  }
}