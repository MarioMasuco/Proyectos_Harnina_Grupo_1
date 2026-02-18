package com.example.localmarket;

import java.io.Serializable;

public class Producto implements Serializable {

    public String nombre;
    public String categoria;
    public String descripcion;
    public double precio;
    public boolean disponible;
    public int imagenRes;

    // Constructor para catálogo
    public Producto(String nombre, String categoria, double precio,
                    boolean disponible, int imagenRes) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.disponible = disponible;
        this.imagenRes = imagenRes;
        this.descripcion = "";
    }

    // Constructor para detalle
    public Producto(String nombre, String categoria, String descripcion,
                    double precio, boolean disponible, int imagenRes) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.imagenRes = imagenRes;
    }
}
