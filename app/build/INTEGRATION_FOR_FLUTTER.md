Integración con Flutter (Platform Channels)

Resumen

Este documento explica cómo integrar un frontend Flutter con la lógica existente en el módulo Android de esta app usando Platform Channels (MethodChannel). No se añade código Flutter al repositorio: se deja el adaptador Android (`app/flutter/FlutterBridge.java`) y las instrucciones necesarias para que el equipo Flutter implemente el cliente Dart.

Principios clave

- NO se integra Flutter dentro del repositorio; solo se proporciona una capa Android (bridge) que expone funciones en JSON.
- Todas las llamadas son síncronas en su API (devuelven String JSON). En el lado Flutter deberás invocar los métodos de forma asíncrona y procesar JSON.
- Seguridad: el bridge no maneja autenticación avanzada. Si excedes el alcance de la aplicación local, considera un backend con autenticación/HTTPS y validación de JWT.

Métodos expuestos (Android - `FlutterBridge`)

La clase `com.example.apptienda.flutter.FlutterBridge` expone métodos estáticos que puedes invocar desde Android. Estos son los métodos disponibles y los formatos JSON esperados:

1) getAllProducts(Context)
- Descripción: devuelve un arreglo JSON con todos los productos.
- Formato de respuesta (String JSON):
  [
    { "id": 1, "nombre": "...,", "precioCompra": 10.0, "precioVenta": 13.0, "stock": 5.0, "categoria": "...", "imagenUrl": "..." },
    ...
  ]
- Errores: devuelve { "error": "mensaje" }

2) createProduct(Context, String jsonProduct)
- Descripción: crea un producto a partir de un JSON y retorna { "id": <nuevoId> } o error.
- Formato de entrada (jsonProduct):
  { "nombre": "Nombre", "precioCompra": 10.0, "precioVenta": 12.0, "stock": 5.0, "categoria": "Ropa", "imagenUrl": "content://..." }
- Formato de respuesta: { "id": 123 } o { "error": "mensaje" }

3) listSales(Context)
- Descripción: devuelve un arreglo JSON con las ventas registradas.
- Formato de respuesta:
  [
    { "id": 1, "codigo": "V-000001", "productoId": 2, "nombreProducto": "...", "cantidad": 1.0, "precioUnitario": 15.0, "total": 17.7, "fecha": 1660000000000 },
    ...
  ]

4) registerSale(Context, String jsonSale)
- Descripción: registra una venta y retorna { "id": <id>, "codigo": "V-..." } o { "error": "mensaje" }
- Formato de entrada (jsonSale):
  {
    "codigo": "V-000000",      // opcional
    "dni": "12345678",         // opcional
    "productoId": 2,            // 0 o ausente para venta manual
    "nombreProducto": "Item X", // usado si productoId absent
    "precioUnitario": 10.0,
    "cantidad": 1.0
  }

Cómo llamar desde Flutter (ejemplo - Dart)

En Dart crea un MethodChannel y llama a las funciones nativas. Ejemplo:

```dart
import 'dart:convert';
import 'package:flutter/services.dart';

final channel = MethodChannel('com.example.apptienda/bridge');

Future<List<Map<String,dynamic>>> getAllProducts() async {
  final String json = await channel.invokeMethod('getAllProducts');
  final dynamic parsed = jsonDecode(json);
  if (parsed is List) {
    return List<Map<String,dynamic>>.from(parsed);
  }
  throw Exception('Unexpected response: $parsed');
}

Future<int> createProduct(Map<String,dynamic> product) async {
  final String jsonResp = await channel.invokeMethod('createProduct', jsonEncode(product));
  final Map<String,dynamic> resp = jsonDecode(jsonResp);
  if (resp.containsKey('id')) return resp['id'];
  throw Exception(resp['error'] ?? 'Unknown error');
}
```

Nota: En el lado Android deberás registrar los handlers (ver sección siguiente). Este archivo README solo provee el contrato y los formatos.

Cómo registrar MethodChannel en Android (guía técnica)

- Recomendación: el registro del `MethodChannel` se hace en `MainActivity` (o en un plugin) usando el embedding de Flutter.
- Nombre del channel sugerido: `com.example.apptienda/bridge`.
- Mecanismo esperado en Android: cuando el `MethodCall` llegue, llamar al método correspondiente de `FlutterBridge` y devolver el String JSON.
- Importante: el bridge actual recibe `Context` como primer parámetro; asegúrate de pasar el contexto de la Activity.
- Ejemplo de métodos a manejar (MethodCall.method):
  - "getAllProducts" -> retorna FlutterBridge.getAllProducts(context)
  - "createProduct" -> FlutterBridge.createProduct(context, argsAsString)
  - "listSales" -> FlutterBridge.listSales(context)
  - "registerSale" -> FlutterBridge.registerSale(context, argsAsString)

Hilos y performance

- Las funciones de `FlutterBridge` llaman a la base de datos local (SQLite). Mantén las llamadas fuera del hilo UI en Flutter (usar invokeMethod que ya es asíncrono en Dart) y evita invocar operaciones largas en serie.
- Si necesitas operaciones pesadas, implementa en Android una llamada que ejecute en background (por ejemplo usando `AsyncTask`, `Executors` o coroutines en Kotlin) y retorne el resultado cuando esté listo.

Seguridad

- El token de sesión (si se expone al frontend) debe guardarse de forma segura. Recomendamos EncryptedSharedPreferences si vas a compartir tokens con frontend nativo.
- JWTs: `JwtUtils` en este repositorio NO valida firmas. No confíes en ese código para seguridad crítica.

Alternativa recomendada para producción

- Si el frontend Flutter debe disponer de acceso multiusuario y sincronización entre dispositivos, considera implementar un backend REST con autenticación y exponer endpoints seguros. La lógica de negocio puede reusarse si la extraes a un módulo compartido o la reimplementas en el backend.

Checklist rápido para el equipo Flutter

- [ ] Definir el contrato de MethodChannel (nombre y métodos).
- [ ] Implementar el cliente Dart que invoque los métodos y parse JSON.
- [ ] Validar que las respuestas de error siguen el formato {"error": "mensaje"} y manejarlo en UI.
- [ ] Probar en Android local (emulador/dispositivo) y verificar permisos de almacenamiento si se usan URIs de imágenes.

Contacto y notas finales

Si necesitan que implemente el registro del `MethodChannel` en `MainActivity` como ejemplo, puedo hacerlo; pero el README original explícitamente pide: "NO METAS NADA DE FLUTTER, SOLO DEJA ALGO PARA QUE SE PUEDA AÑADIR DESDE CERO A LA LOGICA" — por eso este documento solo proporciona contrato y guía.

---
Generado automáticamente: integración/contrato para Platform Channels (Android bridge -> Flutter).
