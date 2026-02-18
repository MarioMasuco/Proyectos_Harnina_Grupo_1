package com.example.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IniciSesion extends AppCompatActivity {
private Button registrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inici_sesion);
        registrar=findViewById(R.id.registrar);


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), Registro.class);
                startActivity(intent);
            }
        });
    }
}

/*
val btnMostrarDialogo: Button = findViewById(R.id.btnMostrarDialogo)
        btnMostrarDialogo.setOnClickListener {
    mostrarDialogoRecuperarContrasena()
}
    }

private fun mostrarDialogoRecuperarContrasena() {
    // 1. Inflar el layout personalizado
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.dialog_recuperar_contrasena, null)

    // 2. Obtener una referencia al EditText del layout inflado
    val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)

            // 3. Crear el AlertDialog
            val builder = AlertDialog.Builder(this)
    builder.setView(dialogView) // Establecer la vista personalizada

    // Configurar el título
    builder.setTitle("Recuperar Contraseña")

    // Configurar el botón positivo (Enviar)
    builder.setPositiveButton("Enviar") { dialog, which ->
            val correoIngresado = etCorreo.text.toString().trim()

        // Aquí puedes agregar la lógica para validar el correo
        if (correoIngresado.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su correo", Toast.LENGTH_SHORT).show()
            // Opcional: mantener el diálogo abierto si hay error
            // return@setPositiveButton
        } else {
            // Lógica para enviar el correo de recuperación
            Toast.makeText(this, "Enviando instrucciones a: $correoIngresado", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Cierra el diálogo
        }
    }

    // Configurar el botón negativo (Cancelar)
    builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.cancel() // Cierra el diálogo
    }

    // 4. Crear y mostrar el diálogo
    val dialog: AlertDialog = builder.create()
    dialog.show()

    // (Opcional) Cambiar el color del botón "Enviar" a azul
    dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(getColor(R.color.blue_500))
}
}*/