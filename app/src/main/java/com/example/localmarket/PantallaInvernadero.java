package com.example.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PantallaInvernadero extends AppCompatActivity {

    // 🔘 Botones del menú inferior
    private ImageView btnFavorito;
    private ImageView btnInvernadero;
    private ImageView btnAjuste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_invernadero);

        inicializarVistas();
        configurarEventos();
    }

    /**
     * Inicializa las vistas
     */
    private void inicializarVistas() {
        btnFavorito = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjuste = findViewById(R.id.btnAjuste);
    }

    /**
     * Configura los listeners de los botones
     */
    private void configurarEventos() {

        // ❤️ Favoritos
        btnFavorito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaFavorito.class))
        );

        // 🌱 Invernadero (pantalla actual)
        btnInvernadero.setOnClickListener(v -> {
            // No hace nada porque ya estás aquí
        });

        // ⚙️ Ajustes
        btnAjuste.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaConfiguracion.class))
        );
    }
}
