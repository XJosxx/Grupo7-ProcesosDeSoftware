package com.example.apptienda.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * NetworkUtils
 * Pequeñas utilidades relacionadas con la conectividad de red.
 * - Método isOnline(context) devuelve true si hay conectividad activa.
 * Nota: requiere permiso ACCESS_NETWORK_STATE si se usa en runtime en ciertas APIs.
 */
public class NetworkUtils {

	public static boolean isOnline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) return false;
			NetworkInfo ni = cm.getActiveNetworkInfo();
			return ni != null && ni.isConnected();
		} catch (Exception e) {
			return false;
		}
	}

}

