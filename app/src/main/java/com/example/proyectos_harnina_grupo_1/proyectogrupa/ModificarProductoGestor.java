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

public class ModificarProductoGestor extends AppCompatActivity {

    private EditText etNombre, etInfo, etPrecio;
    private TextView titulo;
    private ImageButton invernadero, cerrarSesion, configuracion;
    private ImageButton confNombre, confInfo, confPrecio;
    private int posicion;
    private String nombreActual, infoActual, precioActual, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_producto_gestor);

        titulo = findViewById(R.id.nombre);
        etNombre = findViewById(R.id.nombreUsuR);
        etInfo = findViewById(R.id.infoEditable);
        etPrecio = findViewById(R.id.precioEditable);
        confNombre = findViewById(R.id.confnombre);
        confInfo = findViewById(R.id.emailcf);
        confPrecio = findViewById(R.id.contrasefacf);
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
            titulo.setText("Modificar Producto");
            nombreActual = getIntent().getStringExtra("nombre");
            infoActual = getIntent().getStringExtra("descripcion");
            precioActual = getIntent().getStringExtra("precio");
            etNombre.setText(nombreActual);
            etInfo.setText(infoActual);
            etPrecio.setText(precioActual);
        } else {
            titulo.setText("Crear Producto");
            nombreActual = "";
            infoActual = "";
            precioActual = "";
            etNombre.setText("");
            etInfo.setText("");
            etPrecio.setText("");
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

        confInfo.setOnClickListener(v -> {
            infoActual = etInfo.getText().toString().trim();
            if (infoActual.isEmpty()) {
                Toast.makeText(this, "La información no puede estar vacía", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Información confirmada", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confPrecio.setOnClickListener(v -> {
            precioActual = etPrecio.getText().toString().trim();
            if (precioActual.isEmpty()) {
                Toast.makeText(this, "El precio no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Precio confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });
    }

    private void intentarGuardar() {
        if (nombreActual.isEmpty() || infoActual.isEmpty() || precioActual.isEmpty()) {
            return;
        }
        Intent resultado = new Intent();
        resultado.putExtra("nombre", nombreActual);
        resultado.putExtra("descripcion", infoActual);
        resultado.putExtra("precio", precioActual);
        resultado.putExtra("docId", docId);
        resultado.putExtra("posicion", posicion);
        setResult(RESULT_OK, resultado);
        finish();
    }
}