package com.example.apptienda;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptienda.Autenticacion.AuthManager;

/**
 * MainActivity (headless)
 * - Versión reducida: no usa layouts/XML ni view binding.
 * - Solo inicializa componentes de lógica que puedan necesitar un Context.
 * - Ideal para mantener punto de entrada lógico sin forzar dependencias a recursos.
 */
public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializamos managers de lógica que requieran Context pero sin inflar vistas
        authManager = new AuthManager(getApplicationContext());
    }

}
