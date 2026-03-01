package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class GestionProducto extends AppCompatActivity {

    // --- Vistas y Firebase ---
    private Button eliminar, crear, modificar, ver;
    private ImageButton cerrarsesion, invernadero, configuracion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recy;

    // --- Datos y Adaptadores ---
    private List<String> categoriasTienda = new ArrayList<>();
    private List<Producto> listaProductos = new ArrayList<>();
    private ProductoAdapter adapter;
    private Producto productoSeleccionado = null;
    private String tiendaId;
    private String tiendaNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_producto);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tiendaId = getIntent().getStringExtra("TIENDA_ID");
        tiendaNombre = getIntent().getStringExtra("TIENDA_NOMBRE");

        // Vincular vistas
        eliminar = findViewById(R.id.eliminar);
        crear = findViewById(R.id.crear);
        modificar = findViewById(R.id.modificar);
        ver = findViewById(R.id.vertienda);
        cerrarsesion = findViewById(R.id.cerrarSesion);
        invernadero = findViewById(R.id.invernadero);
        configuracion = findViewById(R.id.ajustes);
        recy = findViewById(R.id.recyProductos);

        // Poner el nombre de la tienda en el título
        android.widget.TextView tvTitulo = findViewById(R.id.nombre);
        if (tvTitulo != null && tiendaNombre != null) {
            tvTitulo.setText("Productos de " + tiendaNombre);
        }

        // --- OPTIMIZACIÓN: Configurar RecyclerView sin redundancia ---
        recy.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(this, producto -> {
            productoSeleccionado = producto;
            Toast.makeText(this, "Seleccionado: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
        });
        recy.setAdapter(adapter);

        // --- CAMBIO CLAVE: Primero cargamos los datos de la TIENDA (categorías) ---
        // Esto asegura que 'categoriasTienda' esté lleno antes de intentar usarlo.
        cargarDatosTienda();

        // --- Listeners de los botones (sin cambios) ---
        crear.setOnClickListener(v -> mostrarDialogoCrear());

        eliminar.setOnClickListener(v -> {
            if (productoSeleccionado == null) {
                Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogoEliminar();
        });

        modificar.setOnClickListener(v -> {
            if (productoSeleccionado == null) {
                Toast.makeText(this, "Selecciona un producto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(GestionProducto.this, ModificarProducto.class);
            intent.putExtra("productoId", productoSeleccionado.getId());
            intent.putExtra("productoNombre", productoSeleccionado.getNombre());
            intent.putExtra("productoDescripcion", productoSeleccionado.getDescripcion());
            intent.putStringArrayListExtra("productoCategorias", (ArrayList<String>) productoSeleccionado.getCategorias());
            intent.putExtra("productoPrecio", productoSeleccionado.getPrecio());
            intent.putExtra("productoStock", productoSeleccionado.getStock());
            intent.putExtra("productoDisponible", productoSeleccionado.isDisponible());
            intent.putExtra("productoImagenUrl", productoSeleccionado.getImagenUrl());
            // Pasamos también el ID y nombre de la tienda para poder volver
            intent.putExtra("tiendaId", tiendaId);
            intent.putExtra("tiendaNombre", tiendaNombre);
            startActivity(intent);
        });

        ver.setOnClickListener(v -> finish()); // Volver a la pantalla anterior

        cerrarsesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(GestionProducto.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        configuracion.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(GestionProducto.this);
            dialog.mostrar();
        });

        invernadero.setOnClickListener(v -> {
            Intent intent = new Intent(GestionProducto.this, PantallaInvernadero.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // --- CAMBIO: Recargamos los datos de la tienda al volver para asegurar que las categorías estén actualizadas ---
        // Esto es más robusto que solo recargar los productos.
        cargarDatosTienda();
    }

    // ── Cargar productos de la tienda específica ───────────────────────────────
    private void cargarProductos() {
        if (tiendaId == null) {
            Toast.makeText(this, "Error: No se pudo identificar la tienda.", Toast.LENGTH_SHORT).show();
            Log.e("GestionProducto", "tiendaId es nulo. No se pueden cargar productos.");
            return;
        }

        Log.d("GestionProducto", "Cargando productos para tiendaId: " + tiendaId);

        db.collection("productos")
                .whereEqualTo("tiendaId", tiendaId)
                .get()
                .addOnSuccessListener(result -> {
                    List<Producto> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : result) {
                        // --- CAMBIO CLAVE: Leer el array de categorías ---
                        List<String> cats = new ArrayList<>();
                        if (doc.get("categorias") != null) {
                            // Firestore lo devuelve como List<Object>, lo casteamos a List<String>
                            cats = (List<String>) doc.get("categorias");
                        }

                        Producto p = new Producto(
                                doc.getId(),
                                doc.getString("nombre"),
                                doc.getString("descripcion"),
                                cats, // <-- Pasar la lista de categorías
                                doc.getDouble("precio"),
                                doc.getLong("stock").intValue(),
                                doc.getBoolean("disponible"),
                                doc.getString("imagenUrl"),
                                tiendaId
                        );
                        lista.add(p);
                    }
                    adapter.actualizarLista(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e("GestionProducto", "Error al cargar productos", e);
                    Toast.makeText(this, "Error al cargar productos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ── Diálogo CREAR producto ────────────────────────────────────────────────
    private void mostrarDialogoCrear() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogo_crear_producto, null);

        // Vincular vistas
        EditText etNombre = dialogView.findViewById(R.id.NombreProducto);
        EditText etDescripcion = dialogView.findViewById(R.id.DescripcionProducto);
        EditText etPrecio = dialogView.findViewById(R.id.PrecioProducto);
        EditText etStock = dialogView.findViewById(R.id.StockProducto);
        Spinner spinnerDisponible = dialogView.findViewById(R.id.spinnerDisponibles);
        Button btnCrear = dialogView.findViewById(R.id.btnCrearProductos);

        // --- CAMBIO CLAVE: Configurar el RecyclerView para categorías ---
        RecyclerView recyclerViewCategorias = dialogView.findViewById(R.id.recycler_view_categorias);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this));
        CategoriaCheckAdapter categoriaAdapter = new CategoriaCheckAdapter(categoriasTienda, new ArrayList<>());
        recyclerViewCategorias.setAdapter(categoriaAdapter);

        // El spinner de disponible se queda igual
        ArrayAdapter<String> adapterDisp = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Disponible", "No disponible"});
        adapterDisp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisponible.setAdapter(adapterDisp);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCrear.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String precioStr = etPrecio.getText().toString().trim();
            String stockStr = etStock.getText().toString().trim();
            boolean disponible = spinnerDisponible.getSelectedItemPosition() == 0;

            // --- OBTENER LA LISTA DE CATEGORÍAS SELECCIONADAS ---
            List<String> categoriasSeleccionadas = categoriaAdapter.getCategoriasSeleccionadas();

            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }
            if (categoriasSeleccionadas.isEmpty()) {
                Toast.makeText(this, "Debes seleccionar al menos una categoría", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(precioStr)) {
                etPrecio.setError("El precio es obligatorio");
                return;
            }

            double precio;
            int stock;
            try {
                precio = Double.parseDouble(precioStr);
                stock = TextUtils.isEmpty(stockStr) ? 0 : Integer.parseInt(stockStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Precio o stock inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> nuevoProducto = new HashMap<>();
            nuevoProducto.put("nombre", nombre);
            nuevoProducto.put("descripcion", descripcion);
            // --- GUARDAR LA LISTA DE CATEGORÍAS EN FIRESTORE ---
            nuevoProducto.put("categorias", categoriasSeleccionadas);
            nuevoProducto.put("precio", precio);
            nuevoProducto.put("stock", stock);
            nuevoProducto.put("disponible", disponible);
            nuevoProducto.put("imagenUrl", "");
            nuevoProducto.put("tiendaId", tiendaId);
            nuevoProducto.put("favorito", false);
            nuevoProducto.put("imagenRes", 0);

            db.collection("productos")
                    .add(nuevoProducto)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Producto creado correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarProductos();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al crear: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        dialog.show();
    }

    // ── Cargar datos de la tienda (categorías) ─────────────────────────────────
    private void cargarDatosTienda() {
        if (tiendaId == null) {
            Toast.makeText(this, "Error: ID de tienda no recibido.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("tiendas").document(tiendaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Actualizar el nombre de la tienda en el título
                        tiendaNombre = documentSnapshot.getString("nombre");
                        android.widget.TextView tvTitulo = findViewById(R.id.nombre);
                        if (tvTitulo != null) {
                            tvTitulo.setText("Productos de " + tiendaNombre);
                        }

                        // --- CARGAR LAS CATEGORÍAS ESPECÍFICAS DE LA TIENDA ---
                        categoriasTienda = (List<String>) documentSnapshot.get("categorias");
                        if (categoriasTienda == null || categoriasTienda.isEmpty()) {
                            // Si no hay categorías, ponemos una por defecto para que no falle el spinner
                            categoriasTienda = new ArrayList<>();
                            categoriasTienda.add("General");
                            Toast.makeText(this, "Esta tienda no tiene categorías definidas. Se usará 'General'.", Toast.LENGTH_LONG).show();
                        }

                        // --- UNA VEZ CARGADAS LAS CATEGORÍAS, CARGAMOS LOS PRODUCTOS ---
                        cargarProductos();
                    } else {
                        Toast.makeText(this, "Error: La tienda no existe.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos de la tienda: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ── Diálogo ELIMINAR producto (sin cambios) ─────────────────────────────────
    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que quieres eliminar " + productoSeleccionado.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.collection("productos").document(productoSeleccionado.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                productoSeleccionado = null;
                                cargarProductos(); // Recargar solo la lista de productos
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}