import 'dart:ffi';

import 'package:flutter/services.dart';

class BridgeFlutter {
  // Canales de comunicación con Android
  final _channelLogin = const MethodChannel('samples.flutter.dev/Login');
  final _channelProductos =
      const MethodChannel('samples.flutter.dev/Productos');
  final _channelVenta = const MethodChannel('samples.flutter.dev/Venta');
  final _channelCompra = const MethodChannel('samples.flutter.dev/Compra');

  // -------- LOGIN --------
  Future<Map<String, dynamic>> login(String dni, String password) async {
    try {
      final result = await _channelLogin.invokeMethod('login', [dni, password]);
      if (result != null && result is Map) {
        return Map<String, dynamic>.from(result);
      }
      return {'status': 'error', 'mensaje': 'Respuesta inválida del servidor'};
    } on PlatformException catch (e) {
      return {'status': 'error', 'mensaje': 'Error de conexión: ${e.message}'};
    } catch (e) {
      return {'status': 'error', 'mensaje': 'Error desconocido: $e'};
    }
  }

  // -------- PRODUCTOS --------
  Future<List<dynamic>> obtenerProductos() async {
    try {
      final result = await _channelProductos.invokeMethod('getProduct',[]);
      if (result != null && result is List) {
        return result;
      }
      return [];
    } catch (e) {
      print("Error obteniendo productos: $e");
      return [];
    }
  }

  Future<Map<String, dynamic>> agregarProducto(
      Map<String, dynamic> producto) async
  {
    try {
      final result =
          await _channelProductos.invokeMethod('addProduct', [producto]);
      return Map<String, dynamic>.from(result);
    } catch (e) {
      return {'status': 'error', 'mensaje': e.toString()};
    }
  }

  Future<Map<String, dynamic>> actualizarProducto(
      Map<String, dynamic> producto) async {
    try {
      final result =
          await _channelProductos.invokeMethod('editProduct', [producto]);
      return Map<String, dynamic>.from(result);
    } catch (e) {
      return {'status': 'error', 'mensaje': e.toString()};
    }
  }

  Future<Map<String, dynamic>> eliminarProducto(int id) async {
    try {
      final result =
          await _channelProductos.invokeMethod('deleteProduct', [id]);
      return Map<String, dynamic>.from(result);
    } catch (e) {
      return {'status': 'error', 'mensaje': e.toString()};
    }
  }


  Future<double?> getGananciaTotal() async {
    try {
      final result = await _channelProductos.invokeMethod('SumGanancia',[]);
      if (result != null) {
        return result;
      }
      return 0.0;


    } catch (e) {
      print("Error obteniendo productos: $e");
      return 0.0;
    }
  }

  // -------- VENTAS --------
  Future<Map<String, dynamic>> listarVentas() async {
    try {
      final result = await _channelVenta.invokeMethod('listVentas', []);
      return Map<String, dynamic>.from(result);
    } catch (e) {
      return {'status': 'error', 'mensaje': e.toString(), 'ventas': []};
    }
  }

  Future<Map<String, dynamic>> registrarVenta(
      Map<String, dynamic> venta, List<Map<String, dynamic>> detalles) async {
    try {
      final result =
          await _channelVenta.invokeMethod('regVenta', [venta, detalles]);
      return {'status': 'ok', 'id': result};
    } catch (e) {
      return {'status': 'error', 'mensaje': e.toString()};
    }
  }

  // -------- COMPRAS --------
  Future<List<dynamic>> listarCompras() async {
    try {
      final result = await _channelCompra.invokeMethod('listCompras');
      if (result != null && result is List) {
        return result;
      }
      return [];
    } catch (e) {
      print("Error obteniendo compras: $e");
      return [];
    }
  }

  Future<Map<String, dynamic>> registrarCompra(
      Map<String, dynamic> compra, List<Map<String, dynamic>> detalles) async {
    try {
      final result =
          await _channelCompra.invokeMethod('RegCompra', [compra, detalles]);
      return Map<String, dynamic>.from(result);
    } catch (e) {
      return {'status': 'error', 'mensaje': e.toString()};
    }
  }

  // Método genérico para llamadas futuras si se necesita
  Future<dynamic> callMethod(String channelName, String method,
      [dynamic arguments]) async {
    MethodChannel channel;
    switch (channelName) {
      case 'Login':
        channel = _channelLogin;
        break;
      case 'Productos':
        channel = _channelProductos;
        break;
      case 'Venta':
        channel = _channelVenta;
        break;
      case 'Compra':
        channel = _channelCompra;
        break;
      default:
        throw Exception("Canal no encontrado: $channelName");
    }
    return await channel.invokeMethod(method, arguments);
  }
}
