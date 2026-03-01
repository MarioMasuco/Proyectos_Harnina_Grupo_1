package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PantallaFavorito extends BaseActivity {

    private ImageView btnMenu, btnCarrito;
    private ImageView btnFavoritos, btnInvernadero, btnAjustes;
    private RecyclerView recyclerFavoritos;
    private LinearLayout layoutVacio;

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_favorito);

        db  = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        inicializarVistas();
        configurarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarFavoritosDesdeFirebase();
    }

    private void inicializarVistas() {
        btnMenu        = findViewById(R.id.btnMenu);
        btnCarrito     = findViewById(R.id.btnCarrit);
        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);

        inicializarFiltro(false);

        recyclerFavoritos = findViewById(R.id.recyclerFavoritos);
        recyclerFavoritos.setLayoutManager(new LinearLayoutManager(this));
        layoutVacio = findViewById(R.id.layoutVacio);
    }

    private void cargarFavoritosDesdeFirebase() {
        if (uid == null) {
            Toast.makeText(this, "Inicia sesión para ver tus favoritos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios").document(uid).collection("favoritos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Producto> favoritos = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Producto p = new Producto();
                        p.id          = doc.getId();
                        p.nombre      = doc.getString("nombre");
                        p.descripcion = doc.getString("descripcion");
                        p.imagenUrl   = doc.getString("imagenUrl");
                        p.precio      = doc.getDouble("precio") != null ? doc.getDouble("precio") : 0;
                        p.stock       = doc.getLong("stock") != null ? doc.getLong("stock").intValue() : 0;
                        p.disponible  = p.stock > 0;
                        p.favorito    = true;

                        p.categoria = new ArrayList<>();
                        List<?> catRaw = (List<?>) doc.get("categorias");
                        if (catRaw != null) {
                            for (Object o : catRaw) {
                                if (o != null) p.categoria.add(o.toString());
                            }
                        }

                        favoritos.add(p);
                    }

                    if (favoritos.isEmpty()) {
                        recyclerFavoritos.setVisibility(View.GONE);
                        layoutVacio.setVisibility(View.VISIBLE);
                    } else {
                        recyclerFavoritos.setVisibility(View.VISIBLE);
                        layoutVacio.setVisibility(View.GONE);
                        recyclerFavoritos.setAdapter(new ProductoAdapter(favoritos));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void configurarEventos() {
        btnMenu.setOnClickListener(v -> toggleFiltro());
        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaCesta.class)));
        btnFavoritos.setOnClickListener(v -> {});
        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaInvernadero.class)));
        btnAjustes.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaConfiguracion.class)));
    }
}