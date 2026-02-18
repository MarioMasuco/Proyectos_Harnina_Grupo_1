package com.example.localmarket;

public class FiltroItem {

    public static final int HEADER = 0;
    public static final int CATEGORIA = 1;
    public static final int SUBCATEGORIA = 2;
    public static final int PRECIO = 3;
    public static final int TIENDA = 4;

    public int tipo;
    public String texto;

    // estado
    public boolean visible = true;
    public boolean seleccionado = false;
    public boolean expandido = true;

    public FiltroItem(int tipo, String texto) {
        this.tipo = tipo;
        this.texto = texto;
    }
}
