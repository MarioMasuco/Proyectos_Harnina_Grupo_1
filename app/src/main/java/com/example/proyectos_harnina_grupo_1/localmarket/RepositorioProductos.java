package com.example.proyectos_harnina_grupo_1.localmarket;

import java.util.ArrayList;
import java.util.List;

public class RepositorioProductos {

    private static final List<Producto> productos = new ArrayList<>();

    public static void setProductos(List<Producto> lista) {
        productos.clear();
        productos.addAll(lista);
    }

    public static List<Producto> getProductos() {
        return productos;
    }

    public static List<Producto> getFavoritos() {
        List<Producto> favoritos = new ArrayList<>();
        for (Producto p : productos) {
            if (p.favorito) {
                favoritos.add(p);
            }
        }
        return favoritos;
    }
}