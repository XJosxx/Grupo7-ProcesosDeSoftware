package com.example.apptienda;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptienda.Autenticacion.AuthManager;

/**
 * LauncherActivity
 * - Actividad mínima que actúa como punto de entrada (LAUNCHER) pero no infla vistas.
 * - Está pensada para permitir que el equipo de frontend (Flutter u otro) implemente la UI
 *   por separado sin depender de los layouts XML actuales.
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializamos el manejador de sesión por si alguien necesita consultar estado.
        AuthManager authManager = new AuthManager(getApplicationContext());

        // No iniciamos Activities con layouts. Simplemente cerramos esta actividad.
        // El equipo frontend podrá decidir cómo lanzar su UI y cuándo invocar la lógica nativa.
        finish();
    }

}
