package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ModificarTiendaADM extends AppCompatActivity {

    private static final String TAG = "ModificarTiendaADM"; // Tag para los logs

    private EditText etNombre, etGestor, etCategorias;
    private TextView tvInformacion, tvPermisos;

    private ImageButton btnConfNombre, btnEmailCf, btnCategoriasCf, btnTipoUsuCf, btnPermisosCf, btnImagenCf;
    private ImageButton cerrarsesion, invernadero, configuracion;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String tiendaId;
    private String tiendaNombre;
    private String tiendaDescripcion;
    private String tiendaGestorId;
    private String tiendaImagenUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_tienda_adm);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Recoger datos del Intent
        tiendaId = getIntent().getStringExtra("tiendaId");
        tiendaNombre = getIntent().getStringExtra("tiendaNombre");
        tiendaDescripcion = getIntent().getStringExtra("tiendaDescripcion");
        tiendaGestorId = getIntent().getStringExtra("tiendaGestorId");
        tiendaImagenUrl = getIntent().getStringExtra("tiendaImagenUrl");

        // Log para verificar que los datos se reciben correctamente
        Log.d(TAG, "onCreate: ID recibido -> " + tiendaId);
        Log.d(TAG, "onCreate: Nombre recibido -> " + tiendaNombre);

        // Vincular vistas
        etNombre = findViewById(R.id.nombreUsuR);
        etGestor = findViewById(R.id.emailUSU);
        etCategorias = findViewById(R.id.Costes);
        tvInformacion = findViewById(R.id.estadoPedidoM);
        tvPermisos = findViewById(R.id.Permisos);


        btnConfNombre = findViewById(R.id.confnombre);
        btnEmailCf = findViewById(R.id.emailcf);
        btnCategoriasCf = findViewById(R.id.contrasefacf);
        btnTipoUsuCf = findViewById(R.id.tipoUsucf);
        btnPermisosCf = findViewById(R.id.imageButton5);


        cerrarsesion = findViewById(R.id.cerrarSesion);
        invernadero = findViewById(R.id.invernadero);
        configuracion = findViewById(R.id.ajustes);

        // Rellenar campos con los datos actuales
        etNombre.setText(tiendaNombre);
        etGestor.setText(tiendaGestorId);
        etCategorias.setText(tiendaDescripcion);
        tvInformacion.setText(tiendaNombre);
        tvPermisos.setText(tiendaGestorId);

        // Cargar imagen


        // ── Botón guardar nombre (CORREGIDO PARA QUE ABRA DIÁLOGO) ───────────────────────────────────────────────
        btnConfNombre.setOnClickListener(v ->
                mostrarDialogoCampo("Cambiar nombre", tiendaNombre, nuevoValor ->
                        guardarCampo("nombre", nuevoValor, () -> {
                            tiendaNombre = nuevoValor;
                            tvInformacion.setText(nuevoValor);
                            etNombre.setText(nuevoValor); // Actualizamos el EditText para que muestre el nuevo valor
                        })
                )
        );

        // ── Botón guardar gestor (CORREGIDO PARA QUE ABRA DIÁLOGO) ───────────────────────────────────────────────
        btnEmailCf.setOnClickListener(v ->
                mostrarDialogoCampo("Cambiar gestor (ID)", tiendaGestorId, nuevoValor ->
                        guardarCampo("gestorId", nuevoValor, () -> {
                            tiendaGestorId = nuevoValor;
                            tvPermisos.setText(nuevoValor);
                            etGestor.setText(nuevoValor); // Actualizamos el EditText
                        })
                )
        );

        // ── Botón guardar categorías/descripción (CORREGIDO PARA QUE ABRA DIÁLOGO) ───────────────────────────────
        btnCategoriasCf.setOnClickListener(v ->
                mostrarDialogoCampo("Cambiar descripción", tiendaDescripcion, nuevoValor ->
                        guardarCampo("descripcion", nuevoValor, () -> {
                            tiendaDescripcion = nuevoValor;
                            etCategorias.setText(nuevoValor); // Actualizamos el EditText
                        })
                )
        );

        // ── Botón información ──────────────────────────────────────────────────
        btnTipoUsuCf.setOnClickListener(v ->
                mostrarDialogoCampo("Cambiar información", tiendaNombre, nuevoValor ->
                        guardarCampo("nombre", nuevoValor, () -> {
                            tiendaNombre = nuevoValor;
                            tvInformacion.setText(nuevoValor);
                            etNombre.setText(nuevoValor);
                        })
                )
        );

        // ── Botón permisos/gestor ──────────────────────────────────────────────
        btnPermisosCf.setOnClickListener(v ->
                mostrarDialogoCampo("Cambiar gestor", tiendaGestorId, nuevoValor ->
                        guardarCampo("gestorId", nuevoValor, () -> {
                            tiendaGestorId = nuevoValor;
                            tvPermisos.setText(nuevoValor);
                            etGestor.setText(nuevoValor);
                        })
                )
        );


        // ── Cerrar sesión ──────────────────────────────────────────────────────
        cerrarsesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ModificarTiendaADM.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // ── Configuración ──────────────────────────────────────────────────────
        configuracion.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(ModificarTiendaADM.this);
            dialog.mostrar();
        });

        // ── Invernadero ────────────────────────────────────────────────────────
        invernadero.setOnClickListener(v -> {
            Intent intent = new Intent(ModificarTiendaADM.this, PantallaInvernadero.class);
            startActivity(intent);
        });
    }

    // ── Guardar un campo en Firestore ─────────────────────────────────────────
    private void guardarCampo(String campo, String valor, Runnable onExito) {
        Log.d(TAG, "Intentando guardar campo: " + campo + " con valor: '" + valor + "' en el documento ID: " + tiendaId);

        if (tiendaId == null || tiendaId.isEmpty()) {
            Log.e(TAG, "Error: tiendaId es nulo o vacío. No se puede guardar.");
            Toast.makeText(this, "Error interno: ID de tienda no encontrado.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> cambios = new HashMap<>();
        cambios.put(campo, valor);

        db.collection("tiendas").document(tiendaId)
                .update(cambios)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "¡Éxito! Campo '" + campo + "' actualizado en Firestore.");
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    if (onExito != null) onExito.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar el campo '" + campo + "' en Firestore.", e);
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ── Diálogo genérico para editar un campo ─────────────────────────────────
    private void mostrarDialogoCampo(String titulo, String valorActual, OnGuardarListener listener) {
        View view = getLayoutInflater().inflate(R.layout.dialogo_recuperar_contrasena, null);
        EditText etCampo = view.findViewById(R.id.etCorreo);
        etCampo.setText(valorActual);
        etCampo.setHint(titulo);

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String valor = etCampo.getText().toString().trim();
                    listener.onGuardar(valor);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    interface OnGuardarListener {
        void onGuardar(String valor);
    }
}