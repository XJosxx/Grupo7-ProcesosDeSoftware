package com.example.myapplication.flutterbridge;

import com.example.myapplication.entidades.*;
import com.example.myapplication.servicios.*;
import com.example.myapplication.servicios.implementacion.*;
import com.example.myapplication.DTOs.MontosCalculados;
import com.example.myapplication.DTOs.BoletaVentaDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que maneja las llamadas al MethodChannel desde Flutter.
 * Proporciona métodos para interactuar con los servicios de la aplicación.
 * Cada método corresponde a una funcionalidad específica que puede ser invocada desde Flutter.
 * Cada método devuelve un Map<String, Object> que representa la respuesta a ser enviada de vuelta a Flutter.
 * Cada método maneja excepciones y errores, devolviendo mensajes adecuados en caso de fallos.
 * Los métodos incluyen funcionalidades para login, gestión de productos, ventas y compras.
 * Cada método está documentado para facilitar su comprensión y uso.
 * Ejemplo de uso:
 *   MethodChannelHandler handler = new MethodChannelHandler();
 *  Map<String, Object> response = handler.login(dni, password);
 * Ejemplo de llamado desde Flutter:
 *   final Map<String, dynamic> response = await methodChannel.invokeMethod('login', {'
 * dni': dni, 'password': password});
 * Cada método está diseñado para ser fácilmente integrable con la lógica de Flutter.
 * 
 * EJEMPLO 2:
 * 
 * // En Flutter llamas así:
void obtenerTodasLasVentas() async {
 * try {
    final resultado = await platform.invokeMethod('listarVentas');
    // No necesitas pasar parámetros, solo el nombre del método
    
    // ⬇️ ESTO ES LO QUE RECIBES:
    print('Respuesta completa: $resultado');
    
    if (resultado['status'] == 'ok') {
  *    print('✅ ${resultado['mensaje']}');
   *   print('📊 Total de ventas: ${resultado['total']}');
  *    
  *    List<dynamic> ventas = resultado['ventas'];
   *   print('--- LISTA DE VENTAS ---');
 *     for (var venta in ventas) {
        print('ID: ${venta['id']}');
        print('Fecha: ${venta['fecha']}');
        print('Total: S/. ${venta['total']}');
        print('Boleta: ${venta['numeroBoleta']}');
        print('Cliente: ${venta['clienteDni']}');
        print('-------------------');
  *    }
    } else {
  *    print('❌ Error: ${resultado['mensaje']}');
    }
 * } catch (e) {
    print('💥 Error de conexión: $e');
 * }
}

// Llamar la función
obtenerTodasLasVentas();

*
*
*LO QUE SE RECIBE:
 * 
 * {
 * "status": "ok",
 * "mensaje": "Ventas obtenidas exitosamente",
 * "ventas": [
    {
   *   "id": 1,
   *   "fecha": "2024-01-15",
   *   "total": 185.50,
   *   "numeroBoleta": "B20240115-0001",
   *   "clienteDni": "70123456",
   *   "vendedorId": 1,
  *    "monto": 185.50
    },
    {
  *    "id": 2,
 *     "fecha": "2024-01-15", 
  *    "total": 320.00,
 *     "numeroBoleta": "B20240115-0002",
  *    "clienteDni": "70876543",
  *    "vendedorId": 2,
  *    "monto": 320.00
    },
    {
   *   "id": 3,
  *    "fecha": "2024-01-14",
   *   "total": 95.80,
  *    "numeroBoleta": "B20240114-0001",
   *   "clienteDni": "71234567",
  *    "vendedorId": 1,
   *   "monto": 95.80
    }
 * ],
 * "total": 3
}
 * 
 */

public class MethodChannelHandler {

    private ServicioUsuario usuarioService = new ServicioUsuarioImplementacion();
    private ServicioProducto productoService = new ServicioProductoImplementacion();
    private ServicioVenta ventaService = new ServicioVentaImplementacion();
    private ServicioCompra compraService = new ServicioCompraImplementacion();

    // -------- LOGIN --------
    
    public Map<String, Object> login(String dni, String password) {
        Usuario u = usuarioService.login(dni, password);
        if (u != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ok");
            response.put("mensaje", "Login exitoso");
            response.put("usuario", u.getNombre());
            response.put("rol", u.getRol());
            return response;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("mensaje", "Credenciales inválidas");
        return error;
    }

    // -------- PRODUCTOS --------
    public List<Producto> obtenerProductos() {
        return productoService.obtenerTodos();
    }

    // -------- AGREGAR PRODUCTO --------
    public Map<String, Object> agregarProducto(Producto producto) {
        try {
            boolean exito = productoService.agregarProducto(producto);

            Map<String, Object> resultado = new HashMap<>();
            if (exito) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Producto agregado exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo agregar el producto. Verifique los datos.");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error interno: " + e.getMessage());
            return error;
        }
    }

    // -------- ACTUALIZAR PRODUCTO --------
    public Map<String, Object> actualizarProducto(Producto producto) {
        try {
            // Usar el método update del repository en lugar de actualizarInventario
            // O si actualizarInventario es correcto, mantenerlo
            productoService.actualizarInventario(producto);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "ok");
            resultado.put("mensaje", "Producto actualizado exitosamente");
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al actualizar producto: " + e.getMessage());
            return error;
        }
    }

    // -------- ELIMINAR PRODUCTO --------
    public Map<String, Object> eliminarProducto(int idProducto) {
        try {
            boolean exito = productoService.eliminarProducto(idProducto);

            Map<String, Object> resultado = new HashMap<>();
            if (exito) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Producto eliminado exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo eliminar el producto. Verifique que no tenga stock.");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error interno: " + e.getMessage());
            return error;
        }
    }
    public List<Producto> buscarProductos(String criterio, String tipo) {
        return productoService.buscarProductos(criterio, tipo);
    }

    // -------- VALIDACIÓN DE PRODUCTOS --------
    public boolean validarProductoExiste(int productoId) {
        return ventaService.validarProductoExiste(productoId);
    }

    public List<Producto> buscarProductoEnVentaPorIdONombre(String criterio) {
        return ventaService.buscarProductoEnVentaPorIdONombre(criterio);
    }

    // -------- CÁLCULOS DE MONTO --------
    public Map<String, Object> calcularMontos(double precioUnitario, int cantidad) {
        MontosCalculados montos = ventaService.calcularMontos(precioUnitario, cantidad);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("subtotal", montos.getSubtotal());
        resultado.put("igv", montos.getIgvSolo());
        resultado.put("total", montos.getTotalConIGV());

        return resultado;
    }

    public Map<String, Object> calcularMontosVentaCompleta(List<DetalleVenta> detalles) {
        MontosCalculados montos = ventaService.calcularMontosVentaCompleta(detalles);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("subtotal", montos.getSubtotal());
        resultado.put("igv", montos.getIgvSolo());
        resultado.put("total", montos.getTotalConIGV());

        return resultado;
    }

    public double calcularTotalVenta(List<DetalleVenta> detalles) {
        return ventaService.calcularTotalVenta(detalles);
    }

    // -------- VENTAS --------
    public int registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        return ventaService.registrarVenta(venta, detalles);
    }

    
    public Map<String, Object> listarVentas() {
        try {
            List<Venta> ventas = ventaService.listarVentas();

    // Construir la respuesta
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "ok");
            resultado.put("mensaje", "Ventas obtenidas exitosamente");
            resultado.put("ventas", ventas);
            resultado.put("total", ventas.size());
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al obtener ventas: " + e.getMessage());
            error.put("ventas", new ArrayList<>());
            error.put("total", 0);
            return error;
        }
    }

    // -------- REGISTRAR COMPRA --------
    public Map<String, Object> registrarCompra(Compra compra, List<DetalleCompra> detalles) {
        try {
            boolean exito = compraService.registrarCompra(compra, detalles);

            Map<String, Object> resultado = new HashMap<>();
            if (exito) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Compra registrada exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo registrar la compra. Verifique los datos.");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al registrar compra: " + e.getMessage());
            return error;
        }
    }

    public List<Compra> listarCompras() {
        return compraService.listarCompras();
    }



    // -------- ELIMINAR DETALLE DE COMPRA --------
    public Map<String, Object> eliminarDetalleCompra(int detalleId) {
        try {
            boolean eliminado = compraService.eliminarDetalleCompra(detalleId);

            Map<String, Object> resultado = new HashMap<>();
            if (eliminado) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Detalle eliminado exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo eliminar el detalle");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", e.getMessage());
            return error;
        }
    }

    // -------- EDITAR DETALLE DE COMPRA --------
    public Map<String, Object> editarDetalleCompra(int detalleId, int cantidad, double precio) {
        try {
            boolean actualizado = compraService.editarDetalleCompra(detalleId, cantidad, precio);

            Map<String, Object> resultado = new HashMap<>();
            if (actualizado) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Detalle actualizado exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo actualizar el detalle");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", e.getMessage());
            return error;
        }
    }

    // -------- BOLETA DE VENTA --------
    public Map<String, Object> generarBoleta(int ventaId, List<DetalleVenta> detalles) {
        try {
            BoletaVentaDTO boleta = ventaService.generarBoleta(ventaId, detalles);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("numeroBoleta", boleta.getVenta().getNumeroBoleta());
            resultado.put("fecha", boleta.getVenta().getFecha());
            resultado.put("cliente", boleta.getCliente() != null ? boleta.getCliente().getNombre() : "Cliente no registrado");
            resultado.put("vendedor", boleta.getVendedor() != null ? boleta.getVendedor().getNombre() : "Vendedor no registrado");
            resultado.put("detalles", boleta.getDetalles());
            resultado.put("subtotal", boleta.getSubtotal());
            resultado.put("igv", boleta.getIgv());
            resultado.put("total", boleta.getTotal());
            resultado.put("status", "success");

            return resultado;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", e.getMessage());
            return error;
        }
    }

    public Map<String, Object> validarDNI(String dni) {
        return com.example.myapplication.util.ResponseUtil.validarDNIConMensaje(dni);
    }

    // -------- COMPRAS - AGREGAR PRODUCTO NUEVO --------
    public Map<String, Object> agregarProductoNuevoACompra(DetalleCompra detalle) {
        try {
            DetalleCompra detalleValidado = compraService.agregarProductoNuevoACompra(detalle);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "ok");
            resultado.put("mensaje", "Producto agregado exitosamente");
            resultado.put("detalle", detalleValidado);
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", e.getMessage());
            return error;
        }
    }

    public Map<String, Object> validarDatosProductoNuevo(String codigo, int cantidad, double precioCompra) {
        boolean esValido = compraService.validarDatosProductoNuevo(codigo, cantidad, precioCompra);

        Map<String, Object> resultado = new HashMap<>();
        if (esValido) {
            resultado.put("status", "ok");
            resultado.put("mensaje", "Datos del producto válidos");
        } else {
            resultado.put("status", "error");
            resultado.put("mensaje", "Datos del producto inválidos");
        }
        return resultado;
    }

    // -------- COMPRAS - AGREGAR PRODUCTO EXISTENTE --------
    public Map<String, Object> agregarProductoExistenteACompra(int productoId, int cantidad) {
        try {
            DetalleCompra detalle = compraService.agregarProductoExistenteACompra(productoId, cantidad);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "ok");
            resultado.put("mensaje", "Producto existente agregado exitosamente");
            resultado.put("detalle", detalle);
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", e.getMessage());
            return error;
        }
    }


    // -------- VENTAS - FILTROS --------
    public Map<String, Object> obtenerVentasPorDia(String fecha) {
        try {
            List<Venta> ventas = ventaService.obtenerVentasPorDia(fecha);
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "ok");
            resultado.put("ventas", ventas);
            resultado.put("total", ventas.size());
            return resultado;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al obtener ventas: " + e.getMessage());
            error.put("ventas", new ArrayList<>());
            error.put("total", 0);
            return error;
        }
    }


    // -------- PRODUCTOS - DETALLE --------
    public Map<String, Object> obtenerProductoPorId(int id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            Map<String, Object> resultado = new HashMap<>();
            if (producto != null) {
                resultado.put("status", "ok");
                resultado.put("producto", producto);
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "Producto no encontrado");
            }
            return resultado;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al obtener producto: " + e.getMessage());
            return error;
        }
    }

    // -------- VALIDACIÓN DE STOCK --------
    public Map<String, Object> validarStock(int productoId, int cantidad) {
        try {
            boolean stockValido = productoService.validarStock(productoId, cantidad);
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", "ok");
            resultado.put("stockValido", stockValido);
            resultado.put("mensaje", stockValido ? "Stock suficiente" : "Stock insuficiente");
            return resultado;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al validar stock: " + e.getMessage());
            return error;
        }
    }

    public Map<String, Object> eliminarVenta(int ventaId) {
        try {
            boolean exito = ventaService.eliminarVenta(ventaId);

            Map<String, Object> resultado = new HashMap<>();
            if (exito) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Venta eliminada exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo eliminar la venta");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al eliminar venta: " + e.getMessage());
            return error;
        }
    }

    // -------- ELIMINAR COMPRA --------
    public Map<String, Object> eliminarCompra(int compraId) {
        try {
            boolean exito = compraService.eliminarCompra(compraId);

            Map<String, Object> resultado = new HashMap<>();
            if (exito) {
                resultado.put("status", "ok");
                resultado.put("mensaje", "Compra eliminada exitosamente");
            } else {
                resultado.put("status", "error");
                resultado.put("mensaje", "No se pudo eliminar la compra");
            }
            return resultado;

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("mensaje", "Error al eliminar compra: " + e.getMessage());
            return error;
        }
    }

}