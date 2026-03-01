package com.example.proyectos_harnina_grupo_1.proyectogrupa;

public class ItemTiendaGestor {

    private String nombre;
    private String descripcion;
    private String propietario;
    private int foto;
    private String docId;

    public ItemTiendaGestor(String nombre, String descripcion, String propietario, int foto) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.propietario = propietario;
        this.foto = foto;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }

    public int getFoto() { return foto; }
    public void setFoto(int foto) { this.foto = foto; }

    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
}