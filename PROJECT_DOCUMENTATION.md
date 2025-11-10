# Documentación del Proyecto - AppTienda (Headless Android)

## Resumen de Cambios
Se ha realizado una refactorización importante para separar la lógica de negocio de la interfaz de usuario, eliminando las dependencias de XML y Flutter.

### Archivos Modificados/Eliminados

#### Actividades Principales
- `MainActivity`: Convertida a versión headless sin XML binding
- `LauncherActivity`: Nueva actividad LAUNCHER minimalista
- `FlutterBridge`: Convertido a placeholder que devuelve JSON de error

#### Archivos UI Eliminados
- Fragments de dashboard
- Fragments de home
- Fragments de notifications
- Activities dependientes de UI

### Puntos de Integración para Frontend

#### 1. Base de Datos
- Se mantiene la estructura SQLite original (ver `app/database/`)
- Tablas principales: productos, ventas, compras, usuarios
- Acceso mediante Room (AndroidX)

#### 2. Puntos de Entrada
- `LauncherActivity`: Punto de entrada principal
- `MainActivity`: Maneja la lógica de negocio core
- Repositories: Mantienen la lógica de acceso a datos

### Arquitectura Actual

```
app/
├── database/          # Capa de persistencia SQLite
├── repositories/      # Acceso a datos
└── services/         # Lógica de negocio
```

### Integración con Nuevo Frontend

Para integrar un nuevo frontend:

1. **Opción A - Actividad Custom**
   - Crear nueva actividad que extienda de `AppCompatActivity`
   - Inyectar servicios/repositories necesarios
   - Implementar UI propia

2. **Opción B - Framework Multiplataforma**
   - Crear nuevo bridge (similar al antiguo FlutterBridge)
   - Implementar métodos para:
     - Gestión de productos
     - Proceso de ventas
     - Gestión de usuarios
     - Reportes

### Notas de Implementación
- Se mantiene Room como ORM
- Los servicios son independientes de la UI
- Repositories implementan patrón Repository
- La autenticación está desacoplada de la UI

### Próximos Pasos Recomendados
1. Verificar build completo
2. Implementar pruebas de integración
3. Documentar APIs de servicios
4. Crear ejemplos de integración# Documentación del proyecto — AppTienda

Fecha: 9 de noviembre de 2025

Esta documentación inicial explica la estructura del proyecto, las responsabilidades de los módulos principales y describe con detalle las clases y métodos clave que encontré al analizar el código. Está pensada para que tu equipo pueda entender las conexiones, cómo compilar y qué mejorar.

## Índice

- Resumen del proyecto
- Estructura principal de carpetas
- Cómo compilar / ejecutar (entorno Windows)
- Módulos principales
  - Base de datos local (`DatabaseHelper`)
  - Repositorios (DAO-like)
  - Servicios (lógica de negocio)
  - Autenticación y sesión
  - Networking (Retrofit / APIs)
  - UI principal (Actividades / navegación)
- Modelos de datos
- Contratos / formatos de datos
- Casos de error y consideraciones
- Archivos revisados y pendientes de documentar
- Próximos pasos sugeridos


## Resumen del proyecto

AppTienda es una aplicación móvil Android (Java) para la gestión de productos, ventas y compras. Usa una base de datos SQLite local gestionada por `DatabaseHelper`, tiene capas de repositorio que exponen operaciones CRUD, servicios que implementan la lógica de negocio y una UI basada en Activities/Fragments con navegación por BottomNavigation.

El proyecto tiene soporte para llamadas a un backend (OpenAPI documentado en `app/openapi-backend.yaml`) usando Retrofit (interfaces `ProductApi`, etc.), aunque la clase `ApiClient` está como esqueleto y requiere configuración de Retrofit/OkHttp.

Se usa almacenamiento local de sesión mediante `AuthManager` (SharedPreferences). La autenticación/registro es local (hash con SHA-256 y token generado en base64) y se sugiere reforzar para producción.


## Estructura principal de carpetas (resumen)

- `/app` — módulo Android principal
  - `build.gradle.kts` — configuración del módulo (dependencias como Room, Retrofit, Glide, etc.)
  - `openapi-backend.yaml` — especificación OpenAPI del backend
  - `src/main/java/com/example/apptienda/` — código fuente Java
    - `data/` — `DatabaseHelper`, `UsuarioRepository`, `VentaRepository`, `CompraRepository`
    - `servicios/` — `ServicioAuth`, `ServicioProducto`, `ServicioVenta` (si existe)
    - `network/` — `ApiClient`, `ProductApi`, `PurchaseApi`, `SalesApi`
    - `Autenticacion/` — `AuthManager`, `JwtUtils` (métodos utilitarios)
    - `ui/` — actividades y fragments (AuthActivity, MainActivity, ProductActivity, SalesActivity, ReportActivity, etc.)
    - `modelos/` — POJOs: `Producto`, `Usuario`, `modeloVenta`, `modeloCompra`, `Categoria`...
    - `adapter/` — adaptadores para listas (ProductAdapter, SalesAdapter...)
    - `utils/` — utilidades varias (JwtUtils, NetworkUtils placeholder)
  - `src/main/res/` — layouts, strings, themes, xml provider paths
  - `database/` — `seed.sql`, `schema.sql`, `db.properties.example` (scripts y ayuda para la BD)


