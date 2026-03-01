package com.example.proyectos_harnina_grupo_1.localmarket;

public class SensorItem {
    private String nombre;
    private String valor;
    private int imagenResId;
    private String entityId;

    public SensorItem(String nombre, String valor, int imagenResId, String entityId) {
        this.nombre = nombre;
        this.valor = valor;
        this.imagenResId = imagenResId;
        this.entityId = entityId;
    }

    public String getNombre() { return nombre; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public int getImagenResId() { return imagenResId; }
    public String getEntityId() { return entityId; }
}
