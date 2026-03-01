package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TiendaGestor extends AppCompatActivity {
    private ItemTiendaGestor itemSeleccionado = null;
    private int posicionSeleccionada = -1;
    private TiendaAdapterGestor adapter;
    private List<ItemTiendaGestor> lista = new ArrayList<>();
    private FirebaseFirestore db;
    private Button btnCrear, btnModificar, btnEliminar, verTienda;
    private ImageButton invernadero, cerrarSesion, configuracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda_gestor);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        btnCrear = findViewById(R.id.inicio8);
        btnModificar = findViewById(R.id.inicio9);
        btnEliminar = findViewById(R.id.inicio6);
        verTienda = findViewById(R.id.inicio);
        invernadero = findViewById(R.id.imageButton4);
        cerrarSesion = findViewById(R.id.imageButton);
        configuracion = findViewById(R.id.imageButton2);

        db = FirebaseFirestore.getInstance();

        adapter = new TiendaAdapterGestor(lista, new TiendaAdapterGestor.OnItemClickListener() {
            @Override
            public void onItemClick(ItemTiendaGestor item, int posicion) {
                itemSeleccionado = item;
                posicionSeleccionada = posicion;
            }
            @Override
            public void onConfirmarClick(ItemTiendaGestor item, int posicion) {}
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarTiendas();

        invernadero.setOnClickListener(v -> startActivity(new Intent(this, PantallaInvernadero.class)));
        cerrarSesion.setOnClickListener(v -> startActivity(new Intent(this, IniciSesion.class)));
        configuracion.setOnClickListener(v -> {
            DialogUtils.mostrarDialogoConfiguracion(this);
        });
        btnCrear.setOnClickListener(v -> {
            // 1️⃣ Inflar layout del diálogo
            View dialogView = getLayoutInflater().inflate(R.layout.dialogo_tienda_ges, null);

            // 2️⃣ Obtener referencias a los EditText y botón del diálogo
            EditText etNombreTienda = dialogView.findViewById(R.id.etCorreo);
            EditText etUsuarioProp = dialogView.findViewById(R.id.etCorreo3);
            EditText etDescripcion = dialogView.findViewById(R.id.Correo3);
            Button btnCrearDialog = dialogView.findViewById(R.id.inicio7);

            // 3️⃣ Crear el AlertDialog
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            dialog.show();

            // 4️⃣ Configurar botón crear dentro del diálogo
            btnCrearDialog.setOnClickListener(view -> {
                String nombreTienda = etNombreTienda.getText().toString().trim();
                String usuarioProp = etUsuarioProp.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();

                // Validaciones simples
                if(nombreTienda.isEmpty()){
                    etNombreTienda.setError("Introduce el nombre de la tienda");
                    return;
                }
                if(usuarioProp.isEmpty()){
                    etUsuarioProp.setError("Introduce el usuario propietario");
                    return;
                }

                // Crear mapa con los datos
                Map<String, Object> tiendaData = new HashMap<>();
                tiendaData.put("nombre", nombreTienda);
                tiendaData.put("propietario", usuarioProp);
                tiendaData.put("descripcion", descripcion);

                // Guardar en Firestore
                db.collection("tiendas")
                        .add(tiendaData)
                        .addOnSuccessListener(documentReference -> {
                            // Añadir a la lista local y actualizar RecyclerView
                            ItemTiendaGestor nuevoItem = new ItemTiendaGestor(nombreTienda, descripcion, usuarioProp, R.drawable.estado_confirmar);
                            nuevoItem.setDocId(documentReference.getId());
                            lista.add(nuevoItem);
                            adapter.notifyItemInserted(lista.size() - 1);

                            Toast.makeText(this, "Tienda creada correctamente", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // cerrar diálogo
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al crear tienda: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            });
        });

        btnModificar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un ítem primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ModificadorTiendaGes.class);
            intent.putExtra("nombre", itemSeleccionado.getNombre());
            intent.putExtra("descripcion", itemSeleccionado.getDescripcion());
            intent.putExtra("propietario", itemSeleccionado.getPropietario());
            intent.putExtra("docId", itemSeleccionado.getDocId());
            intent.putExtra("posicion", posicionSeleccionada);
            startActivityForResult(intent, 1);
        });

        btnEliminar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un ítem primero", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("tiendas").document(itemSeleccionado.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        adapter.eliminarItem(posicionSeleccionada);
                        itemSeleccionado = null;
                        posicionSeleccionada = -1;
                        Toast.makeText(this, "Tienda eliminada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    );
        });

        verTienda.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona una tienda primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, GestionGestorM2.class);
            intent.putExtra("nombreTienda", itemSeleccionado.getNombre());
            intent.putExtra("docId", itemSeleccionado.getDocId());
            startActivity(intent);
        });
    }

    private void cargarTiendas() {
        db.collection("tiendas").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombre = doc.getString("nombre");
                        String descripcion = doc.getString("descripcion");
                        String propietario = doc.getString("propietario");
                        ItemTiendaGestor item = new ItemTiendaGestor(nombre, descripcion, propietario, R.drawable.estado_confirmar);
                        item.setDocId(doc.getId());
                        lista.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar tiendas", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String nombre = data.getStringExtra("nombre");
            String descripcion = data.getStringExtra("descripcion");
            String propietario = data.getStringExtra("propietario");
            int posicion = data.getIntExtra("posicion", -1);
            String docId = data.getStringExtra("docId");

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombre);
            datos.put("descripcion", descripcion);
            datos.put("propietario", propietario);

            if (posicion == -1) {
                db.collection("tiendas").add(datos)
                        .addOnSuccessListener(docRef -> {
                            ItemTiendaGestor nuevo = new ItemTiendaGestor(nombre, descripcion, propietario, R.drawable.estado_confirmar);
                            nuevo.setDocId(docRef.getId());
                            adapter.agregarItem(nuevo);
                            Toast.makeText(this, "Tienda creada", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show()
                        );
            } else {
                db.collection("tiendas").document(docId).update(datos)
                        .addOnSuccessListener(aVoid -> {
                            ItemTiendaGestor item = lista.get(posicion);
                            item.setNombre(nombre);
                            item.setDescripcion(descripcion);
                            item.setPropietario(propietario);
                            adapter.modificarItem(item, posicion);
                            Toast.makeText(this, "Tienda modificada", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show()
                        );
            }
        }
    }
}