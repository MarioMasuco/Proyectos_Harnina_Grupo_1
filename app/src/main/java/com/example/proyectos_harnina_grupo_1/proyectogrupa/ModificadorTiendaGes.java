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

public class ModificadorTiendaGes extends AppCompatActivity {

    private EditText etNombre, etDescripcion, etPropietario;
    private TextView titulo;
    private ImageButton invernadero, cerrarSesion, configuracion;
    private ImageButton confNombre, confDescripcion, confPropietario;
    private int posicion;
    private String nombreActual, descripcionActual, propietarioActual, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificador_tienda_ges);

        titulo = findViewById(R.id.nombre);
        etNombre = findViewById(R.id.nombreUsuR);
        etDescripcion = findViewById(R.id.infoEditable);
        etPropietario = findViewById(R.id.precioEditable);
        confNombre = findViewById(R.id.confnombre);
        confDescripcion = findViewById(R.id.emailcf);
        confPropietario = findViewById(R.id.contrasefacf);
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

        if (posicion != -1) {
            titulo.setText("Modificar Tienda");
            nombreActual = getIntent().getStringExtra("nombre");
            descripcionActual = getIntent().getStringExtra("descripcion");
            propietarioActual = getIntent().getStringExtra("propietario");
            etNombre.setText(nombreActual);
            etDescripcion.setText(descripcionActual);
            etPropietario.setText(propietarioActual);
        } else {
            titulo.setText("Crear Tienda");
            nombreActual = "";
            descripcionActual = "";
            propietarioActual = "";
            etNombre.setText("");
            etDescripcion.setText("");
            etPropietario.setText("");
        }

        // Cada botón confirma su campo y guarda si todos están rellenos
        confNombre.setOnClickListener(v -> {
            nombreActual = etNombre.getText().toString().trim();
            if (nombreActual.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Nombre confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confDescripcion.setOnClickListener(v -> {
            descripcionActual = etDescripcion.getText().toString().trim();
            if (descripcionActual.isEmpty()) {
                Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Descripción confirmada", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confPropietario.setOnClickListener(v -> {
            propietarioActual = etPropietario.getText().toString().trim();
            if (propietarioActual.isEmpty()) {
                Toast.makeText(this, "El propietario no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Propietario confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });
    }

    private void intentarGuardar() {
        if (nombreActual.isEmpty() || descripcionActual.isEmpty() || propietarioActual.isEmpty()) {
            return; // Esperar a que se confirmen todos los campos
        }

        Intent resultado = new Intent();
        resultado.putExtra("nombre", nombreActual);
        resultado.putExtra("descripcion", descripcionActual);
        resultado.putExtra("propietario", propietarioActual);
        resultado.putExtra("docId", docId);
        resultado.putExtra("posicion", posicion);
        setResult(RESULT_OK, resultado);
        finish();
    }
}