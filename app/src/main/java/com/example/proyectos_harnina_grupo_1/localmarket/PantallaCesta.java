package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

public class PantallaCesta extends BaseActivity {

    private ImageView btnMenu, btnCarrito;
    private ImageView btnFavoritos, btnInvernadero, btnAjustes;
    private RecyclerView recyclerCesta;
    private LinearLayout layoutCestaVacia;
    private TextView txtTotal;
    private Button btnComprar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_cesta);

        inicializarVistas();
        configurarEventos();
        actualizarCesta();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarCesta();
    }

    private void inicializarVistas() {
        btnMenu    = findViewById(R.id.btnMenu);
        btnCarrito = findViewById(R.id.btnCarrito);
        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);
        btnComprar     = findViewById(R.id.btnComprar);
        txtTotal       = findViewById(R.id.txtTotal);
        recyclerCesta  = findViewById(R.id.recyclerCesta);
        layoutCestaVacia = findViewById(R.id.layoutCestaVacia);

        inicializarFiltro(false);

        recyclerCesta.setLayoutManager(new LinearLayoutManager(this));
    }

    private void actualizarCesta() {
        boolean vacia = Carrito.getInstancia().getProductos().isEmpty();

        if (vacia) {
            recyclerCesta.setVisibility(View.GONE);
            layoutCestaVacia.setVisibility(View.VISIBLE);
            btnComprar.setEnabled(false);
            btnComprar.setAlpha(0.5f);
        } else {
            recyclerCesta.setVisibility(View.VISIBLE);
            layoutCestaVacia.setVisibility(View.GONE);
            btnComprar.setEnabled(true);
            btnComprar.setAlpha(1f);

            CestaAdapter adapter = new CestaAdapter(Carrito.getInstancia().getProductos());
            adapter.setOnTotalCambiadoListener(nuevoTotal -> {
                txtTotal.setText("Total a pagar: " + String.format("%.2f €", nuevoTotal));
                if (Carrito.getInstancia().getProductos().isEmpty()) {
                    recyclerCesta.setVisibility(View.GONE);
                    layoutCestaVacia.setVisibility(View.VISIBLE);
                    btnComprar.setEnabled(false);
                    btnComprar.setAlpha(0.5f);
                }
            });
            recyclerCesta.setAdapter(adapter);
        }

        txtTotal.setText("Total a pagar: " +
                String.format("%.2f €", Carrito.getInstancia().getTotal()));
    }

    private void configurarEventos() {
        btnMenu.setOnClickListener(v -> toggleFiltro());
        btnCarrito.setOnClickListener(v -> {});
        btnFavoritos.setOnClickListener(v -> startActivity(new Intent(this, PantallaFavorito.class)));
        btnInvernadero.setOnClickListener(v -> startActivity(new Intent(this, PantallaInvernadero.class)));
        btnAjustes.setOnClickListener(v -> startActivity(new Intent(this, PantallaConfiguracion.class)));
        btnComprar.setOnClickListener(v -> startActivity(new Intent(this, PantallaPago.class)));
    }
}