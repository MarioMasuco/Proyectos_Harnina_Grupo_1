package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TiendaAdmin extends AppCompatActivity {

    private Button eliminar, crear, modificar, ver;
    private ImageButton cerrarsesion, invernadero, configuracion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recy;

    private List<Tienda> listaTiendas = new ArrayList<>();
    private TiendaAdapter adapter;
    private Tienda tiendaSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda_admin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        eliminar = findViewById(R.id.eliminar);
        crear = findViewById(R.id.crear);
        modificar = findViewById(R.id.modificar);
        ver = findViewById(R.id.vertienda);
        cerrarsesion = findViewById(R.id.cerrarSesion);
        invernadero = findViewById(R.id.invernadero);
        configuracion = findViewById(R.id.ajustes);
        recy = findViewById(R.id.recyclerTienda);

        // Configurar RecyclerView
        adapter = new TiendaAdapter(this, tienda -> {
            tiendaSeleccionada = tienda;
            Toast.makeText(this, "Seleccionada: " + tienda.getNombre(), Toast.LENGTH_SHORT).show();
        });
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        // Cargar tiendas
        cargarTiendas();

        // Botón crear
        crear.setOnClickListener(v -> mostrarDialogoCrear());

        // Botón eliminar
        eliminar.setOnClickListener(v -> {
            if (tiendaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una tienda primero", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogoEliminar();
        });

        // Botón modificar → va a ModificarTiendaADM
        modificar.setOnClickListener(v -> {
            if (tiendaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una tienda primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(TiendaAdmin.this, ModificarTiendaADM.class);
            intent.putExtra("tiendaId", tiendaSeleccionada.getId());
            intent.putExtra("tiendaNombre", tiendaSeleccionada.getNombre());
            intent.putExtra("tiendaDescripcion", tiendaSeleccionada.getDescripcion());
            intent.putExtra("tiendaGestorId", tiendaSeleccionada.getGestorId());
            // CAMBIO: Añadido para pasar también la URL de la imagen
            intent.putExtra("tiendaImagenUrl", tiendaSeleccionada.getImagenUrl());
            startActivity(intent);
        });
        // Botón ver → va a GestionAdmM2
        ver.setOnClickListener(v -> {
            // 1. Comprobar si hay una tienda seleccionada
            if (tiendaSeleccionada == null) {
                Toast.makeText(this, "Selecciona una tienda para ver su gestión", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Crear el Intent para ir a GestionAdmM2
            Intent intent = new Intent(TiendaAdmin.this, GestionAdmM2.class);

            // 3. Poner TODOS los datos de la tienda seleccionada en el Intent
            //    Usamos tiendaSeleccionada.get...() para obtener los datos
            intent.putExtra("TIENDA_ID", tiendaSeleccionada.getId());
            intent.putExtra("TIENDA_NOMBRE", tiendaSeleccionada.getNombre());
            intent.putExtra("TIENDA_DESCRIPCION", tiendaSeleccionada.getDescripcion());
            intent.putExtra("TIENDA_GESTOR_ID", tiendaSeleccionada.getGestorId());
            intent.putExtra("TIENDA_IMAGEN_URL", tiendaSeleccionada.getImagenUrl());

            // 4. Iniciar la actividad
            startActivity(intent);
        });

        // Botón cerrar sesión
        cerrarsesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(TiendaAdmin.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Botón configuración
        configuracion.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(TiendaAdmin.this);
            dialog.mostrar();
        });

        // Botón invernadero
        invernadero.setOnClickListener(v -> {
            Intent intent = new Intent(TiendaAdmin.this, PantallaInvernadero.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTiendas(); // Recargar al volver de ModificarTiendaADM
    }

    // ── Cargar tiendas desde Firestore ────────────────────────────────────────
    private void cargarTiendas() {
        db.collection("tiendas")
                .get()
                .addOnSuccessListener(result -> {
                    List<Tienda> tiendasCargadas = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : result) {
                        // --- CARGAR LA LISTA DE CATEGORÍAS DESDE FIRESTORE ---
                        List<String> categorias = (List<String>) doc.get("categorias");

                        Tienda t = new Tienda(
                                doc.getId(),
                                doc.getString("nombre"),
                                doc.getString("descripcion"),
                                doc.getString("gestorId"),
                                doc.getString("adminId"),
                                doc.getString("imagenUrl"),
                                categorias // Pasar la lista al constructor
                        );
                        tiendasCargadas.add(t);
                    }
                    adapter.actualizarLista(tiendasCargadas);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar tiendas: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ── Diálogo CREAR tienda (COMPLETAMENTE REESCRITO) ─────────────────────────────
    private void mostrarDialogoCrear() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogo_tienda_adm, null);

        // Vincular las vistas del diálogo
        EditText etNombre = dialogView.findViewById(R.id.etCorreo);
        EditText etDescripcion = dialogView.findViewById(R.id.etCorreo2);
        // --- VINCULAR EL NUEVO EDITTEXTO DE CATEGORÍAS ---
        EditText etCategorias = dialogView.findViewById(R.id.etCategorias);
        Spinner spinnerGestores = dialogView.findViewById(R.id.spinner);
        Button btnCrear = dialogView.findViewById(R.id.inicio7);

        cargarGestoresEnSpinner(spinnerGestores);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            // --- LEER EL TEXTO DE CATEGORÍAS ---
            String categoriasStr = etCategorias.getText().toString().trim();

            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }

            // --- PROCESAR LAS CATEGORÍAS ---
            List<String> listaCategorias = new ArrayList<>();
            if (!TextUtils.isEmpty(categoriasStr)) {
                // Dividir el texto por comas y limpiar los espacios
                String[] categoriasArray = categoriasStr.split(",");
                for (String cat : categoriasArray) {
                    String categoriaLimpia = cat.trim();
                    if (!categoriaLimpia.isEmpty()) {
                        listaCategorias.add(categoriaLimpia);
                    }
                }
            }


            // Obtener el ID del gestor seleccionado (lógica existente)
            String gestorId = null;
            String selectedItem = (String) spinnerGestores.getSelectedItem();
            if (selectedItem != null && !selectedItem.equals("Sin gestor asignado")) {
                int start = selectedItem.lastIndexOf("(") + 1;
                int end = selectedItem.lastIndexOf(")");
                if (start > 0 && end > start) {
                    gestorId = selectedItem.substring(start, end);
                }
            }

            // Crear el mapa con los datos para Firestore
            Map<String, Object> nuevaTienda = new HashMap<>();
            nuevaTienda.put("nombre", nombre);
            nuevaTienda.put("descripcion", descripcion);
            // --- AÑADIR LA LISTA DE CATEGORÍAS AL MAPA ---
            nuevaTienda.put("categorias", listaCategorias);
            nuevaTienda.put("gestorId", gestorId);
            nuevaTienda.put("adminId", mAuth.getCurrentUser().getUid());
            nuevaTienda.put("imagenUrl", null); // Opcional: inicializar como null

            // Guardar en Firestore
            db.collection("tiendas")
                    .add(nuevaTienda)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Tienda creada correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarTiendas();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al crear: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        dialog.show();
    }


    // ── Cargar gestores en el Spinner ─────────────────────────────────────────
    private void cargarGestoresEnSpinner(Spinner spinner) {
        db.collection("usuarios")
                .whereEqualTo("rol", "gestor")
                .get()
                .addOnSuccessListener(result -> {
                    List<String> nombresGestores = new ArrayList<>();
                    nombresGestores.add("Sin gestor asignado");
                    for (QueryDocumentSnapshot doc : result) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) {
                            // Formateamos para mostrar nombre y poder extraer el ID después
                            nombresGestores.add(nombre + " (" + doc.getId() + ")");
                        }
                    }
                    ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, nombresGestores);
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapterSpinner);
                });
    }

    // ── Diálogo ELIMINAR tienda ───────────────────────────────────────────────
    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tienda")
                .setMessage("¿Estás seguro de que quieres eliminar " + tiendaSeleccionada.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.collection("tiendas").document(tiendaSeleccionada.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Tienda eliminada", Toast.LENGTH_SHORT).show();
                                tiendaSeleccionada = null;
                                cargarTiendas();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}