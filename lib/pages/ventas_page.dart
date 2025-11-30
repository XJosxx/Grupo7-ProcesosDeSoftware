import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../modelos/product_model.dart';
import '../bridge_flutter.dart';
import '../widgets/optimized_image.dart';
import '../widgets/animated_list_item.dart';
import '../servicios/activity_service.dart';
import '../modelos/activity_event_model.dart';
import 'dart:math';
import 'package:audioplayers/audioplayers.dart';
import '../servicios/preferences_service.dart';
import '../widgets/stitch_loader.dart';

class VentasPage extends StatefulWidget {
  const VentasPage({super.key});

  @override
  State<VentasPage> createState() => _VentasPageState();
}

class _VentasPageState extends State<VentasPage> with SingleTickerProviderStateMixin {
  final BridgeFlutter _bridge = BridgeFlutter();
  List<Product> _products = [];
  //DNI aleatorio
  String _generateRandomDNI() {
    var rng = Random();
    // Genera 8 dígitos aleatorios
    return List.generate(8, (_) => rng.nextInt(10)).join();
  }

  // 🛒 ESTADO DEL CARRITO
  final List<Map<String, dynamic>> _cart = [];

  bool _isLoading = true;
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    // 4 Pestañas: Catálogo, Promociones, Venta Rápida, Carrito
    _tabController = TabController(length: 4, vsync: this);
    _loadProducts();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
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
          stock: p['cantidad'] ?? 0,
          imagePath: p['imagePath'],
          salePrice: p['salePrice'],
        );
      }).toList();
    } catch (e) {
      print("Error loading products: $e");
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  // --- LÓGICA DEL CARRITO ---
  void _addToCart(Product product, int quantity) {
    setState(() {
      final existingIndex = _cart.indexWhere((item) => item['product'].id == product.id);

      if (existingIndex >= 0) {
        int currentQty = _cart[existingIndex]['qty'];
        int newQty = currentQty + quantity;
        if (newQty > product.stock) newQty = product.stock;
        _cart[existingIndex]['qty'] = newQty;
      } else {
        _cart.add({
          'product': product,
          'qty': quantity,
          'price': product.onSale ? product.salePrice : product.price
        });
      }
    });

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${product.name} agregado al carrito'),
        duration: const Duration(milliseconds: 600),
        behavior: SnackBarBehavior.floating,
      ),
    );
  }

  void _removeFromCart(int index) {
    setState(() {
      _cart.removeAt(index);
    });
  }

  double _calculateTotal() {
    return _cart.fold(0.0, (sum, item) => sum + (item['price'] * item['qty']));
  }

  // --- PROCESAR VENTA DEL CARRITO (CHECKOUT) ---
  Future<void> _processCheckout() async {
    if (_cart.isEmpty) return;
    setState(() => _isLoading = true);

    try {
      final ventaMap = {
        'descripcion': 'Venta Carrito (${_cart.length} items)',
        'clienteDni': _generateRandomDNI(), // ⚠️ RECUERDA EJECUTAR EL SQL DEL PASO 1
      };

      final List<Map<String, dynamic>> detallesList = _cart.map((item) {
        Product p = item['product'];
        return {
          'productoId': int.parse(p.id),
          'cantidad': item['qty'],
          'precioUnitario': item['price'],
        };
      }).toList();

      final response = await _bridge.registrarVenta(ventaMap, detallesList);

      if (response['status'] == 'ok') {
        // REPRODUCIR SONIDO SI ESTÁ ACTIVADO
        if (PreferencesService.instance.soundNotifier.value) {
          try {
            final player = AudioPlayer();
            // Asegúrate de tener el archivo assets/sounds/sale.mp3
            await player.play(AssetSource('sounds/sale.mp3'));
          } catch (e) {
            print("Error reproduciendo sonido: $e");
          }
        }
        // ÉXITO REAL
        ActivityService.instance.addActivity(ActivityEvent(
          title: 'Venta Carrito',
          subtitle: '${_cart.length} items por S/${_calculateTotal().toStringAsFixed(2)}',
          icon: Icons.shopping_cart_checkout,
          color: Colors.green,
          timestamp: DateTime.now(),
        ));

        double totalVenta = _calculateTotal();

        // Limpiar y Recargar Stock
        setState(() {
          _cart.clear();
        });
        await _loadProducts();

        if (mounted) {
          _showOldStyleInvoiceDialog(response['id'].toString(), detallesList, totalVenta, isManual: false);
        }
      } else {
        // ERROR DEL BACKEND (Aquí caía antes)
        _showErrorSnack('Error Backend: ${response['mensaje']}');
      }
    } catch (e) {
      _showErrorSnack('Error de conexión: $e');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }

  }

  // --- LÓGICA VENTA RÁPIDA (MANUAL) ---
  Future<void> _processQuickSale(String name, double price, int qty) async {
    setState(() => _isLoading = true);

    final product = _products.firstWhere(
            (p) => p.name.toLowerCase() == name.toLowerCase(),
        orElse: () => Product(id: '0', name: name, category: '', price: price)
    );

    int prodId = int.parse(product.id);

    final ventaMap = {
      'descripcion': 'Venta Rápida: $name',
      'clienteDni': _generateRandomDNI(), //
    };

    final detalleMap = {
      'productoId': prodId,
      'cantidad': qty,
      'precioUnitario': price,
    };

    try {
      final response = await _bridge.registrarVenta(ventaMap, [detalleMap]);

      if (response['status'] == 'ok') {

        // REPRODUCIR SONIDO SI ESTÁ ACTIVADO
        if (PreferencesService.instance.soundNotifier.value) {
          try {
            final player = AudioPlayer();
            // Asegúrate de tener el archivo assets/sounds/sale.mp3
            await player.play(AssetSource('sounds/sale.mp3'));
          } catch (e) {
            print("Error reproduciendo sonido: $e");
          }
        }

        ActivityService.instance.addActivity(ActivityEvent(
          title: 'Venta Rápida',
          subtitle: '$name (x$qty)',
          icon: Icons.flash_on,
          color: Colors.orange,
          timestamp: DateTime.now(),
        ));

        await _loadProducts();

        if (mounted) {
          _showOldStyleInvoiceDialog(
              response['id'].toString(),
              [detalleMap],
              price * qty,
              isManual: true,
              manualName: name
          );
        }
      } else {
        _showErrorSnack('Error: ${response['mensaje']}');
      }
    } catch (e) {
      _showErrorSnack('Error: $e');
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _showErrorSnack(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(msg), backgroundColor: Colors.red));
  }

  // --- UI PRINCIPAL ---

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Punto de Venta'),
        automaticallyImplyLeading: false,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadProducts,
            tooltip: "Recargar Stock",
          )
        ],
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: [
            const Tab(icon: Icon(Icons.store), text: 'Catálogo'),
            const Tab(icon: Icon(Icons.star), text: 'Promociones'),
            const Tab(icon: Icon(Icons.flash_on), text: 'Venta Rápida'),
            Tab(
                icon: Badge(
                  isLabelVisible: _cart.isNotEmpty,
                  label: Text('${_cart.length}'),
                  child: const Icon(Icons.shopping_cart),
                ),
                text: 'Carrito'
            ),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildCatalogTab(isPromosOnly: false),
          _buildCatalogTab(isPromosOnly: true),
          _buildQuickSaleTab(),
          _buildCartTab(),
        ],
      ),
    );
  }

  // --- WIDGETS DE PESTAÑAS ---

  Widget _buildCatalogTab({required bool isPromosOnly}) {
    if (_isLoading) return const StitchLoader();

    final productList = isPromosOnly
        ? _products.where((p) => p.onSale).toList()
        : _products;

    if (productList.isEmpty) {
      return Center(child: Text(isPromosOnly ? "No hay promociones activas" : "Catálogo vacío"));
    }

    return GridView.builder(
      padding: const EdgeInsets.all(8),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 2,
          childAspectRatio: 0.60,
          crossAxisSpacing: 10,
          mainAxisSpacing: 10
      ),
      itemCount: productList.length,
      itemBuilder: (context, index) {
        final product = productList[index];
        bool outOfStock = product.stock <= 0;

        return AnimatedListItem(
          index: index,
          child: Card(
            elevation: 3,
            clipBehavior: Clip.antiAlias,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Expanded(
                  child: Stack(
                    children: [
                      Positioned.fill(child: OptimizedImage(imagePath: product.imagePath)),
                      if (outOfStock)
                        Container(color: Colors.white54, child: const Center(child: Text("AGOTADO", style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)))),
                      if (product.onSale)
                        Positioned(top: 5, right: 5, child: Container(padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2), color: Colors.red, child: const Text("OFERTA", style: TextStyle(color: Colors.white, fontSize: 10)))),
                    ],
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(product.name, maxLines: 1, overflow: TextOverflow.ellipsis, style: const TextStyle(fontWeight: FontWeight.bold)),
                      if (product.onSale) ...[
                        Text("S/${product.price.toStringAsFixed(2)}", style: const TextStyle(decoration: TextDecoration.lineThrough, fontSize: 11, color: Colors.grey)),
                        Text("S/${product.salePrice!.toStringAsFixed(2)}", style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.red, fontSize: 16)),
                      ] else
                        Text("S/${product.price.toStringAsFixed(2)}", style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),

                      Text("Stock: ${product.stock}", style: TextStyle(fontSize: 11, color: outOfStock ? Colors.red : Colors.green)),
                      const SizedBox(height: 5),
                      SizedBox(
                        height: 35, // Botón un poco más alto
                        width: double.infinity,
                        child: ElevatedButton(
                          style: ElevatedButton.styleFrom(padding: EdgeInsets.zero),
                          onPressed: outOfStock ? null : () => _showQuantityDialog(product),
                          child: const Text("Agregar al Carrito"),
                        ),
                      )
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildQuickSaleTab() {
    final nameCtrl = TextEditingController();
    final priceCtrl = TextEditingController();
    final qtyCtrl = TextEditingController(text: '1');
    final formKey = GlobalKey<FormState>();

    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Form(
        key: formKey,
        child: Column(
          children: [
            const Icon(Icons.flash_on, size: 60, color: Colors.orange),
            const SizedBox(height: 10),
            const Text("Venta Manual Directa", style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const Text("Registra una venta sin usar el catálogo", style: TextStyle(color: Colors.grey)),
            const SizedBox(height: 30),
            TextFormField(
              controller: nameCtrl,
              decoration: const InputDecoration(labelText: 'Nombre del Producto', border: OutlineInputBorder(), prefixIcon: Icon(Icons.label)),
              validator: (v) => v!.isEmpty ? 'Requerido' : null,
            ),
            const SizedBox(height: 15),
            Row(
              children: [
                Expanded(
                  child: TextFormField(
                    controller: priceCtrl,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: 'Precio (S/)', border: OutlineInputBorder(), prefixIcon: Icon(Icons.attach_money)),
                    validator: (v) => v!.isEmpty ? 'Requerido' : null,
                  ),
                ),
                const SizedBox(width: 15),
                Expanded(
                  child: TextFormField(
                    controller: qtyCtrl,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(labelText: 'Cantidad', border: OutlineInputBorder(), prefixIcon: Icon(Icons.numbers)),
                    validator: (v) => v!.isEmpty ? 'Requerido' : null,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 30),
            SizedBox(
              width: double.infinity,
              height: 55,
              child: ElevatedButton.icon(
                icon: const Icon(Icons.check),
                label: const Text("VENDER AHORA"),
                style: ElevatedButton.styleFrom(backgroundColor: Colors.orange, foregroundColor: Colors.white),
                onPressed: () {
                  if (formKey.currentState!.validate()) {
                    _processQuickSale(
                        nameCtrl.text,
                        double.parse(priceCtrl.text),
                        int.parse(qtyCtrl.text)
                    );
                  }
                },
              ),
            )
          ],
        ),
      ),
    );
  }

  Widget _buildCartTab() {
    if (_cart.isEmpty) {
      return const Center(child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [Icon(Icons.shopping_cart_outlined, size: 80, color: Colors.grey), Text("El carrito está vacío")]));
    }

    return Column(
      children: [
        Expanded(
          child: ListView.separated(
            padding: const EdgeInsets.all(12),
            itemCount: _cart.length,
            separatorBuilder: (c, i) => const Divider(),
            itemBuilder: (context, index) {
              final item = _cart[index];
              final Product p = item['product'];
              return ListTile(
                leading: SizedBox(width: 50, child: OptimizedImage(imagePath: p.imagePath)),
                title: Text(p.name),
                subtitle: Text('${item['qty']} x S/${item['price'].toStringAsFixed(2)}'),
                trailing: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text('S/${(item['qty']*item['price']).toStringAsFixed(2)}', style: const TextStyle(fontWeight: FontWeight.bold)),
                    IconButton(icon: const Icon(Icons.delete, color: Colors.red), onPressed: () => _removeFromCart(index))
                  ],
                ),
              );
            },
          ),
        ),
        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(color: Colors.white, boxShadow: [BoxShadow(blurRadius: 10, color: Colors.black12)]),
          child: Column(
            children: [
              Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [const Text("TOTAL:", style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)), Text("S/${_calculateTotal().toStringAsFixed(2)}", style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold, color: Colors.green))]),
              const SizedBox(height: 15),
              SizedBox(width: double.infinity, height: 50, child: ElevatedButton(onPressed: _processCheckout, child: const Text("CONFIRMAR VENTA"))),
            ],
          ),
        )
      ],
    );
  }

  void _showQuantityDialog(Product product) {
    int qty = 1;
    showDialog(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setStateDialog) => AlertDialog(
          title: Text("Agregar: ${product.name}"),
          content: Row(mainAxisAlignment: MainAxisAlignment.center, children: [
            IconButton(icon: const Icon(Icons.remove), onPressed: qty > 1 ? () => setStateDialog(() => qty--) : null),
            Text("$qty", style: const TextStyle(fontSize: 24)),
            IconButton(icon: const Icon(Icons.add), onPressed: qty < product.stock ? () => setStateDialog(() => qty++) : null),
          ]),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text("Cancelar")),
            ElevatedButton(onPressed: () { Navigator.pop(context); _addToCart(product, qty); }, child: const Text("Agregar"))
          ],
        ),
      ),
    );
  }

  // 🏛️ BOLETA ESTILO ANTIGUO RESTAURADA
  void _showOldStyleInvoiceDialog(String id, List<dynamic> items, double total, {required bool isManual, String? manualName}) {
    final theme = Theme.of(context);
    final double subtotal = total / 1.18;
    final double igv = total - subtotal;
    final DateTime now = DateTime.now();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          titlePadding: const EdgeInsets.all(0),
          title: Container(
            padding: const EdgeInsets.only(top: 24, bottom: 16),
            width: double.infinity,
            decoration: BoxDecoration(
              color: Colors.green[50],
              borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
            ),
            child: const Column(
              children: [
                Icon(Icons.check_circle_outline, color: Colors.green, size: 60),
                SizedBox(height: 12),
                Text('¡Venta Exitosa!', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
              ],
            ),
          ),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                const Text("TOTAL PAGADO", style: TextStyle(color: Colors.grey, fontSize: 12)),
                Text(
                  'S/${total.toStringAsFixed(2)}',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 36, color: theme.colorScheme.primary),
                ),
                const SizedBox(height: 16),
                const Divider(),
                const SizedBox(height: 16),
                // Lista de productos
                if (isManual)
                  _buildDetailRow("Producto:", "$manualName (Venta Rápida)")
                else ...[
                  const Text("Detalles:", style: TextStyle(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 5),
                  ...items.map((item) {
                    String pName = "Producto";
                    try {
                      pName = _products.firstWhere((p) => p.id == item['productoId'].toString()).name;
                    } catch(e) {}
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 4.0),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Expanded(child: Text(pName, style: const TextStyle(fontSize: 13))),
                          Text("x${item['cantidad']}  S/${item['precioUnitario']}", style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
                        ],
                      ),
                    );
                  }),
                ],
                const SizedBox(height: 16),
                const Divider(),
                _buildDetailRow("Subtotal:", "S/${subtotal.toStringAsFixed(2)}"),
                const SizedBox(height: 8),
                _buildDetailRow("IGV (18%):", "S/${igv.toStringAsFixed(2)}"),
                const SizedBox(height: 8),
                _buildDetailRow("Fecha:", DateFormat('dd/MM/yy hh:mma').format(now)),
                const SizedBox(height: 8),
                _buildDetailRow("Nro Boleta:", "B-$id"),
                const SizedBox(height: 24),
                const Center(
                  child: Text('¡Gracias por tu compra!', style: TextStyle(fontStyle: FontStyle.italic, color: Colors.grey)),
                ),
              ],
            ),
          ),
          actionsPadding: const EdgeInsets.all(16),
          actions: [
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30))),
                child: const Text('Cerrar', style: TextStyle(fontSize: 16)),
                onPressed: () => Navigator.of(context).pop(),
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildDetailRow(String title, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(title, style: const TextStyle(color: Colors.grey, fontSize: 14)),
        const SizedBox(width: 10),
        Flexible(
          child: Text(value, textAlign: TextAlign.end, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
        ),
      ],
    );
  }
}