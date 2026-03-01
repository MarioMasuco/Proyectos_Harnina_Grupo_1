package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PantallaConfiguracion extends BaseActivity {

    private ImageView btnMenu, btnCarrito;

    private ImageView btnFavoritos, btnInvernadero, btnAjustes;

    private RecyclerView recyclerConfiguracion;
    private ConfiguracionAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ActivityResultLauncher<String> galeriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_configuracion);

        mAuth   = FirebaseAuth.getInstance();
        db      = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> { if (uri != null) subirFotoAStorage(uri); }
        );

        inicializarVistas();
        configurarEventos();
        cargarDatosUsuario();
    }

    private void inicializarVistas() {
        btnMenu    = findViewById(R.id.btnMenu);
        btnCarrito = findViewById(R.id.btnCarrit);

        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);

        inicializarFiltro(false);

        recyclerConfiguracion = findViewById(R.id.recyclerConfiguracion);
        recyclerConfiguracion.setLayoutManager(new LinearLayoutManager(this));
    }

    public void abrirGaleria() {
        galeriaLauncher.launch("image/*");
    }

    private void subirFotoAStorage(Uri uri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        StorageReference ref = storage.getReference()
                .child("fotos_perfil/" + user.getUid() + ".jpg");

        ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String url = downloadUri.toString();
                            db.collection("usuarios").document(user.getUid())
                                    .update("fotoPerfil", url)
                                    .addOnSuccessListener(unused -> {
                                        if (adapter != null) adapter.actualizarFoto(url);
                                        Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                                    });
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al subir foto: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            mostrarConfiguracion("—", "—", "—", "", new ArrayList<>());
            return;
        }

        String uid   = user.getUid();
        String email = user.getEmail() != null ? user.getEmail() : "—";

        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    String nombre     = doc.getString("nombre")     != null ? doc.getString("nombre")     : "—";
                    String rol        = doc.getString("rol")        != null ? doc.getString("rol")        : "—";
                    String fotoPerfil = doc.getString("fotoPerfil") != null ? doc.getString("fotoPerfil") : "";
                    cargarHistorialYMostrar(uid, nombre, email, rol, fotoPerfil);
                })
                .addOnFailureListener(e ->
                        mostrarConfiguracion("—", email, "—", "", new ArrayList<>())
                );
    }

    private void cargarHistorialYMostrar(String uid, String nombre,
                                         String email, String rol, String fotoPerfil) {
        db.collection("usuarios").document(uid)
                .collection("pedidos")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    List<String> historial = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        double total   = doc.getDouble("total")     != null ? doc.getDouble("total")     : 0;
                        long ts        = doc.getLong("timestamp")   != null ? doc.getLong("timestamp")   : 0;
                        String estado  = doc.getString("estado")    != null ? doc.getString("estado")    : "—";
                        String fecha   = new java.text.SimpleDateFormat("dd/MM/yyyy",
                                java.util.Locale.getDefault()).format(new java.util.Date(ts));
                        historial.add(fecha + "  —  " + String.format("%.2f €", total) + "  (" + estado + ")");
                    }
                    mostrarConfiguracion(nombre, email, rol, fotoPerfil, historial);
                })
                .addOnFailureListener(e ->
                        mostrarConfiguracion(nombre, email, rol, fotoPerfil, new ArrayList<>())
                );
    }

    private void mostrarConfiguracion(String nombre, String email, String rol,
                                      String fotoPerfil, List<String> historial) {
        adapter = new ConfiguracionAdapter(this, nombre, email, rol, fotoPerfil, historial);
        recyclerConfiguracion.setAdapter(adapter);
    }

    private void configurarEventos() {

        btnMenu.setOnClickListener(v -> toggleFiltro());

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaCesta.class)));

        btnFavoritos.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaFavorito.class)));

        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaInvernadero.class)));

        btnAjustes.setOnClickListener(v -> { });
    }
}