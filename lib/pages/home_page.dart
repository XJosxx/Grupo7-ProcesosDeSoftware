import '../modelos/product_model.dart'; // Ruta corregida
import '../servicios/product_service.dart'; // Ruta corregida
import '../widgets/activity_card.dart'; // Ruta corregida
import '../widgets/summary_card.dart'; // Ruta corregida
import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import '../screens/LoginScreen.dart'; // ⭐️ Importar LoginScreen para cerrar sesión
import '../servicios/activity_service.dart'; // 👈 NUEVA IMPORTACIÓN
import '../modelos/activity_event_model.dart'; // 👈 NUEVA IMPORTACIÓN

class HomePage extends StatelessWidget {
  final String userRole;
  const HomePage({super.key, required this.userRole});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // 👩‍💼 7. Diferencias entre Usuario y Admin
        title: Text(userRole == 'admin' ? "Hola, Neli!" : "Bienvenido a NeliShop"),
        automaticallyImplyLeading: false,
        actions: [
          // ⭐️ Menú de cuenta con "Cerrar Sesión" y "Soporte"
          PopupMenuButton<String>(
            onSelected: (value) {
              if (value == 'logout') {
                // Navega a Login y borra el historial
                Navigator.of(context).pushAndRemoveUntil(
                  MaterialPageRoute(builder: (context) => const LoginScreen()),
                      (Route<dynamic> route) => false, // Borra todas las rutas
                );
              } else if (value == 'support') {
                // Muestra diálogo de soporte
                showDialog(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Soporte Técnico'),
                    content: const Text('Para ayuda, contacta a: soporte@nelishop.com'),
                    actions: [
                      TextButton(
                        onPressed: () => Navigator.of(context).pop(),
                        child: const Text('Cerrar'),
                      ),
                    ],
                  ),
                );
              }
            },
            // El ícono de persona es el botón
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: CircleAvatar(
                backgroundColor: Theme.of(context).colorScheme.primary.withOpacity(0.1),
                child: Icon(Icons.person, color: Theme.of(context).colorScheme.primary),
              ),
            ),
            itemBuilder: (BuildContext context) => <PopupMenuEntry<String>>[
              const PopupMenuItem<String>(
                value: 'support',
                child: ListTile(
                  leading: Icon(Icons.support_agent),
                  title: Text('Soporte Técnico'),
                ),
              ),
              const PopupMenuItem<String>(
                value: 'logout',
                child: ListTile(
                  leading: Icon(Icons.logout, color: Colors.red),
                  title: Text('Cerrar Sesión', style: TextStyle(color: Colors.red)),
                ),
              ),
            ],
          ),
          const SizedBox(width: 8), // Pequeño espacio
        ],
      ),
      // Muestra una vista diferente según el rol
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: userRole == 'admin'
            ? _buildAdminHome(context)
            : _buildUserHome(context),
      ),
    );
  }

  // 👩‍💼 VISTA DE ADMIN (Resumen y Actividad)
  Widget _buildAdminHome(BuildContext context) {
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          "Resumen del negocio",
          style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 12),
        // Grid de resumen
        GridView.count(
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          children: [
            SummaryCard(
              icon: Icons.trending_up,
              title: "Ventas totales",
              value: "S/12,500", // 👈 CAMBIO S/
              color: theme.colorScheme.primary.withOpacity(0.1),
              iconColor: theme.colorScheme.primary,
            ),
            SummaryCard(
              icon: Icons.inventory_2,
              title: "Productos",
              value: "350",
              color: theme.colorScheme.secondary.withOpacity(0.1),
              iconColor: theme.colorScheme.secondary,
            ),
          ],
        ).animate().fadeIn(duration: 300.ms), // ✨ Animación
        const SizedBox(height: 24),
        const Text(
          "Actividad reciente",
          style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 12),

        // --- SECCIÓN DE ACTIVIDAD DINÁMICA ---
        // Se reemplazan las tarjetas estáticas por un ValueListenableBuilder
        ValueListenableBuilder<List<ActivityEvent>>(
          valueListenable: ActivityService.instance.activityNotifier,
          builder: (context, activities, child) {
            if (activities.isEmpty) {
              return const Center(
                child: Text(
                  "No hay actividad reciente.",
                  style: TextStyle(color: Colors.grey),
                ),
              );
            }

            // Muestra solo las últimas 5 actividades
            return Column(
              children: activities.take(5).map((event) {
                return ActivityCard(
                  icon: event.icon,
                  title: event.title,
                  subtitle: event.subtitle,
                  time: _formatTimeAgo(event.timestamp), // Formatea la hora
                  color: event.color,
                ).animate().slideX(begin: -0.2).fadeIn(); // ✨ Animación
              }).toList(),
            );
          },
        ),
      ],
    );
  }

  // 🧑‍💻 VISTA DE USUARIO (Promociones y Carrusel)
  Widget _buildUserHome(BuildContext context) {
    // Escucha los cambios del ProductService
    final productService = ProductService.instance;
    return ValueListenableBuilder<List<Product>>(
        valueListenable: productService.productsNotifier,
        builder: (context, products, child) {
          final promotions = productService.getPromotions();

          return Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Sección de Promociones
              Text(
                "¡Promociones del Día!",
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 12),
              if (promotions.isEmpty)
                Text("No hay promociones hoy, vuelve pronto.")
              else
              // Carrusel horizontal de promociones
                SizedBox(
                  height: 120,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: promotions.length,
                    itemBuilder: (context, index) {
                      final product = promotions[index];
                      return _buildPromoCard(context, product);
                    },
                  ),
                ),
              SizedBox(height: 24),

              // Sección de "Todos los Productos"
              Text(
                "Catálogo de Productos",
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              ),
              SizedBox(height: 12),
              // Aquí podrías poner otro carrusel o un GridView
              // Por ahora, un simple contador
              Text("Total de productos en tienda: ${products.length}"),
            ],
          ).animate().fadeIn(duration: 300.ms); // ✨ Animación
        });
  }

  // ⭐️ NUEVO: Tarjeta de Promoción para el carrusel de usuario
  Widget _buildPromoCard(BuildContext context, Product product) {
    final theme = Theme.of(context);
    return Container(
      width: 250, // Ancho fijo para el carrusel
      margin: const EdgeInsets.only(right: 12),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(12),
        gradient: LinearGradient(
          colors: [
            theme.colorScheme.primary.withOpacity(0.8),
            theme.colorScheme.primary,
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        boxShadow: [
          BoxShadow(
            color: theme.colorScheme.primary.withOpacity(0.3),
            blurRadius: 8,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              product.name,
              style: const TextStyle(
                color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: 16,
              ),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 8),
            Text(
              "AHORA S/${product.salePrice!.toStringAsFixed(2)}", // 👈 CAMBIO S/
              style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 20),
            ),
            Text(
              "Antes S/${product.price.toStringAsFixed(2)}", // 👈 CAMBIO S/
              style: TextStyle(
                color: Colors.white.withOpacity(0.8),
                decoration: TextDecoration.lineThrough,
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }

  // 🕰️ NUEVO: Helper para formatear la hora como "Hace 2 horas"
  String _formatTimeAgo(DateTime time) {
    final difference = DateTime.now().difference(time);
    if (difference.inDays > 0) {
      return 'Hace ${difference.inDays} d';
    } else if (difference.inHours > 0) {
      return 'Hace ${difference.inHours} h';
    } else if (difference.inMinutes > 0) {
      return 'Hace ${difference.inMinutes} m';
    } else {
      return 'Ahora';
    }
  }
}