package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import java.util.List;

public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private List<String> categorias;
    private double precio;
    private int stock;
    private boolean disponible;
    private String imagenUrl;
    private String tiendaId;

    public Producto() {}

    public Producto(String id, String nombre, String descripcion, List<String> categorias,
                    double precio, int stock, boolean disponible, String imagenUrl, String tiendaId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categorias = categorias;
        this.precio = precio;
        this.stock = stock;
        this.disponible = disponible;
        this.imagenUrl = imagenUrl;
        this.tiendaId = tiendaId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<String> getCategorias() {
        return categorias;
    }

    // Setter para la nueva lista
    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public String getTiendaId() { return tiendaId; }
    public void setTiendaId(String tiendaId) { this.tiendaId = tiendaId; }
}