package com.example.apptienda.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apptienda.MainActivity;
import com.example.apptienda.Autenticacion.AuthManager;
import com.example.apptienda.R;

/**
 * Actividad de autenticación.
 * - Inicia AuthManager para comprobar sesión local.
 * - Si ya hay token válido almacenado, redirige a MainActivity.
 * - Aquí se debe implementar el formulario de login y el flujo de registro.
 */
public class AuthActivity extends AppCompatActivity {

	private AuthManager authManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Si no tienes layout específico aún, usamos el layout principal solo para evitar errores de recursos.
		setContentView(R.layout.activity_main);

		authManager = new AuthManager(getApplicationContext());

		// Ejemplo: si ya está autenticado, vamos a MainActivity
		if (authManager.isLoggedIn()) {
			startMainActivity();
			finish();
		}

		// Aquí se debe: 1) mostrar formulario, 2) validar credenciales, 3) pedir token al backend y guardarlo.
	}

	private void startMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

}
