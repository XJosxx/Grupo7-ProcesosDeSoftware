package io.carpets;

import android.os.Bundle; // ✅ Import necesario
import android.os.StrictMode; // ✅ Import necesario
import androidx.annotation.NonNull;

import java.util.List;

import io.carpets.bridge.BridgeCompra;
import io.carpets.bridge.BridgeMain;
import io.carpets.bridge.BridgeProducto;
import io.carpets.bridge.BridgeVenta;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

/**
 * LauncherActivity Corregido
 * - Incluye StrictMode para permitir BD en hilo principal.
 * - Incluye result.success() en TODOS los canales para que Flutter reciba los datos.
 */
public class LauncherActivity extends FlutterActivity {

    // Nombres de los canales
    private static final String PRODUCT = "samples.flutter.dev/Productos";
    private static final String VENTA = "samples.flutter.dev/Venta";
    private static final String LOGIN = "samples.flutter.dev/Login";
    private static final String COMPRA = "samples.flutter.dev/Compra";

    // 🔴 1. CORRECCIÓN CRÍTICA: Permitir conexión a BD (AWS) en el hilo principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        // Canal Productos
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), PRODUCT)
                .setMethodCallHandler(
                        (call, result) -> {
                            BridgeProducto BP = new BridgeProducto();
                            // ✅ Esto estaba bien
                            result.success(BP.Dirigir(call.method, (List<Object>) call.arguments));
                        }
                );

        // Canal Ventas
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), VENTA)
                .setMethodCallHandler(
                        (call, result) -> {
                            BridgeVenta BV = new BridgeVenta();
                            // 🔴 2. CORRECCIÓN: Faltaba result.success(). Sin esto, Flutter se cuelga.
                            result.success(BV.Dirigir(call.method, (List<Object>) call.arguments));
                        }
                );

        // Canal Login
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), LOGIN)
                .setMethodCallHandler(
                        (call, result) -> {
                            BridgeMain BM = new BridgeMain();
                            // ✅ Esto estaba bien
                            result.success(BM.Dirigir(call.method, (List<Object>) call.arguments));
                        }
                );

        // Canal Compras
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), COMPRA)
                .setMethodCallHandler(
                        (call, result) -> {
                            BridgeCompra BC = new BridgeCompra();
                            // 🔴 3. CORRECCIÓN: Faltaba result.success()
                            result.success(BC.Dirigir(call.method, (List<Object>) call.arguments));
                        }
                );
    }
}