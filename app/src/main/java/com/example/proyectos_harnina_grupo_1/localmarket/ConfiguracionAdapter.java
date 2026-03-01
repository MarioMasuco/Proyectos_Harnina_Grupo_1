package com.example.proyectos_harnina_grupo_1.localmarket;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ConfiguracionAdapter
        extends RecyclerView.Adapter<ConfiguracionAdapter.VH> {

    private final PantallaConfiguracion activity;
    private final Context context;
    private String nombre;
    private String email;
    private final String rol;
    private String fotoPerfil;
    private final List<String> historial;

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private VH viewHolder;

    public ConfiguracionAdapter(PantallaConfiguracion activity, String nombre,
                                String email, String rol, String fotoPerfil,
                                List<String> historial) {
        this.activity   = activity;
        this.context    = activity;
        this.nombre     = nombre;
        this.email      = email;
        this.rol        = rol;
        this.fotoPerfil = fotoPerfil;
        this.historial  = historial;
        this.db         = FirebaseFirestore.getInstance();
        this.mAuth      = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_configuracion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        viewHolder = h;

        Glide.with(context)
                .load((fotoPerfil != null && !fotoPerfil.isEmpty()) ? fotoPerfil : null)
                .circleCrop()
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.avatar_placeholder)
                .into(h.imgPerfil);

        h.txtNombre.setText("Nombre usuario: " + nombre);
        h.txtEmail.setText("Email: " + email);
        h.txtContrasena.setText("Contraseña: ••••••••");
        h.txtRol.setText("Tipo de usuario: " + rol);

        if (historial == null || historial.isEmpty()) {
            h.txtHistorial.setText("Sin pedidos registrados");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String linea : historial) {
                sb.append("• ").append(linea).append("\n");
            }
            h.txtHistorial.setText(sb.toString().trim());
        }

        h.btnEditarPerfil.setOnClickListener(v -> activity.abrirGaleria());

        h.btnEditarNombre.setOnClickListener(v -> {
            android.widget.EditText edt = new android.widget.EditText(context);
            edt.setHint("Nuevo nombre");
            edt.setText(nombre);

            new AlertDialog.Builder(context)
                    .setTitle("Editar nombre")
                    .setView(edt)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String nuevoNombre = edt.getText().toString().trim();
                        if (nuevoNombre.isEmpty()) return;
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) return;
                        db.collection("usuarios").document(user.getUid())
                                .update("nombre", nuevoNombre)
                                .addOnSuccessListener(unused -> {
                                    nombre = nuevoNombre;
                                    h.txtNombre.setText("Nombre usuario: " + nombre);
                                    Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        h.btnEditarEmail.setOnClickListener(v -> {
            android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(48, 16, 48, 0);

            android.widget.EditText edtPass = new android.widget.EditText(context);
            edtPass.setHint("Contraseña actual");
            edtPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

            android.widget.EditText edtNuevoEmail = new android.widget.EditText(context);
            edtNuevoEmail.setHint("Nuevo email");
            edtNuevoEmail.setText(email);

            layout.addView(edtPass);
            layout.addView(edtNuevoEmail);

            new AlertDialog.Builder(context)
                    .setTitle("Editar email")
                    .setView(layout)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String passActual = edtPass.getText().toString().trim();
                        String nuevoEmail = edtNuevoEmail.getText().toString().trim();
                        if (passActual.isEmpty() || nuevoEmail.isEmpty()) {
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null || user.getEmail() == null) return;
                        AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(), passActual);
                        user.reauthenticate(cred)
                                .addOnSuccessListener(unused ->
                                        user.updateEmail(nuevoEmail)
                                                .addOnSuccessListener(u -> {
                                                    db.collection("usuarios").document(user.getUid())
                                                            .update("email", nuevoEmail);
                                                    email = nuevoEmail;
                                                    h.txtEmail.setText("Email: " + email);
                                                    Toast.makeText(context, "Email actualizado", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                )
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        h.btnEditarContrasena.setOnClickListener(v -> {
            android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(48, 16, 48, 0);

            android.widget.EditText edtActual = new android.widget.EditText(context);
            edtActual.setHint("Contraseña actual");
            edtActual.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

            android.widget.EditText edtNueva = new android.widget.EditText(context);
            edtNueva.setHint("Nueva contraseña");
            edtNueva.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

            layout.addView(edtActual);
            layout.addView(edtNueva);

            new AlertDialog.Builder(context)
                    .setTitle("Cambiar contraseña")
                    .setView(layout)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String passActual = edtActual.getText().toString().trim();
                        String passNueva  = edtNueva.getText().toString().trim();
                        if (passActual.isEmpty() || passNueva.isEmpty()) {
                            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (passNueva.length() < 6) {
                            Toast.makeText(context, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null || user.getEmail() == null) return;
                        AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(), passActual);
                        user.reauthenticate(cred)
                                .addOnSuccessListener(unused ->
                                        user.updatePassword(passNueva)
                                                .addOnSuccessListener(u ->
                                                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                                )
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                )
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    public void actualizarFoto(String url) {
        fotoPerfil = url;
        if (viewHolder != null) {
            Glide.with(context)
                    .load(url)
                    .circleCrop()
                    .into(viewHolder.imgPerfil);
        }
    }

    @Override
    public int getItemCount() { return 1; }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgPerfil, btnEditarPerfil, btnEditarNombre, btnEditarEmail, btnEditarContrasena;
        TextView txtNombre, txtEmail, txtContrasena, txtRol, txtHistorial;

        VH(@NonNull View v) {
            super(v);
            imgPerfil           = v.findViewById(R.id.imgPerfil);
            btnEditarPerfil     = v.findViewById(R.id.btnEditarPerfil);
            btnEditarNombre     = v.findViewById(R.id.btnEditarNombre);
            btnEditarEmail      = v.findViewById(R.id.btnEditarEmail);
            btnEditarContrasena = v.findViewById(R.id.btnEditarContrasena);
            txtNombre           = v.findViewWithTag("txtNombre");
            txtEmail            = v.findViewWithTag("txtEmail");
            txtContrasena       = v.findViewWithTag("txtContrasena");
            txtRol              = v.findViewWithTag("txtRol");
            txtHistorial        = v.findViewWithTag("txtHistorial");
        }
    }
}