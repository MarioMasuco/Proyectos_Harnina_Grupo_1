package com.example.proyectos_harnina_grupo_1.proyectogrupa;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaCatalogo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class IniciSesion extends AppCompatActivity {

    private Button registrar, inicio;
    private EditText email, contrasena;
    private CheckBox recordarContra;
    private TextView recuperacion;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_RECORDAR = "recordar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inici_sesion);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Vincular vistas
        registrar = findViewById(R.id.registrar);
        inicio = findViewById(R.id.inicioLogin);
        email = findViewById(R.id.emailLogin);
        contrasena = findViewById(R.id.contraseñaLogin);
        recordarContra = findViewById(R.id.recondar);
        recuperacion = findViewById(R.id.productoslist);

        // Cargar email guardado si el usuario marcó "recordar"
        if (prefs.getBoolean(KEY_RECORDAR, false)) {
            email.setText(prefs.getString(KEY_EMAIL, ""));
            recordarContra.setChecked(true);
        }

        // Botón ir a Registro
        registrar.setOnClickListener(v -> {
            Intent intent = new Intent(IniciSesion.this, Registro.class);
            startActivity(intent);
        });

        // Botón iniciar sesión
        inicio.setOnClickListener(v -> iniciarSesion());

        // TextView recuperar contraseña
        recuperacion.setOnClickListener(v -> mostrarDialogoRecuperacion());
    }

    private void iniciarSesion() {
        String emailStr = email.getText().toString().trim();
        String passStr = contrasena.getText().toString().trim();

        // ── Validaciones ──────────────────────────────────────────────────────
        if (TextUtils.isEmpty(emailStr)) {
            email.setError("El email es obligatorio");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Introduce un email válido");
            return;
        }
        if (TextUtils.isEmpty(passStr)) {
            contrasena.setError("La contraseña es obligatoria");
            return;
        }

        // ── Guardar o borrar email según el checkbox ───────────────────────────
        SharedPreferences.Editor editor = prefs.edit();
        if (recordarContra.isChecked()) {
            editor.putString(KEY_EMAIL, emailStr);
            editor.putBoolean(KEY_RECORDAR, true);
        } else {
            editor.remove(KEY_EMAIL);
            editor.putBoolean(KEY_RECORDAR, false);
        }
        editor.apply();

        // ── Login con Firebase Auth ───────────────────────────────────────────
        mAuth.signInWithEmailAndPassword(emailStr, passStr)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Obtener el rol del usuario desde Firestore
                    db.collection("usuarios").document(uid)
                            .get()
                            .addOnSuccessListener(document -> {
                                if (document.exists()) {
                                    String rol = document.getString("rol");
                                    redirigirPorRol(rol);
                                } else {
                                    Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                });
    }

    private void redirigirPorRol(String rol) {
        Intent intent;
        switch (rol) {
            case "admin":
                intent = new Intent(IniciSesion.this, GestionAdmM.class);
                break;
            case "gestor":
                intent = new Intent(IniciSesion.this, GestionGestorM.class);
                break;
            case "cliente":
            default:
                intent = new Intent(IniciSesion.this, PantallaCatalogo.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void mostrarDialogoRecuperacion() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogo_recuperar_contrasena, null);
        EditText etCorreo = dialogView.findViewById(R.id.etCorreo);
        Button btnEnviar = dialogView.findViewById(R.id.inicio2);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnEnviar.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();

            if (TextUtils.isEmpty(correo)) {
                etCorreo.setError("Introduce tu correo");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.setError("Introduce un email válido");
                return;
            }

            mAuth.sendPasswordResetEmail(correo)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Correo de recuperación enviado a " + correo, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        dialog.show();
    }
}

