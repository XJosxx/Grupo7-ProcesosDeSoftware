package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

/**
 * LauncherActivity minimalista.
 * Punto de entrada LAUNCHER para la app headless. No contiene UI; inicia MainActivity y finaliza.
 */
class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("AppTienda", "LauncherActivity starting headless MainActivity")

        // Iniciar MainActivity headless
        val intent = Intent(this, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        // Pasar extras si es necesario en el futuro
        startActivity(intent)

        // No mostrar UI: terminar el launcher para dejar MainActivity en primer plano (si lo desea)
        finish()
    }
}
