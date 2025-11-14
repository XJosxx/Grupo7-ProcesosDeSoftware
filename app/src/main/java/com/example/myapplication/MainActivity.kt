package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

/**
 * Headless MainActivity
 * Esta versión no usa bindings ni layouts; está pensada para contener la lógica core
 * y exponer puntos de integración para un frontend externo.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No setContentView: actividad headless.
        Log.i("AppTienda", "MainActivity (headless) started")

        // Ejemplo: arrancar lógica de negocio en background o inicializar singletons.
        // Aquí sólo dejamos un log y nos quedamos listos para recibir intents.
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("AppTienda", "MainActivity received intent: ${intent?.action}")
    }
}