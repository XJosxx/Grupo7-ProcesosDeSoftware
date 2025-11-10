package com.example.apptienda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptienda.R;
import com.example.apptienda.modelos.Producto;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    // ProductAdapter
    // - Adapter para listar productos en un RecyclerView.
    // - Recibe una lista de `Producto` y un listener para manejar clicks (ver interfaz abajo).
    // - Responsable de inflar `item_producto.xml` y bindear los datos a las vistas.
    private List<Producto> productos;
    private Context context;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Producto producto);
        void onEditClick(Producto producto);
        void onDeleteClick(Producto producto);
    }

    public ProductAdapter(Context context, List<Producto> productos, OnProductClickListener listener) {
        this.context = context;
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);
        // Delegate: el ViewHolder sabe cómo actualizar sus vistas con el modelo Producto
        holder.bind(producto);
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void actualizarProductos(List<Producto> nuevosProductos) {
        this.productos = nuevosProductos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagenProducto;
        private TextView nombreProducto;
        private TextView precioProducto;
        private TextView stockProducto;
        private TextView categoriaProducto;
        private View btnEditar;
        private View btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenProducto = itemView.findViewById(R.id.imagenProducto);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            precioProducto = itemView.findViewById(R.id.precioProducto);
            stockProducto = itemView.findViewById(R.id.stockProducto);
            categoriaProducto = itemView.findViewById(R.id.categoriaProducto);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

    /**
     * Actualiza las vistas del item con los datos del producto.
     * - Carga la imagen con Glide (o placeholder si no existe URL).
     * - Formatea precio y stock.
     * - Delegates clicks al listener proporcionado por la Activity/Fragment.
     */
    public void bind(final Producto producto) {
            nombreProducto.setText(producto.getNombre());
            precioProducto.setText(String.format("S/. %.2f", producto.getPrecioVenta()));
            stockProducto.setText(String.format("Stock: %.2f", producto.getStock()));
            categoriaProducto.setText(producto.getCategoria());

            if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
                Glide.with(context)
                    .load(producto.getImagenUrl())
                    .placeholder(R.drawable.placeholder_producto)
                    .error(R.drawable.error_producto)
                    .into(imagenProducto);
            } else {
                imagenProducto.setImageResource(R.drawable.placeholder_producto);
            }

            // Clicks: delegar al listener provisto por la Activity/Fragment
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(producto);
            });

            btnEditar.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(producto);
            });

            btnEliminar.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(producto);
            });
        }
    }
}
