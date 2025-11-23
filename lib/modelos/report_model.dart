
class Report {
  final String id;
  final String productName;
  final int quantity;
  final double subtotal;
  final double igv;
  final double total;
  final DateTime date;

  Report({
    required this.id,
    required this.productName,
    required this.quantity,
    required this.subtotal,
    required this.igv,
    required this.total,
    required this.date,
  });
}