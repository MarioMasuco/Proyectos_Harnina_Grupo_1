package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;

public abstract class BaseActivity extends AppCompatActivity {

    protected View filtroInclude;
    protected ImageView btnCerrarFiltro;
    protected Button btnVolverCatalogo;
    protected Button btnAplicarFiltro;
    protected Button btnCerrarSesion;
    protected boolean filtroVisible = false;

    protected void inicializarFiltro(boolean mostrarAplicarFiltro) {
        filtroInclude = findViewById(R.id.filtroInclude);
        filtroInclude.setVisibility(View.GONE);

        btnCerrarFiltro   = filtroInclude.findViewById(R.id.btnCerrarFiltro);
        btnVolverCatalogo = filtroInclude.findViewById(R.id.btnVolverCatalogo);
        btnAplicarFiltro  = filtroInclude.findViewById(R.id.btnAplicarFiltro);
        btnCerrarSesion   = filtroInclude.findViewById(R.id.btnCerrarSesion);

        btnAplicarFiltro.setVisibility(mostrarAplicarFiltro ? View.VISIBLE : View.GONE);

        btnCerrarFiltro.setOnClickListener(v -> cerrarFiltro());

        btnVolverCatalogo.setOnClickListener(v -> {
            cerrarFiltro();
            Intent intent = new Intent(this, PantallaCatalogo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this,
                    com.example.proyectos_harnina_grupo_1.proyectogrupa.IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    protected void toggleFiltro() {
        filtroVisible = !filtroVisible;
        filtroInclude.setVisibility(filtroVisible ? View.VISIBLE : View.GONE);
    }

    protected void cerrarFiltro() {
        filtroVisible = false;
        filtroInclude.setVisibility(View.GONE);
    }
}