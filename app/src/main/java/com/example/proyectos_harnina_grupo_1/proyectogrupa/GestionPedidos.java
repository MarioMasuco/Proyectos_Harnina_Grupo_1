package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionPedidos extends AppCompatActivity implements PedidoAdapter.OnPedidoClickListener {

    // --- VISTAS ---
    private TextView tvTitulo;
    private RecyclerView recyclerViewPedidos;
    private AppCompatButton btnCrear, btnModificar, btnEliminar, btnVerTienda;
    private ImageButton btnAjustes, btnCerrarSesion, btnInvernadero;

    // --- DATOS Y ADAPTADORES ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Pedido> listaPedidos;
    private PedidoAdapter adapter;
    private Pedido pedidoSeleccionado = null;

    // --- VARIABLES DE LA TIENDA ---
    private String tiendaId, tiendaNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pedidos);

        tiendaId = getIntent().getStringExtra("TIENDA_ID");
        tiendaNombre = getIntent().getStringExtra("TIENDA_NOMBRE");

        if (tiendaId == null) {
            Toast.makeText(this, "Error: No se pudo identificar la tienda.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        inicializarVistas();
        setupRecyclerView();
        setupBotones();
        cargarPedidos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPedidos();
    }

    // --- CONFIGURACIÓN INICIAL ---
    private void inicializarVistas() {
        tvTitulo = findViewById(R.id.nombre);
        recyclerViewPedidos = findViewById(R.id.recyclerViewPedidos);
        btnCrear = findViewById(R.id.crear);
        btnModificar = findViewById(R.id.modificar);
        btnEliminar = findViewById(R.id.eliminar);
        btnVerTienda = findViewById(R.id.vertienda);
        btnAjustes = findViewById(R.id.ajustes);
        btnCerrarSesion = findViewById(R.id.cerrarSesion);
        btnInvernadero = findViewById(R.id.invernadero);

        tvTitulo.setText("Pedidos de: " + tiendaNombre);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private void setupRecyclerView() {
        listaPedidos = new ArrayList<>();
        adapter = new PedidoAdapter(this, listaPedidos, this);
        recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPedidos.setAdapter(adapter);
    }

    private void setupBotones() {
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(GestionPedidos.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(GestionPedidos.this, PantallaInvernadero.class))
        );

        btnAjustes.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(GestionPedidos.this);
            dialog.mostrar();
        });

        btnCrear.setOnClickListener(v -> mostrarDialogoCrear());

        btnModificar.setOnClickListener(v -> {
            if (pedidoSeleccionado != null) {
                Intent intent = new Intent(GestionPedidos.this, ModificarPedido.class);
                intent.putExtra("PEDIDO_ID", pedidoSeleccionado.getId());
                intent.putExtra("TIENDA_ID", tiendaId);
                intent.putExtra("TIENDA_NOMBRE", tiendaNombre);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(v -> {
            if (pedidoSeleccionado == null) return;
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar pedido")
                    .setMessage("¿Estás seguro de que quieres eliminar el pedido de " +
                            pedidoSeleccionado.getClienteNombre() + "?")
                    .setPositiveButton("Eliminar", (d, w) -> {
                        db.collection("pedidos").document(pedidoSeleccionado.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show();
                                    pedidoSeleccionado = null;
                                    btnModificar.setEnabled(false);
                                    btnEliminar.setEnabled(false);
                                    cargarPedidos();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnVerTienda.setOnClickListener(v -> finish());
    }

    // --- CARGA DE DATOS ---
    private void cargarPedidos() {
        db.collection("pedidos")
                .whereEqualTo("tiendaId", tiendaId)
                .get()
                .addOnSuccessListener(result -> {
                    listaPedidos.clear();
                    for (QueryDocumentSnapshot doc : result) {
                        Pedido pedido = doc.toObject(Pedido.class);
                        pedido.setId(doc.getId());
                        listaPedidos.add(pedido);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar pedidos: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // --- LÓGICA DEL DIÁLOGO DE CREAR PEDIDO (versión mejorada y segura) ---

    // *** CAMBIO 1: Añadimos esta clase interna para guardar los datos del producto de forma segura ***
    public static class ProductoItem {
        public String id;
        public String nombre;
        public double precio;

        public ProductoItem(String id, String nombre, double precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }

        // Sobrescribimos toString() para que se muestre bien en el diálogo
        @Override
        public String toString() {
            return nombre + " (" + precio + "€)";
        }
    }

    private void mostrarDialogoCrear() {
        // *** CAMBIO 2: Usamos nuestra lista segura de productos ***
        List<ProductoItem> productosDisponibles = new ArrayList<>();
        List<String> idsClientes = new ArrayList<>();
        List<String> nombresClientes = new ArrayList<>();

        // Cargar clientes
        db.collection("usuarios").whereEqualTo("rol", "cliente").get()
                .addOnSuccessListener(clientesResult -> {
                    idsClientes.clear();
                    nombresClientes.clear();
                    for (QueryDocumentSnapshot doc : clientesResult) {
                        idsClientes.add(doc.getId());
                        nombresClientes.add(doc.getString("nombre"));
                    }
                    nombresClientes.add(0, "Selecciona un cliente..."); // Placeholder
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar clientes: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );

        // Cargar productos
        db.collection("productos")
                .whereEqualTo("tiendaId", tiendaId)
                .whereEqualTo("disponible", true)
                .get()
                .addOnSuccessListener(productosResult -> {
                    productosDisponibles.clear();
                    for (QueryDocumentSnapshot doc : productosResult) {
                        String nombre = doc.getString("nombre");
                        Double precio = doc.getDouble("precio");
                        if (nombre != null && precio != null) {
                            productosDisponibles.add(new ProductoItem(doc.getId(), nombre, precio));
                        }
                    }
                    if (productosDisponibles.isEmpty()) {
                        Toast.makeText(this, "No hay productos disponibles para esta tienda.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Una vez cargados los datos, mostramos el diálogo
                    crearYMostrarDialogo(productosDisponibles, idsClientes, nombresClientes);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar productos: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void crearYMostrarDialogo(List<ProductoItem> productos, List<String> idsClientes, List<String> nombresClientes) {
        // --- Crear vistas del diálogo por código ---
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);

        TextView tvCliente = new TextView(this);
        tvCliente.setText("Cliente:");
        tvCliente.setTextSize(16);
        Spinner spinnerClientes = new Spinner(this);
        ArrayAdapter<String> adapterClientes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombresClientes);
        adapterClientes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClientes.setAdapter(adapterClientes);

        TextView tvProductos = new TextView(this);
        tvProductos.setText("Productos: ninguno seleccionado");
        tvProductos.setTextSize(14);
        tvProductos.setPadding(0, 16, 0, 4);

        Button btnSeleccionarProductos = new Button(this);
        btnSeleccionarProductos.setText("Seleccionar productos");

        EditText etTotal = new EditText(this);
        etTotal.setHint("Total (se calcula automáticamente)");
        etTotal.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etTotal.setEnabled(false); // Lo deshabilitamos para que el usuario no lo toque

        TextView tvEstado = new TextView(this);
        tvEstado.setText("Estado:");
        tvEstado.setTextSize(16);
        tvEstado.setPadding(0, 8, 0, 0);
        Spinner spinnerEstado = new Spinner(this);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"pendiente", "en proceso", "completado", "cancelado"});
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        layout.addView(tvCliente);
        layout.addView(spinnerClientes);
        layout.addView(tvProductos);
        layout.addView(btnSeleccionarProductos);
        layout.addView(etTotal);
        layout.addView(tvEstado);
        layout.addView(spinnerEstado);

        // --- Lista para guardar los productos elegidos ---
        List<String> productosElegidos = new ArrayList<>();

        // --- Listener del botón de seleccionar productos ---
        btnSeleccionarProductos.setOnClickListener(v -> {
            // *** CAMBIO 3: Creamos el array de strings para el diálogo a partir de nuestra lista segura ***
            String[] nombresParaDialogo = productos.stream()
                    .map(ProductoItem::toString)
                    .toArray(String[]::new);
            boolean[] seleccionados = new boolean[productos.size()];

            new AlertDialog.Builder(this)
                    .setTitle("Selecciona productos")
                    .setMultiChoiceItems(nombresParaDialogo, seleccionados, (d, which, isChecked) -> seleccionados[which] = isChecked)
                    .setPositiveButton("Siguiente", (d, w) -> {
                        List<Integer> indicesSeleccionados = new ArrayList<>();
                        for (int i = 0; i < seleccionados.length; i++) {
                            if (seleccionados[i]) indicesSeleccionados.add(i);
                        }
                        if (indicesSeleccionados.isEmpty()) {
                            Toast.makeText(this, "Selecciona al menos un producto", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // *** CAMBIO 4: Pasamos la lista segura de productos al diálogo de cantidades ***
                        mostrarDialogoCantidades(indicesSeleccionados, productos, productosElegidos, tvProductos, etTotal);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // --- Construcción del diálogo principal ---
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Crear nuevo pedido")
                .setView(layout)
                .setPositiveButton("Crear", null) // Listener nulo para controlarlo manualmente
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        // Manejar el clic del botón "Crear" para validar antes de cerrar
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int posCliente = spinnerClientes.getSelectedItemPosition();
            if (posCliente == 0) {
                Toast.makeText(this, "Selecciona un cliente", Toast.LENGTH_SHORT).show();
                return;
            }
            if (productosElegidos.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un producto", Toast.LENGTH_SHORT).show();
                return;
            }
            String totalStr = etTotal.getText().toString().trim();
            if (TextUtils.isEmpty(totalStr)) {
                Toast.makeText(this, "El total no puede estar vacío.", Toast.LENGTH_SHORT).show();
                return;
            }

            double total = 0;
            try {
                total = Double.parseDouble(totalStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error en el formato del total.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> nuevoPedido = new HashMap<>();
            nuevoPedido.put("tiendaId", tiendaId);
            nuevoPedido.put("tiendaNombre", tiendaNombre);
            nuevoPedido.put("clienteId", idsClientes.get(posCliente));
            nuevoPedido.put("clienteNombre", nombresClientes.get(posCliente));
            nuevoPedido.put("productos", productosElegidos);
            nuevoPedido.put("total", total);
            nuevoPedido.put("estado", spinnerEstado.getSelectedItem().toString());
            nuevoPedido.put("fecha", new Date());

            db.collection("pedidos")
                    .add(nuevoPedido)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Pedido creado correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Cerrar solo si tiene éxito
                        cargarPedidos();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al crear: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }

    // *** CAMBIO 5: El método de cantidades ahora recibe la lista segura de productos ***
    private void mostrarDialogoCantidades(
            List<Integer> indices,
            List<ProductoItem> productos, // <-- Ahora recibimos la lista segura
            List<String> productosElegidos,
            TextView tvProductos,
            EditText etTotal) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);
        List<EditText> camposCantidad = new ArrayList<>();

        for (int i : indices) {
            ProductoItem producto = productos.get(i); // Obtenemos el objeto seguro
            TextView tvNombre = new TextView(this);
            tvNombre.setText(producto.toString()); // Usamos su toString()
            tvNombre.setTextSize(14);
            tvNombre.setPadding(0, 12, 0, 2);

            EditText etCantidad = new EditText(this);
            etCantidad.setHint("Cantidad");
            etCantidad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            etCantidad.setText("1");

            layout.addView(tvNombre);
            layout.addView(etCantidad);
            camposCantidad.add(etCantidad);
        }

        new AlertDialog.Builder(this)
                .setTitle("¿Cuántos de cada uno?")
                .setView(layout)
                .setPositiveButton("Confirmar", (d, w) -> {
                    productosElegidos.clear();
                    double total = 0;
                    StringBuilder sb = new StringBuilder();

                    for (int j = 0; j < indices.size(); j++) {
                        int idx = indices.get(j);
                        ProductoItem producto = productos.get(idx); // Obtenemos el objeto seguro de nuevo
                        int cantidad = 1;
                        try {
                            String cantStr = camposCantidad.get(j).getText().toString().trim();
                            cantidad = TextUtils.isEmpty(cantStr) ? 1 : Integer.parseInt(cantStr);
                        } catch (NumberFormatException e) {
                            cantidad = 1;
                        }

                        productosElegidos.add(producto.nombre + " x" + cantidad);
                        // *** CAMBIO 6: ACCESO SEGURO AL PRECIO SIN PARSEO ***
                        double subtotal = producto.precio * cantidad;
                        total += subtotal;
                        sb.append(producto.nombre).append(" x").append(cantidad)
                                .append(" = ").append(String.format("%.2f", subtotal)).append("€\n");
                    }

                    tvProductos.setText("Seleccionados:\n" + sb.toString().trim());
                    etTotal.setText(String.format("%.2f", total));
                })
                .setNegativeButton("Atrás", null)
                .show();
    }

    // --- Callback del adaptador ---
    @Override
    public void onPedidoClick(Pedido pedido) {
        pedidoSeleccionado = pedido;
        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
        Toast.makeText(this, "Pedido seleccionado: " + pedido.getClienteNombre(), Toast.LENGTH_SHORT).show();
    }
}