package com.example.apptienda.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptienda.R;
import com.example.apptienda.data.DatabaseHelper;
import com.example.apptienda.modelos.Producto;
import com.example.apptienda.modelos.modeloVenta;
import com.example.apptienda.servicios.ServicioProducto;
import com.example.apptienda.servicios.ServicioVenta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * SalesActivity
 * - Actividad que permite registrar ventas, listarlas y generar boletas.
 * - En esta versión la Activity usa `ServicioVenta` y `ServicioProducto` para la lógica de negocio,
 *   y `DatabaseHelper` como dependencia de compatibilidad.
 * - Consideraciones de diseño:
 *   - El spinner de productos se popula desde `ServicioProducto`.
 *   - Las operaciones de registro/eliminación delegan en `ServicioVenta` que aplica validaciones
 *     (stock, precios) y las persiste vía el repositorio.
 *   - Mantén operaciones de I/O fuera del hilo UI si migras a llamadas remotas.
 */
public class SalesActivity extends AppCompatActivity implements com.example.apptienda.adapter.SalesAdapter_Proto.OnVentaSeleccionadaListener {

	private DatabaseHelper dbHelper;
	private ServicioProducto servicioProducto;
	private ServicioVenta servicioVenta;

	private TextView tvVentaId;
	private TextView tvSubtotal;
	private TextView tvIgv;
	private TextView tvTotal;
	private TextView tvPrecioSugerido;
	private EditText etDni;
	private Spinner spinnerProductos;
	private CheckBox cbProductoManual;
	private EditText etProductoManual;
	private EditText etPrecioCajera;
	private EditText etCantidad;
	private EditText etCostoUnitario;
	private Button btnRegistrarVenta;
	private Button btnCancelar;
	private Button btnEliminar;
	private Button btnBoleta;
	private Button btnAvisarProducto;
	private RecyclerView rvVentas;

