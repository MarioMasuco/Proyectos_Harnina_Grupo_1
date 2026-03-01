// Archivo: Usuario.java
package com.example.proyectos_harnina_grupo_1.proyectogrupa;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String rol;
    private String permisos;
    private String imagenUrl; // Campo para la URL de la imagen
    private String estado; // "activo", "bloqueado", "confirmar"

    // Constructor vacío obligatorio para Firestore
    public Usuario() {}

    // Constructor para crear usuarios
    public Usuario(String id, String nombre, String email, String rol, String permisos, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.permisos = permisos;
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getPermisos() { return permisos; }
    public void setPermisos(String permisos) { this.permisos = permisos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}