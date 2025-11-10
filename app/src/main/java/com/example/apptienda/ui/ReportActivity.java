package com.example.apptienda.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptienda.R;

/**
 * ReportActivity
 * - Actividad diseñada para mostrar reportes (ventas, compras, estadísticas).
 * - Actualmente es un placeholder; implementar filtros y carga de datos desde `DatabaseHelper`
 *   o desde un repositorio cuando se defina el formato de reportes.
 */
public class ReportActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Si existe layout activity_report.xml, se puede descomentar la siguiente línea
		// setContentView(R.layout.activity_report);
	}

}
