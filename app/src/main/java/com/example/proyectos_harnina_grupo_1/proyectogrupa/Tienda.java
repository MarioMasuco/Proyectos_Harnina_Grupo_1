package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import java.util.List;

public class Tienda {
    private String id;
    private String nombre;
    private String descripcion;
    private String gestorId;
    private String adminId;
    private String imagenUrl;
    // --- NUEVO CAMPO PARA LAS CATEGORÍAS ---
    private List<String> categorias;

    public Tienda() {}

    // Constructor actualizado
    public Tienda(String id, String nombre, String descripcion, String gestorId, String adminId, String imagenUrl, List<String> categorias) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.gestorId = gestorId;
        this.adminId = adminId;
        this.imagenUrl = imagenUrl;
        this.categorias = categorias;
    }

    // Getters y Setters existentes...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getGestorId() { return gestorId; }
    public void setGestorId(String gestorId) { this.gestorId = gestorId; }
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    // --- NUEVOS GETTER Y SETTER PARA CATEGORÍAS ---
    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }
}