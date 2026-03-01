package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DialogUtils {

    public static void mostrarDialogoConfiguracion(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.item_configuracion, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        // ==================== Elementos principales ====================
        ImageView btnEditarPerfil = dialogView.findViewById(R.id.btnEditarPerfil);
        ImageView imgPerfil = dialogView.findViewById(R.id.imgPerfil);

        CardView cardView = (CardView) dialogView;
        LinearLayout rootLinear = (LinearLayout) cardView.getChildAt(0);
        LinearLayout filasLayout = (LinearLayout) rootLinear.getChildAt(1);

        LinearLayout nombreLayout = (LinearLayout) filasLayout.getChildAt(0);
        LinearLayout emailLayout = (LinearLayout) filasLayout.getChildAt(1);
        LinearLayout contrasenaLayout = (LinearLayout) filasLayout.getChildAt(2);
        LinearLayout tipoLayout = (LinearLayout) filasLayout.getChildAt(3);
        LinearLayout permisosLayout = (LinearLayout) filasLayout.getChildAt(4);

        TextView tvNombre = (TextView) nombreLayout.getChildAt(0);
        TextView tvEmail = (TextView) emailLayout.getChildAt(0);
        TextView tvContrasena = (TextView) contrasenaLayout.getChildAt(0);
        TextView tvTipo = (TextView) tipoLayout.getChildAt(0);
        TextView tvPermisos = (TextView) permisosLayout.getChildAt(0);

        // ==================== Cargar datos desde Firebase ====================
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvNombre.setText(document.getString("nombre"));
                        tvEmail.setText(document.getString("email"));
                        tvContrasena.setText(document.getString("contrasena"));
                        tvTipo.setText(document.getString("rol"));
                        tvPermisos.setText(document.getString("permisos"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show());

        // ==================== Hacer editable al tocar ====================
        View.OnClickListener makeEditable = v -> {
            TextView tv = (TextView) v;
            EditText edit = new EditText(context);
            edit.setText(tv.getText().toString());
            edit.setTextSize(15f);
            LinearLayout parent = (LinearLayout) tv.getParent();
            parent.removeView(tv);
            parent.addView(edit, 0);
        };

        tvNombre.setOnClickListener(makeEditable);
        tvEmail.setOnClickListener(makeEditable);
        tvContrasena.setOnClickListener(makeEditable);
        tvTipo.setOnClickListener(makeEditable);
        tvPermisos.setOnClickListener(makeEditable);

        // ==================== Guardar cambios ====================
        btnEditarPerfil.setOnClickListener(v -> {
            String nombre = ((EditText) nombreLayout.getChildAt(0)).getText().toString().trim();
            String email = ((EditText) emailLayout.getChildAt(0)).getText().toString().trim();
            String contrasena = ((EditText) contrasenaLayout.getChildAt(0)).getText().toString().trim();
            String tipo = ((EditText) tipoLayout.getChildAt(0)).getText().toString().trim();
            String permisos = ((EditText) permisosLayout.getChildAt(0)).getText().toString().trim();

            db.collection("usuarios").document(uid)
                    .update(
                            "nombre", nombre,
                            "email", email,
                            "contrasena", contrasena,
                            "rol", tipo,
                            "permisos", permisos
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e1 -> Toast.makeText(context, "Error al actualizar: " + e1.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
