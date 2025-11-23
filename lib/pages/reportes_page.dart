import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../modelos/report_model.dart'; // Ruta corregida
import '../servicios/ReportService.dart'; // Ruta corregida (usando tu nombre de archivo)


class ReportesPage extends StatefulWidget {
  const ReportesPage({super.key});

  @override
  State<ReportesPage> createState() => _ReportesPageState();
}

class _ReportesPageState extends State<ReportesPage> {
  final List<Report> _selectedReports = [];
  final DateFormat _dateFormatter = DateFormat('dd/MM/yyyy hh:mma');

  void _showPdfDialog() {
    final bool hasSelection = _selectedReports.isNotEmpty;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Descargar Reportes en PDF'),
          content: const Text('¿Qué reportes deseas descargar?'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
                _showSnackbar('Descargando todos los reportes (simulado)...');
              },
              child: const Text('Descargar Todos'),
            ),
            TextButton(
              onPressed: hasSelection ? () {
                Navigator.of(context).pop();
                _showSnackbar('Descargando ${_selectedReports.length} reportes seleccionados (simulado)...');
              } : null,
              child: Text('Descargar Seleccionados (${_selectedReports.length})'),
            ),
          ],
        );
      },
    );
  }

  void _showSnackbar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        duration: const Duration(seconds: 2),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reportes de Ventas'),
        automaticallyImplyLeading: false,
        actions: [
          IconButton(
            icon: const Icon(Icons.picture_as_pdf),
            tooltip: 'Descargar PDF',
            onPressed: _showPdfDialog,
          ),
        ],
      ),
      body: ValueListenableBuilder<List<Report>>(
        // Escucha el servicio de reportes
        valueListenable: ReportService.instance.reportsNotifier,
        builder: (context, reports, child) {
          if (reports.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.bar_chart, size: 80, color: Colors.grey[300]),
                  const SizedBox(height: 16),
                  const Text(
                    "No hay reportes",
                    style: TextStyle(fontSize: 22, color: Colors.grey),
                  ),
                  const Text(
                    "Las ventas completadas aparecerán aquí.",
                    style: TextStyle(color: Colors.grey),
                  ),
                ],
              ),
            );
          }

          // Lista de reportes
          return ListView.builder(
            itemCount: reports.length,
            itemBuilder: (context, index) {
              final report = reports[index];
              final isSelected = _selectedReports.contains(report);

              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                child: CheckboxListTile(
                  value: isSelected,
                  onChanged: (bool? value) {
                    setState(() {
                      if (value == true) {
                        _selectedReports.add(report);
                      } else {
                        _selectedReports.remove(report);
                      }
                    });
                  },
                  title: Text(
                    '${report.productName} (x${report.quantity})',
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  subtitle: Text(_dateFormatter.format(report.date)),
                  // CORRECCIÓN: 'trailing:' -> 'secondary:'
                  secondary: Text(
                    'S/${report.total.toStringAsFixed(2)}', // 👈 CAMBIO
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                  ),
                  activeColor: Theme.of(context).colorScheme.primary,
                ),
              );
            },
          );
        },
      ),
    );
  }
}