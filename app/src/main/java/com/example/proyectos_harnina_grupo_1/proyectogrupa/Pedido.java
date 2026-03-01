package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import java.util.Date;
import java.util.List;

public class Pedido {
    private String id;
    private String tiendaId;
    private String tiendaNombre; // Añadido para mostrar el nombre de la tienda
    private String clienteId;
    private String clienteNombre;
    private List<String> productos; // ¡CAMBIO CLAVE! Una lista con los nombres de los productos.
    private Date fecha;
    private String estado;
    private double total;

    // Constructor vacío requerido por Firestore
    public Pedido() {}

    // Getters y Setters (asegúrate de añadir los nuevos)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTiendaId() { return tiendaId; }
    public void setTiendaId(String tiendaId) { this.tiendaId = tiendaId; }
    public String getTiendaNombre() { return tiendaNombre; } // Nuevo getter
    public void setTiendaNombre(String tiendaNombre) { this.tiendaNombre = tiendaNombre; } // Nuevo setter
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public List<String> getProductos() { return productos; } // Nuevo getter
    public void setProductos(List<String> productos) { this.productos = productos; } // Nuevo setter
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}