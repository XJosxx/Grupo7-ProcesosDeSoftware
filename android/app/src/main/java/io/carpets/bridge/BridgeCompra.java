package io.carpets.bridge;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.carpets.flutterbridge.MethodChannelHandler;

/**
 * FlutterBridge (deprecated)
 * - El bridge que antes exponía métodos para Flutter fue deshabilitado.
 * - Mantengo una clase placeholder para que referencias al paquete no rompan la compilación.
 * - Si el equipo frontend necesita un bridge, deberán implementar un MethodChannel/REST
 *   sobre esta lógica en una versión separada.
 */
public class BridgeCompra{


    //Sin argumentos
    private final String listarCompras = "listCompras";
    //Con un argumento o más
    private final String eliminarDetalleCompra = "DeleteDetComp";
    private final String agregarProductoNuevoACompra = "AddProdToComp";
    private final String eliminarCompra = "DelCompra";
    private final String editarDetalleCompra = "EditDetComp"; //Tiene 3 args
    private final String validarDatosProductoNuevo = "ValDatProNov"; //Tiene 3 argus

    //Con dos argumentos
    private final String registrarCompra = "RegCompra";
    private final String agregarProductoExistenteACompra = "addProdExToComp";

    MethodChannelHandler MCH;

    public BridgeCompra(){
        MCH = new MethodChannelHandler();
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
        else if( List.size() == 2)  { RedirigirBifunction(Funcion, List); }
        else  { RedirigirFunction(Funcion, List); }
    }

    private Object Redirigir(String Funcion, List<Object> List) {
        return VoidFunc.get(Funcion);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object RedirigirFunction(String Funcion, List<Object> List){
        if(List.size() == 1) return Funct.get(Funcion).apply(List.get(0));
        else return Funct.get(Funcion).apply(List);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object RedirigirBifunction(String Funcion, List<Object> List) {
        if(List.size() == 3) {return Bifunc.get(Funcion).apply(List.get(0), List.get(1));}
        else {return Bifunc.get(Funcion).apply(List.get(0), List.get(1));}


    }



    void CargarFunciones(){
        //Sin Parametros
        VoidFunc.put(listarCompras, (Object l)->{return MCH.listarCompras();});


        // Con un solo param
        Funct.put(agregarProductoNuevoACompra, (Object detalle)->{
            //Convertir detalle a DetalleCompra
            return MCH.agregarProductoNuevoACompra(null);
        });
        Funct.put(eliminarCompra, (Object compraId) -> {return MCH.eliminarCompra((int) compraId);});
        Funct.put(eliminarDetalleCompra, (Object detalleId) -> {return MCH.eliminarDetalleCompra((int) detalleId);});

        //Mas de un parametro

        Funct.put(editarDetalleCompra, (Object Lista) ->{
            List<Object> List = (List<Object>) Lista;
            int detalleId = (int) List.get(0);
            int cantidad = (int) List.get(1);
            double precio = (double) List.get(2);
           return MCH.editarDetalleCompra(detalleId, cantidad, precio);
        });

        Funct.put(validarDatosProductoNuevo, (Object Lista) ->{
            List<Object> List = (List<Object>) Lista;
            String codigo = (String) List.get(0);
            int cantidad = (int) List.get(1);
            double precioCompra = (double) List.get(2);
            return MCH.validarDatosProductoNuevo(codigo, cantidad, precioCompra);
        });

        //Con dos Parametros
        Bifunc.put(registrarCompra,(Object Compra, Object detalles)->{
            //Convertir Compra al objeto Compra
            //convertir detalles a List<DetallesCompra>
            return MCH.registrarCompra(null, null);});
        Bifunc.put(agregarProductoExistenteACompra, (Object productoId, Object cantidad)->{
            return MCH.agregarProductoExistenteACompra((int) productoId, (int) cantidad);
        }

        );
    }
     
}