## Cómo compilar / ejecutar (Windows)

En Windows (PowerShell) desde la raíz del repo puedes compilar la app (modo debug) con:

```powershell
.
\gradlew.bat assembleDebug
```

Para instalar en un dispositivo conectado (si el APK es generado) puedes usar `installDebug`:

```powershell
.
\gradlew.bat installDebug
```

Notas:
- El `compileSdk` es 34 y `targetSdk` 34. Asegúrate de tener el SDK correspondiente instalado en Android Studio.
- El proyecto usa Java 17 en `compileOptions`.


## Módulos principales (documentación detallada)

### 1) Base de datos local — `DatabaseHelper` (app/src/main/java/com/example/apptienda/data/DatabaseHelper.java)

Resumen: Clase que extiende `SQLiteOpenHelper` y centraliza la lógica de almacenamiento local para productos, usuarios, ventas y compras. Maneja creación/upgrade de tablas y operaciones CRUD así como reglas de negocio relacionadas con stock y generación de códigos de venta.

Esquema de tablas (principalmente):
- `productos`:
  - id (PK), nombre, precio_compra, precio_venta, stock, categoria, imagen_url, activo (0/1), fecha_creacion, ultima_modificacion
- `usuarios`:
  - id (PK), nombre, email (UNIQUE), password (hashed), rol, fecha_creacion
- `ventas`:
  - id (PK), codigo (UNIQUE), producto_id (FK productos.id), producto_nombre, dni_cliente, precio_unitario, costo_unitario, cantidad, igv_porcentaje, igv_monto, total_sin_igv, total_con_igv, fecha
- `compras`:
  - id (PK), producto_id (FK), proveedor_ruc, proveedor_nombre, precio_unitario, cantidad, total, fecha

Funciones/métodos clave (resumen):
- insertarProducto(Producto): Inserta un producto y devuelve el id.
- obtenerProducto(long id): Devuelve un `Producto` mapeado o `null`.
- obtenerTodosLosProductos(): Lista activos ordenados por nombre.
- obtenerProductosPorCategoria(String): Filtra por categoría.
- actualizarProducto(Producto): Actualiza y setea `ultima_modificacion`.
- eliminarProducto(long id): No borra físicamente; marca `activo=0` y actualiza `ultima_modificacion`.

- insertarUsuario(Usuario): Inserta usuario (password ya debe estar hashed por el servicio).
- obtenerUsuarioPorEmail(String): Busca por email (para login/registro).
- listarUsuarios(), actualizarUsuario(), eliminarUsuario(id)

Ventas:
- registrarVenta(modeloVenta): Lógica transaccional que:
  - Ajusta stock (llamando a `aplicarAjusteStock`) si la venta tiene producto registrado.
  - Genera código si no existe (`generarCodigoVenta`).
  - Calcula totales (usa métodos en `modeloVenta`) y persiste la venta.
  - Si stock insuficiente lanza IllegalStateException.
- actualizarVenta(modeloVenta)
- eliminarVenta(ventaId): Reversión de stock si aplica (en transacción).
- listarVentas(), obtenerVentaPorCodigo(), obtenerVentaPorId()

Compras:
- registrarCompra(modeloCompra): Transaccional. Inserta compra, actualiza stock y actualiza precioCompra si cambia.
- listarCompras(), eliminarCompra()

Helpers:
- aplicarAjusteStock(SQLiteDatabase, productoId, delta): Lee stock actual y aplica `delta` (positivo para compras, negativo para ventas). Redondea a 2 decimales y previene stock negativo.
- generarCodigoVenta(SQLiteDatabase): Genera un `V-000001` incremental basado en el max id en la tabla `ventas`.

Consideraciones/Edge-cases:
- Las transacciones aseguran consistencia en ventas/compras.
- `aplicarAjusteStock` devuelve `false` si producto no existe o el ajuste produciría stock < 0.
- Métodos usan `getReadableDatabase()` y `getWritableDatabase()` apropiadamente y cierran cursores en `finally`.


### 2) Repositorios (DAO-like)

- `CompraRepository` — Método registrar(lista/eliminar) delegando en `DatabaseHelper`.
- `UsuarioRepository` — `crear`, `buscarPorEmail`, `listar`, `actualizar`, `eliminar`.
- `VentaRepository` — registrar/listar/obtener/actualizar/eliminar/generarCodigo.

