Base de datos SQLite para AppTienda (móvil)

Archivos:
- `schema.sql`: crea las tablas principales (productos, ventas, compras, usuarios) en sintaxis SQLite.
- `seed.sql`: datos de ejemplo para pruebas.
- `db.properties.example`: plantilla opcional (no necesaria para SQLite en el dispositivo).

Instrucciones rápidas para Android:
1) En Android lo más recomendable es usar Room (AndroidX) como capa de persistencia sobre SQLite.
   - Añade las dependencias de Room en `build.gradle.kts` (implementation y kapt/annotationProcessor según corresponda).
   - Define entidades (`@Entity`), DAOs (`@Dao`) y la base de datos (`@Database`).
2) Si solo quieres importar el SQL para pruebas en un emulador:
   - Puedes ejecutar los scripts con una herramienta sqlite3 o usar Android Studio Database Inspector para ejecutar comandos SQL contra la BD del app.

Ejemplo de flujo con Room (resumen):
- Crear entidad `Producto` con anotaciones `@Entity`.
- Crear `ProductoDao` con métodos `@Query("SELECT * FROM productos")`, `@Insert`, `@Update`, `@Delete`.
- Crear `AppDatabase extends RoomDatabase` especificando entidades.
- Instanciar Room.databaseBuilder(context, AppDatabase.class, "apptienda.db")

Seguridad y buenas prácticas:
- No incluyas credenciales en la app.
- Para datos sensibles considera almacenamiento cifrado (EncryptedSharedPreferences o SQLCipher).

Nota: `db.properties.example` queda por compatibilidad si más tarde decides poner un backend que use JDBC; no es necesario para SQLite local.


