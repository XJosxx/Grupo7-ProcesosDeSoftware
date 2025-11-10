package com.example.apptienda.modelos;

/**
 * Enum con las categorias base del proyecto. Util para development y validacion.
 */
public enum CategoriaEnum {
    ROPA("Ropa"),
    CALZADO("Calzado"),
    LIBRERIA("Libreria"),
    MAQUILLAJE("Maquillaje"),
    UTENSILIOS_COCINA("Utensilios de cocina"),
    PRODUCTO_LIMPIEZA("Producto de limpieza"),
    HOGAR("Hogar"),
    JUGUETES("Juguetes"),
    TECNOLOGIA("Tecnologia"),
    ACCESORIOS("Accesorios");

    private final String label;

    CategoriaEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
