package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModificarPedido extends AppCompatActivity {

    // --- VISTAS ---
    private TextView tvTitulo;
    private EditText etNombreCliente, etCosteTotal;
    private TextView tvEstado, tvListaProductos;
    private ImageButton btnCambiarCliente, btnCambiarCoste, btnCambiarEstado, btnCambiarProductos;
    private ImageButton btnAjustes, btnCerrarSesion, btnInvernadero;

    // --- DATOS Y FIREBASE ---
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String pedidoId;
    private Pedido pedidoActual; // Guardaremos el pedido cargado aquí
    private String tiendaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_pedido);

        // Inicializar Firebase y vistas
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        inicializarVistas();

        // Obtener el ID del pedido desde el Intent
        pedidoId = getIntent().getStringExtra("PEDIDO_ID");
        tiendaId = getIntent().getStringExtra("TIENDA_ID");

        if (pedidoId == null || tiendaId == null) {
            Toast.makeText(this, "Error: No se pudo identificar el pedido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar listeners
        setupBotones();

        // Cargar los datos del pedido
        cargarDatosPedido();
    }

    private void inicializarVistas() {
        // Mapeamos los IDs de tu XML a nuestras variables
        tvTitulo = findViewById(R.id.nombre);
        etNombreCliente = findViewById(R.id.nombreUsuR);
        etCosteTotal = findViewById(R.id.Costes);
        tvEstado = findViewById(R.id.estadoPedidoM);
        tvListaProductos = findViewById(R.id.Permisos); // Reutilizamos este campo para la lista de productos

        btnCambiarCliente = findViewById(R.id.confnombre);
        btnCambiarCoste = findViewById(R.id.contrasefacf);
        btnCambiarEstado = findViewById(R.id.tipoUsucf);
        // El botón de 'emailcf' no lo usaremos, puedes ocultarlo si quieres
        findViewById(R.id.emailcf).setVisibility(View.GONE);
        findViewById(R.id.emailUSU).setVisibility(View.GONE);
        findViewById(R.id.email).setVisibility(View.GONE);
        // El botón 'imageButton5' tampoco lo usamos
        findViewById(R.id.imageButton5).setVisibility(View.GONE);

        btnAjustes = findViewById(R.id.ajustes);
        btnCerrarSesion = findViewById(R.id.cerrarSesion);
        btnInvernadero = findViewById(R.id.invernadero);

        // Cambiamos el texto de las etiquetas para que tenga más sentido
        ((TextView)findViewById(R.id.contraseña)).setText("Total Coste :");
        ((TextView)findViewById(R.id.tipoUsu)).setText("Estado :");
        ((TextView)findViewById(R.id.textView13)).setText("Productos :");
    }

    private void setupBotones() {
        // Botones de navegación (similares a los de otras pantallas)
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ModificarPedido.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(ModificarPedido.this, PantallaInvernadero.class))
        );

        btnAjustes.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(ModificarPedido.this);
            dialog.mostrar();
        });

        // --- LISTENERS PARA MODIFICAR CAMPOS ---
        btnCambiarCliente.setOnClickListener(v -> mostrarDialogoCambiarCliente());
        btnCambiarCoste.setOnClickListener(v -> mostrarDialogoCambiarProductos()); // Es más lógico cambiar el coste modificando los productos
        btnCambiarEstado.setOnClickListener(v -> mostrarDialogoCambiarEstado());
    }

    private void cargarDatosPedido() {
        db.collection("pedidos").document(pedidoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pedidoActual = documentSnapshot.toObject(Pedido.class);
                        pedidoActual.setId(documentSnapshot.getId());

                        // Rellenar los campos con los datos del pedido
                        tvTitulo.setText("Modificar Pedido de " + pedidoActual.getClienteNombre());
                        etNombreCliente.setText(pedidoActual.getClienteNombre());
                        etCosteTotal.setText(String.format("%.2f", pedidoActual.getTotal()));
                        tvEstado.setText(pedidoActual.getEstado());

                        // Mostrar la lista de productos
                        String productosStr = TextUtils.join("\n", pedidoActual.getProductos());
                        tvListaProductos.setText(productosStr);
                    } else {
                        Toast.makeText(this, "El pedido no existe.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar el pedido: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    // --- DIÁLOGOS DE MODIFICACIÓN ---

    private void mostrarDialogoCambiarEstado() {
        String[] estados = {"pendiente", "en proceso", "completado", "cancelado"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar estado del pedido");

        // Creamos un Spinner para seleccionar el nuevo estado
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Seleccionamos el estado actual en el spinner
        for (int i = 0; i < estados.length; i++) {
            if (estados[i].equals(pedidoActual.getEstado())) {
                spinner.setSelection(i);
                break;
            }
        }

        builder.setView(spinner);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoEstado = spinner.getSelectedItem().toString();
            actualizarCampo("estado", nuevoEstado);
            tvEstado.setText(nuevoEstado);
            pedidoActual.setEstado(nuevoEstado); // Actualizamos el objeto en memoria
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoCambiarCliente() {
        List<String> idsClientes = new ArrayList<>();
        List<String> nombresClientes = new ArrayList<>();

        db.collection("usuarios").whereEqualTo("rol", "cliente").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        idsClientes.add(doc.getId());
                        nombresClientes.add(doc.getString("nombre"));
                    }
                    if (nombresClientes.isEmpty()) {
                        Toast.makeText(this, "No hay clientes disponibles.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Crear el diálogo con el Spinner de clientes
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Cambiar cliente");

                    Spinner spinner = new Spinner(this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    // Seleccionar el cliente actual
                    for (int i = 0; i < nombresClientes.size(); i++) {
                        if (nombresClientes.get(i).equals(pedidoActual.getClienteNombre())) {
                            spinner.setSelection(i);
                            break;
                        }
                    }

                    builder.setView(spinner);

                    builder.setPositiveButton("Guardar", (dialog, which) -> {
                        int posicion = spinner.getSelectedItemPosition();
                        String nuevoId = idsClientes.get(posicion);
                        String nuevoNombre = nombresClientes.get(posicion);

                        // Actualizar ambos campos en Firestore
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("clienteId", nuevoId);
                        updates.put("clienteNombre", nuevoNombre);

                        db.collection("pedidos").document(pedidoId).update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Cliente actualizado.", Toast.LENGTH_SHORT).show();
                                    etNombreCliente.setText(nuevoNombre);
                                    pedidoActual.setClienteNombre(nuevoNombre);
                                    tvTitulo.setText("Modificar Pedido de " + nuevoNombre);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar cliente.", Toast.LENGTH_SHORT).show());
                    });

                    builder.setNegativeButton("Cancelar", null);
                    builder.show();
                });
    }

    private void mostrarDialogoCambiarProductos() {
        // Este diálogo es complejo, pero es la forma correcta de hacerlo.
        // Reutiliza la lógica de crear un pedido, pero precargando los datos actuales.
        Toast.makeText(this, "Función de modificar productos en desarrollo. Es compleja.", Toast.LENGTH_LONG).show();
        // NOTA: Implementar esto requeriría una lógica similar a la de crear un pedido,
        // pero parseando la lista de productos actual para precargar las cantidades.
        // Por ahora, lo dejamos como un placeholder.
    }

    // Método genérico para actualizar un campo
    private void actualizarCampo(String campo, Object valor) {
        db.collection("pedidos").document(pedidoId).update(campo, valor)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Guardado.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar.", Toast.LENGTH_SHORT).show());
    }
}