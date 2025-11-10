package com.example.apptienda.flutter;

/**
 * FlutterBridge (deprecated)
 * - El bridge que antes exponía métodos para Flutter fue deshabilitado.
 * - Mantengo una clase placeholder para que referencias al paquete no rompan la compilación.
 * - Si el equipo frontend necesita un bridge, deberán implementar un MethodChannel/REST
 *   sobre esta lógica en una versión separada.
 */
public final class FlutterBridge {

    private FlutterBridge() {}

    public static String unsupported() {
        return "{\"error\":\"Flutter bridge removed - use backend or implement Platform Channels separately\"}";
    }

}
