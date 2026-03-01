package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

public class PantallaProducto extends BaseActivity {

    private ImageView btnMenu, btnCarrito;

    private ImageView btnFavoritos, btnInvernadero, btnAjustes;

    private RecyclerView recyclerProductoDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_producto);

        inicializarVistas();
        configurarRecyclerViews();
        configurarProducto();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnMenu    = findViewById(R.id.btnMenu);
        btnCarrito = findViewById(R.id.btnCarrit);

        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);

        inicializarFiltro(false);

        recyclerProductoDetalle = findViewById(R.id.recyclerProductos);
    }

    private void configurarRecyclerViews() {
        recyclerProductoDetalle.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarProducto() {
        Producto producto = (Producto) getIntent().getSerializableExtra("producto");
        if (producto != null) {
            recyclerProductoDetalle.setAdapter(new ProductoDetalleAdapter(producto));
        }
    }

    private void configurarEventos() {
        btnMenu.setOnClickListener(v -> toggleFiltro());

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaCesta.class)));

        btnFavoritos.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaFavorito.class)));

        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaInvernadero.class)));

        btnAjustes.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaConfiguracion.class)));
    }
}