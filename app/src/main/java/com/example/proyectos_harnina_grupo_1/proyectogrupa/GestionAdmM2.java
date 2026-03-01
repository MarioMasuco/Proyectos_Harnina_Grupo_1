package com.example.proyectos_harnina_grupo_1.proyectogrupa; // Asegúrate que este es tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.proyectos_harnina_grupo_1.R;
import com.example.proyectos_harnina_grupo_1.localmarket.PantallaInvernadero;
import com.google.firebase.auth.FirebaseAuth;

public class GestionAdmM2 extends AppCompatActivity {

    // --- VARIABLES PARA LAS VISTAS ---
    private TextView tvTituloTienda;
    private AppCompatButton btnVerProductos, btnVerPedidos, btnVerClientes;
    private ImageButton btnAjustes, btnCerrarSesion, btnInvernadero;

    // --- VARIABLES PARA LOS DATOS DE LA TIENDA ---
    private String tiendaId, tiendaNombre, tiendaDescripcion, tiendaGestorId, tiendaImagenUrl;

    // --- FIREBASE ---
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que el nombre del layout es el correcto. Por ejemplo: activity_gestion_adm_m2.xml
        setContentView(R.layout.activity_ma_gestion_adm_m2);

        mAuth = FirebaseAuth.getInstance();

        // 1. RECIBIR LOS DATOS DEL INTENT
        Intent intent = getIntent();
        tiendaId = intent.getStringExtra("TIENDA_ID");
        tiendaNombre = intent.getStringExtra("TIENDA_NOMBRE");
        tiendaDescripcion = intent.getStringExtra("TIENDA_DESCRIPCION");
        tiendaGestorId = intent.getStringExtra("TIENDA_GESTOR_ID");
        tiendaImagenUrl = intent.getStringExtra("TIENDA_IMAGEN_URL");


        // Verificar que los datos esenciales llegaron
        if (tiendaId == null || tiendaNombre == null) {
            Toast.makeText(this, "Error: No se recibieron los datos de la tienda.", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no hay datos
            return;
        }

        // 2. VINCULAR VISTAS Y CONFIGURAR
        inicializarVistas();
        setupBotones();
    }

    private void inicializarVistas() {
        // Vincular con los IDs de tu XML
        tvTituloTienda = findViewById(R.id.nombre); // El TextView que dice "Gestion ADMIN"
        btnVerProductos = findViewById(R.id.productos);
        btnVerPedidos = findViewById(R.id.pedidos);
        btnVerClientes = findViewById(R.id.clientes);
        btnAjustes = findViewById(R.id.ajustes);
        btnCerrarSesion = findViewById(R.id.cerrarSesion);
        btnInvernadero = findViewById(R.id.invernadero);

        // Actualizar el título con el nombre de la tienda
        tvTituloTienda.setText("Gestionando: " + tiendaNombre);
    }

    private void setupBotones() {
        // --- BOTÓN VER PRODUCTOS ---
        btnVerProductos.setOnClickListener(v -> {
            Intent intent = new Intent(GestionAdmM2.this, GestionProducto.class); // CAMBIA ESTO por el nombre de tu Activity de productos
            intent.putExtra("TIENDA_ID", tiendaId);
            intent.putExtra("TIENDA_NOMBRE", tiendaNombre);
            startActivity(intent);
        });

        // --- BOTÓN VER PEDIDOS ---
        btnVerPedidos.setOnClickListener(v -> {
            Intent intent = new Intent(GestionAdmM2.this, GestionPedidos.class); // CAMBIA ESTO por el nombre de tu Activity de pedidos
            intent.putExtra("TIENDA_ID", tiendaId);
            intent.putExtra("TIENDA_NOMBRE", tiendaNombre);
            startActivity(intent);
        });

        // --- BOTÓN VER CLIENTES ---
       // btnVerClientes.setOnClickListener(v -> {
         //   Intent intent = new Intent(GestionAdmM2.this, GestionClientes.class); // CAMBIA ESTO por el nombre de tu Activity de clientes
         //   intent.putExtra("TIENDA_ID", tiendaId);
         //   intent.putExtra("TIENDA_NOMBRE", tiendaNombre);
        //    startActivity(intent);
     //   });

        // --- BOTONES DE LA BARRA INFERIOR ---
        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(GestionAdmM2.this, IniciSesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnInvernadero.setOnClickListener(v -> {
            startActivity(new Intent(GestionAdmM2.this, PantallaInvernadero.class));
        });

        btnAjustes.setOnClickListener(v -> {
            DialogConfiguracionPerfil dialog = new DialogConfiguracionPerfil(GestionAdmM2.this);
            dialog.mostrar();
        });
    }
}