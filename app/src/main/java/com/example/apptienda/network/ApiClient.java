package com.example.apptienda.network;

import android.content.Context;

/**
 * Cliente API esqueleto. Aqui puedes configurar Retrofit/OkHttp para llamadas HTTP.
 * Por ahora se deja como placeholder para que puedas completarlo segun tu backend.
 */
public class ApiClient {

	private Context context;

	public ApiClient(Context context) {
		this.context = context;
	}

	// Ejemplo de siguientes pasos (implementación sugerida):
	// - Crear una instancia singleton de Retrofit con la baseUrl del backend.
	// - Configurar OkHttpClient con interceptores para añadir cabeceras (p. ej. Authorization)
	//   usando el token almacenado por `AuthManager` o la abstracción `AuthRepository`.
	// - Proveer métodos para obtener instancias de servicios (ej. ProductApi, SalesApi).
	// - Este cliente es opcional si decides exponer la lógica nativa vía Platform Channels
	//   y mantener la persistencia local (SQLite) como fuente principal.

	// Nota: dejar esta clase como es cumple con el requisito de NO introducir Flutter en el
	// código; sirve como placeholder/documentación para cuando se integre un backend REST.

}
