package com.example.proyectos_harnina_grupo_1.proyectogrupa;

public class ItemPedidoGestor {
    private String idPedido, nombreCliente, productos, coste, estado, docId;

    public ItemPedidoGestor(String idPedido, String nombreCliente, String productos, String coste, String estado) {
        this.idPedido = idPedido;
        this.nombreCliente = nombreCliente;
        this.productos = productos;
        this.coste = coste;
        this.estado = estado;
    }

    public String getIdPedido() { return idPedido; }
    public String getNombreCliente() { return nombreCliente; }
    public String getProductos() { return productos; }
    public String getCoste() { return coste; }
    public String getEstado() { return estado; }
    public String getDocId() { return docId; }
    public void setDocId(String docId) { this.docId = docId; }
    public void setIdPedido(String idPedido) { this.idPedido = idPedido; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public void setProductos(String productos) { this.productos = productos; }
    public void setCoste(String coste) { this.coste = coste; }
    public void setEstado(String estado) { this.estado = estado; }


}