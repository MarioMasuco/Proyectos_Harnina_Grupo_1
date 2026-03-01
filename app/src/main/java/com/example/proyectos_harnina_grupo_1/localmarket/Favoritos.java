package com.example.proyectos_harnina_grupo_1.localmarket;

import java.util.ArrayList;
import java.util.List;

public class Favoritos {

    private static Favoritos instancia;
    private final List<Producto> favoritos = new ArrayList<>();

    private Favoritos() {}

    public static Favoritos getInstancia() {
        if (instancia == null) {
            instancia = new Favoritos();
        }
        return instancia;
    }

    public void toggleFavorito(Producto producto) {
        if (favoritos.contains(producto)) {
            favoritos.remove(producto);
        } else {
            favoritos.add(producto);
        }
    }

    public boolean esFavorito(Producto producto) {
        return favoritos.contains(producto);
    }

    public List<Producto> getFavoritos() {
        return favoritos;
    }
}