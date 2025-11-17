package io.carpets.bridge;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * FlutterBridge (deprecated)
 * - El bridge que antes exponía métodos para Flutter fue deshabilitado.
 * - Mantengo una clase placeholder para que referencias al paquete no rompan la compilación.
 * - Si el equipo frontend necesita un bridge, deberán implementar un MethodChannel/REST
 *   sobre esta lógica en una versión separada.
 */
public class BridgeProducto{

    HashMap<String, Function<Object, Object>> VoidFunc= new HashMap<String, Function<Object, Object>>();
    HashMap<String, Function<Object, Object>> Funct= new HashMap<String, Function<Object, Object>>();
    HashMap<String, BiFunction<Object, Object, Object>> Bifunc = new HashMap<String, BiFunction<Object, Object, Object>>();

     public BridgeProducto(){
         
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
         Bifunc.put("No way",(Object a,Object b) ->
         {int v1 = (int) a;int v2 = (int) b;
             return v1+v2;});
     }

}
