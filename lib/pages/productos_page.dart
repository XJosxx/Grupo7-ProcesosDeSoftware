import 'package:flutter/material.dart';
import '../modelos/product_model.dart';
import '../bridge_flutter.dart';
import '../widgets/optimized_image.dart';
import '../widgets/animated_list_item.dart';
import '../widgets/stitch_loader.dart';

class ProductosPage extends StatefulWidget {
  const ProductosPage({super.key});

  @override
  State<ProductosPage> createState() => _ProductosPageState();
}

// Enum para los tipos de ordenamiento
enum SortType { nombreAZ, precioMayorMenor, precioMenorMayor }

class _ProductosPageState extends State<ProductosPage> {
  final BridgeFlutter _bridge = BridgeFlutter();

  // Datos
  List<Product> _allProducts = [];
  List<String> _categories = [];

  // Filtros
  bool _isLoading = true;
  String _searchQuery = '';
  String? _selectedCategory;
  SortType _selectedSort = SortType.nombreAZ;

  @override
  void initState() {
    super.initState();
    _loadProducts();
  }

  // Carga de datos desde Java
  Future<void> _loadProducts() async {
    if (!mounted) return;
    setState(() => _isLoading = true);

    try {
      final List<dynamic> rawProducts = await _bridge.obtenerProductos();
      _allProducts = rawProducts.map((p) {
        // Mapeo seguro de datos
        return Product(
          id: p['id'].toString(),
          name: p['nombre'] ?? 'Sin nombre',
          category: p['categoriaNombre'] ?? 'General',
          price: double.tryParse(p['precioVenta'].toString()) ?? 0.0,
          stock: int.tryParse(p['stock'].toString()) ?? 0,
          imagePath: p['imagePath'], // Ahora Java envía esto correctamente
          salePrice: p['salePrice'],
        );
      }).toList();

      // Extraer categorías únicas
      _categories = _allProducts.map((p) => p.category).toSet().toList();
      _categories.sort();

    } catch (e) {
      print("Error loading products: $e");
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  // Lógica de Filtrado en Flutter
  List<Product> _getFilteredProducts() {
    // 1. Filtrar
    List<Product> temp = _allProducts.where((product) {
      final nameMatch = product.name.toLowerCase().contains(_searchQuery.toLowerCase());
      final categoryMatch = _selectedCategory == null || product.category == _selectedCategory;
      return nameMatch && categoryMatch;
    }).toList();

    // 2. Ordenar
    temp.sort((a, b) {
      switch (_selectedSort) {
        case SortType.nombreAZ:
          return a.name.compareTo(b.name);
        case SortType.precioMayorMenor:
          return b.price.compareTo(a.price); // Descendente
        case SortType.precioMenorMayor:
          return a.price.compareTo(b.price); // Ascendente
      }
    });

    return temp;
  }

  // --- GESTIÓN: AUMENTAR / DISMINUIR STOCK ---
  Future<void> _editStock(Product product) async {
    final controller = TextEditingController();
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Stock: ${product.name}'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Actual: ${product.stock} unidades'),
            const SizedBox(height: 10),
            TextField(
              controller: controller,
              keyboardType: TextInputType.number,
              autofocus: true,
              decoration: const InputDecoration(
                labelText: 'Cantidad a AGREGAR',
                hintText: 'Ej: 5 (suma) o -2 (resta)',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar')),
          ElevatedButton(onPressed: () => Navigator.pop(context, true), child: const Text('Guardar')),
        ],
      ),
    );

    if (confirm == true && controller.text.isNotEmpty) {
      int addQty = int.tryParse(controller.text) ?? 0;
      int newTotal = product.stock + addQty;

      if (newTotal < 0) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Error: El stock no puede ser negativo')));
        return;
      }

      // IMPORTANTE: Enviamos todos los datos necesarios al Backend
      // Java ahora leerá el "id" y "imagePath" correctamente.
      final productMap = {
        'id': int.parse(product.id),
        'nombre': product.name,
        'precioCompra': 0.0, // Backend requiere double
        'precioVenta': product.price,
        'cantidad': newTotal,
        'categoriaNombre': product.category,
        'imagePath': product.imagePath,
      };

      setState(() => _isLoading = true);
      final response = await _bridge.actualizarProducto(productMap);

      if (mounted) {
        if (response['status'] == 'ok') {
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Stock actualizado'), backgroundColor: Colors.green));
          _loadProducts(); // Recargar lista
        } else {
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: ${response['mensaje']}')));
          setState(() => _isLoading = false);
        }
      }
    }
  }

  // --- GESTIÓN: ELIMINAR ---
  Future<void> _deleteProduct(Product product) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Eliminar Producto'),
        content: Text('¿Estás seguro de eliminar "${product.name}"?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Cancelar')),
          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Desactivar', style: TextStyle(color: Colors.red))),
        ],
      ),
    );

    if (confirm == true) {
      setState(() => _isLoading = true);
      final response = await _bridge.eliminarProducto(int.parse(product.id));

      if (mounted) {
        if (response['status'] == 'ok') {
          ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Producto eliminado')));
          _loadProducts();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: ${response['mensaje']}')));
          setState(() => _isLoading = false);
        }
      }
    }
  }

  // --- DIÁLOGO DE FILTROS ---
  void _showFilterDialog() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (context) => StatefulBuilder(
        builder: (context, setStateInModal) => Padding(
          padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
          child: Container(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text('Filtros y Orden', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
                    if (_selectedCategory != null || _selectedSort != SortType.nombreAZ)
                      TextButton(
                          onPressed: () {
                            setState(() { _selectedCategory = null; _selectedSort = SortType.nombreAZ; });
                            Navigator.pop(context);
                          },
                          child: const Text("Limpiar")
                      )
                  ],
                ),
                const Divider(),
                const SizedBox(height: 10),

                // Categoría
                const Text("Categoría:", style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey)),
                DropdownButtonFormField<String>(
                  value: _selectedCategory,
                  isExpanded: true,
                  hint: const Text('Todas'),
                  items: [
                    const DropdownMenuItem(value: null, child: Text("Todas")),
                    ..._categories.map((c) => DropdownMenuItem(value: c, child: Text(c))),
                  ],
                  onChanged: (v) => setStateInModal(() => _selectedCategory = v),
                ),
                const SizedBox(height: 20),

                // Orden
                const Text("Ordenar por:", style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey)),
                _buildRadioTile("Nombre (A - Z)", SortType.nombreAZ, setStateInModal),
                _buildRadioTile("Precio (Mayor a Menor)", SortType.precioMayorMenor, setStateInModal),
                _buildRadioTile("Precio (Menor a Mayor)", SortType.precioMenorMayor, setStateInModal),

                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  height: 50,
                  child: ElevatedButton(
                    onPressed: () {
                      setState(() {});
                      Navigator.pop(context);
                    },
                    child: const Text("Aplicar"),
                  ),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildRadioTile(String title, SortType val, StateSetter setModalState) {
    return RadioListTile<SortType>(
      title: Text(title),
      value: val,
      groupValue: _selectedSort,
      contentPadding: EdgeInsets.zero,
      activeColor: Theme.of(context).primaryColor,
      onChanged: (v) => setModalState(() => _selectedSort = v!),
    );
  }

  // --- TARJETA DE PRODUCTO ---
  Widget _buildProductCard(Product product) {
    final theme = Theme.of(context);
    bool outOfStock = product.stock <= 0;

    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      clipBehavior: Clip.antiAlias,
      child: Stack(
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Imagen
              Expanded(
                child: Stack(
                  children: [
                    Positioned.fill(
                      child: Opacity(
                        opacity: outOfStock ? 0.5 : 1.0,
                        child: OptimizedImage(imagePath: product.imagePath),
                      ),
                    ),
                    if (outOfStock)
                      Container(color: Colors.white54, child: const Center(child: Text("AGOTADO", style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)))),
                  ],
                ),
              ),
              // Datos
              Padding(
                padding: const EdgeInsets.all(10),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(product.name, maxLines: 1, overflow: TextOverflow.ellipsis, style: const TextStyle(fontWeight: FontWeight.bold)),
                    Text(product.category, style: const TextStyle(fontSize: 11, color: Colors.grey)),
                    const SizedBox(height: 4),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text("Stock: ${product.stock}", style: TextStyle(fontSize: 12, color: outOfStock ? Colors.red : Colors.green)),
                        Text("S/${product.price.toStringAsFixed(2)}", style: TextStyle(fontWeight: FontWeight.bold, color: theme.primaryColor)),
                      ],
                    )
                  ],
                ),
              )
            ],
          ),
          // Menú de opciones
          Positioned(
            top: 0, right: 0,
            child: PopupMenuButton<String>(
              icon: const Icon(Icons.more_vert, color: Colors.black54),
              onSelected: (v) {
                if (v == 'edit') _editStock(product);
                if (v == 'del') _deleteProduct(product);
              },
              itemBuilder: (c) => [
                const PopupMenuItem(value: 'edit', child: Row(children: [Icon(Icons.edit, color: Colors.blue), SizedBox(width: 8), Text("Editar Stock")])),
                const PopupMenuItem(value: 'del', child: Row(children: [Icon(Icons.delete, color: Colors.red), SizedBox(width: 8), Text("Eliminar")])),
              ],
            ),
          )
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final filteredList = _getFilteredProducts();

    return Scaffold(
      appBar: AppBar(
        title: const Text("Productos"),
        automaticallyImplyLeading: false,
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: _loadProducts),
          Stack(
            children: [
              IconButton(icon: const Icon(Icons.filter_list), onPressed: _showFilterDialog),
              if (_selectedCategory != null || _selectedSort != SortType.nombreAZ)
                Positioned(right: 8, top: 8, child: Container(padding: const EdgeInsets.all(4), decoration: const BoxDecoration(color: Colors.red, shape: BoxShape.circle))),
            ],
          )
        ],
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(60),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Buscar por nombre...',
                prefixIcon: const Icon(Icons.search),
                filled: true, fillColor: Colors.grey[100],
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(30), borderSide: BorderSide.none),
                contentPadding: const EdgeInsets.symmetric(horizontal: 20),
              ),
              onChanged: (val) => setState(() => _searchQuery = val),
            ),
          ),
        ),
      ),
      body: _isLoading
          ? const StitchLoader()
          : filteredList.isEmpty
          ? Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [Icon(Icons.search_off, size: 60, color: Colors.grey[300]), const Text("No hay productos")]))
          : RefreshIndicator(
        onRefresh: _loadProducts,
        child: GridView.builder(
          padding: const EdgeInsets.all(12),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            mainAxisSpacing: 12, crossAxisSpacing: 12,
            childAspectRatio: 0.70,
          ),
          itemCount: filteredList.length,
          itemBuilder: (context, index) {
            return AnimatedListItem(
              index: index,
              child: _buildProductCard(filteredList[index]),
            );
          },
        ),
      ),
    );
  }
}