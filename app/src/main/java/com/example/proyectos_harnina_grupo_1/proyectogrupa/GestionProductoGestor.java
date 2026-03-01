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

public class GestionProductoGestor extends AppCompatActivity {
    private ItemProductoGestor itemSeleccionado = null;
    private int posicionSeleccionada = -1;
    private ProductoAdapterGestor adapter;
    private List<ItemProductoGestor> lista = new ArrayList<>();
    private String docIdTienda, nombreTienda;
    private Button verTienda;
    private FirebaseFirestore db;
    private ImageButton invernadero, cerrarSesion, configuracion;
    private Button btnCrear, btnModificar, btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_producto_gestor);

        nombreTienda = getIntent().getStringExtra("nombreTienda");
        docIdTienda = getIntent().getStringExtra("docId");
        db = FirebaseFirestore.getInstance();

        verTienda = findViewById(R.id.inicio);
        TextView titulo = findViewById(R.id.nombre);
        titulo.setText("Productos de " + nombreTienda);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        btnCrear = findViewById(R.id.inicio8);
        btnModificar = findViewById(R.id.inicio9);
        btnEliminar = findViewById(R.id.inicio6);
        invernadero = findViewById(R.id.imageButton4);
        cerrarSesion = findViewById(R.id.imageButton);
        configuracion = findViewById(R.id.imageButton2);

        adapter = new ProductoAdapterGestor(lista, (item, posicion) -> {
            itemSeleccionado = item;
            posicionSeleccionada = posicion;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarProductos();

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
            Intent intent = new Intent(this, ModificarProductoGestor.class);
            startActivityForResult(intent, 1);
        });

        btnModificar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ModificarProductoGestor.class);
            intent.putExtra("nombre", itemSeleccionado.getNombre());
            intent.putExtra("descripcion", itemSeleccionado.getDescripcion());
            intent.putExtra("precio", itemSeleccionado.getPrecio());
            intent.putExtra("docId", itemSeleccionado.getDocId());
            intent.putExtra("posicion", posicionSeleccionada);
            startActivityForResult(intent, 1);
        });

        btnEliminar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("tiendas").document(docIdTienda)
                    .collection("productos").document(itemSeleccionado.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        adapter.eliminarItem(posicionSeleccionada);
                        itemSeleccionado = null;
                        posicionSeleccionada = -1;
                        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void cargarProductos() {
        db.collection("tiendas").document(docIdTienda)
                .collection("productos").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ItemProductoGestor item = new ItemProductoGestor(
                                doc.getString("nombre"),
                                doc.getString("descripcion"),
                                doc.getString("precio")
                        );
                        item.setDocId(doc.getId());
                        lista.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String nombre = data.getStringExtra("nombre");
            String descripcion = data.getStringExtra("descripcion");
            String precio = data.getStringExtra("precio");
            int posicion = data.getIntExtra("posicion", -1);
            String docId = data.getStringExtra("docId");

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombre);
            datos.put("descripcion", descripcion);
            datos.put("precio", precio);

            if (posicion == -1) {
                db.collection("tiendas").document(docIdTienda)
                        .collection("productos").add(datos)
                        .addOnSuccessListener(docRef -> {
                            ItemProductoGestor nuevo = new ItemProductoGestor(nombre, descripcion, precio);
                            nuevo.setDocId(docRef.getId());
                            adapter.agregarItem(nuevo);
                            Toast.makeText(this, "Producto creado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show()
                        );
            } else {
                db.collection("tiendas").document(docIdTienda)
                        .collection("productos").document(docId).update(datos)
                        .addOnSuccessListener(aVoid -> {
                            ItemProductoGestor item = lista.get(posicion);
                            item.setNombre(nombre);
                            item.setDescripcion(descripcion);
                            item.setPrecio(precio);
                            adapter.modificarItem(item, posicion);
                            Toast.makeText(this, "Producto modificado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show()
                        );
            }
        }
    }
}