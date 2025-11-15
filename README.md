# PROCESOSUX-1

Documentación del proyecto: estructura, cómo ejecutar, y guía para conectar el frontend Flutter mediante MethodChannel.

---

## 1. Visión general

Proyecto Android con lógica de negocio (servicios y repositorios JDBC) organizada dentro del módulo `app`. El frontend planeado es Flutter; la comunicación entre Flutter y la lógica Java se hace mediante `MethodChannel` (puente nativo). La base de datos se accede por JDBC (clase `ConfiguracionBaseDatos`).

Nota importante: para producción es recomendable extraer la lógica backend en un servicio independiente (REST API) y que Flutter consuma por HTTP. La integración por `MethodChannel` está pensada para aplicaciones que deben ejecutar lógica nativa directamente en el dispositivo.

---

## 2. Estructura del proyecto (ubicaciones clave)

- `app/src/main/java/com/example/myapplication/Configuracion/ConfiguracionBaseDatos.java` — conexión JDBC.
- `app/src/main/java/com/example/myapplication/entidades/` — entidades Java (Producto, Venta, Usuario, Compra, DetalleVenta, DetalleCompra, etc.).
- `app/src/main/java/com/example/myapplication/repositories/` — interfaces de repositorio (CRUD y búsquedas).
- `app/src/main/java/com/example/myapplication/repositories/implementacion/` — implementaciones JDBC de repositorios.
- `app/src/main/java/com/example/myapplication/servicios/` — interfaces de servicios (lógica de negocio pública).
- `app/src/main/java/com/example/myapplication/servicios/implementacion/` — implementaciones de servicios (orquestación y validaciones).
- `app/src/main/java/com/example/myapplication/flutterbridge/MethodChannelHandler.java` — adaptador que expone métodos consumibles desde Flutter.

---

## 3. Cómo compilar y validar localmente

Desde PowerShell en la raíz del repo:

```powershell
cd C:\Users\JONATAN\Desktop\PROCESOSUX-1
.\gradlew.bat clean :app:assembleDebug --refresh-dependencies
```

Compilar solo Java del módulo app:

```powershell
.\gradlew.bat :app:compileDebugJava
```

Si ves errores sobre métodos abstractos o firmas que no coinciden: limpia el build y reinicia el language server del IDE.

---

## 4. Configuración de la base de datos

Archivo: `app/src/main/java/com/example/myapplication/Configuracion/ConfiguracionBaseDatos.java`.

Por seguridad, NO guardes credenciales en el repositorio. Actualmente el proyecto contiene valores de ejemplo; cámbialos por variables de entorno o un archivo `local.properties` no versionado.

Ejemplo de cambio (recomendado): cargar credenciales desde variables de entorno o `BuildConfig` en Gradle.

---

## 5. Integración Flutter — resumen técnico

Flujo básico:

1. En Android (`MainActivity`) crea y registra un `MethodChannel` con un nombre (ej. `com.example.myapplication/native`).
2. En el handler del `MethodChannel` (MainActivity) invoca los métodos del `MethodChannelHandler` Java y retorna el resultado a Flutter.
3. En Flutter usa `MethodChannel.invokeMethod(methodName, args)` para llamar a la lógica nativa.

Ejemplo mínimo en Android (Java) — `MainActivity.java` (registro del canal):

```java
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.example.myapplication/native";
    private final com.example.myapplication.flutterbridge.MethodChannelHandler handler = new com.example.myapplication.flutterbridge.MethodChannelHandler();

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL)
            .setMethodCallHandler((call, result) -> {
                try {
                    String method = call.method;
                    Object args = call.arguments;
                    switch (method) {
                        case "login":
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> loginArgs = (java.util.Map<String, Object>) args;
                            java.util.Map<String, Object> res = handler.login((String)loginArgs.get("dni"), (String)loginArgs.get("password"));
                            result.success(res);
                            break;
                        case "obtenerProductos":
                            result.success(handler.obtenerProductos());
                            break;
                        // Agregar más mapeos según los métodos implementados en MethodChannelHandler
                        default:
                            result.notImplemented();
                    }
                } catch (Exception e) {
                    result.error("ERROR", e.getMessage(), null);
                }
            });
    }
}
```

Ejemplo en Flutter (Dart):

```dart
import 'package:flutter/services.dart';

final platform = MethodChannel('com.example.myapplication/native');

Future<Map<String, dynamic>> login(String dni, String password) async {
  final res = await platform.invokeMethod('login', {'dni': dni, 'password': password});
  return Map<String, dynamic>.from(res);
}

Future<List<dynamic>> obtenerProductos() async {
  final res = await platform.invokeMethod('obtenerProductos');
  return List<dynamic>.from(res);
}
```

### Conexión Flutter — paso a paso (detallado)

1) Elige un nombre de canal y mantenlo constante en Android y Flutter, por ejemplo: `com.example.myapplication/native`.

2) Registrar el `MethodChannel` en `MainActivity` (ejemplo con ejecución en background para evitar bloquear):

Java (MainActivity.java) — ejecutar llamadas pesadas en hilo de fondo:

```java
new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL)
    .setMethodCallHandler((call, result) -> {
        // Ejecutar en hilo de fondo para no bloquear el hilo del framework/UI
        new Thread(() -> {
            try {
                switch (call.method) {
                    case "login":
                        java.util.Map<String, Object> loginArgs = (java.util.Map<String, Object>) call.arguments;
                        java.util.Map<String, Object> res = handler.login((String)loginArgs.get("dni"), (String)loginArgs.get("password"));
                        // Enviar resultado de vuelta en el hilo principal
                        runOnUiThread(() -> result.success(res));
                        break;
                    default:
                        runOnUiThread(() -> result.notImplemented());
                }
            } catch (Exception e) {
                runOnUiThread(() -> result.error("ERROR", e.getMessage(), null));
            }
        }).start();
    });
```

