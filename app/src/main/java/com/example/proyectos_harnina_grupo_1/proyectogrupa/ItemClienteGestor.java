package com.example.proyectos_harnina_grupo_1.proyectogrupa;

public class ItemClienteGestor {
    private String nombre, descripcion, tipoUsuario, idCliente, docId;

    public ItemClienteGestor(String nombre, String descripcion, String tipoUsuario, String idCliente) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoUsuario = tipoUsuario;
        this.idCliente = idCliente;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getTipoUsuario() { return tipoUsuario; }
    public String getIdCliente() { return idCliente; }
    public String getDocId() { return docId; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public void setDocId(String docId) { this.docId = docId; }
}