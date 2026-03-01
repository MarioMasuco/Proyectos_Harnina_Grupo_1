package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class GestionClientesGestor extends AppCompatActivity {
    private ItemClienteGestor itemSeleccionado = null;
    private int posicionSeleccionada = -1;
    private ClienteAdapterGestor adapter;
    private List<ItemClienteGestor> lista = new ArrayList<>();
    private String docIdTienda, nombreTienda;
    private Button verTienda;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_clientes);

        nombreTienda = getIntent().getStringExtra("nombreTienda");
        docIdTienda = getIntent().getStringExtra("docId");
        db = FirebaseFirestore.getInstance();

        verTienda = findViewById(R.id.inicio);
        TextView titulo = findViewById(R.id.nombre);
        titulo.setText("Clientes de " + nombreTienda);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Button btnCrear = findViewById(R.id.inicio8);
        Button btnModificar = findViewById(R.id.inicio9);
        Button btnEliminar = findViewById(R.id.inicio6);
        ImageButton invernadero = findViewById(R.id.imageButton4);
        ImageButton cerrarSesion = findViewById(R.id.imageButton);
        ImageButton configuracion = findViewById(R.id.imageButton2);

        adapter = new ClienteAdapterGestor(lista, (item, posicion) -> {
            itemSeleccionado = item;
            posicionSeleccionada = posicion;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarClientes();

        invernadero.setOnClickListener(v -> startActivity(new Intent(this, PantallaInvernadero.class)));
        cerrarSesion.setOnClickListener(v -> startActivity(new Intent(this, IniciSesion.class)));
        configuracion.setOnClickListener(v -> {
            DialogUtils.mostrarDialogoConfiguracion(this);
        });

        verTienda.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionGestorM2.class);
            intent.putExtra("nombreTienda", nombreTienda);
            intent.putExtra("docId", docIdTienda);
            startActivity(intent);
        });

        btnCrear.setOnClickListener(v -> {
            Intent intent = new Intent(this, UsuarioModificarGestor.class);
            startActivityForResult(intent, 1);
        });

        btnModificar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un cliente primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, UsuarioModificarGestor.class);
            intent.putExtra("nombre", itemSeleccionado.getNombre());
            intent.putExtra("descripcion", itemSeleccionado.getDescripcion());
            intent.putExtra("tipoUsuario", itemSeleccionado.getTipoUsuario());
            intent.putExtra("idCliente", itemSeleccionado.getIdCliente());
            intent.putExtra("docId", itemSeleccionado.getDocId());
            intent.putExtra("posicion", posicionSeleccionada);
            startActivityForResult(intent, 1);
        });

        btnEliminar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un cliente primero", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("tiendas").document(docIdTienda)
                    .collection("clientes").document(itemSeleccionado.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        adapter.eliminarItem(posicionSeleccionada);
                        itemSeleccionado = null;
                        posicionSeleccionada = -1;
                        Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void cargarClientes() {
        db.collection("tiendas").document(docIdTienda)
                .collection("clientes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ItemClienteGestor item = new ItemClienteGestor(
                                doc.getString("nombre"),
                                doc.getString("descripcion"),
                                doc.getString("tipoUsuario"),
                                doc.getString("idCliente")
                        );
                        item.setDocId(doc.getId());
                        lista.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String nombre = data.getStringExtra("nombre");
            String descripcion = data.getStringExtra("descripcion");
            String tipoUsuario = data.getStringExtra("tipoUsuario");
            String idCliente = data.getStringExtra("idCliente");
            int posicion = data.getIntExtra("posicion", -1);
            String docId = data.getStringExtra("docId");

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombre);
            datos.put("descripcion", descripcion);
            datos.put("tipoUsuario", tipoUsuario);
            datos.put("idCliente", idCliente);

            if (posicion == -1) {
                db.collection("tiendas").document(docIdTienda)
                        .collection("clientes").add(datos)
                        .addOnSuccessListener(docRef -> {
                            ItemClienteGestor nuevo = new ItemClienteGestor(nombre, descripcion, tipoUsuario, idCliente);
                            nuevo.setDocId(docRef.getId());
                            adapter.agregarItem(nuevo);
                            Toast.makeText(this, "Cliente creado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show()
                        );
            } else {
                db.collection("tiendas").document(docIdTienda)
                        .collection("clientes").document(docId).update(datos)
                        .addOnSuccessListener(aVoid -> {
                            ItemClienteGestor item = lista.get(posicion);
                            item.setNombre(nombre);
                            item.setDescripcion(descripcion);
                            item.setTipoUsuario(tipoUsuario);
                            item.setIdCliente(idCliente);
                            adapter.modificarItem(item, posicion);
                            Toast.makeText(this, "Cliente modificado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show()
                        );
            }
        }
    }
}