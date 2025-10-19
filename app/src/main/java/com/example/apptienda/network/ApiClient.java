package com.example.apptienda.network;

import android.content.Context;

/**
 * Cliente API esqueleto. Aquí puedes configurar Retrofit/OkHttp para llamadas HTTP.
 * Por ahora se deja como placeholder para que puedas completarlo según tu backend.
 */
public class ApiClient {

	private Context context;

	public ApiClient(Context context) {
		this.context = context;
	}

	// Ejemplo de siguientes pasos:
	// - Crear instancia Retrofit singleton con baseUrl del backend.
	// - Añadir interceptor para añadir Authorization header con token desde AuthManager.
	// - Proveer métodos para obtener instancias de servicios (ej. ProductApi.class).

}