	private com.example.apptienda.adapter.SalesAdapter_Proto salesAdapter;
	private final List<Producto> productos = new ArrayList<>();
	private Producto productoSeleccionado;
	private modeloVenta ventaSeleccionada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);

		dbHelper = new DatabaseHelper(this);
		servicioProducto = new ServicioProducto(dbHelper);
		servicioVenta = new ServicioVenta(dbHelper, servicioProducto);

		inicializarVistas();
		configurarRecycler();
		configurarListeners();
		cargarProductos();
		cargarVentas();
		generarCodigoVenta();
		calcularTotales();
	}

	private void inicializarVistas() {
		tvVentaId = findViewById(R.id.tvVentaId);
		tvSubtotal = findViewById(R.id.tvSubtotal);
		tvIgv = findViewById(R.id.tvIgv);
		tvTotal = findViewById(R.id.tvTotal);
		tvPrecioSugerido = findViewById(R.id.tvPrecioSugerido);
		etDni = findViewById(R.id.etDni);
		spinnerProductos = findViewById(R.id.spinnerProductos);
		cbProductoManual = findViewById(R.id.cbProductoManual);
		etProductoManual = findViewById(R.id.etProductoManual);
		etPrecioCajera = findViewById(R.id.etPrecioCajera);
		etCantidad = findViewById(R.id.etCantidad);
		etCostoUnitario = findViewById(R.id.etCostoUnitario);
		btnRegistrarVenta = findViewById(R.id.btnRegistrarVenta);
		btnCancelar = findViewById(R.id.btnCancelar);
		btnEliminar = findViewById(R.id.btnEliminarVenta);
		btnBoleta = findViewById(R.id.btnGenerarBoleta);
		btnAvisarProducto = findViewById(R.id.btnAvisarProducto);
		rvVentas = findViewById(R.id.rvVentas);
	}

	private void configurarRecycler() {
		salesAdapter = new com.example.apptienda.adapter.SalesAdapter_Proto(dbHelper, this);
		rvVentas.setLayoutManager(new LinearLayoutManager(this));
		rvVentas.setAdapter(salesAdapter);
		rvVentas.setNestedScrollingEnabled(false);
	}

	private void configurarListeners() {
		spinnerProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position > 0) {
					productoSeleccionado = productos.get(position - 1);
					tvPrecioSugerido.setText(String.format(Locale.getDefault(),
							"Precio sugerido: S/. %.2f", productoSeleccionado.getPrecioVenta()));
					if (TextUtils.isEmpty(etPrecioCajera.getText())) {
						etPrecioCajera.setText(String.format(Locale.getDefault(), "%.2f", productoSeleccionado.getPrecioVenta()));
					}
					cbProductoManual.setChecked(false);
					etCostoUnitario.setVisibility(View.GONE);
					etProductoManual.setVisibility(View.GONE);
				} else {
					productoSeleccionado = null;
					tvPrecioSugerido.setText("Precio sugerido: S/. 0.00");
				}
				calcularTotales();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});

		cbProductoManual.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				etProductoManual.setVisibility(View.VISIBLE);
				etCostoUnitario.setVisibility(View.VISIBLE);
				spinnerProductos.setSelection(0);
				productoSeleccionado = null;
			} else {
				etProductoManual.setText("");
				etCostoUnitario.setText("");
				etProductoManual.setVisibility(View.GONE);
				etCostoUnitario.setVisibility(View.GONE);
			}
		});

		TextWatcher recalculoWatcher = new SimpleTextWatcher(this::calcularTotales);
		etPrecioCajera.addTextChangedListener(recalculoWatcher);
		etCantidad.addTextChangedListener(recalculoWatcher);

		btnRegistrarVenta.setOnClickListener(v -> registrarVenta());
		btnCancelar.setOnClickListener(v -> limpiarFormulario());
		btnEliminar.setOnClickListener(v -> eliminarVentaSeleccionada());
		btnBoleta.setOnClickListener(v -> mostrarBoleta());
		btnAvisarProducto.setOnClickListener(v -> Toast.makeText(this, "Aviso enviado para registrar producto.", Toast.LENGTH_SHORT).show());
	}

	private void cargarProductos() {
		productos.clear();
		productos.addAll(servicioProducto.listarProductos());

		List<String> nombres = new ArrayList<>();
		nombres.add("Selecciona un producto");
		for (Producto producto : productos) {
			nombres.add(producto.getNombre());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerProductos.setAdapter(adapter);
	}

	private void cargarVentas() {
		salesAdapter.actualizarVentas(servicioVenta.listarVentas());
	}

	private void generarCodigoVenta() {
		tvVentaId.setText(servicioVenta.generarCodigoVenta());
	}

	private void registrarVenta() {
		try {
			modeloVenta venta = construirVentaDesdeFormulario();
			servicioVenta.registrarVenta(venta);
			Toast.makeText(this, "Venta registrada", Toast.LENGTH_SHORT).show();
			cargarVentas();
			limpiarFormulario();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private modeloVenta construirVentaDesdeFormulario() {
		modeloVenta venta = new modeloVenta();
		venta.setCodigo(tvVentaId.getText().toString());
		venta.setDniComprador(etDni.getText().toString().trim());
		venta.setPrecioUnitario(obtenerNumero(etPrecioCajera));
		venta.setCantidad(obtenerNumero(etCantidad));
		venta.setFecha(new Date());

		if (venta.getPrecioUnitario() <= 0) {
			throw new IllegalArgumentException("Ingresa un precio valido");
		}
		if (venta.getCantidad() <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
		}

		if (!cbProductoManual.isChecked() && productoSeleccionado != null) {
			venta.setProductoId(productoSeleccionado.getId());
			venta.setNombreProductoManual(productoSeleccionado.getNombre());
			venta.setCostoUnitario(productoSeleccionado.getPrecioCompra());
		} else {
			String nombreManual = etProductoManual.getText().toString().trim();
			if (TextUtils.isEmpty(nombreManual)) {
				throw new IllegalArgumentException("Ingresa el nombre del producto manual");
			}
			venta.setNombreProductoManual(nombreManual);
			venta.setCostoUnitario(obtenerNumero(etCostoUnitario));
		}

		venta.setIgvPorcentaje(modeloVenta.DEFAULT_IGV);
		venta.recalcularTotales();
		return venta;
	}

	private double obtenerNumero(EditText editText) {
		String texto = editText.getText().toString().trim();
		if (TextUtils.isEmpty(texto)) {
			return 0;
		}
		try {
			return Double.parseDouble(texto);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Dato numerico invalido");
		}
	}

	private void limpiarFormulario() {
		etDni.setText("");
		spinnerProductos.setSelection(0);
		cbProductoManual.setChecked(false);
		etProductoManual.setText("");
		etProductoManual.setVisibility(View.GONE);
		etPrecioCajera.setText("");
		etCantidad.setText("1");
		etCostoUnitario.setText("");
		etCostoUnitario.setVisibility(View.GONE);
		generarCodigoVenta();
		ventaSeleccionada = null;
		actualizarBotonesSeleccion();
		calcularTotales();
	}

	private void calcularTotales() {
		double precio = obtenerNumero(etPrecioCajera);
		double cantidad = obtenerNumero(etCantidad);
		double subtotal = precio * cantidad;
		double igv = subtotal * modeloVenta.DEFAULT_IGV;
		double total = subtotal + igv;

		tvSubtotal.setText(String.format(Locale.getDefault(), "Subtotal: S/. %.2f", subtotal));
		tvIgv.setText(String.format(Locale.getDefault(), "IGV: S/. %.2f", igv));
		tvTotal.setText(String.format(Locale.getDefault(), "Total: S/. %.2f", total));
	}

	private void eliminarVentaSeleccionada() {
		if (ventaSeleccionada == null) {
			return;
		}

		new AlertDialog.Builder(this)
				.setTitle("Eliminar venta")
				.setMessage("?Seguro de eliminar la venta " + ventaSeleccionada.getCodigo() + "?")
				.setPositiveButton("Eliminar", (dialog, which) -> {
					if (servicioVenta.eliminarVenta(ventaSeleccionada.getId())) {
						Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show();
						ventaSeleccionada = null;
						actualizarBotonesSeleccion();
						cargarVentas();
						limpiarFormulario();
					} else {
						Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("Cancelar", null)
				.show();
	}

	private void mostrarBoleta() {
		if (ventaSeleccionada == null) {
			Toast.makeText(this, "Selecciona una venta", Toast.LENGTH_SHORT).show();
			return;
		}
		String boleta = servicioVenta.generarBoleta(ventaSeleccionada);
		new AlertDialog.Builder(this)
				.setTitle("Boleta")
				.setMessage(boleta)
				.setPositiveButton("Cerrar", null)
				.show();
	}

	private void actualizarBotonesSeleccion() {
		boolean haySeleccion = ventaSeleccionada != null;
		btnEliminar.setEnabled(haySeleccion);
		btnBoleta.setEnabled(haySeleccion);
	}

	@Override
	public void onVentaSeleccionada(modeloVenta venta) {
		this.ventaSeleccionada = venta;
		actualizarBotonesSeleccion();
		etDni.setText(venta.getDniComprador());
		etPrecioCajera.setText(String.format(Locale.getDefault(), "%.2f", venta.getPrecioUnitario()));
		etCantidad.setText(String.format(Locale.getDefault(), "%.2f", venta.getCantidad()));
		tvVentaId.setText(venta.getCodigo());

		if (venta.tieneProductoRegistrado()) {
			cbProductoManual.setChecked(false);
			for (int i = 0; i < productos.size(); i++) {
				if (productos.get(i).getId() == venta.getProductoId()) {
					spinnerProductos.setSelection(i + 1);
					break;
				}
			}
		} else {
			cbProductoManual.setChecked(true);
			etProductoManual.setText(venta.getNombreProductoManual());
			etCostoUnitario.setText(String.format(Locale.getDefault(), "%.2f", venta.getCostoUnitario()));
		}
		calcularTotales();
	}

	// Listeners helper classes
	private static class SimpleTextWatcher implements TextWatcher {
		private final Runnable callback;

		SimpleTextWatcher(Runnable callback) {
			this.callback = callback;
		}

		@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override
		public void afterTextChanged(Editable s) {
			if (callback != null) {
				callback.run();
			}
		}
	}

}
