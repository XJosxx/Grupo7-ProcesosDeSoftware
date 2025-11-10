package com.example.apptienda.utils;

import android.util.Base64;

/**
 * Utilidades minimas para manejo de JWT (decodificar payload).
 * NOTA: Esto es solo para lectura basica; la verificacion de firmas debe hacerse en el servidor.
 */
public class JwtUtils {

	public static String decodePayload(String jwt) {
		if (jwt == null) return null;
		String[] parts = jwt.split("\\.");
		if (parts.length < 2) return null;
		try {
			byte[] decoded = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
			return new String(decoded);
		} catch (IllegalArgumentException e) {
			// Si la cadena no esta bien codificada, devolvemos null
			return null;
		}
	}

}
