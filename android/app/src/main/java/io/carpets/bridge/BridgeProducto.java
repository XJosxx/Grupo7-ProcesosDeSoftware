package io.carpets.bridge;

import android.os.Build;

import androidx.annotation.RequiresApi;
import io.carpets.flutterbridge.MethodChannelHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import io.carpets.entidades.Producto;

/**
 * FlutterBridge (deprecated)
 * - El bridge que antes exponía métodos para Flutter fue deshabilitado.
 * - Mantengo una clase placeholder para que referencias al paquete no rompan la compilación.
 * - Si el equipo frontend necesita un bridge, deberán implementar un MethodChannel/REST
 *   sobre esta lógica en una versión separada.
 */
public class BridgeProducto{

    //Listado de claves
    //Sin parámetros
    private final String obtenerProductos =                     "getProduct";
    
    //Un solo parametro
    
    private final String agregarProducto =                      "addProduct";
    private final String actualizarProducto =                   "editProduct";
    private final String eliminarProducto =                     "deleteProduct";
    private final String validarProductoExiste =                "ProductoExists";
    private final String buscarProductoEnVentaPorIdONombre =    "SearchIdNombre";

    private final String obtenerProductoPorId =                 "getProdID";
    //Dos Parametros
    private final String buscarProductos =                      "searchProducts";

    private final String validarStock =                         "ValStock";

    HashMap<String, Function<Object, Object>> VoidFunc= new HashMap<String, Function<Object, Object>>();
    HashMap<String, Function<Object, Object>> Funct= new HashMap<String, Function<Object, Object>>();
    HashMap<String, BiFunction<Object, Object, Object>> Bifunc = new HashMap<String, BiFunction<Object, Object, Object>>();

    MethodChannelHandler MCH;


     public BridgeProducto(){
         MCH = new MethodChannelHandler();
         CargarFunciones();
         
     }

    public Object Dirigir(String Funcion, List<Object> List){
        if(List.isEmpty())        { return Redirigir(Funcion, List); }
        else if( List.size() == 1 ) { return RedirigirFunction(Funcion, List); }
        else                        { return RedirigirBifunction(Funcion, List); }
    }

    private Object Redirigir(String Funcion, List<Object> List) {
        return VoidFunc.get(Funcion);
    }

    private Object RedirigirFunction(String Funcion, List<Object> List){
        return Funct.get(Funcion).apply(List.get(0));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object RedirigirBifunction(String Funcion, List<Object> List) {
        return Bifunc.get(Funcion).apply(List.get(0), List.get(1));
    }



     void CargarFunciones(){
        //Funciones sin parámetros
            VoidFunc.put(obtenerProductos, (Object r) -> MCH.obtenerProductos());

        //Funciones con un parámetro
        Funct.put(agregarProducto, (Object Map) -> {
            Map<String, Object> Mapa = (Map<String, Object>) Map;
            return MCH.agregarProducto(
                new Producto(
                        (int)       Mapa.get("id"),
                        (String)    Mapa.get("nombre"),
                        (Date)      Mapa.get("fechaIngreso"),
                        (double)    Mapa.get("precioCompra"),
                        (double)    Mapa.get("precioVenta"),
                        (int)       Mapa.get("cantidad"),
                        (String)    Mapa.get("categoriaNombre"),
                        (String)    Mapa.get("codigo")
                    ));

        });
        Funct.put(actualizarProducto, (Object Map) -> {
            Map<String, Object> Mapa = (Map<String, Object>) Map;
            return MCH.actualizarProducto(
                new Producto(
                        (int)       Mapa.get("id"),
                        (String)    Mapa.get("nombre"),
                        (Date)      Mapa.get("fechaIngreso"),
                        (double)    Mapa.get("precioCompra"),
                        (double)    Mapa.get("precioVenta"),
                        (int)       Mapa.get("cantidad"),
                        (String)    Mapa.get("categoriaNombre"),
                        (String)    Mapa.get("codigo")
                    ));
        });

        Funct.put(eliminarProducto, (Object idProducto) -> {
            return MCH.eliminarProducto((int)idProducto);
        });
        
        Funct.put(validarProductoExiste, (Object productoId) -> {return MCH.validarProductoExiste((int) productoId);});
        Funct.put(buscarProductoEnVentaPorIdONombre, (Object criterio) -> {return MCH.buscarProductoEnVentaPorIdONombre((String)criterio);});
        Funct.put(obtenerProductoPorId, (Object id) -> {return MCH.obtenerProductoPorId((int) id);});
        //Falta CalcularMontos VentaCompleta y CalcularTotalVenta


        
        //Funciones con dos o más parametros
        Bifunc.put(buscarProductos, (Object criterio, Object tipo) -> { return MCH.buscarProductos((String)criterio, (String)tipo); });
        Bifunc.put(validarStock, (Object productoId, Object cantidad) -> {return MCH.validarStock((int) productoId, (int) cantidad);});
     }

}
