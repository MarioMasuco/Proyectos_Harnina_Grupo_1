package com.example.proyectos_harnina_grupo_1.localmarket;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantallaPago extends BaseActivity {

    private static final String PREFS = "datos_factura";

    private ImageView btnMenu, btnCarrito;
    private ImageView btnFavoritos, btnInvernadero, btnAjustes;

    private EditText edtNombre, edtApellidos, edtEmail, edtTelefono;
    private EditText edtComunidad, edtProvincia, edtCodPostal, edtPais;
    private EditText edtTitular, edtTarjeta, edtCaducidad, edtCVV;

    private TextView txtTotalProductos, txtGastosEnvio, txtIVA, txtTotalPagar;
    private RecyclerView recyclerProductosPago;
    private Button btnComprar;
    private FirebaseFirestore db;
    private String uid;
    private SharedPreferences prefs;
    private boolean editandoTarjeta = false;
    private boolean editandoFecha   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_pago);

        db    = FirebaseFirestore.getInstance();
        uid   = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        inicializarVistas();
        aplicarFiltrosDeEntrada();
        aplicarFormatoAutomatico();
        configurarEventos();
        cargarDatosGuardados();
        cargarProductosPago();
        calcularTotales();
    }

    private void inicializarVistas() {
        btnMenu    = findViewById(R.id.btnMenu);
        btnCarrito = findViewById(R.id.btnCarrito);
        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);
        btnComprar     = findViewById(R.id.btnComprar);

        txtTotalProductos = findViewById(R.id.txtTotalProductos);
        txtGastosEnvio    = findViewById(R.id.txtGastosEnvio);
        txtIVA            = findViewById(R.id.txtIVA);
        txtTotalPagar     = findViewById(R.id.txtTotalPagar);
        recyclerProductosPago = findViewById(R.id.recyclerProductosPago);

        inicializarFiltro(false);

        edtNombre    = findViewById(R.id.edtNombre);
        edtApellidos = findViewById(R.id.edtApellidos);
        edtEmail     = findViewById(R.id.edtEmail);
        edtTelefono  = findViewById(R.id.edtTelefono);
        edtComunidad = findViewById(R.id.edtComunidad);
        edtProvincia = findViewById(R.id.edtProvincia);
        edtCodPostal = findViewById(R.id.edtCodPostal);
        edtPais      = findViewById(R.id.edtPais);
        edtTitular   = findViewById(R.id.edtTitular);
        edtTarjeta   = findViewById(R.id.edtTarjeta);
        edtCaducidad = findViewById(R.id.edtCaducidad);
        edtCVV       = findViewById(R.id.edtCVV);
    }

    private void aplicarFiltrosDeEntrada() {

        InputFilter soloLetras = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (!Character.isLetter(c) && c != ' ') return "";
            }
            return null;
        };

        InputFilter soloDigitos = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) return "";
            }
            return null;
        };

        edtNombre.setFilters(new InputFilter[]{soloLetras, new InputFilter.LengthFilter(40)});
        edtApellidos.setFilters(new InputFilter[]{soloLetras, new InputFilter.LengthFilter(60)});
        edtTitular.setFilters(new InputFilter[]{soloLetras, new InputFilter.LengthFilter(60)});
        edtPais.setFilters(new InputFilter[]{soloLetras, new InputFilter.LengthFilter(40)});
        edtComunidad.setFilters(new InputFilter[]{soloLetras, new InputFilter.LengthFilter(60)});
        edtProvincia.setFilters(new InputFilter[]{soloLetras, new InputFilter.LengthFilter(60)});

        edtTelefono.setFilters(new InputFilter[]{soloDigitos, new InputFilter.LengthFilter(9)});

        edtCodPostal.setFilters(new InputFilter[]{soloDigitos, new InputFilter.LengthFilter(5)});

        edtCVV.setFilters(new InputFilter[]{soloDigitos, new InputFilter.LengthFilter(4)});

        edtTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});

        edtCaducidad.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
    }

    private void aplicarFormatoAutomatico() {

        edtTarjeta.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}

            public void afterTextChanged(Editable s) {
                if (editandoTarjeta) return;
                editandoTarjeta = true;

                String digits = s.toString().replace(" ", "");
                if (digits.length() > 16) digits = digits.substring(0, 16);

                StringBuilder formateado = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i > 0 && i % 4 == 0) formateado.append(' ');
                    formateado.append(digits.charAt(i));
                }

                edtTarjeta.setText(formateado);
                edtTarjeta.setSelection(formateado.length());
                editandoTarjeta = false;
            }
        });

        edtCaducidad.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            public void onTextChanged(CharSequence s, int a, int b, int c) {}

            public void afterTextChanged(Editable s) {
                if (editandoFecha) return;
                editandoFecha = true;

                String digits = s.toString().replace("/", "");
                if (digits.length() > 4) digits = digits.substring(0, 4);

                StringBuilder formateado = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (!Character.isDigit(digits.charAt(i))) continue;
                    if (i == 2) formateado.append('/');
                    formateado.append(digits.charAt(i));
                }

                edtCaducidad.setText(formateado);
                edtCaducidad.setSelection(formateado.length());
                editandoFecha = false;
            }
        });
    }

    private void cargarDatosGuardados() {
        edtNombre.setText(prefs.getString("nombre", ""));
        edtApellidos.setText(prefs.getString("apellidos", ""));
        edtEmail.setText(prefs.getString("email", ""));
        edtTelefono.setText(prefs.getString("telefono", ""));
        edtComunidad.setText(prefs.getString("comunidad", ""));
        edtProvincia.setText(prefs.getString("provincia", ""));
        edtCodPostal.setText(prefs.getString("codPostal", ""));
        edtPais.setText(prefs.getString("pais", ""));
        edtTitular.setText(prefs.getString("titular", ""));
        edtTarjeta.setText(prefs.getString("tarjeta", ""));
        edtCaducidad.setText(prefs.getString("caducidad", ""));
    }

    private void guardarDatosEnPrefs(String nombre, String apellidos, String email,
                                     String telefono, String comunidad, String provincia,
                                     String codPostal, String pais, String titular,
                                     String tarjeta, String caducidad) {
        prefs.edit()
                .putString("nombre",    nombre)
                .putString("apellidos", apellidos)
                .putString("email",     email)
                .putString("telefono",  telefono)
                .putString("comunidad", comunidad)
                .putString("provincia", provincia)
                .putString("codPostal", codPostal)
                .putString("pais",      pais)
                .putString("titular",   titular)
                .putString("tarjeta",   tarjeta)
                .putString("caducidad", caducidad)
                .apply();
    }

    private boolean luhn(String numero) {
        String digits = numero.replace(" ", "");
        if (digits.length() < 13 || digits.length() > 19) return false;
        int suma = 0;
        boolean doble = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doble) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            suma += d;
            doble = !doble;
        }
        return suma % 10 == 0;
    }

    private boolean fechaValida(String fecha) {
        if (!fecha.matches("\\d{2}/\\d{2}")) return false;
        int mes = Integer.parseInt(fecha.substring(0, 2));
        int anio = Integer.parseInt(fecha.substring(3)) + 2000;
        if (mes < 1 || mes > 12) return false;
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        int mesHoy  = hoy.get(java.util.Calendar.MONTH) + 1;
        int anioHoy = hoy.get(java.util.Calendar.YEAR);
        return anio > anioHoy || (anio == anioHoy && mes >= mesHoy);
    }

    private void configurarEventos() {
        btnMenu.setOnClickListener(v -> toggleFiltro());
        btnCarrito.setOnClickListener(v -> startActivity(new Intent(this, PantallaCesta.class)));
        btnFavoritos.setOnClickListener(v -> startActivity(new Intent(this, PantallaFavorito.class)));
        btnInvernadero.setOnClickListener(v -> startActivity(new Intent(this, PantallaInvernadero.class)));
        btnAjustes.setOnClickListener(v -> startActivity(new Intent(this, PantallaConfiguracion.class)));
        btnComprar.setOnClickListener(v -> validarYGuardarPedido());
    }

    private void validarYGuardarPedido() {
        String nombre    = edtNombre.getText().toString().trim();
        String apellidos = edtApellidos.getText().toString().trim();
        String email     = edtEmail.getText().toString().trim();
        String telefono  = edtTelefono.getText().toString().trim();
        String comunidad = edtComunidad.getText().toString().trim();
        String provincia = edtProvincia.getText().toString().trim();
        String codPostal = edtCodPostal.getText().toString().trim();
        String pais      = edtPais.getText().toString().trim();
        String titular   = edtTitular.getText().toString().trim();
        String tarjeta   = edtTarjeta.getText().toString().trim();
        String caducidad = edtCaducidad.getText().toString().trim();
        String cvv       = edtCVV.getText().toString().trim();

        if (nombre.isEmpty())    { edtNombre.setError("Obligatorio");    edtNombre.requestFocus();    return; }
        if (apellidos.isEmpty()) { edtApellidos.setError("Obligatorio"); edtApellidos.requestFocus(); return; }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email no válido"); edtEmail.requestFocus(); return;
        }
        if (telefono.length() < 9) { edtTelefono.setError("9 dígitos");    edtTelefono.requestFocus(); return; }
        if (comunidad.isEmpty()) { edtComunidad.setError("Obligatorio"); edtComunidad.requestFocus(); return; }
        if (provincia.isEmpty()) { edtProvincia.setError("Obligatorio"); edtProvincia.requestFocus(); return; }
        if (codPostal.length() < 5) { edtCodPostal.setError("5 dígitos"); edtCodPostal.requestFocus(); return; }
        if (pais.isEmpty())      { edtPais.setError("Obligatorio");      edtPais.requestFocus();      return; }
        if (titular.isEmpty())   { edtTitular.setError("Obligatorio");   edtTitular.requestFocus();   return; }

        if (!luhn(tarjeta)) {
            edtTarjeta.setError("Número de tarjeta no válido");
            edtTarjeta.requestFocus();
            return;
        }

        if (!fechaValida(caducidad)) {
            edtCaducidad.setError("Formato MM/AA o tarjeta caducada");
            edtCaducidad.requestFocus();
            return;
        }

        if (cvv.length() < 3) {
            edtCVV.setError("CVV inválido");
            edtCVV.requestFocus();
            return;
        }

        if (Carrito.getInstancia().getProductos().isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        guardarDatosEnPrefs(nombre, apellidos, email, telefono,
                comunidad, provincia, codPostal, pais, titular, tarjeta, caducidad);

        // 💳 Mostrar pantalla de procesando antes de guardar
        mostrarDialogoProcesando(nombre, apellidos, email, telefono,
                comunidad, provincia, codPostal, pais, titular);
    }

    private void mostrarDialogoProcesando(String nombre, String apellidos, String email,
                                          String telefono, String comunidad, String provincia,
                                          String codPostal, String pais, String titular) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_procesando_pago);

        ProgressBar progressBar = dialog.findViewById(R.id.progressPago);
        TextView txtEstado      = dialog.findViewById(R.id.txtEstadoPago);

        dialog.show();

        new Handler().postDelayed(() -> txtEstado.setText("Verificando tarjeta..."), 800);
        new Handler().postDelayed(() -> txtEstado.setText("Procesando pago..."), 2000);
        new Handler().postDelayed(() -> txtEstado.setText("✅ ¡Pago aprobado!"), 3200);
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            guardarPedidoEnFirestore(nombre, apellidos, email, telefono,
                    comunidad, provincia, codPostal, pais, titular);
        }, 4000);
    }

    private void guardarPedidoEnFirestore(String nombre, String apellidos,
                                          String email, String telefono,
                                          String comunidad, String provincia,
                                          String codPostal, String pais, String titular) {
        double subtotal    = Carrito.getInstancia().getTotal();
        double gastosEnvio = subtotal > 0 ? 2.99 : 0;
        double iva         = subtotal * 0.06;
        double total       = subtotal + gastosEnvio + iva;

        List<Map<String, Object>> productosMap = new ArrayList<>();
        for (ProductoCesta p : Carrito.getInstancia().getProductos()) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre",    p.nombre);
            item.put("precio",    p.precio);
            item.put("cantidad",  p.cantidad);
            item.put("subtotal",  p.precio * p.cantidad);
            item.put("imagenUrl", p.imagenUrl != null ? p.imagenUrl : "");
            productosMap.add(item);
        }

        Map<String, Object> comprador = new HashMap<>();
        comprador.put("nombre",    nombre + " " + apellidos);
        comprador.put("email",     email);
        comprador.put("telefono",  telefono);
        comprador.put("direccion", comunidad + ", " + provincia + ", " + codPostal + ", " + pais);
        comprador.put("titular",   titular);

        Map<String, Object> pedido = new HashMap<>();
        pedido.put("uid",         uid != null ? uid : "anonimo");
        pedido.put("comprador",   comprador);
        pedido.put("productos",   productosMap);
        pedido.put("subtotal",    subtotal);
        pedido.put("gastosEnvio", gastosEnvio);
        pedido.put("iva",         iva);
        pedido.put("total",       total);
        pedido.put("timestamp",   System.currentTimeMillis());
        pedido.put("estado",      "pagado");

        db.collection("pedidos")
                .add(pedido)
                .addOnSuccessListener(ref -> {
                    if (uid != null) {
                        db.collection("usuarios").document(uid)
                                .collection("pedidos").document(ref.getId()).set(pedido);
                    }

                    Intent intent = new Intent(this, PantallaPagoRealizado.class);
                    intent.putExtra("pedidoId",  ref.getId());
                    intent.putExtra("nombre",    nombre + " " + apellidos);
                    intent.putExtra("email",     email);
                    intent.putExtra("telefono",  telefono);
                    intent.putExtra("direccion", comunidad + ", " + provincia + ", " + codPostal + ", " + pais);
                    intent.putExtra("subtotal",  subtotal);
                    intent.putExtra("envio",     gastosEnvio);
                    intent.putExtra("iva",       iva);
                    intent.putExtra("total",     total);

                    Carrito.getInstancia().vaciar();
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void calcularTotales() {
        double subtotal    = Carrito.getInstancia().getTotal();
        double gastosEnvio = subtotal > 0 ? 2.99 : 0;
        double iva         = subtotal * 0.06;
        double total       = subtotal + gastosEnvio + iva;
        txtTotalProductos.setText(String.format("%.2f €", subtotal));
        txtGastosEnvio.setText(String.format("%.2f €", gastosEnvio));
        txtIVA.setText(String.format("%.2f €", iva));
        txtTotalPagar.setText(String.format("%.2f €", total));
    }

    private void cargarProductosPago() {
        recyclerProductosPago.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductosPago.setAdapter(new CestaAdapter(Carrito.getInstancia().getProductos()));
    }
}