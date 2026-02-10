package com.example.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantallaCesta extends AppCompatActivity {

    // 🔝 Barra superior
    private ImageView btnMenu;
    private ImageView btnCarrito;

    // 🔘 Menú inferior
    private ImageView btnFavoritos;
    private ImageView btnInvernadero;
    private ImageView btnAjustes;

    // 🎛️ Filtro
    private View filtroInclude;
    private RecyclerView recyclerFiltro;
    private ImageView btnCerrarFiltro;
    private Button btnVolverCatalogo;
    private boolean filtroVisible = false;

    // 🛒 Cesta
    private RecyclerView recyclerCesta;
    private Button btnComprar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_cesta);

        inicializarVistas();
        configurarRecyclerViews();
        configurarEventos();
    }

    /**
     * Inicializa todas las vistas
     */
    private void inicializarVistas() {

        // Barra superior
        btnMenu = findViewById(R.id.btnMenu);
        btnCarrito = findViewById(R.id.btnCarrito);

        // Menú inferior
        btnFavoritos = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes = findViewById(R.id.btnAjuste);

        // Botón comprar
        btnComprar = findViewById(R.id.btnComprar);

        // Filtro
        filtroInclude = findViewById(R.id.filtroInclude);
        filtroInclude.setVisibility(View.GONE);

        recyclerFiltro = filtroInclude.findViewById(R.id.recyclerFiltro);
        btnCerrarFiltro = filtroInclude.findViewById(R.id.btnCerrarFiltro);
        btnVolverCatalogo = filtroInclude.findViewById(R.id.btnVolverCatalogo);

        // Cesta
        recyclerCesta = findViewById(R.id.recyclerCesta);
    }

    /**
     * Configura los RecyclerView
     */
    private void configurarRecyclerViews() {

        recyclerFiltro.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiltro.setAdapter(new FiltroAdapter(crearDatosFiltro()));

        recyclerCesta.setLayoutManager(new LinearLayoutManager(this));
        recyclerCesta.setAdapter(
                new CestaAdapter(Carrito.getInstancia().getProductos())
        );
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarEventos() {

        // ☰ Abrir / cerrar filtro
        btnMenu.setOnClickListener(v -> {
            filtroVisible = !filtroVisible;
            filtroInclude.setVisibility(filtroVisible ? View.VISIBLE : View.GONE);
        });

        // ❌ Cerrar filtro
        btnCerrarFiltro.setOnClickListener(v -> {
            filtroInclude.setVisibility(View.GONE);
            filtroVisible = false;
        });

        // 🔙 Volver al catálogo
        btnVolverCatalogo.setOnClickListener(v -> {
            startActivity(new Intent(this, PantallaCatalogo.class));
            finish();
        });

        // 🛒 Carrito (ya estás aquí)
        btnCarrito.setOnClickListener(v -> {
            // No hace nada
        });

        // ❤️ Favoritos
        btnFavoritos.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaFavorito.class))
        );

        // 🌱 Invernadero
        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaInvernadero.class))
        );

        // ⚙️ Ajustes
        btnAjustes.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaConfiguracion.class))
        );

        // 💳 Comprar
        btnComprar.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaPago.class))
        );
    }

    /**
     * Datos del filtro
     */
    private List<FiltroItem> crearDatosFiltro() {

        List<FiltroItem> datos = new ArrayList<>();

        datos.add(new FiltroItem(FiltroItem.HEADER, "Filtro"));

        datos.add(new FiltroItem(FiltroItem.HEADER, "Categorías"));
        datos.add(new FiltroItem(FiltroItem.CATEGORIA, "Alimentación"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA, "Verduras"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA, "Fruta"));

        datos.add(new FiltroItem(FiltroItem.CATEGORIA, "Impresiones 3D"));

        datos.add(new FiltroItem(FiltroItem.CATEGORIA, "Ferretería"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA, "Herramientas"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA, "Materiales"));

        datos.add(new FiltroItem(FiltroItem.CATEGORIA, "Otra"));

        datos.add(new FiltroItem(FiltroItem.HEADER, "Precio"));
        datos.add(new FiltroItem(FiltroItem.PRECIO, ""));

        datos.add(new FiltroItem(FiltroItem.HEADER, "Tienda"));
        datos.add(new FiltroItem(FiltroItem.TIENDA, "IES Inver"));
        datos.add(new FiltroItem(FiltroItem.TIENDA, "IES 3D design"));
        datos.add(new FiltroItem(FiltroItem.TIENDA, "Ferretox"));

        return datos;
    }
}
