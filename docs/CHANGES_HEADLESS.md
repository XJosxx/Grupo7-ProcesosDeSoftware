# Cambios: Refactor Headless — AppTienda

Fecha: 9 de noviembre de 2025

Resumen breve:
- Se convirtió `MainActivity` a una versión headless (sin uso de XML / view binding).
- Se añadió `LauncherActivity` como punto de entrada LAUNCHER minimalista que inicia `MainActivity`.
- Se añadió `FlutterBridge` placeholder (devuelve JSON de error) para que el nuevo frontend implemente su propio bridge.
- `AndroidManifest.xml` actualizado para usar `LauncherActivity` como LAUNCHER y mantener `MainActivity` declarada.

Cómo integrar un nuevo frontend:
- Opción A: Crear una Activity que extienda `AppCompatActivity` e inyecte servicios/repositorios; puede arrancar `MainActivity` o comunicarse con los repositorios directamente.
- Opción B: Implementar un nuevo bridge (similar a `FlutterBridge`) que exponga métodos JSON y los consuma desde el frontend.

Archivos añadidos/modificados:
- Modificado: `app/src/main/java/com/example/myapplication/MainActivity.kt` (headless)
- Añadido: `app/src/main/java/com/example/myapplication/LauncherActivity.kt` (LAUNCHER)
- Añadido: `app/src/main/java/com/example/myapplication/bridge/FlutterBridge.kt` (placeholder)
- Modificado: `app/src/main/AndroidManifest.xml` (Launcher cambiado)
- Añadido: `docs/CHANGES_HEADLESS.md` (este archivo)

Siguientes pasos recomendados:
1. Asegurar que los servicios/repositories estén implementados y sean independientes de UI.
2. Implementar tests unitarios para los servicios de negocio.
3. Crear un bridge completo para el frontend que exponga los métodos necesarios (gestión de productos, ventas, usuarios, reportes).
4. Verificar build completo con `./gradlew.bat assembleDebug` en Windows.

Notas:
- Este cambio evita tocar layouts y fragments existentes para minimizar el riesgo; si deseas eliminar los fragments y layouts, puedo hacerlo en una siguiente iteración (ver riesgos en la navegación y recursos referenciados).
- Si necesitas que `MainActivity` exponga una API basada en Intents o AIDL para comunicación, puedo implementar ejemplos.
