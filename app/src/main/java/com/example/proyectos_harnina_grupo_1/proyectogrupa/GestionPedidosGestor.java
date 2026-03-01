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

public class GestionPedidosGestor extends AppCompatActivity {
    private ItemPedidoGestor itemSeleccionado = null;
    private int posicionSeleccionada = -1;
    private PedidoAdapterGestor adapter;
    private List<ItemPedidoGestor> lista = new ArrayList<>();
    private String docIdTienda, nombreTienda;
    private Button verTienda;
    private FirebaseFirestore db;
    private ImageButton invernadero, cerrarSesion, configuracion;
    private Button btnCrear, btnModificar, btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pedidos_gestor);

        nombreTienda = getIntent().getStringExtra("nombreTienda");
        docIdTienda = getIntent().getStringExtra("docId");
        db = FirebaseFirestore.getInstance();

        verTienda = findViewById(R.id.inicio);
        TextView titulo = findViewById(R.id.nombre);
        titulo.setText("Pedidos de " + nombreTienda);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        btnCrear = findViewById(R.id.inicio8);
        btnModificar = findViewById(R.id.inicio9);
        btnEliminar = findViewById(R.id.inicio6);
        invernadero = findViewById(R.id.imageButton4);
        cerrarSesion = findViewById(R.id.imageButton);
        configuracion = findViewById(R.id.imageButton2);

        adapter = new PedidoAdapterGestor(lista, (item, posicion) -> {
            itemSeleccionado = item;
            posicionSeleccionada = posicion;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cargarPedidos();

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
            Intent intent = new Intent(this, PedidoModificarGestor.class);
            startActivityForResult(intent, 1);
        });

        btnModificar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un pedido primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PedidoModificarGestor.class);
            intent.putExtra("idPedido", itemSeleccionado.getIdPedido());
            intent.putExtra("nombreCliente", itemSeleccionado.getNombreCliente());
            intent.putExtra("productos", itemSeleccionado.getProductos());
            intent.putExtra("coste", itemSeleccionado.getCoste());
            intent.putExtra("estado", itemSeleccionado.getEstado());
            intent.putExtra("docId", itemSeleccionado.getDocId());
            intent.putExtra("posicion", posicionSeleccionada);
            startActivityForResult(intent, 1);
        });

        btnEliminar.setOnClickListener(v -> {
            if (itemSeleccionado == null) {
                Toast.makeText(this, "Selecciona un pedido primero", Toast.LENGTH_SHORT).show();
                return;
            }
            db.collection("tiendas").document(docIdTienda)
                    .collection("pedidos").document(itemSeleccionado.getDocId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        adapter.eliminarItem(posicionSeleccionada);
                        itemSeleccionado = null;
                        posicionSeleccionada = -1;
                        Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void cargarPedidos() {
        db.collection("tiendas").document(docIdTienda)
                .collection("pedidos").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ItemPedidoGestor item = new ItemPedidoGestor(
                                doc.getString("idPedido"),
                                doc.getString("nombreCliente"),
                                doc.getString("productos"),
                                doc.getString("coste"),
                                doc.getString("estado")
                        );
                        item.setDocId(doc.getId());
                        lista.add(item);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String idPedido = data.getStringExtra("idPedido");
            String nombreCliente = data.getStringExtra("nombreCliente");
            String productos = data.getStringExtra("productos");
            String coste = data.getStringExtra("coste");
            String estado = data.getStringExtra("estado");
            int posicion = data.getIntExtra("posicion", -1);
            String docId = data.getStringExtra("docId");

            Map<String, Object> datos = new HashMap<>();
            datos.put("idPedido", idPedido);
            datos.put("nombreCliente", nombreCliente);
            datos.put("productos", productos);
            datos.put("coste", coste);
            datos.put("estado", estado);

            if (posicion == -1) {
                db.collection("tiendas").document(docIdTienda)
                        .collection("pedidos").add(datos)
                        .addOnSuccessListener(docRef -> {
                            ItemPedidoGestor nuevo = new ItemPedidoGestor(idPedido, nombreCliente, productos, coste, estado);
                            nuevo.setDocId(docRef.getId());
                            adapter.agregarItem(nuevo);
                            Toast.makeText(this, "Pedido creado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al crear", Toast.LENGTH_SHORT).show()
                        );
            } else {
                db.collection("tiendas").document(docIdTienda)
                        .collection("pedidos").document(docId).update(datos)
                        .addOnSuccessListener(aVoid -> {
                            ItemPedidoGestor item = lista.get(posicion);
                            item.setIdPedido(idPedido);
                            item.setNombreCliente(nombreCliente);
                            item.setProductos(productos);
                            item.setCoste(coste);
                            item.setEstado(estado);
                            adapter.modificarItem(item, posicion);
                            Toast.makeText(this, "Pedido modificado", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show()
                        );
            }
        }
    }
}