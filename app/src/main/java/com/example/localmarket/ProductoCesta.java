package com.example.localmarket;

public class ProductoCesta {

    public String nombre;
    public String info;
    public double precio;
    public int cantidad;
    public int imagenRes;

    public ProductoCesta(String nombre, String info, double precio, int cantidad, int imagenRes) {
        this.nombre = nombre;
        this.info = info;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenRes = imagenRes;
    }
}
