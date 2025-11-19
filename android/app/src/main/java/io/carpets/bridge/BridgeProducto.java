package io.carpets.bridge;

import android.os.Build;

import androidx.annotation.RequiresApi;
import io.carpets.flutterbridge.MethodChannelHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.google.gson.gson;
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
    private final String calcularTotalVenta =                   "calcTotVenta";
    private final String calcularMontosVentaCompleta =          "calcAllMontos";

    //Dos Parametros
    private final String buscarProductos =                      "searchProducts";
    private final String calcularMontos =                       "calcMontos";

    HashMap<String, Function<Object, Object>> VoidFunc= new HashMap<String, Function<Object, Object>>();
    HashMap<String, Function<Object, Object>> Funct= new HashMap<String, Function<Object, Object>>();
    HashMap<String, BiFunction<Object, Object, Object>> Bifunc = new HashMap<String, BiFunction<Object, Object, Object>>();

    MethodChannelHandler MCH;

    Gson Gson = new Gson();

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
            VoidFunc.put(obtenerProductos, MCH.actualizarProducto());

        //Funciones con un parámetro
        Funct.put(agregarProducto, (Map<String, Object> Mapa) -> {
            MCH.agregarProducto(
                new Producto(
                    Mapa.get("id"),
                    Mapa.get("nombre"),
                    Mapa.get("fechaIngreso"),
                    Mapa.get("precioCompra"),
                    Mapa.get("precioVenta"),
                    Mapa.get("cantidad"),
                    Mapa.get("categoriaNombre"),
                    Mapa.get("codigo")
                    ));
        });
        Funct.put(actualizarProducto, (Map<String, Object> Mapa) -> {
            MCH.actualizarProducto(
                new Producto(
                    Mapa.get("id"),
                    Mapa.get("nombre"),
                    Mapa.get("fechaIngreso"),
                    Mapa.get("precioCompra"),
                    Mapa.get("precioVenta"),
                    Mapa.get("cantidad"),
                    Mapa.get("categoriaNombre"),
                    Mapa.get("codigo")
                    ));
        });

        Funct.put(eliminarProducto, (Object idProducto) -> {
            MCH.eliminarProducto(idProducto);
        });
        
        Funct.put(validarProductoExiste, (Object productoId) -> MCH.validarProductoExiste((int) productoId));
        Funct.put(buscarProductoEnVentaPorIdONombre, (Object criterio) -> MCH.buscarProductoEnVentaPorIdONombre(criterio));
        //Falta CalcularMontos VentaCompleta y CalcularTotalVenta
        
        
        //Funciones con dos o más parametros
        Bifunc.put(buscarProductos, (Object criterio, Object tipo) -> MCH.buscarProductos((String)criterio, (String)tipo));
        Bifunc.put(calcularMontos, (Object precioUnitario, Object cantidad) -> MCH.calcularMontos((double) precioUnitario, (int) cantidad));
        
     }

}
