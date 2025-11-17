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
public class BridgeCompra{
    public BridgeCompra(){

        CargarFunciones();

    }

    //Function es una funcion hecha variable, el primer Object es el parametro que necesita y el segundo es el retorno de la función.
    //Para Funciones sin parámetros con o sin retorno.
    HashMap<String, Function<Object, Object>> VoidFunc= new HashMap<String, Function<Object, Object>>();
    //Para Funciones con un parametro con o sin retorno.
    HashMap<String, Function<Object, Object>> Funct= new HashMap<String, Function<Object, Object>>();
    //Para Funciones con dos parametros con o sin retorno.
    HashMap<String, BiFunction<Object, Object, Object>> Bifunc = new HashMap<String, BiFunction<Object, Object, Object>>();

    //NECESIDAD: Cuando se llame a una función y esta no retorne nada, recordar poner "return null;"

    //Escoge hacia qué tipo de funcion debe dirigirse el pedido.
    public void Dirigir(String Funcion, List<Object> List){
        if(List.isEmpty())        { Redirigir(Funcion, List); }
        else if( List.size() == 1 ) { RedirigirFunction(Funcion, List); }
        else                        { RedirigirBifunction(Funcion, List); }
    }

    private Object Redirigir(String Funcion, List<Object> List) {
        return VoidFunc.get(Funcion);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object RedirigirFunction(String Funcion, List<Object> List){
        return Funct.get(Funcion).apply(List.get(0));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object RedirigirBifunction(String Funcion, List<Object> List) {
        return Bifunc.get(Funcion).apply(List.get(0), List.get(1));
    }



    void CargarFunciones(){

    }
     
}
