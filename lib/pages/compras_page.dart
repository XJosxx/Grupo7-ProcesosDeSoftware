import 'dart:io'; // Para manejar archivos de imagen
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart'; // 👈 Importar ImagePicker
// Importa servicios y modelos
import '../modelos/product_model.dart'; // Ruta corregida
import '../servicios/product_service.dart'; // Ruta corregida
import '../modelos/activity_event_model.dart'; // 👈 NUEVA IMPORTACIÓN
import '../servicios/activity_service.dart'; // 👈 NUEVA IMPORTACIÓN

// 📸 Se convierte a StatefulWidget para manejar la imagen seleccionada
class ComprasPage extends StatefulWidget {
  const ComprasPage({super.key});

  @override
  State<ComprasPage> createState() => _ComprasPageState();
}

class _ComprasPageState extends State<ComprasPage> {
  // 📸 Variable para guardar la imagen
  File? _selectedImage;

  // 📸 Método para seleccionar imagen
  Future<void> _pickImage() async {
    final ImagePicker picker = ImagePicker();
    // Pide al usuario que elija (Galería o Cámara)
    final XFile? image = await picker.pickImage(source: ImageSource.gallery);

    if (image != null) {
      setState(() {
        _selectedImage = File(image.path);
      });
    }
  }

  // 🛒 Diálogo para añadir producto
  void _showAddProductDialog(BuildContext context) {
    // Controladores para los campos de texto
    final nameController = TextEditingController();
    final categoryController = TextEditingController();
    final priceController = TextEditingController();
    final formKey = GlobalKey<FormState>();

    // Reinicia la imagen seleccionada cada vez que se abre el diálogo
    _selectedImage = null;

    showDialog(
      context: context,
      // StatefulBuilder para que el diálogo pueda actualizar su propia UI (para la imagen)
      builder: (BuildContext dialogContext) {
        return StatefulBuilder(
          builder: (context, setStateInDialog) {
            return AlertDialog(
              title: const Text('Agregar Nuevo Producto'),
              content: Form(
                key: formKey,
                child: SingleChildScrollView(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      // --- Selector de Imagen (Opcional) ---
                      Container(
                        width: 120,
                        height: 120,
                        decoration: BoxDecoration(
                          color: Colors.grey[200],
                          borderRadius: BorderRadius.circular(12),
                          image: _selectedImage != null
                              ? DecorationImage(
                            image: FileImage(_selectedImage!),
                            fit: BoxFit.cover,
                          )
                              : null,
                        ),
                        child: InkWell(
                          onTap: () async {
                            // Llama al picker y actualiza el diálogo
                            await _pickImage();
                            setStateInDialog(() {}); // Actualiza la imagen
                          },
                          child: _selectedImage == null
                              ? const Center(
                            child: Icon(Icons.add_a_photo,
                                color: Colors.grey, size: 40),
                          )
                              : null,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Agregar imagen (Opcional)',
                        style: TextStyle(color: Colors.grey[600], fontSize: 12),
                      ),
                      const SizedBox(height: 16),
                      // --- Campos de Texto ---
                      TextFormField(
                        controller: nameController,
                        decoration:
                        const InputDecoration(labelText: 'Nombre del Producto'),
                        validator: (v) => v!.isEmpty ? "Requerido" : null,
                      ),
                      TextFormField(
                        controller: categoryController,
                        decoration:
                        const InputDecoration(labelText: 'Categoría'),
                        validator: (v) => v!.isEmpty ? "Requerido" : null,
                      ),
                      TextFormField(
                        controller: priceController,
                        decoration:
                        const InputDecoration(labelText: 'Precio de Compra (S/)'), // 👈 CAMBIO S/
                        keyboardType: TextInputType.number,
                        validator: (v) => v!.isEmpty ? "Requerido" : null,
                      ),
                    ],
                  ),
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.of(context).pop(),
                  child: const Text('Cancelar'),
                ),
                ElevatedButton(
                  onPressed: () {
                    if (formKey.currentState!.validate()) {
                      // Crea el nuevo producto
                      final newProduct = Product(
                        id: DateTime.now().millisecondsSinceEpoch.toString(),
                        name: nameController.text,
                        category: categoryController.text,
                        price: double.parse(priceController.text),
                        imagePath: _selectedImage?.path, // Guarda la ruta
                      );

                      // 🛒 Guarda en el servicio central
                      ProductService.instance.addProduct(newProduct);

                      // ⭐️ YAPE: Añade a la actividad reciente
                      ActivityService.instance.addActivity(
                        ActivityEvent(
                          title: 'Producto agregado',
                          subtitle: newProduct.name, // Muestra el nombre
                          icon: Icons.add,
                          color: Colors.blue,
                          timestamp: DateTime.now(),
                        ),
                      );

                      Navigator.of(context).pop();
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Producto guardado con éxito'),
                          backgroundColor: Colors.green,
                        ),
                      );
                    }
                  },
                  child: const Text('Guardar Producto'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // 🛒 3. Botón flotante para agregar productos
      appBar: AppBar(
        title: const Text('Compras'),
        automaticallyImplyLeading: false,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.shopping_cart_checkout_sharp, size: 80, color: Colors.grey[300]),
            const SizedBox(height: 16),
            const Text(
              "Historial de Compras",
              style: TextStyle(fontSize: 22, color: Colors.grey),
            ),
            const Text(
              "Aquí aparecerá tu registro de compras.",
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showAddProductDialog(context),
        tooltip: 'Agregar Producto',
        child: const Icon(Icons.add),
      ),
    );
  }
}