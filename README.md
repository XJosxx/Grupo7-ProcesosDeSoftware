/*
# PROCESOSUX-1

Documentación del proyecto: estructura, cómo ejecutar, y guía para conectar el frontend Flutter mediante MethodChannel.

---

## 1. Visión general

Proyecto Android con lógica de negocio (servicios y repositorios JDBC) organizada dentro del módulo `app`.
El frontend planeado es Flutter; la comunicación entre Flutter y la lógica Java se hace mediante `MethodChannel`
(puente nativo). La base de datos se accede por JDBC (clase `ConfiguracionBaseDatos`).

Nota importante: para producción es recomendable extraer la lógica backend en un servicio independiente (REST API)
y que Flutter consuma por HTTP. La integración por `MethodChannel` está pensada para aplicaciones que deben ejecutar
lógica nativa directamente en el dispositivo.

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

    cd C:\Users\JONATAN\Desktop\PROCESOSUX-1
    .\gradlew.bat clean :app:assembleDebug --refresh-dependencies

Compilar solo Java del módulo app:

    .\gradlew.bat :app:compileDebugJava

Si ves errores sobre métodos abstractos o firmas que no coinciden: limpia el build y reinicia el language server del IDE.

---

## 4. Configuración de la base de datos

Archivo: `app/src/main/java/com/example/myapplication/Configuracion/ConfiguracionBaseDatos.java`.

Por seguridad, NO guardes credenciales en el repositorio. Actualmente el proyecto contiene valores de ejemplo;
cámbialos por variables de entorno o un archivo `local.properties` no versionado.

Ejemplo recomendado: cargar credenciales desde variables de entorno o `BuildConfig` en Gradle.

---

## 5. Integración Flutter — resumen técnico

Flujo básico:

1. En Android (`MainActivity`) crea y registra un `MethodChannel` con un nombre (ej. `com.example.myapplication/native`).
2. En el handler del `MethodChannel` (MainActivity) invoca los métodos del `MethodChannelHandler` Java y retorna el resultado a Flutter.
3. En Flutter usa `MethodChannel.invokeMethod(methodName, args)` para llamar a la lógica nativa.

Ejemplo mínimo en Android (Java) — registro del canal:

// (Código de ejemplo, pero comentado para que no genere errores)

    // import io.flutter.embedding.android.FlutterActivity;
    // import io.flutter.embedding.engine.FlutterEngine;
    // import io.flutter.plugin.common.MethodCall;
    // import io.flutter.plugin.common.MethodChannel;

    // public class MainActivity extends FlutterActivity {
    //     private static final String CHANNEL = "com.example.myapplication/native";
    //     private final com.example.myapplication.flutterbridge.MethodChannelHandler handler = 
    //         new com.example.myapplication.flutterbridge.MethodChannelHandler();

    //     @Override
    //     public void configureFlutterEngine(FlutterEngine flutterEngine) {
    //         super.configureFlutterEngine(flutterEngine);
    //         new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL)
    //             .setMethodCallHandler((call, result) -> {
    //                 try {
    //                     switch (call.method) {
    //                         case "login":
    //                             java.util.Map<String, Object> loginArgs = (java.util.Map<String, Object>) call.arguments;
    //                             java.util.Map<String, Object> res = handler.login(
    //                                 (String)loginArgs.get("dni"), 
    //                                 (String)loginArgs.get("password")
    //                             );
    //                             result.success(res);
    //                             break;
    //                         case "obtenerProductos":
    //                             result.success(handler.obtenerProductos());
    //                             break;
    //                         default:
    //                             result.notImplemented();
    //                     }
    //                 } catch (Exception e) {
    //                     result.error("ERROR", e.getMessage(), null);
    //                 }
    //             });
    //     }
    // }

Ejemplo de Flutter:

    // final platform = MethodChannel('com.example.myapplication/native');
    // Future<Map<String, dynamic>> login(String dni, String password) async {
    //     final res = await platform.invokeMethod('login', {'dni': dni, 'password': password});
    //     return Map<String, dynamic>.from(res);
    // }

---

## 6. Métodos expuestos y contratos (resumen)

- `login` — args: `{ 'dni': String, 'password': String }` → respuesta Map con `status`, `mensaje`, `usuario`, `rol`.
- `obtenerProductos` — args: none → devuelve `List<Producto>` como lista de Map.
- `agregarProducto` — args: `Map` con campos de `Producto`.
- `actualizarProducto` — args: Map con datos del producto.
- `eliminarProducto` — args: `{ 'idProducto': int }`.
- `buscarProductos` — args: `{ 'criterio': String, 'tipo': String }`.
- `buscarProductoEnVentaPorIdONombre` — args: `{ 'criterio': String }`.
- `calcularMontos` — args: `{ 'precioUnitario': double, 'cantidad': int }`.
- `registrarVenta` — args: `{ 'venta': Map, 'detalles': List<Map> }`.

---

## 7. Seguridad y buenas prácticas

- No subir credenciales a Git.
- Validar parámetros en Flutter y Android.
- Considerar migrar a API REST si el proyecto crece.

---

## 8. Siguientes pasos sugeridos

- Generar ejemplos JSON/Map para cada método.
- Crear un `MainActivity` completo con todos los métodos mapeados.
- Ejecutar una compilación automática y revisar errores.

FIN DEL DOCUMENTO
*/
