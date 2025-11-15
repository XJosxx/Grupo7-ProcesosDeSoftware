package com.example.myapplication.bridge

import com.example.myapplication.util.json.JSONObject

/**
 * Placeholder bridge para integraciones multiplataforma.
 * Actualmente devuelve un JSON con error indicando que el bridge fue removido.
 */
object FlutterBridge {
    /**
     * Simula una invocación al bridge.
     * @param method nombre del método a invocar
     * @param args mapa de argumentos (opcional)
     * @return String JSON con resultado o error
     */
    @JvmStatic
    fun invoke(method: String, args: Map<String, Any>? = null): String {
        val err = JSONObject()
        err.put("error", "bridge_unavailable")
        err.put("message", "El bridge nativo fue removido. Implementa un nuevo bridge para integrar tu frontend.")
        err.put("requested_method", method)
        if (args != null) {
            val jArgs = JSONObject()
            for ((k, v) in args) {
                jArgs.put(k, v)
            }
            err.put("args", jArgs)
        }
        return err.toString()
    }
}
