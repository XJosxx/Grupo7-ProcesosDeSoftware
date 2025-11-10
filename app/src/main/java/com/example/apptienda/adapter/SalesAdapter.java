package com.example.apptienda.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptienda.R;
import com.example.apptienda.data.DatabaseHelper;
import com.example.apptienda.modelos.Producto;
import com.example.apptienda.modelos.modeloVenta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador que lista las ventas registradas y permite seleccionarlas.
 */
public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {

	/*
	 * SalesAdapter
	 * - Muestra una lista de `modeloVenta` en un RecyclerView.
	 * - Usa `DatabaseHelper` para resolver el nombre del producto cuando la venta referencia
	 *   a un producto por id. Esto hace que el bind sea dependiente de I/O en el hilo principal
	 *   si DatabaseHelper realiza acceso a disco; en grandes listados es recomendable
	 *   prefetch/caching o usar consultas en background para evitar bloqueos.
	 */

	public interface OnVentaSeleccionadaListener {
		void onVentaSeleccionada(modeloVenta venta);
	}

	private final List<modeloVenta> ventas = new ArrayList<>();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
	private final DatabaseHelper dbHelper;
	private final OnVentaSeleccionadaListener listener;

	public SalesAdapter(DatabaseHelper dbHelper, OnVentaSeleccionadaListener listener) {
		this.dbHelper = dbHelper;
		this.listener = listener;
	}

	public void actualizarVentas(List<modeloVenta> nuevasVentas) {
		ventas.clear();
		if (nuevasVentas != null) {
			ventas.addAll(nuevasVentas);
		}
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
		return new SalesViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
		modeloVenta venta = ventas.get(position);
		// Bind de datos; cuidado con operaciones costosas aquí (p. ej. acceder a DB en main thread)
		holder.bind(venta);
		holder.itemView.setOnClickListener(v -> {
			if (listener != null) {
				listener.onVentaSeleccionada(venta);
			}
		});
	}

	@Override
	public int getItemCount() {
		return ventas.size();
	}

	class SalesViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvCodigo;
		private final TextView tvProducto;
		private final TextView tvCantidad;
		private final TextView tvTotal;
		private final TextView tvFecha;

		SalesViewHolder(@NonNull View itemView) {
			super(itemView);
			tvCodigo = itemView.findViewById(R.id.tvCodigoVenta);
			tvProducto = itemView.findViewById(R.id.tvProducto);
			tvCantidad = itemView.findViewById(R.id.tvCantidad);
			tvTotal = itemView.findViewById(R.id.tvTotal);
			tvFecha = itemView.findViewById(R.id.tvFecha);
		}

		void bind(modeloVenta venta) {
			tvCodigo.setText("Codigo: " + venta.getCodigo());
			tvProducto.setText("Producto: " + obtenerNombreProducto(venta));
			tvCantidad.setText(String.format(Locale.getDefault(), "Cantidad: %.2f", venta.getCantidad()));
			tvTotal.setText(String.format(Locale.getDefault(), "Total: S/. %.2f", venta.getTotal()));
			tvFecha.setText("Fecha: " + dateFormat.format(venta.getFecha()));
		}

		private String obtenerNombreProducto(modeloVenta venta) {
			// Si la venta refiere a un producto registrado, se consulta la BD para obtener el nombre.
			// - Si no existe o la venta tiene nombre manual, se usa el nombre manual.
			// - Atención: llamar a `dbHelper.obtenerProducto` en cada bind puede ser costoso; si la
			//   lista es grande, considerar resolver los nombres antes (prefetch) o cachearlos.
			if (venta.tieneProductoRegistrado()) {
				Producto producto = dbHelper.obtenerProducto(venta.getProductoId());
				if (producto != null) {
					return producto.getNombre();
				}
			}
			return venta.getNombreProductoManual() != null ? venta.getNombreProductoManual() : "Producto no registrado";
		}
	}
}
