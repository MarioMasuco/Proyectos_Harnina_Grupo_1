package com.example.proyectos_harnina_grupo_1.proyectogrupa;

public class ItemProductoGestor {
    private String nombre, descripcion, precio, docId;

    public ItemProductoGestor(String nombre, String descripcion, String precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getPrecio() { return precio; }
    public String getDocId() { return docId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecio(String precio) { this.precio = precio; }
    public void setDocId(String docId) { this.docId = docId; }
}