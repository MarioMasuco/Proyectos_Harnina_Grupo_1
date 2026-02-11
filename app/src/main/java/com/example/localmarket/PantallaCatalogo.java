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

public class PantallaCatalogo extends AppCompatActivity {

    // 🔘 Menú inferior
    private ImageView btnFavoritos;
    private ImageView btnInvernadero;
    private ImageView btnAjustes;

    // 🔝 Barra superior
    private ImageView btnMenu;
    private ImageView btnCarrito;

    // 🎛️ Filtro
    private View filtroInclude;
    private ImageView btnCerrarFiltro;
    private Button btnVolverCatalogo;
    private boolean filtroVisible = false;

    // 📦 RecyclerViews
    private RecyclerView recyclerFiltro;
    private RecyclerView recyclerProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_catalogo);

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
        btnCarrito = findViewById(R.id.btnCarrit);

        // Menú inferior
        btnFavoritos = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes = findViewById(R.id.btnAjuste);

        // Filtro
        filtroInclude = findViewById(R.id.filtroInclude);
        filtroInclude.setVisibility(View.GONE);

        recyclerFiltro = filtroInclude.findViewById(R.id.recyclerFiltro);
        btnCerrarFiltro = filtroInclude.findViewById(R.id.btnCerrarFiltro);
        btnVolverCatalogo = findViewById(R.id.btnVolverCatalogo);

        // Productos
        recyclerProductos = findViewById(R.id.recyclerProductos);
    }

    /**
     * Configura los RecyclerView
     */
    private void configurarRecyclerViews() {

        recyclerFiltro.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiltro.setAdapter(new FiltroAdapter(crearDatosFiltro()));

        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(new ProductoAdapter(crearProductos()));
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarEventos() {

        // ☰ Mostrar / ocultar filtro
        btnMenu.setOnClickListener(v -> {
            filtroVisible = !filtroVisible;
            filtroInclude.setVisibility(filtroVisible ? View.VISIBLE : View.GONE);
        });

        // ❌ Cerrar filtro
        btnCerrarFiltro.setOnClickListener(v -> {
            filtroInclude.setVisibility(View.GONE);
            filtroVisible = false;
        });

        // 🛒 Carrito
        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaCesta.class))
        );

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

        // 🔙 Volver al catálogo
        btnVolverCatalogo.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaCatalogo.class))
        );
    }

    /**
     * Datos de productos
     */
    private List<Producto> crearProductos() {
        List<Producto> productos = new ArrayList<>();

        productos.add(new Producto(
                "Tomates ecológicos",
                "Verduras frescas",
                2.5,
                true,
                R.drawable.logo3
        ));

        productos.add(new Producto(
                "Soporte 3D",
                "PLA",
                5.99,
                true,
                R.drawable.sensor
        ));

        productos.add(new Producto(
                "Martillo",
                "Acero",
                9.9,
                false,
                R.drawable.iconomen
        ));

        return productos;
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