Estos repositorios son simples wrappers que aíslan `DatabaseHelper` y facilitan la inyección en servicios.


### 3) Servicios (lógica de negocio)

- `ServicioProducto` (app/src/main/java/com/example/apptienda/servicios/ServicioProducto.java):
  - Crear/obtener/listar/actualizar/desactivar producto.
  - `actualizarStock(productoId, cantidad)` — obtiene producto, ajusta el stock en memoria y persiste.
  - `calcularValorInventario()` — suma precioCompra * stock de todos los productos.
  - `buscarProductosBajoStock(int limiteMinimo)` — filtro local sobre la lista completa.

- `ServicioAuth` (app/src/main/java/com/example/apptienda/servicios/ServicioAuth.java):
  - `registrar(nombre,email,password,rol)` — valida existencia por email, hashea password con SHA-256 (Base64) y crea usuario.
  - `login(email,password)` — verifica hash y devuelve `Usuario`.
  - `guardarSesion(Usuario)` — genera token (Base64 simple) y lo guarda en `AuthManager`.
  - `logout()` — delega a `AuthManager`.

Observaciones de seguridad: el hashing con SHA-256 + Base64 es un paso, pero para producción deberías usar algoritmos con sal y trabajo (bcrypt/scrypt/argon2). Además, la generación de token aquí no es un JWT firmado; solo es un token local. Para integrarse con un backend real se debería usar JWT firmado por servidor.


### 4) Autenticación y sesión

- `AuthManager` (app/src/main/java/com/example/apptienda/Autenticacion/AuthManager.java):
  - Guarda token, email y rol en `SharedPreferences`.
  - Métodos: `saveSession`, `saveToken`, `getToken`, `getEmail`, `getRole`, `isLoggedIn`, `logout`.
  - Uso: instanciado en `AuthActivity` y `MainActivity` para proteger navegación y persistir sesión.

- `JwtUtils` (hay dos implementaciones):
  - `utils.JwtUtils.decodePayload(jwt)` — decodifica payload (útil para debug / lectura rápida).
  - `Autenticacion.JwtUtils` — extrae email, role y valida expiración leyendo payload JSON. NOTA: ambas son utilidades de lectura; no validan firma.


### 5) Networking

- `ApiClient` — Esqueleto para configurar Retrofit/OkHttp. Requiere implementación para crear instancia singleton de Retrofit, añadir interceptor para token de `AuthManager`, timeouts, logging, etc.

- `ProductApi` — Interface Retrofit con endpoints (getAll, getByCategoria, getById, create, update, delete, buscarProductos, actualizarStock). Está acorde al `openapi-backend.yaml`.

Recomendación: implementar `ApiClient` con Retrofit + OkHttp + Interceptor que lea token desde `AuthManager` y lo inyecte en `Authorization: Bearer <token>`.


### 6) UI principal

- `AuthActivity` — Pantalla de login/registro. Usa `ServicioAuth` para registrar/login y `AuthManager` para guardar la sesión. Controla visibilidad/validaciones básicas en UI.

- `MainActivity` — Inflates `ActivityMainBinding`, configura `BottomNavigationView` y NavController. En `onResume()` redirige a `AuthActivity` si el usuario no está logueado.

- Otras Activities: `ProductActivity`, `SalesActivity`, `ReportActivity` — presentes en el manifiesto; cada una debe usar los servicios/repositorios adecuados para mostrar y operar sobre datos.


## Modelos de datos (ubicación: `modelos/`)

Principales POJOs:
- `Producto` — id, nombre, precioCompra, precioVenta, stock, categoria, imagenUrl, activo, fechaCreacion, ultimaModificacion.
- `Usuario` — id, nombre, email, password (hashed), rol.
- `modeloVenta` — campos para venta, cálculo de totales, método `recalcularTotales()`.
- `modeloCompra` — campos para compras/reabastecimiento.
- `Categoria`, `CategoriaEnum` — enumeración/categorías de producto.


## Contratos / formatos de datos

- OpenAPI: `app/openapi-backend.yaml` define `/api/productos` endpoints y el schema `Producto`. Si se enlaza un backend real, mantener esquema sincronizado con las interfaces Retrofit.

- Token: la app actualmente genera tokens locales en `ServicioAuth` (Base64 de email:timestamp:uuid). `Autenticacion.JwtUtils` asume tokens con payload JSON con campos `email`, `exp`, `role` — esto es compatible con JWT reales pero la app no firma ni valida.


## Casos de error y consideraciones

