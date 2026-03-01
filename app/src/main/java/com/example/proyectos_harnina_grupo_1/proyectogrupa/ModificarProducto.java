package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // <-- CAMBIO CRÍTICO 1: Importar EditText
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModificarProducto extends AppCompatActivity {

    // --- Variables de la UI ---
    // CAMBIO CRÍTICO 2: De TextInputEditText a EditText para que coincida con tu XML
    private EditText etNombre, etDescripcion, etPrecio, etStock;
    private RecyclerView recyclerViewCategorias;
    private Button btnGuardar;
    private ImageButton cerrarsesion, invernadero, configuracion;

    // --- Variables de Firebase y Datos ---
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String productoId;
    private String tiendaId;

    // Variables para controlar la carga de datos y evitar errores
    private List<String> categoriasTienda = new ArrayList<>();
    private List<String> categoriasProducto = new ArrayList<>();
    private boolean datosProductoCargados = false;
    private boolean datosTiendaCargados = false;
    private CategoriaCheckAdapter categoriaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_producto);


        mAuth = FirebaseAuth.getInstance();

        // Vincular vistas
        initViews();

        // Obtener datos del Intent
        Intent intent = getIntent();
        productoId = intent.getStringExtra("productoId");
        tiendaId = intent.getStringExtra("tiendaId");

        // Obtenemos las categorías del producto desde el Intent antes de empezar
        ArrayList<String> categoriasActuales = intent.getStringArrayListExtra("productoCategorias");
        if (categoriasActuales != null) {
            this.categoriasProducto = categoriasActuales;
        }

        if (productoId == null || tiendaId == null) {
            Toast.makeText(this, "Error: Faltan datos del producto.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar datos del producto y de la tienda
        cargarDatosProducto();
        cargarCategoriasTienda();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etStock = findViewById(R.id.etStock);
        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        btnGuardar = findViewById(R.id.btnGuardar);

        cerrarsesion = findViewById(R.id.cerrarSesion);
        invernadero = findViewById(R.id.invernadero);
        configuracion = findViewById(R.id.ajustes);
    }

    private void setupListeners() {
        btnGuardar.setOnClickListener(v -> guardarCambios());

        cerrarsesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ModificarProducto.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        configuracion.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(ModificarProducto.this);
            dialog.mostrar();
        });

        invernadero.setOnClickListener(v -> {
            Intent intent = new Intent(ModificarProducto.this, PantallaInvernadero.class);
            startActivity(intent);
        });
    }

    private void cargarDatosProducto() {
        db.collection("productos").document(productoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        etNombre.setText(documentSnapshot.getString("nombre"));
                        etDescripcion.setText(documentSnapshot.getString("descripcion"));
                        etPrecio.setText(String.valueOf(documentSnapshot.getDouble("precio")));
                        etStock.setText(String.valueOf(documentSnapshot.getLong("stock")));

                        datosProductoCargados = true;
                        // Llamamos a un método que comprueba si todo está listo para configurar la vista
                        comprobarYConfigurarVista();
                    } else {
                        Toast.makeText(this, "El producto no existe.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar producto: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void cargarCategoriasTienda() {
        db.collection("tiendas").document(tiendaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // MEJORA: Comprobamos que el campo sea una lista para evitar fallos
                        Object categoriasObj = documentSnapshot.get("categorias");
                        if (categoriasObj instanceof List) {
                            categoriasTienda = (List<String>) categoriasObj;
                        } else {
                            Log.w("ModificarProducto", "El campo 'categorias' de la tienda no es una lista o es nulo.");
                            categoriasTienda = new ArrayList<>(); // Valor por defecto
                        }
                    }
                    datosTiendaCargados = true;
                    comprobarYConfigurarVista();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar categorías de la tienda: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * Este método se asegura de que tanto los datos del producto como los de la tienda
     * se hayan cargado antes de configurar el RecyclerView. Evita condiciones de carrera.
     */
    private void comprobarYConfigurarVista() {
        if (datosProductoCargados && datosTiendaCargados) {
            recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(this));
            categoriaAdapter = new CategoriaCheckAdapter(categoriasTienda, categoriasProducto);
            recyclerViewCategorias.setAdapter(categoriaAdapter);
        }
    }

    private void guardarCambios() {
        // CAMBIO CRÍTICO 3: Comprobamos que el adaptador se haya inicializado
        if (categoriaAdapter == null) {
            Toast.makeText(this, "Por favor, espera a que se carguen las categorías.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Nombre, precio y stock son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        long stock;
        try {
            precio = Double.parseDouble(precioStr);
            stock = Long.parseLong(stockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El precio y el stock deben ser números válidos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener la lista de categorías seleccionadas del adaptador
        List<String> nuevasCategorias = categoriaAdapter.getCategoriasSeleccionadas();

        Map<String, Object> actualizaciones = new HashMap<>();
        actualizaciones.put("nombre", nombre);
        actualizaciones.put("descripcion", descripcion);
        actualizaciones.put("precio", precio);
        actualizaciones.put("stock", stock);
        actualizaciones.put("categorias", nuevasCategorias);

        db.collection("productos").document(productoId)
                .update(actualizaciones)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Producto actualizado correctamente.", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la actividad y volver a la lista
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}