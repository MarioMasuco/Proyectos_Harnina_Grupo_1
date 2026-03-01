// Archivo: GestionUsu.java
package com.example.proyectos_harnina_grupo_1.proyectogrupa;

// ... (importaciones) ...
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

public class GestionUsu extends AppCompatActivity {

    private Button eliminar, crear, modificar;
    private ImageButton cerrarsesion, invernadero, configuracion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recy;

    // Esta lista ahora es solo temporal en la Activity.
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private UsuarioAdapter adapter;
    private Usuario usuarioSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usu);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        eliminar = findViewById(R.id.eliminar);
        crear = findViewById(R.id.crear);
        modificar = findViewById(R.id.modificar);
        cerrarsesion = findViewById(R.id.cerrarSesion);
        invernadero = findViewById(R.id.invernadero);
        configuracion = findViewById(R.id.ajustes);
        recy = findViewById(R.id.recyUSU);

        // ¡CAMBIO CLAVE! El constructor del adapter ya no necesita la lista.
        adapter = new UsuarioAdapter(this, usuario -> {
            usuarioSeleccionado = usuario;
            Toast.makeText(this, "Seleccionado: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
        });
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(adapter);

        // Cargar usuarios de Firestore
        cargarUsuarios();

        // ... (el resto de tus onClickListeners se quedan igual) ...
        crear.setOnClickListener(v -> mostrarDialogoCrear());
        eliminar.setOnClickListener(v -> {
            if (usuarioSeleccionado == null) {
                Toast.makeText(this, "Selecciona un usuario primero", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogoEliminar();
        });
        modificar.setOnClickListener(v -> {
            if (usuarioSeleccionado == null) {
                Toast.makeText(this, "Selecciona un usuario primero", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarDialogoModificar();
        });
        invernadero.setOnClickListener(v -> {
            Intent intent = new Intent(GestionUsu.this, PantallaInvernadero.class);
            startActivity(intent);
        });
        cerrarsesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(GestionUsu.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        configuracion.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(GestionUsu.this);
            dialog.mostrar();
        });
    }

    // ── MÉTODO CLAVE MODIFICADO ─────────────────────────────
    private void cargarUsuarios() {
        db.collection("usuarios")
                .get()
                .addOnSuccessListener(result -> {
                    Log.d("GestionUsu", "Carga exitosa. Documentos encontrados: " + result.size());

                    // 1. Creamos una lista NUEVA y temporal para esta carga
                    List<Usuario> usuariosCargados = new ArrayList<>();

                    // 2. Llenamos esa lista nueva
                    for (QueryDocumentSnapshot doc : result) {
                        Usuario u = new Usuario(
                                doc.getId(),
                                doc.getString("nombre"),
                                doc.getString("email"),
                                doc.getString("rol"),
                                doc.getString("permisos"),
                                doc.getString("estado")
                        );
                        u.setImagenUrl(doc.getString("imagenUrl"));
                        usuariosCargados.add(u);
                    }

                    // 3. Pasamos la lista NUEVA al adapter
                    adapter.actualizarLista(usuariosCargados);

                    Log.d("GestionUsu", "Lista de " + usuariosCargados.size() + " usuarios pasada al adaptador.");
                })
                .addOnFailureListener(e -> {
                    Log.e("GestionUsu", "Error al cargar usuarios", e);
                    Toast.makeText(this, "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ── Diálogo CREAR usuario ─────────────────────────────────────────────────
    private void mostrarDialogoCrear() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
        EditText etEmail = new EditText(this);
        etEmail.setHint("Email");
        etEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText etContrasena = new EditText(this);
        etContrasena.setHint("Contraseña (mín. 6 caracteres)");
        etContrasena.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Spinner para el rol
        Spinner spinnerRol = new Spinner(this);
        ArrayAdapter<String> adapterRol = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"cliente", "gestor", "admin"});
        adapterRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterRol);

        layout.addView(etNombre);
        layout.addView(etEmail);
        layout.addView(etContrasena);
        layout.addView(spinnerRol);

        new AlertDialog.Builder(this)
                .setTitle("Crear nuevo usuario")
                .setView(layout)
                .setPositiveButton("Crear", (d, w) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String pass = etContrasena.getText().toString().trim();
                    String rol = spinnerRol.getSelectedItem().toString();

                    if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                        Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pass.length() < 6) {
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    crearUsuarioEnFirebase(nombre, email, pass, rol);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearUsuarioEnFirebase(String nombre, String email, String pass, String rol) {
        // Guardamos el usuario actual para restaurarlo después
        FirebaseAuth authTemp = FirebaseAuth.getInstance();

        authTemp.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> nuevoUsuario = new HashMap<>();
                    nuevoUsuario.put("nombre", nombre);
                    nuevoUsuario.put("email", email);
                    nuevoUsuario.put("rol", rol);
                    nuevoUsuario.put("permisos", "");
                    nuevoUsuario.put("telefono", "");
                    nuevoUsuario.put("direccion", "");
                    nuevoUsuario.put("estado", "confirmar");

                    db.collection("usuarios").document(uid)
                            .set(nuevoUsuario)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                                cargarUsuarios();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al crear usuario: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ── Diálogo ELIMINAR usuario ──────────────────────────────────────────────
    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de que quieres eliminar a " + usuarioSeleccionado.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    db.collection("usuarios").document(usuarioSeleccionado.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                usuarioSeleccionado = null;
                                cargarUsuarios();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Diálogo MODIFICAR usuario ─────────────────────────────────────────────
    private void mostrarDialogoModificar() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nombre");
        etNombre.setText(usuarioSeleccionado.getNombre());

        // Spinner para el rol
        Spinner spinnerRol = new Spinner(this);
        ArrayAdapter<String> adapterRol = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"cliente", "gestor", "admin"});
        adapterRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterRol);

        // Seleccionar el rol actual
        String rolActual = usuarioSeleccionado.getRol();
        if (rolActual != null) {
            switch (rolActual) {
                case "gestor": spinnerRol.setSelection(1); break;
                case "admin": spinnerRol.setSelection(2); break;
                default: spinnerRol.setSelection(0); break;
            }
        }

        // Spinner para el estado
        Spinner spinnerEstado = new Spinner(this);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"activo", "bloqueado", "confirmar"});
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        // Seleccionar el estado actual
        String estadoActual = usuarioSeleccionado.getEstado();
        if (estadoActual != null) {
            switch (estadoActual) {
                case "bloqueado": spinnerEstado.setSelection(1); break;
                case "confirmar": spinnerEstado.setSelection(2); break;
                default: spinnerEstado.setSelection(0); break;
            }
        }

        layout.addView(etNombre);
        layout.addView(spinnerRol);
        layout.addView(spinnerEstado);

        new AlertDialog.Builder(this)
                .setTitle("Modificar usuario")
                .setView(layout)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nuevoNombre = etNombre.getText().toString().trim();
                    String nuevoRol = spinnerRol.getSelectedItem().toString();
                    String nuevoEstado = spinnerEstado.getSelectedItem().toString();

                    if (TextUtils.isEmpty(nuevoNombre)) {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> cambios = new HashMap<>();
                    cambios.put("nombre", nuevoNombre);
                    cambios.put("rol", nuevoRol);
                    cambios.put("estado", nuevoEstado);

                    db.collection("usuarios").document(usuarioSeleccionado.getId())
                            .update(cambios)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                                usuarioSeleccionado = null;
                                cargarUsuarios();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al modificar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}