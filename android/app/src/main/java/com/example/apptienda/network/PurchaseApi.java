package com.example.apptienda.network;

import com.example.apptienda.modelos.modeloCompra;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;

/**
 * Endpoints REST para el modulo de compras.
 */
public interface PurchaseApi {

	@GET("/api/compras")
	Call<List<modeloCompra>> listarCompras();

	@GET("/api/compras/{id}")
	Call<modeloCompra> obtenerCompra(@Path("id") long id);

	@POST("/api/compras")
	Call<modeloCompra> registrar(@Body modeloCompra compra);

	@PUT("/api/compras/{id}")
	Call<modeloCompra> actualizar(@Path("id") long id, @Body modeloCompra compra);

	@DELETE("/api/compras/{id}")
	Call<Void> eliminar(@Path("id") long id);
}
