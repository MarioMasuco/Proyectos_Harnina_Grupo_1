package com.example.proyectos_harnina_grupo_1.localmarket;

import java.io.Serializable;
import java.util.List;

public class Producto implements Serializable {

    public String id;
    public String nombre;
    public List<String> categoria;
    public String descripcion;
    public double precio;
    public boolean disponible;
    public int imagenRes;
    public String imagenUrl;
    public boolean favorito = false;
    public int stock;

    public Producto() {}

    public Producto(String nombre, List<String> categoria, String descripcion,
                    double precio, int stock, int imagenRes) {
        this.nombre      = nombre;
        this.categoria   = categoria;
        this.descripcion = descripcion;
        this.precio      = precio;
        this.stock       = stock;
        this.disponible  = stock > 0;
        this.imagenRes   = imagenRes;
    }
}