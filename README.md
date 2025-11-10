# Grupo7-Procesos_De_Software
Repositorio de Github respecto al proyecto elaborado por el grupo 7 del curso de Procesos de Software, en el cual vamos a tener la asignacion de roles y ramas principales. Asi como tambien el sustento de la eleccion del lenguaje y los frameworks correspondientes.

## Detalles de proyecto

***Arquitectura seleccionada: Modelo - Vista - Controlador (MVC)***

Justificación:

 - El patrón MVC permite separar la lógica de negocio, la presentación y el control de flujo de la aplicación, logrando.

 - Mantenimiento más sencillo, ya que cada capa tiene responsabilidades claras.

 - Reutilización de código, pues el modelo puede ser usado por distintas interfaces.

 - Escalabilidad, lo que facilita añadir nuevas funciones sin afectar el resto del sistema.

 - Trabajo en equipo más ordenado, cada integrante puede trabajar en una capa distinta (Modelo, Vista o Controlador).



***Lenguaje de programación seleccionado: Java, Dart***

Justificación:

 - Al ser una aplicación destinada al uso vía Móvil y Computador, es necesaria la viabilidad de lenguajes que puedan compilarse en ambas modalidades.

 - Java puede compilarse en ambos dispositivos, junto a su gran velocidad y capacidad para la programación orientada a objetos y funcional, es evidentelo apto que es para el proyecto.

 - Dart es un lenguaje de programación orientado a Objetos, sin embargo, lo usaremos para la creación del apartado FrontEnd del proyecto. Este lenguajejunto con Flutter, de Google, nos apoyará bastante en la presentación visual y eficiencia de uso del mismo.



***Framework(s) seleccionado(s):***

**JWT(Json Web Tokens)**
Justificación:

 - Simplicidad para 3 usuarios: No necesitas complicadas sesiones en el servidor.

 - Cada petición contiene toda la información necesaria, haciendo el sistema más simple de mantener.

 - Escalabilidad inmediata: Si después quieres agregar más usuarios, JWT funciona igual sin cambiar nada en la autenticación.

 - Control de permisos integrado: En el mismo token puedes incluir el rol de cada usuario (admin, supervisor, empleado) para controlar qué puede hacer cada uno en el inventario.

 - Mayor Facilidad ya que JWT elimina complejidad innecesaria mientras mantiene la aplicación segura y preparada para crecer


**Flutter flow (para el frontend)**
Justificación:

 - Nos permite diseñar la aplicación móvil de manera visual y rápida.

 - Facilita generar aplicaciones que funcionan en Android (y en el futuro también en iOS) sin tener que programar todo a mano.

 - Tiene integración con la base de datos y con la API del servidor, lo que simplifica la conexión con Spring Boot.

 - Genera una interfaz amigable e intuitiva, adaptada al perfil de la dueña y vendedores.

 - Nos ayuda a centrarnos en las funcionalidades y no solo en la programación detallada.

**String boot  (para el backend)**
Justificación:

 - Permite desarrollar aplicaciones de manera rápida y ordenada.

 - Facilita la conexión con la base de datos que usaremos para el inventario, ventas y compras.

 - Es un framework muy usado en la industria, lo que asegura estabilidad y buenas prácticas.

 - Nos ayuda a separar bien la lógica del negocio (reglas) de la interfaz (lo que ve el usuario).

 - Hace que el sistema sea escalable, es decir, que en un futuro se pueda ampliar sin rehacerlo desde cero.





**Evidencia de configuración de GitHub: (enlace al repositorio)**

https://github.com/XJosxx/Grupo7-ProcesosDeSoftware
