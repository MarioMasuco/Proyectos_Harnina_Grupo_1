package com.example.localmarket;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private static Carrito instancia;
    private final List<ProductoCesta> productos = new ArrayList<>();

    private Carrito() {}

    public static Carrito getInstancia() {
        if (instancia == null) {
            instancia = new Carrito();
        }
        return instancia;
    }

    public void agregarProducto(Producto producto, int cantidad) {

        // Si ya existe → suma cantidad
        for (ProductoCesta p : productos) {
            if (p.nombre.equals(producto.nombre)) {
                p.cantidad += cantidad;
                return;
            }
        }

        // Si no existe → añade nuevo
        productos.add(new ProductoCesta(
                producto.nombre,
                producto.descripcion,
                producto.precio,
                cantidad,
                producto.imagenRes
        ));
    }

    public List<ProductoCesta> getProductos() {
        return productos;
    }
}
