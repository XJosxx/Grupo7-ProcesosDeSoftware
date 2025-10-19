package com.example.apptienda.network;

import com.example.apptienda.modelos.Producto;
import java.util.List;

/**
 * Interfaz/esqueleto para las operaciones de producto.
 * Si usas Retrofit, convierte los métodos en @GET/@POST con los tipos adecuados.
 */
public interface ProductApi {
	// Ejemplo Retrofit:
	// @GET("/productos")
	// Call<List<Producto>> getAll();
	// @GET("/productos/{id}")
	// Call<Producto> getById(@Path("id") int id);
	// @POST("/productos")
	// Call<Producto> create(@Body Producto p);
	// @PUT("/productos/{id}")
	// Call<Producto> update(@Path("id") int id, @Body Producto p);
	// @DELETE("/productos/{id}")
	// Call<Void> delete(@Path("id") int id);

	List<Producto> getAll();
	Producto getById(int id);
	Producto create(Producto p);
	Producto update(int id, Producto p);
	void delete(int id);
}
