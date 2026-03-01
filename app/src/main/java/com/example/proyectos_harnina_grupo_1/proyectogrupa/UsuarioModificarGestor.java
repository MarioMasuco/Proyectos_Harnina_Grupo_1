package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;

public class UsuarioModificarGestor extends AppCompatActivity {

    private EditText etNombre, etEmail, etContrasena;
    private TextView titulo, tvTipoUsuario, tvPermisos;
    private ImageButton invernadero, cerrarSesion, configuracion;
    private ImageButton confNombre, confEmail, confContrasena;
    private int posicion;
    private String nombreActual, emailActual, contrasenaActual, tipoUsuarioActual, docId, idCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_modificar_gestor);

        titulo = findViewById(R.id.nombre);
        etNombre = findViewById(R.id.nombreUsuR);
        etEmail = findViewById(R.id.infoEditable);
        etContrasena = findViewById(R.id.contasenaUSUR);
        tvTipoUsuario = findViewById(R.id.cantidadEditable);
        tvPermisos = findViewById(R.id.categoriaEditable);
        confNombre = findViewById(R.id.confnombre);
        confEmail = findViewById(R.id.emailcf);
        confContrasena = findViewById(R.id.contrasefacf);
        invernadero = findViewById(R.id.imageButton4);
        cerrarSesion = findViewById(R.id.imageButton);
        configuracion = findViewById(R.id.imageButton2);

        invernadero.setOnClickListener(v -> startActivity(new Intent(this, PantallaInvernadero.class)));
        cerrarSesion.setOnClickListener(v -> startActivity(new Intent(this, IniciSesion.class)));
        configuracion.setOnClickListener(v -> {
            DialogUtils.mostrarDialogoConfiguracion(this);
        });
        posicion = getIntent().getIntExtra("posicion", -1);
        docId = getIntent().getStringExtra("docId");
        idCliente = getIntent().getStringExtra("idCliente");

        if (posicion != -1) {
            titulo.setText("Modificar Usuario");
            nombreActual = getIntent().getStringExtra("nombre");
            emailActual = getIntent().getStringExtra("descripcion");
            contrasenaActual = "";
            tipoUsuarioActual = getIntent().getStringExtra("tipoUsuario");
            etNombre.setText(nombreActual);
            etEmail.setText(emailActual);
            etContrasena.setText("");
            tvTipoUsuario.setText(tipoUsuarioActual);
            tvPermisos.setText(idCliente);
        } else {
            titulo.setText("Crear Usuario");
            nombreActual = "";
            emailActual = "";
            contrasenaActual = "";
            tipoUsuarioActual = "";
            etNombre.setText("");
            etEmail.setText("");
            etContrasena.setText("");
            tvTipoUsuario.setText("");
            tvPermisos.setText("");
        }

        confNombre.setOnClickListener(v -> {
            nombreActual = etNombre.getText().toString().trim();
            if (nombreActual.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Nombre confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confEmail.setOnClickListener(v -> {
            emailActual = etEmail.getText().toString().trim();
            if (emailActual.isEmpty()) {
                Toast.makeText(this, "El email no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Email confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confContrasena.setOnClickListener(v -> {
            contrasenaActual = etContrasena.getText().toString().trim();
            if (contrasenaActual.isEmpty()) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Contraseña confirmada", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });
    }

    private void intentarGuardar() {
        if (nombreActual.isEmpty() || emailActual.isEmpty() || contrasenaActual.isEmpty()) {
            return;
        }
        Intent resultado = new Intent();
        resultado.putExtra("nombre", nombreActual);
        resultado.putExtra("descripcion", emailActual);
        resultado.putExtra("tipoUsuario", tipoUsuarioActual);
        resultado.putExtra("idCliente", idCliente);
        resultado.putExtra("docId", docId);
        resultado.putExtra("posicion", posicion);
        setResult(RESULT_OK, resultado);
        finish();
    }
}