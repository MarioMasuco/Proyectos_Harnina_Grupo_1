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

public class PedidoModificarGestor extends AppCompatActivity {

    private EditText etCliente, etProductos, etCoste;
    private TextView titulo, tvEstado;
    private ImageButton invernadero, cerrarSesion, configuracion;
    private ImageButton confCliente, confProductos, confCoste;
    private int posicion;
    private String clienteActual, productosActual, costeActual, estadoActual, docId, idPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_modificar_gestor);

        titulo = findViewById(R.id.nombre);
        etCliente = findViewById(R.id.nombreUsuR);
        etProductos = findViewById(R.id.infoEditable);
        etCoste = findViewById(R.id.precioEditable);
        tvEstado = findViewById(R.id.cantidadEditable);
        confCliente = findViewById(R.id.confnombre);
        confProductos = findViewById(R.id.emailcf);
        confCoste = findViewById(R.id.contrasefacf);
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
        idPedido = getIntent().getStringExtra("idPedido");

        if (posicion != -1) {
            titulo.setText("Modificar Pedido");
            clienteActual = getIntent().getStringExtra("nombreCliente");
            productosActual = getIntent().getStringExtra("productos");
            costeActual = getIntent().getStringExtra("coste");
            estadoActual = getIntent().getStringExtra("estado");
            etCliente.setText(clienteActual);
            etProductos.setText(productosActual);
            etCoste.setText(costeActual);
            tvEstado.setText(estadoActual);
        } else {
            titulo.setText("Crear Pedido");
            clienteActual = "";
            productosActual = "";
            costeActual = "";
            estadoActual = "";
            etCliente.setText("");
            etProductos.setText("");
            etCoste.setText("");
            tvEstado.setText("");
        }

        confCliente.setOnClickListener(v -> {
            clienteActual = etCliente.getText().toString().trim();
            if (clienteActual.isEmpty()) {
                Toast.makeText(this, "El cliente no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Cliente confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confProductos.setOnClickListener(v -> {
            productosActual = etProductos.getText().toString().trim();
            if (productosActual.isEmpty()) {
                Toast.makeText(this, "Los productos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Productos confirmados", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });

        confCoste.setOnClickListener(v -> {
            costeActual = etCoste.getText().toString().trim();
            if (costeActual.isEmpty()) {
                Toast.makeText(this, "El coste no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Coste confirmado", Toast.LENGTH_SHORT).show();
            intentarGuardar();
        });
    }

    private void intentarGuardar() {
        if (clienteActual.isEmpty() || productosActual.isEmpty() || costeActual.isEmpty()) {
            return;
        }
        Intent resultado = new Intent();
        resultado.putExtra("nombreCliente", clienteActual);
        resultado.putExtra("productos", productosActual);
        resultado.putExtra("coste", costeActual);
        resultado.putExtra("estado", estadoActual);
        resultado.putExtra("idPedido", idPedido);
        resultado.putExtra("docId", docId);
        resultado.putExtra("posicion", posicion);
        setResult(RESULT_OK, resultado);
        finish();
    }
}