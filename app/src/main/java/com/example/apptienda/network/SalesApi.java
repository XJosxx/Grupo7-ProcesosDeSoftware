package com.example.apptienda.network;

import com.example.apptienda.modelos.modeloVenta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Endpoints REST para el modulo de ventas.
 */
public interface SalesApi {

	@GET("/api/ventas")
	Call<List<modeloVenta>> listarVentas();

	@GET("/api/ventas")
	Call<List<modeloVenta>> listarVentasPorFecha(@Query("fecha") String fechaIso8601);

	@GET("/api/ventas/{codigo}")
	Call<modeloVenta> obtenerPorCodigo(@Path("codigo") String codigo);

	@POST("/api/ventas")
	Call<modeloVenta> registrar(@Body modeloVenta venta);

	@PUT("/api/ventas/{codigo}")
	Call<modeloVenta> actualizar(@Path("codigo") String codigo, @Body modeloVenta venta);

	@DELETE("/api/ventas/{codigo}")
	Call<Void> eliminar(@Path("codigo") String codigo);
}
