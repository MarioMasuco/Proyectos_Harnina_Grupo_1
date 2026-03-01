package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    private EditText email, nombreUsu, contrasena, contrasena2;
    private RadioButton cliente, gestor;
    private Button registrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        email = findViewById(R.id.emailUSUR);
        nombreUsu = findViewById(R.id.emailUSU);
        contrasena = findViewById(R.id.contasenaUSUR);
        contrasena2 = findViewById(R.id.repetirContrasena);
        cliente = findViewById(R.id.clienteopcion);
        gestor = findViewById(R.id.gestoropcion);
        registrar = findViewById(R.id.registrar2);

        registrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String emailStr = email.getText().toString().trim();
        String nombreStr = nombreUsu.getText().toString().trim();
        String passStr = contrasena.getText().toString().trim();
        String pass2Str = contrasena2.getText().toString().trim();

        // ── Validaciones ──────────────────────────────────────────────────────
        if (TextUtils.isEmpty(emailStr)) {
            email.setError("El email es obligatorio");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Introduce un email válido (ejemplo: usuario@dominio.com)");
            return;
        }
        if (TextUtils.isEmpty(nombreStr)) {
            nombreUsu.setError("El nombre es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(passStr)) {
            contrasena.setError("La contraseña es obligatoria");
            return;
        }
        if (passStr.length() < 6) {
            contrasena.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (!passStr.equals(pass2Str)) {
            contrasena2.setError("Las contraseñas no coinciden");
            return;
        }
        if (!cliente.isChecked() && !gestor.isChecked()) {
            Toast.makeText(this, "Selecciona un tipo de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determinar el rol seleccionado
        String rol = cliente.isChecked() ? "cliente" : "gestor";

        // ── Crear usuario en Firebase Authentication ──────────────────────────
        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // ── Guardar datos en Firestore ─────────────────────────────────
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("nombre", nombreStr);
                    usuario.put("email", emailStr);
                    usuario.put("rol", rol);
                    usuario.put("telefono", "");
                    usuario.put("direccion", "");
                    usuario.put("permisos", "");
                    usuario.put("puesto", "");
                    usuario.put("imagenUrl", "https://cdn-icons-png.flaticon.com/512/149/149071.png");

                    // Si es cliente, añadir preferencias por defecto
                    if (rol.equals("cliente")) {
                        Map<String, Object> preferencias = new HashMap<>();
                        preferencias.put("categoriasFavoritas", new java.util.ArrayList<>());
                        preferencias.put("notificaciones", true);
                        usuario.put("preferencias", preferencias);
                    }

                    // Guardar en la colección "usuarios" usando el UID como ID del documento
                    db.collection("usuarios").document(uid)
                            .set(usuario)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Registro.this, IniciSesion.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}