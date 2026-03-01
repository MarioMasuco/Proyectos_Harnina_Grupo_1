package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;

public class GestionGestorM2 extends AppCompatActivity {
    private ImageButton invernadero, cerrarSesion, configuracion;
    private Button pedidos, clientes, productos;
    private String nombreTienda, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_gestor_m2);

        // Recibir datos de la tienda seleccionada
        nombreTienda = getIntent().getStringExtra("nombreTienda");
        docId = getIntent().getStringExtra("docId");

        invernadero = findViewById(R.id.imageButton4);
        cerrarSesion = findViewById(R.id.imageButton);
        configuracion = findViewById(R.id.imageButton2);
        pedidos = findViewById(R.id.pedidos);
        clientes = findViewById(R.id.clientes);
        productos = findViewById(R.id.productos);

        invernadero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PantallaInvernadero.class);
                startActivity(intent);
            }
        });

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), IniciSesion.class);
                startActivity(intent);
            }
        });

        configuracion.setOnClickListener(v -> {
            DialogUtils.mostrarDialogoConfiguracion(this);
        });

        pedidos.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionPedidosGestor.class);
            intent.putExtra("nombreTienda", nombreTienda);
            intent.putExtra("docId", docId);
            startActivity(intent);
        });

        clientes.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionClientesGestor.class);
            intent.putExtra("nombreTienda", nombreTienda);
            intent.putExtra("docId", docId);
            startActivity(intent);
        });

        productos.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionProductoGestor.class);
            intent.putExtra("nombreTienda", nombreTienda);
            intent.putExtra("docId", docId);
            startActivity(intent);
        });
    }
}