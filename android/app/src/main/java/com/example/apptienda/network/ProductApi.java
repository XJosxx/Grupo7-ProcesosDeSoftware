package com.example.apptienda.network;

import com.example.apptienda.modelos.Producto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * ProductApi
 * - Interfaz Retrofit (esqueleto) para un backend REST opcional que exponga operaciones
 *   sobre productos. Actualmente es solo un placeholder/documentación para futuras integraciones.
 * - Si decides no usar un backend y exponer la lógica a Flutter via Platform Channels,
 *   esta interfaz puede quedar sin uso.
 */
public interface ProductApi {
    @GET("/api/productos")
    Call<List<Producto>> getAll();
    
    @GET("/api/productos/categoria/{categoria}")
    Call<List<Producto>> getByCategoria(@Path("categoria") String categoria);

    @GET("/api/productos/{id}")
    Call<Producto> getById(@Path("id") int id);

    @POST("/api/productos")
    Call<Producto> create(@Body Producto producto);

    @PUT("/api/productos/{id}")
    Call<Producto> update(@Path("id") int id, @Body Producto producto);

    @DELETE("/api/productos/{id}")
    Call<Void> delete(@Path("id") int id);
    
    @GET("/api/productos/buscar")
    Call<List<Producto>> buscarProductos(@Query("query") String query);
    
    @POST("/api/productos/{id}/stock")
    Call<Producto> actualizarStock(@Path("id") int id, @Query("cantidad") int cantidad);
}
