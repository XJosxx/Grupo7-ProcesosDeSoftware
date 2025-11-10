package com.example.apptienda.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoriasManager {
    /**
     * CategoriasManager
     * - Lista centralizada de categorías utilizadas en la app.
     * - Provee helpers para obtener la lista completa, la lista para filtros y validar categorías.
     * Conexión: usada por `ProductActivity` y otros UI para poblar spinners/filtrar productos.
     */
    public static final String CATEGORIA_TODOS = "Todos";
    public static final String CATEGORIA_ROPA = "Ropa";
    public static final String CATEGORIA_CALZADO = "Calzado";
    public static final String CATEGORIA_LIBRERIA = "Libreria";
    public static final String CATEGORIA_MAQUILLAJE = "Maquillaje";
    public static final String CATEGORIA_UTENSILIOS_COCINA = "Utensilios de Cocina";
    public static final String CATEGORIA_PRODUCTOS_LIMPIEZA = "Productos de Limpieza";
    public static final String CATEGORIA_HOGAR = "Hogar";
    public static final String CATEGORIA_JUGUETES = "Juguetes";
    public static final String CATEGORIA_TECNOLOGIA = "Tecnologia";
    public static final String CATEGORIA_ACCESORIOS = "Accesorios";

    public static List<String> obtenerCategorias() {
        return new ArrayList<>(Arrays.asList(
            CATEGORIA_TODOS,
            CATEGORIA_ROPA,
            CATEGORIA_CALZADO,
            CATEGORIA_LIBRERIA,
            CATEGORIA_MAQUILLAJE,
            CATEGORIA_UTENSILIOS_COCINA,
            CATEGORIA_PRODUCTOS_LIMPIEZA,
            CATEGORIA_HOGAR,
            CATEGORIA_JUGUETES,
            CATEGORIA_TECNOLOGIA,
            CATEGORIA_ACCESORIOS
        ));
    }

    public static List<String> obtenerCategoriasParaFiltro() {
        List<String> categorias = obtenerCategorias();
        categorias.remove(CATEGORIA_TODOS);
        return categorias;
    }

    public static boolean esCategoriaValida(String categoria) {
        return obtenerCategorias().contains(categoria);
    }
}