Kotlin (MainActivity.kt) equivalente:

```kotlin
MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
        Thread {
                try {
                        when (call.method) {
                                "login" -> {
                                        val args = call.arguments as Map<String, Any>
                                        val res = handler.login(args["dni"] as String, args["password"] as String)
                                        runOnUiThread { result.success(res) }
                                }
                                else -> runOnUiThread { result.notImplemented() }
                        }
                } catch (e: Exception) {
                        runOnUiThread { result.error("ERROR", e.message, null) }
                }
        }.start()
}
```

3) En Flutter, usa `MethodChannel.invokeMethod` y maneja errores con `PlatformException`:

```dart
import 'package:flutter/services.dart';

final platform = MethodChannel('com.example.myapplication/native');

Future<void> ejemploLogin() async {
    try {
        final Map res = await platform.invokeMethod('login', {'dni': '70123456', 'password': 'secreto'});
        if (res['status'] == 'ok') {
            // Procesar datos
        } else {
            // Mostrar mensaje de error
        }
    } on PlatformException catch (e) {
        // Error en la invocación
        print('PlatformException: ${e.message}');
    }
}
```

4) Tipos soportados y recomendaciones de payload:

- MethodChannel admite tipos JSON-like: `null`, `bool`, `num`, `String`, `List`, `Map`, y `Uint8List`.
- Para objetos complejos envía `Map<String, dynamic>` desde Dart y convierte a `Map` en Java. Evita pasar instancias complejas.
- Para listas de detalles (por ejemplo `detalles` en una venta) usa `List<Map<String, dynamic>>`.

Ejemplo de payload para `agregarProducto` (Dart → Java):

```dart
final producto = {
    'nombre': 'Arroz',
    'precioCompra': 12.5,
    'precioVenta': 18.0,
    'cantidad': 100,
    'categoriaNombre': 'Alimentos',
    // si tu entidad tiene 'codigo' o 'id', inclúyelos
    'codigo': 'ARZ-001'
};
final res = await platform.invokeMethod('agregarProducto', producto);
```

Ejemplo de payload para `registrarVenta`:

```dart
final venta = {
    'fecha': '2025-11-15',
    'numeroBoleta': 'B20251115-0001',
    'clienteDni': '70123456',
    'vendedorId': 1
};

final detalles = [
    {'productoId': 10, 'cantidad': 2, 'precioUnitario': 25.0},
    {'productoId': 12, 'cantidad': 1, 'precioUnitario': 50.0}
];

final res = await platform.invokeMethod('registrarVenta', {'venta': venta, 'detalles': detalles});
```

5) Manejo de respuestas y errores:

- En Java/Android devuelve `result.success(map)` con estructuras sencillas. Evita devolver objetos Java complejos no serializables.
- En Flutter valida el campo `status` de la respuesta y muestra mensajes al usuario.

6) Buenas prácticas adicionales:

- Mantén el canal y nombres de métodos versionados si planeas cambios (`com.example.myapplication/native.v1`).
- No ejecutes consultas pesadas en el hilo principal del dispositivo; usa hilos o `AsyncTask`/`Executors` y devuelve resultados al hilo UI.
- Centraliza la validación de contratos (por ejemplo, campos requeridos) en Flutter antes de invocar.

---


---

## 6. Métodos expuestos y contratos (resumen)

Revisa `MethodChannelHandler` para la lista completa y detalles. A modo de referencia, algunos métodos y contratos:

- `login` — args: `{ 'dni': String, 'password': String }` → respuesta Map con `status`, `mensaje`, `usuario`, `rol`.
- `obtenerProductos` — args: none → devuelve `List<Producto>` (cada producto es un Map con campos de la entidad).
- `agregarProducto` — args: `Map` con campos de `Producto` → devuelve Map con `status` y `mensaje`.
- `actualizarProducto` — args: `Map` producto → Map `status`/`mensaje`.
- `eliminarProducto` — args: `{ 'idProducto': int }` → Map `status`/`mensaje`.
- `buscarProductos` — args: `{ 'criterio': String, 'tipo': String }` → lista de productos.
- `buscarProductoEnVentaPorIdONombre` — args: `{ 'criterio': String }` → lista de productos con stock > 0.
- `calcularMontos` — args: `{ 'precioUnitario': double, 'cantidad': int }` → `{ 'subtotal': double, 'igv': double, 'total': double }`.
- `registrarVenta` — args: `{ 'venta': Map, 'detalles': List<Map> }` → devuelve id (int) o lanza excepción/retorna error.

Para cada método, revisa `MethodChannelHandler` y adáptalo según los nombres de campos que esperes en Flutter.

---

## 7. Seguridad y buenas prácticas

- Elimina credenciales del control de versiones.
- Añade validaciones en el borde (antes de ejecutar operaciones destructivas).
- Considera exponer una API HTTP para facilitar pruebas y separación de responsabilidades.

---

## 8. Siguientes pasos sugeridos

- Generar ejemplos JSON/Map para cada método (puedo generarlos si lo solicitas).
- Crear un `MainActivity` completo con mapeo de todos los métodos del `MethodChannelHandler`.
- Ejecutar una compilación automática y entregarte la salida de errores/warnings.

---

Si quieres, genero ahora ejemplos de `Map`/JSON para los métodos de `MethodChannelHandler` (por ejemplo `agregarProducto`, `registrarVenta`, `validarStock`). Indica cuáles prefieres.
