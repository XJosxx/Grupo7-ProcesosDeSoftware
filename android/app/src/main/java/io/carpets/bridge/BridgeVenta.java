package io.carpets.bridge;

import androidx.annotation.NonNull;

import io.carpets.flutterbridge.MethodChannelHandler;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * FlutterBridge (deprecated)
 * - El bridge que antes exponía métodos para Flutter fue deshabilitado.
 * - Mantengo una clase placeholder para que referencias al paquete no rompan la compilación.
 * - Si el equipo frontend necesita un bridge, deberán implementar un MethodChannel/REST
 *   sobre esta lógica en una versión separada.
 */
public class BridgeVenta{

    //Sin parametros
    private final String listarVentas =                         "listVentas";

    //Un Parametro
    private final String obtenerVentasPorDia =                  "getVentaPorDay";
    private final String eliminarVenta =                        "deleteVenta";
    private final String calcularMontosVentaCompleta =          "calcMontVentCom";
    private final String calcularTotalVenta =                   "calcTotVent";
    //Dos parametros
    private final String registrarVenta =                       "regVenta";
    private final String generarBoleta =                        "genBoletaVenta";
    private final String calcularMontos =                       "calcMontos";

    private MethodChannelHandler MCH;
     public BridgeVenta(){
         MCH = new MethodChannelHandler();
         CargarFunciones();
     }

    HashMap<String, Function<Object, Object>> VoidFunc= new HashMap<String, Function<Object, Object>>();
    HashMap<String, Function<Object, Object>> Funct= new HashMap<String, Function<Object, Object>>();
    HashMap<String, BiFunction<Object, Object, Object>> Bifunc = new HashMap<String, BiFunction<Object, Object, Object>>();


    public void Dirigir(String Funcion, List<Object> List){
        if(List.isEmpty())        { Redirigir(Funcion, List); }
        else if( List.size() == 1 ) { RedirigirFunction(Funcion, List); }
        else                        { RedirigirBifunction(Funcion, List); }
    }

    private Object Redirigir(String Funcion, List<Object> List) {
        return VoidFunc.get(Funcion);
    }

    private Object RedirigirFunction(String Funcion, List<Object> List){
        return Funct.get(Funcion).apply(List.get(0));
    }

    private Object RedirigirBifunction(String Funcion, List<Object> List) {
        return Bifunc.get(Funcion).apply(List.get(0), List.get(1));
    }



    void CargarFunciones(){
        //Funciones sin parámetros
        VoidFunc.put(listarVentas, (Object l)->{return MCH.listarVentas();});

        //Funciones con un parámetro
        Funct.put(obtenerVentasPorDia, (Object fecha)->{return MCH.obtenerVentasPorDia((String) fecha);});
        Funct.put(eliminarVenta, (Object ventaId) ->{return MCH.eliminarVenta((int) ventaId);});
        Funct.put(calcularMontosVentaCompleta, (Object detalles)->{
            //convertir detalles a List<DetalleVenta>
            return MCH.calcularMontosVentaCompleta(null);
        });
        Funct.put(calcularTotalVenta, (Object detalles)->{
            //convertir detalles a List<DetalleVenta>
            return MCH.calcularTotalVenta(null);
        });

        //Funciones con dos o más parametros
        Bifunc.put(calcularMontos, (Object precioUnitario, Object cantidad) -> { return MCH.calcularMontos((double) precioUnitario, (int) cantidad); });

        Bifunc.put(registrarVenta, (Object Map, Object detalles) -> {
            //Hacer objeto Venta
            //Hacer objeto Lista detalleVenta
            return MCH.registrarVenta(null, null);
        });
        Bifunc.put(generarBoleta,(Object ventaId, Object detalles)-> {
            //Hacer convertir detalles a una Lista<DetalleVenta>
            return MCH.generarBoleta((int) ventaId, null);
        });
    }
     
}
