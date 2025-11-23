
class Product {
  final String id;
  String name;
  String category;
  double price;
  String? imagePath; // Para la imagen (opcional)
  double? salePrice; // Para promociones

  Product({
    required this.id,
    required this.name,
    required this.category,
    required this.price,
    this.imagePath,
    this.salePrice,
  });

  // Helper para saber si está en oferta
  bool get onSale => salePrice != null;
}