- DB: `DatabaseHelper` arroja `IllegalStateException` si intenta registrar una venta con stock insuficiente.
- Login/registro: `ServicioAuth` lanza `Exception` con mensaje claro si credenciales inválidas o email ya existe.
- Network: `ApiClient` no implementado; llamadas Retrofit en interfaces devolverán `Call<>` pero requieren configuración del cliente.
- Seguridad:
  - Passwords: usar sal + algoritmo de derivación (bcrypt/argon2) en vez de SHA-256 simple.
  - Tokens: usar JWT firmado por el servidor; validar firma y expiración en cliente cuando sea necesario.
  - No guardar contraseñas en texto ni en logs.


## Archivos revisados hasta ahora

- `README.md` (raíz)
- `build.gradle.kts` (raíz y `app/`)
- `settings.gradle.kts`, `gradle.properties`
- `app/openapi-backend.yaml`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/apptienda/data/DatabaseHelper.java`
- `app/src/main/java/com/example/apptienda/data/{CompraRepository,UsuarioRepository,VentaRepository}.java`
- `app/src/main/java/com/example/apptienda/servicios/{ServicioAuth,ServicioProducto}.java`
- `app/src/main/java/com/example/apptienda/Autenticacion/{AuthManager,JwtUtils}.java`
- `app/src/main/java/com/example/apptienda/utils/JwtUtils.java`
- `app/src/main/java/com/example/apptienda/network/{ApiClient,ProductApi}.java`
- `app/src/main/java/com/example/apptienda/ui/{AuthActivity,MainActivity}.java`
- Listados de paquetes: `adapter/`, `modelos/`, `network/`, `servicios/`, `ui/`, `Autenticacion/`, `utils/`


## Archivos pendientes de documentar (siguiente fase)

- Todos los POJOs en `modelos/` (describir cada propiedad y métodos auxiliares como `recalcularTotales` en `modeloVenta`).
- `adapter/` (describir view holders y binding con layouts para cada adapter: `ProductAdapter`, `SalesAdapter`, `ReportAdapter`).
- UI: fragments dentro de `ui/dashboard`, `ui/home`, `ui/notifications`.
- Implementaciones REST (`PurchaseApi`, `SalesApi`) y usar `ApiClient`.
- `NetworkUtils` (está vacío actualmente) y utilidades auxiliares.
- Recursos XML y layouts (explicar cada layout principal y bindings).


## Plan sugerido y próximos pasos (cómo continuar)

1. Confirmar prioridad: ¿quieren documentación completa ahora o por módulos? Recomendado: por módulos (base de datos + servicios + auth + networking + UI).
2. Automatizar generación parcial: puedo generar documentación para cada archivo Java en formato Markdown (extracto de Javadoc y un resumen de cada método). ¿Deseas que proceda automáticamente con todo el paquete `modelos/` y `ui/`?
3. Mejoras de seguridad: proponer/implementar uso de BCrypt para hashing y reemplazar token local por JWT firmado por backend.
4. Implementar `ApiClient` con Retrofit y un Interceptor que use `AuthManager`.
5. Añadir un archivo `docs/CONTRIBUTING.md` que explique cómo añadir/actualizar la documentación.


## Contrato pequeño (por ejemplo: `DatabaseHelper.registrarVenta`)

- Inputs: `modeloVenta` con productoId o nombre manual, cantidad, precioUnitario, opcional DNI.
- Outputs: `modeloVenta` persistido con id y código generado.
- Errores: lanza `IllegalStateException` si stock insuficiente; transaccional (rollback si falla).

Edge cases:
- Venta sin productoId: usa nombre manual; no ajusta stock.
- Venta con productoId y stock insuficiente: excepción.
- Concurrency: accesos concurrentes a DB en Android deben ser manejados (actualmente las transacciones previenen inconsistencia por hilo, pero revisar si llamadas vienen desde hilos distintos y usar syncronización si necesario).


## Resumen de lo hecho en esta iteración

- Inventarié archivos principales del proyecto.
- Leí y documenté los ficheros de configuración más importantes y módulos clave: DB (`DatabaseHelper`), repositorios, servicios de autenticación y productos, `AuthManager`, utilidades JWT y dos actividades (AuthActivity y MainActivity).
- Creé este archivo `PROJECT_DOCUMENTATION.md` con la documentación inicial y la lista de archivos pendientes.


## ¿Qué hago ahora?

Puedo continuar automáticamente y documentar:
- Todos los POJOs en `app/src/main/java/com/example/apptienda/modelos/`.
- Los adaptadores en `adapter/` y su relación con layouts.
- Los fragments bajo `ui/`.

Indica si prefieres:
- A) Que continúe y genere documentación completa para TODO el código (puede tardar y generar un documento grande). 
- B) Que lo haga por fases (por ejemplo: 1) modelos + adapters, 2) UI + fragments, 3) networking y pruebas).

Si confirmas, procederé con la siguiente fase (empezaría por los POJOs en `modelos/` y generaría descripciones por campo y métodos).


---

Archivo generado automáticamente — puedo actualizarlo y fragmentarlo en `docs/` si prefieres una estructura más navegable.
