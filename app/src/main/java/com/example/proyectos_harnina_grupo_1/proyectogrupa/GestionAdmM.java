package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.firebase.auth.FirebaseAuth;

public class GestionAdmM extends AppCompatActivity {
    private Button tienda, usu;
    private ImageButton cerrarsesion, invernadero, configuracion;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_adm_m);
        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Vincular vistas
        tienda = findViewById(R.id.inicioLogin);
        usu = findViewById(R.id.usuarios);
        cerrarsesion = findViewById(R.id.cerrarSesion);
        invernadero = findViewById(R.id.invernadero);
        configuracion = findViewById(R.id.ajustes);

        // Botón tienda
        invernadero.setOnClickListener(v -> {
            Intent intent = new Intent(GestionAdmM.this, PantallaInvernadero.class);
            startActivity(intent);
        });
        tienda.setOnClickListener(v -> {
            Intent intent = new Intent(GestionAdmM.this, TiendaAdmin.class);
            startActivity(intent);
        });

        // Botón usuarios
        usu.setOnClickListener(v -> {
            Intent intent = new Intent(GestionAdmM.this, GestionUsu.class);
            startActivity(intent);
        });

        // Botón cerrar sesión
        cerrarsesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(GestionAdmM.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Botón configuración → abre diálogo de perfil
        configuracion.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(GestionAdmM.this);
            dialog.mostrar();
        });
    }
}
