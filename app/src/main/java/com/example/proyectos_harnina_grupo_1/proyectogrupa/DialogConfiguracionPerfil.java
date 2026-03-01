package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DialogConfiguracionPerfil {

    private final AppCompatActivity activity;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    // TextViews del card para actualizar en tiempo real
    private TextView tvNombre;
    private TextView tvEmail;
    private TextView tvTipoUsuario;
    private ImageView imgPerfil;

    public DialogConfiguracionPerfil(AppCompatActivity activity) {
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // Llamar a este método para vincular los TextViews del card
    public void setVistas(TextView tvNombre, TextView tvEmail, TextView tvTipoUsuario, ImageView imgPerfil) {
        this.tvNombre = tvNombre;
        this.tvEmail = tvEmail;
        this.tvTipoUsuario = tvTipoUsuario;
        this.imgPerfil = imgPerfil;
    }

    // Mostrar el diálogo principal de configuración
    public void mostrar() {
        View dialogView = activity.getLayoutInflater().inflate(R.layout.item_configuracion_admin, null);

        // Vincular elementos del layout
        ImageView btnFoto = dialogView.findViewById(R.id.btnEditarFotoPerfil);
        ImageView btnNombre = dialogView.findViewById(R.id.editarnombre);
        ImageView btnEmail = dialogView.findViewById(R.id.editaremail);
        ImageView btnContrasena = dialogView.findViewById(R.id.editarcontraseña);
        ImageView btnPermiso = dialogView.findViewById(R.id.editarpermiso);

        // Cargar datos actuales del usuario en el card
        cargarDatosUsuario(dialogView);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Botón editar foto
        btnFoto.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoFoto();
        });

        // Botón editar nombre
        btnNombre.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoCampo("Cambiar nombre", "Nuevo nombre", false, (nuevoValor) -> {
                guardarEnFirestore("nombre", nuevoValor);
                if (tvNombre != null) tvNombre.setText("Nombre usuario: " + nuevoValor);
            });
        });

        // Botón editar email
        btnEmail.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoCambioEmail();
        });

        // Botón editar contraseña
        btnContrasena.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoCambioContrasena();
        });

        // Botón editar permiso/rol
        btnPermiso.setOnClickListener(v -> {
            dialog.dismiss();
            mostrarDialogoCampo("Cambiar rol", "Nuevo rol (cliente, gestor, admin)", false, (nuevoValor) -> {
                String rolLimpio = nuevoValor.toLowerCase().trim();
                if (!rolLimpio.equals("cliente") && !rolLimpio.equals("gestor") && !rolLimpio.equals("admin")) {
                    Toast.makeText(activity, "Rol no válido. Usa: cliente, gestor o admin", Toast.LENGTH_SHORT).show();
                    return;
                }
                guardarEnFirestore("rol", rolLimpio);
                if (tvTipoUsuario != null) tvTipoUsuario.setText("Tipo de usuario: " + rolLimpio);
            });
        });

        dialog.show();
    }

    // ── Cargar datos del usuario en el diálogo ────────────────────────────────
    private void cargarDatosUsuario(View dialogView) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        TextView tvNombreCard = dialogView.findViewById(R.id.tvNombreUsuario);
        TextView tvEmailCard = dialogView.findViewById(R.id.tvEmailUsuario);
        TextView tvTipoCard = dialogView.findViewById(R.id.tvTipoUsuario);

        db.collection("usuarios").document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        if (tvNombreCard != null)
                            tvNombreCard.setText("Nombre usuario: " + document.getString("nombre"));
                        if (tvEmailCard != null)
                            tvEmailCard.setText("Email: " + document.getString("email"));
                        if (tvTipoCard != null)
                            tvTipoCard.setText("Tipo de usuario: " + document.getString("rol"));
                    }
                });
    }

    // ── Diálogo genérico para editar un campo de texto ────────────────────────
    private void mostrarDialogoCampo(String titulo, String hint, boolean esPassword, OnGuardarListener listener) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialogo_recuperar_contrasena, null);
        EditText etCampo = view.findViewById(R.id.etCorreo);
        etCampo.setHint(hint);
        if (esPassword) etCampo.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(activity)
                .setTitle(titulo)
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String valor = etCampo.getText().toString().trim();
                    if (TextUtils.isEmpty(valor)) {
                        Toast.makeText(activity, "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listener.onGuardar(valor);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Cambio de email ────────────────────────────────────────────────────────
    private void mostrarDialogoCambioEmail() {
        View view = activity.getLayoutInflater().inflate(R.layout.dialogo_recuperar_contrasena, null);
        EditText etNuevoEmail = view.findViewById(R.id.etCorreo);
        etNuevoEmail.setHint("Nuevo email");

        new AlertDialog.Builder(activity)
                .setTitle("Cambiar email")
                .setView(view)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nuevoEmail = etNuevoEmail.getText().toString().trim();
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(nuevoEmail).matches()) {
                        Toast.makeText(activity, "Email no válido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.updateEmail(nuevoEmail)
                                .addOnSuccessListener(unused -> {
                                    guardarEnFirestore("email", nuevoEmail);
                                    if (tvEmail != null) tvEmail.setText("Email: " + nuevoEmail);
                                    Toast.makeText(activity, "Email actualizado", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Cambio de contraseña (requiere contraseña actual) ─────────────────────
    private void mostrarDialogoCambioContrasena() {
        EditText etActual = new EditText(activity);
        etActual.setHint("Contraseña actual");
        etActual.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        EditText etNueva = new EditText(activity);
        etNueva.setHint("Nueva contraseña (mín. 6 caracteres)");
        etNueva.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(activity);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);
        layout.addView(etActual);
        layout.addView(etNueva);

        new AlertDialog.Builder(activity)
                .setTitle("Cambiar contraseña")
                .setView(layout)
                .setPositiveButton("Guardar", (d, w) -> {
                    String passActual = etActual.getText().toString().trim();
                    String passNueva = etNueva.getText().toString().trim();

                    if (TextUtils.isEmpty(passActual) || TextUtils.isEmpty(passNueva)) {
                        Toast.makeText(activity, "Rellena ambos campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (passNueva.length() < 6) {
                        Toast.makeText(activity, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.getEmail() != null) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passActual);
                        user.reauthenticate(credential)
                                .addOnSuccessListener(unused ->
                                        user.updatePassword(passNueva)
                                                .addOnSuccessListener(u ->
                                                        Toast.makeText(activity, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                                )
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                                )
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(activity, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Foto de perfil (abre galería) ─────────────────────────────────────────
    private void mostrarDialogoFoto() {
        new AlertDialog.Builder(activity)
                .setTitle("Foto de perfil")
                .setMessage("¿Desde dónde quieres subir la foto?")
                .setPositiveButton("Galería", (d, w) -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    activity.startActivityForResult(intent, 100);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ── Guardar campo en Firestore ────────────────────────────────────────────
    private void guardarEnFirestore(String campo, String valor) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("usuarios").document(user.getUid())
                .update(campo, valor)
                .addOnSuccessListener(unused ->
                        Toast.makeText(activity, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(activity, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    // ── Interface para callbacks ──────────────────────────────────────────────
    interface OnGuardarListener {
        void onGuardar(String valor);
    }
}