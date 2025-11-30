class Product {
  final String id;
  String name;
  String category;
  double price;
  String? imagePath; // Para la imagen
  double? salePrice; // Para promociones
  int stock;

  Product({
    required this.id,
    required this.name,
    required this.category,
    required this.price,
    this.imagePath,
    this.salePrice,
    this.stock = 0, // Default 0
  });

  // Helper para saber si está en oferta
  bool get onSale => salePrice != null;
}
