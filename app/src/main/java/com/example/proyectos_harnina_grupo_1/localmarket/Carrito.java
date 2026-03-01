package com.example.proyectos_harnina_grupo_1.localmarket;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private static Carrito instancia;
    private final List<ProductoCesta> productos = new ArrayList<>();
    private final List<ProductoCesta> ultimaFactura = new ArrayList<>();

    private Carrito() {}

    public static Carrito getInstancia() {
        if (instancia == null) instancia = new Carrito();
        return instancia;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        for (ProductoCesta p : productos) {
            if (p.nombre.equals(producto.nombre)) {
                p.cantidad += cantidad;
                return;
            }
        }
        productos.add(new ProductoCesta(
                producto.nombre,
                producto.descripcion,
                producto.precio,
                cantidad,
                producto.imagenRes,
                producto.imagenUrl != null ? producto.imagenUrl : ""
        ));
    }
    public void eliminarProducto(ProductoCesta producto) {
        productos.remove(producto);
    }

    public List<ProductoCesta> getProductos() { return productos; }

    public double getTotal() {
        double total = 0;
        for (ProductoCesta p : productos) total += p.precio * p.cantidad;
        return total;
    }

    public void vaciar() {
        ultimaFactura.clear();
        ultimaFactura.addAll(productos);
        productos.clear();
    }

    public List<ProductoCesta> getProductosFactura() { return ultimaFactura; }
}