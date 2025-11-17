package com.example.myapplication.servicios.implementacion;

import com.example.myapplication.Configuracion.ConfiguracionBaseDatos;
import com.example.myapplication.entidades.Compra;
import com.example.myapplication.entidades.DetalleCompra;
import com.example.myapplication.entidades.Producto;
import com.example.myapplication.repositories.CompraRepository;
import com.example.myapplication.repositories.DetalleCompraRepository;
import com.example.myapplication.repositories.ProductoRepository;
import com.example.myapplication.servicios.ServicioCompra;
import com.example.myapplication.servicios.ServicioProducto;

import com.example.myapplication.repositories.implementacion.CompraRepositoryImplementacion;
import com.example.myapplication.repositories.implementacion.DetalleCompraRepositoryImplementacion;
import com.example.myapplication.repositories.implementacion.ProductoRepositoryImplementacion;
import com.example.myapplication.servicios.implementacion.ServicioProductoImplementacion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ServicioCompraImplementacion implements ServicioCompra {

    // Dependencias inyectadas (solo interfaz)
    private final CompraRepository compraRepo;
    private final DetalleCompraRepository detalleCompraRepo;
    private final ProductoRepository productoRepo;
    private final ServicioProducto servicioProducto;

    // Límites para validación de datos
    private static final double PRECIO_COMPRA_MINIMO = 0.01;
    private static final double PRECIO_COMPRA_MAXIMO = 100000.0;
    private static final int CANTIDAD_MINIMA = 1;
    private static final int CANTIDAD_MAXIMA = 10000;

    /**
     * Constructor para Inyección de Dependencias.
     * Se usa para testing o frameworks avanzados.
     */
    public ServicioCompraImplementacion(
            CompraRepository compraRepo,
            DetalleCompraRepository detalleCompraRepo,
            ProductoRepository productoRepo,
            ServicioProducto servicioProducto) {
        this.compraRepo = compraRepo;
        this.detalleCompraRepo = detalleCompraRepo;
        this.productoRepo = productoRepo;
        this.servicioProducto = servicioProducto;
    }

    /**
     * Constructor por defecto para compatibilidad.
     * Crea instancias de las implementaciones.
     */
    public ServicioCompraImplementacion() {
        this(new CompraRepositoryImplementacion(),
                new DetalleCompraRepositoryImplementacion(),
                new ProductoRepositoryImplementacion(),
                new ServicioProductoImplementacion());
    }


    /**
     * Implementación Transaccional
     * Registra una compra, sus detalles y actualiza el stock. Requiere gestión transaccional externa.
     */
    @Override
    public boolean registrarCompra(Compra compra, List<DetalleCompra> detalles) {

        try {
            // 1. Guardar compra
            boolean compraGuardada = compraRepo.save(compra);
            if (!compraGuardada || compra.getId() <= 0) {
                System.err.println("Fallo al guardar la compra o al obtener el ID generado.");
                return false;
            }

            // 2. Guardar detalles
            for (DetalleCompra detalle : detalles) {
                detalle.setCompraId(compra.getId());

                boolean detalleOk = detalleCompraRepo.save(detalle);
                if (!detalleOk) {
                    throw new RuntimeException("Fallo al guardar el detalle de compra. Se requiere Rollback manual.");
                }
            }

            // 3. Actualizar stock de productos
            boolean stockActualizado = actualizarStockPorCompra(detalles);
            if (!stockActualizado) {
                throw new RuntimeException("Fallo al actualizar el stock del producto. Se requiere Rollback manual.");
            }

            return true;

        } catch (RuntimeException e) {
            System.err.println(" ERROR TRANSACCIONAL en registrarCompra. Se requiere Rollback: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error inesperado en registrarCompra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método para actualizar el stock de productos basado en los detalles de compra
     */
    public boolean actualizarStockPorCompra(List<DetalleCompra> detalles) {
        try {
            System.out.println("Iniciando actualización de stock para " + detalles.size() + " productos...");

            for (DetalleCompra detalle : detalles) {
                Producto producto = productoRepo.findById(detalle.getProductoId());

                if (producto == null) {
                    System.err.println("Producto no encontrado con ID: " + detalle.getProductoId());
                    throw new RuntimeException("Producto no encontrado con ID: " + detalle.getProductoId());
                }

                if (producto.getId() < 0) {
                    System.out.println("Producto con ID temporal " + producto.getId() + " - omitiendo actualización de stock");
                    continue;
                }


                int stockActual = producto.getCantidad();
                int nuevoStock = stockActual + detalle.getUnidades();

                System.out.println("Actualizando stock producto ID: " + producto.getId() +
                        " - Stock anterior: " + stockActual +
                        " + Unidades compradas: " + detalle.getUnidades() +
                        " = Nuevo stock: " + nuevoStock);

                producto.setCantidad(nuevoStock);

                boolean actualizado = productoRepo.update(producto);
                if (!actualizado) {
                    throw new RuntimeException("Error al actualizar stock del producto ID: " + producto.getId() + " (0 filas afectadas)");
                }

                System.out.println("Stock actualizado correctamente para producto: " + producto.getNombre());
            }

            System.out.println("Actualización de stock completada exitosamente");
            return true;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado en actualizarStockPorCompra: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error inesperado durante la actualización de stock.", e);
        }
    }

    @Override
    public DetalleCompra agregarProductoExistenteACompra(int productoId, int cantidad) {
        try {
            Producto producto = servicioProducto.obtenerPorId(productoId);
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado con ID: " + productoId);
            }

            if (cantidad < CANTIDAD_MINIMA) {
                throw new RuntimeException("La cantidad debe ser al menos " + CANTIDAD_MINIMA);
            }
            if (cantidad > CANTIDAD_MAXIMA) {
                throw new RuntimeException("La cantidad no puede exceder " + CANTIDAD_MAXIMA);
            }

            double precioCompra = producto.getPrecioCompra();
            if (precioCompra < PRECIO_COMPRA_MINIMO) {
                throw new RuntimeException("El precio de compra del producto no es válido: " + precioCompra);
            }

            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(productoId);
            detalle.setUnidades(cantidad);
            detalle.setPrecioUnitario(precioCompra);

            System.out.println("Producto agregado a compra: " + producto.getNombre());

            return detalle;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto existente a compra: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Compra> listarCompras() {
        try {
            return compraRepo.findAll();
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Override
    public DetalleCompra agregarProductoNuevoACompra(DetalleCompra detalle) {
        try {
            if (!validarDetalleCompra(detalle)) {
                throw new RuntimeException("Datos del producto inválidos.");
            }

            if (detalle.getProductoId() <= 0) {
                detalle.setProductoId(generarIdTemporal());
            }

            return detalle;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar producto nuevo a compra: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validarDatosProductoNuevo(String codigo, int cantidad, double precioCompra) {

        if (codigo == null || codigo.trim().isEmpty() || codigo.trim().length() > 50) {
            System.err.println("El código del producto es inválido o demasiado largo.");
            return false;
        }

        if (cantidad < CANTIDAD_MINIMA || cantidad > CANTIDAD_MAXIMA) {
            System.err.println("La cantidad está fuera del rango permitido.");
            return false;
        }

        if (precioCompra < PRECIO_COMPRA_MINIMO || precioCompra > PRECIO_COMPRA_MAXIMO) {
            System.err.println("El precio de compra está fuera del rango permitido.");
            return false;
        }

        return true;
    }

    private boolean validarDetalleCompra(DetalleCompra detalle) {
        if (detalle == null) {
            System.err.println("El detalle de compra no puede ser nulo");
            return false;
        }

        if (detalle.getUnidades() < CANTIDAD_MINIMA || detalle.getUnidades() > CANTIDAD_MAXIMA) {
            System.err.println("Las unidades están fuera del rango permitido.");
            return false;
        }

        if (detalle.getProductoId() == 0) {
            System.err.println("El producto debe tener un identificador válido (positivo o temporal negativo)");
            return false;
        }

        return true;
    }

    /**
     * Genera un ID temporal negativo.
     */
    private int generarIdTemporal() {
        return -Math.abs(UUID.randomUUID().hashCode());
    }

    @Override
    public boolean eliminarDetalleCompra(int detalleId) {
        try {
            DetalleCompra detalle = detalleCompraRepo.findById(detalleId);
            if (detalle == null) {
                System.out.println("Detalle de compra no encontrado con ID: " + detalleId);
                return false;
            }

            boolean eliminado = detalleCompraRepo.delete(detalleId);

            if (eliminado) {
                System.out.println("Detalle de compra eliminado exitosamente");
            } else {
                System.out.println("Error al eliminar detalle de compra (0 filas afectadas)");
            }

            return eliminado;

        } catch (RuntimeException e) {
            System.err.println("Error en eliminarDetalleCompra: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado en eliminarDetalleCompra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean editarDetalleCompra(int detalleId, int cantidad, double precio) {
        try {
            DetalleCompra detalle = detalleCompraRepo.findById(detalleId);
            if (detalle == null) {
                System.out.println("Detalle de compra no encontrado con ID: " + detalleId);
                return false;
            }

            if (cantidad < CANTIDAD_MINIMA || cantidad > CANTIDAD_MAXIMA) {
                System.err.println("Cantidad fuera de rango.");
                return false;
            }
            if (precio < PRECIO_COMPRA_MINIMO || precio > PRECIO_COMPRA_MAXIMO) {
                System.err.println("Precio fuera de rango.");
                return false;
            }

            boolean cambios = false;
            if (detalle.getUnidades() != cantidad) {
                detalle.setUnidades(cantidad);
                cambios = true;
            }

            if (detalle.getPrecioUnitario() != precio) {
                detalle.setPrecioUnitario(precio);
                cambios = true;
            }

            if (!cambios) {
                return true;
            }

            boolean actualizado = detalleCompraRepo.update(detalle);

            if (!actualizado) {
                throw new RuntimeException("Fallo al actualizar detalle de compra (0 filas afectadas).");
            }

            return true;

        } catch (RuntimeException e) {
            System.err.println("Error en editarDetalleCompra: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado en editarDetalleCompra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  Implementación Transaccional
     */
    @Override
    public boolean eliminarCompra(int compraId) {
        try {
            Compra compra = compraRepo.findById(compraId);
            if (compra == null) {
                System.out.println("Compra no encontrada con ID: " + compraId);
                return false;
            }

            List<DetalleCompra> detalles = detalleCompraRepo.findByCompraId(compraId);

            // 3. Revertir el stock de los productos
            for (DetalleCompra detalle : detalles) {
                Producto producto = productoRepo.findById(detalle.getProductoId());
                if (producto != null) {
                    if (producto.getId() < 0) continue;

                    int nuevoStock = producto.getCantidad() - detalle.getUnidades();

                    if (nuevoStock < 0) {
                        System.err.println("Advertencia: Stock negativo para producto ID: " + producto.getId());
                    }

                    producto.setCantidad(nuevoStock);
                    if (!productoRepo.update(producto)) {
                        throw new RuntimeException("Fallo al revertir stock para producto ID: " + producto.getId());
                    }
                } else {
                    System.err.println("Producto no encontrado ID: " + detalle.getProductoId() + " al revertir stock.");
                }
            }

            // 4. Eliminar los detalles de compra primero
            for (DetalleCompra detalle : detalles) {
                if (!detalleCompraRepo.delete(detalle.getId())) {
                    System.err.println("Error al eliminar detalle ID: " + detalle.getId());
                }
            }

            // 5. Finalmente eliminar la compra
            boolean compraEliminada = compraRepo.delete(compraId);

            if (!compraEliminada) {
                throw new RuntimeException("Error al eliminar la compra ID: " + compraId + " (0 filas afectadas).");
            }

            return true;

        } catch (RuntimeException e) {
            System.err.println("ERROR TRANSACCIONAL en eliminarCompra: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error inesperado en eliminarCompra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}