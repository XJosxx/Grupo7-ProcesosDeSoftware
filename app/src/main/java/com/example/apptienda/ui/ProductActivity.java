package com.example.apptienda.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apptienda.R;
import com.example.apptienda.modelos.Producto;
import com.example.apptienda.data.ProductoDatabase; // Asumimos que existe esta clase para la BD

public class ProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_LOGO_REQUEST = 2;

    private TextView tvProductoID;
    private EditText etNombre;
    private EditText etPrecioCompra;
    private EditText etStock;
    private ImageView ivProductoImagen;
    private Switch switchActivado;
    private Button btnGuardar;
    private Button btnEliminar;
    private Button btnSeleccionarImagen;
    private Button btnSeleccionarLogo;

    private Uri imagenSeleccionada;
    private Producto productoActual;
    private boolean modoEdicion = false;
    private ProductoDatabase db; // Base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        // Inicializar base de datos
        db = new ProductoDatabase(this);

        // Inicializar vistas
        inicializarVistas();

        // Configurar listeners
        configurarListeners();

        // Verificar si estamos en modo edición
        if (getIntent().hasExtra("producto_id")) {
            modoEdicion = true;
            int productoId = getIntent().getIntExtra("producto_id", -1);
            cargarProducto(productoId);
            setTitle("Editar Producto");
        } else {
            setTitle("Nuevo Producto");
            btnEliminar.setVisibility(View.GONE);
        }
    }

    private void inicializarVistas() {
        tvProductoID = findViewById(R.id.tvProductoID);
        etNombre = findViewById(R.id.etNombre);
        etPrecioCompra = findViewById(R.id.etPrecioCompra);
        etStock = findViewById(R.id.etStock);
        ivProductoImagen = findViewById(R.id.ivProductoImagen);
        switchActivado = findViewById(R.id.switchActivado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarLogo = findViewById(R.id.btnSeleccionarLogo);
    }

    private void configurarListeners() {
        btnGuardar.setOnClickListener(v -> guardarProducto());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
        btnSeleccionarImagen.setOnClickListener(v -> abrirSelectorImagen());
        btnSeleccionarLogo.setOnClickListener(v -> mostrarSelectorLogos());
    }

    private void cargarProducto(int productoId) {
        // Cargar producto desde la base de datos
        productoActual = db.obtenerProducto(productoId);
        if (productoActual != null) {
            tvProductoID.setText(String.valueOf(productoActual.getID()));
            etNombre.setText(productoActual.getNombre());
            etPrecioCompra.setText(String.valueOf(productoActual.getPrecioCompra()));
            etStock.setText(String.valueOf(productoActual.getStock()));
            switchActivado.setChecked(true); // Asumimos que está activado por defecto
            
            // Cargar imagen si existe
            String imagenPath = productoActual.getImagenPath(); // Asumimos que existe este getter
            if (imagenPath != null) {
                imagenSeleccionada = Uri.parse(imagenPath);
                ivProductoImagen.setImageURI(imagenSeleccionada);
            }
        }
    }

    private void guardarProducto() {
        if (!validarCampos()) {
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        double precioCompra = Double.parseDouble(etPrecioCompra.getText().toString());
        int stock = Integer.parseInt(etStock.getText().toString());
        boolean activado = switchActivado.isChecked();

        if (modoEdicion) {
            // Actualizar producto existente
            productoActual.setNombre(nombre);
            productoActual.setPrecioCompra((int)precioCompra);
            productoActual.setStock(stock);
            // Actualizar imagen y estado activado
            if (imagenSeleccionada != null) {
                // Guardar path de la imagen
                productoActual.setImagenPath(imagenSeleccionada.toString());
            }
            
            db.actualizarProducto(productoActual);
            Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
        } else {
            // Crear nuevo producto
            Producto nuevoProducto = new Producto(
                0, // ID será asignado por la BD
                nombre,
                (int)precioCompra,
                "General" // Categoría por defecto
            );
            nuevoProducto.setStock(stock);
            if (imagenSeleccionada != null) {
                nuevoProducto.setImagenPath(imagenSeleccionada.toString());
            }
            
            db.insertarProducto(nuevoProducto);
            Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private boolean validarCampos() {
        if (TextUtils.isEmpty(etNombre.getText())) {
            etNombre.setError("El nombre es obligatorio");
            return false;
        }

        try {
            double precio = Double.parseDouble(etPrecioCompra.getText().toString());
            if (precio < 0) {
                etPrecioCompra.setError("El precio no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            etPrecioCompra.setError("Ingrese un precio válido");
            return false;
        }

        try {
            int stock = Integer.parseInt(etStock.getText().toString());
            if (stock < 0) {
                etStock.setError("El stock no puede ser negativo");
                return false;
            }
        } catch (NumberFormatException e) {
            etStock.setError("Ingrese un stock válido");
            return false;
        }

        return true;
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
            .setTitle("Eliminar Producto")
            .setMessage("¿Está seguro que desea eliminar este producto?")
            .setPositiveButton("Sí", (dialog, which) -> eliminarProducto())
            .setNegativeButton("No", null)
            .show();
    }

    private void eliminarProducto() {
        if (productoActual != null) {
            db.eliminarProducto(productoActual.getID());
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void mostrarSelectorLogos() {
        // Aquí implementarías un diálogo o activity para mostrar los logos predefinidos
        // Por ahora solo mostraremos un mensaje
        Toast.makeText(this, "Selector de logos pendiente de implementar", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imagenSeleccionada = data.getData();
                ivProductoImagen.setImageURI(imagenSeleccionada);
            } else if (requestCode == SELECT_LOGO_REQUEST) {
                // Manejar la selección de logo predefinido
                // TODO: Implementar selección de logo predefinido
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Aquí podrías agregar opciones al menú si las necesitas
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
