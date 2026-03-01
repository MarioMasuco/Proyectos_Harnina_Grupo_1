package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PantallaCatalogo extends BaseActivity {

    private ImageView btnMenu, btnCarrito;
    private ImageView btnFavoritos, btnInvernadero, btnAjustes;

    private SearchView searchView;
    private RecyclerView recyclerFiltro, recyclerProductos;

    private FirebaseFirestore db;
    private String uid;
    private List<Producto> todosLosProductos = new ArrayList<>();
    private boolean productosCargados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_catalogo);

        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        inicializarVistas();
        configurarEventos();
        cargarProductosDesdeFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (productosCargados) refrescarFavoritos();
    }

    private void inicializarVistas() {
        btnMenu        = findViewById(R.id.btnMenu);
        btnCarrito     = findViewById(R.id.btnCarrit);
        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);

        searchView = findViewById(R.id.searchView);

        inicializarFiltro(true);
        recyclerFiltro = filtroInclude.findViewById(R.id.recyclerFiltro);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void cargarProductosDesdeFirebase() {
        recyclerFiltro.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiltro.setAdapter(new FiltroAdapter(crearDatosFiltro()));

        db.collection("productos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    todosLosProductos.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Producto p = new Producto();
                        p.id          = doc.getId();
                        p.nombre      = doc.getString("nombre");
                        p.descripcion = doc.getString("descripcion");
                        p.imagenUrl   = doc.getString("imagenUrl");
                        p.precio      = doc.getDouble("precio") != null ? doc.getDouble("precio") : 0;
                        p.stock       = doc.getLong("stock") != null ? doc.getLong("stock").intValue() : 0;
                        p.disponible  = p.stock > 0;

                        // ✅ El campo en Firestore se llama "categorias"
                        p.categoria = new ArrayList<>();
                        List<?> catRaw = (List<?>) doc.get("categorias");
                        if (catRaw != null) {
                            for (Object o : catRaw) {
                                if (o != null) p.categoria.add(o.toString());
                            }
                        }

                        todosLosProductos.add(p);
                    }
                    RepositorioProductos.setProductos(todosLosProductos);
                    productosCargados = true;
                    refrescarFavoritos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar productos: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void refrescarFavoritos() {
        if (uid == null) {
            for (Producto p : todosLosProductos) p.favorito = false;
            actualizarAdapter(todosLosProductos);
            return;
        }

        db.collection("usuarios").document(uid).collection("favoritos")
                .get()
                .addOnSuccessListener(snap -> {
                    Set<String> favIds = new HashSet<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) favIds.add(doc.getId());
                    for (Producto p : todosLosProductos) p.favorito = favIds.contains(p.id);
                    actualizarAdapter(todosLosProductos);
                })
                .addOnFailureListener(e -> actualizarAdapter(todosLosProductos));
    }

    private void actualizarAdapter(List<Producto> lista) {
        RecyclerView.Adapter<?> adapter = recyclerProductos.getAdapter();
        if (adapter instanceof ProductoAdapter) {
            ((ProductoAdapter) adapter).actualizarLista(lista);
        } else {
            recyclerProductos.setAdapter(new ProductoAdapter(lista));
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscar(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                buscar(newText);
                return true;
            }
        });

        btnAplicarFiltro.setOnClickListener(v -> {
            FiltroAdapter adapterFiltro = (FiltroAdapter) recyclerFiltro.getAdapter();
            Double min              = adapterFiltro.getMinPrecio();
            Double max              = adapterFiltro.getMaxPrecio();
            List<String> categorias = adapterFiltro.getCategoriasSeleccionadas();

            List<Producto> filtrados = new ArrayList<>();
            for (Producto p : RepositorioProductos.getProductos()) {
                boolean ok = true;
                if (min != null && p.precio < min) ok = false;
                if (max != null && p.precio > max) ok = false;

                // ✅ comparar cada categoría del producto contra el filtro
                if (!categorias.isEmpty()) {
                    boolean coincide = false;
                    if (p.categoria != null) {
                        for (String catProducto : p.categoria) {
                            for (String catFiltro : categorias) {
                                if (catProducto.equalsIgnoreCase(catFiltro)) {
                                    coincide = true;
                                    break;
                                }
                            }
                            if (coincide) break;
                        }
                    }
                    if (!coincide) ok = false;
                }
                if (ok) filtrados.add(p);
            }

            recyclerProductos.setAdapter(new ProductoAdapter(filtrados));
            cerrarFiltro();
            searchView.setQuery("", false);
            searchView.clearFocus();
        });
    }

    private void buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            actualizarAdapter(RepositorioProductos.getProductos());
            return;
        }
        String query = texto.toLowerCase().trim();
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : RepositorioProductos.getProductos()) {
            if (p.nombre != null && p.nombre.toLowerCase().contains(query)) {
                resultado.add(p);
            }
        }
        actualizarAdapter(resultado);
    }

    private List<FiltroItem> crearDatosFiltro() {
        List<FiltroItem> datos = new ArrayList<>();
        datos.add(new FiltroItem(FiltroItem.HEADER,      "Filtro"));
        datos.add(new FiltroItem(FiltroItem.HEADER,      "Categorías"));
        datos.add(new FiltroItem(FiltroItem.CATEGORIA,   "Alimentación"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA,"Verduras"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA,"Frutas"));
        datos.add(new FiltroItem(FiltroItem.CATEGORIA,   "Impresiones 3D"));
        datos.add(new FiltroItem(FiltroItem.CATEGORIA,   "Ferretería"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA,"Herramientas"));
        datos.add(new FiltroItem(FiltroItem.SUBCATEGORIA,"Materiales"));
        datos.add(new FiltroItem(FiltroItem.HEADER,      "Precio"));
        datos.add(new FiltroItem(FiltroItem.PRECIO,      ""));
        datos.add(new FiltroItem(FiltroItem.HEADER,      "Tienda"));
        datos.add(new FiltroItem(FiltroItem.TIENDA,      "IES Inver"));
        datos.add(new FiltroItem(FiltroItem.TIENDA,      "IES 3D design"));
        datos.add(new FiltroItem(FiltroItem.TIENDA,      "Ferretox"));
        return datos;
    }
}