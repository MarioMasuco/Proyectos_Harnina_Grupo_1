package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectos_harnina_grupo_1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PantallaPagoRealizado extends BaseActivity {

    private ImageView btnMenu, btnCarrito;

    private ImageView btnFavoritos, btnInvernadero, btnAjustes;

    private LinearLayout layoutFactura;
    private ScrollView scrollFactura;
    private TextView txtFacturaPedidoId, txtFacturaFecha;
    private TextView txtFacturaNombre, txtFacturaEmail;
    private TextView txtFacturaTelefono, txtFacturaDireccion;
    private TextView txtFacturaProductos;
    private TextView txtFacturaSubtotal, txtFacturaEnvio, txtFacturaIva, txtFacturaTotal;
    private Button btnDescargarPDF;

    private String pedidoId, nombre, email, telefono, direccion;
    private double subtotal, envio, iva, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_pago_realizado);

        recibirDatos();
        inicializarVistas();
        mostrarFactura();
        configurarEventos();
    }

    private void recibirDatos() {
        Intent intent = getIntent();
        pedidoId  = intent.getStringExtra("pedidoId");
        nombre    = intent.getStringExtra("nombre");
        email     = intent.getStringExtra("email");
        telefono  = intent.getStringExtra("telefono");
        direccion = intent.getStringExtra("direccion");
        subtotal  = intent.getDoubleExtra("subtotal", 0);
        envio     = intent.getDoubleExtra("envio", 0);
        iva       = intent.getDoubleExtra("iva", 0);
        total     = intent.getDoubleExtra("total", 0);
    }

    private void inicializarVistas() {
        btnMenu    = findViewById(R.id.btnMenu);
        btnCarrito = findViewById(R.id.btnCarrito);
        btnFavoritos   = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjustes     = findViewById(R.id.btnAjuste);

        inicializarFiltro(false);

        layoutFactura       = findViewById(R.id.layoutFactura);
        scrollFactura       = findViewById(R.id.scrollFactura);
        txtFacturaPedidoId  = findViewById(R.id.txtFacturaPedidoId);
        txtFacturaFecha     = findViewById(R.id.txtFacturaFecha);
        txtFacturaNombre    = findViewById(R.id.txtFacturaNombre);
        txtFacturaEmail     = findViewById(R.id.txtFacturaEmail);
        txtFacturaTelefono  = findViewById(R.id.txtFacturaTelefono);
        txtFacturaDireccion = findViewById(R.id.txtFacturaDireccion);
        txtFacturaProductos = findViewById(R.id.txtFacturaProductos);
        txtFacturaSubtotal  = findViewById(R.id.txtFacturaSubtotal);
        txtFacturaEnvio     = findViewById(R.id.txtFacturaEnvio);
        txtFacturaIva       = findViewById(R.id.txtFacturaIva);
        txtFacturaTotal     = findViewById(R.id.txtFacturaTotal);
        btnDescargarPDF     = findViewById(R.id.btnDescargarPDF);
    }

    private void mostrarFactura() {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        txtFacturaPedidoId.setText("Pedido: #" + (pedidoId != null ? pedidoId : "---"));
        txtFacturaFecha.setText("Fecha: " + fecha);
        txtFacturaNombre.setText("Nombre: " + (nombre != null ? nombre : "---"));
        txtFacturaEmail.setText("Email: " + (email != null ? email : "---"));
        txtFacturaTelefono.setText("Teléfono: " + (telefono != null ? telefono : "---"));
        txtFacturaDireccion.setText("Dirección: " + (direccion != null ? direccion : "---"));

        StringBuilder sbProductos = new StringBuilder();
        for (ProductoCesta p : Carrito.getInstancia().getProductosFactura()) {
            sbProductos.append("• ").append(p.nombre)
                    .append("  x").append(p.cantidad)
                    .append("  →  ").append(String.format("%.2f €", p.precio * p.cantidad))
                    .append("\n");
        }
        txtFacturaProductos.setText(sbProductos.length() > 0
                ? sbProductos.toString().trim()
                : "Ver detalle en tu historial de pedidos");

        txtFacturaSubtotal.setText(String.format("%.2f €", subtotal));
        txtFacturaEnvio.setText(String.format("%.2f €", envio));
        txtFacturaIva.setText(String.format("%.2f €", iva));
        txtFacturaTotal.setText(String.format("%.2f €", total));
    }

    private void configurarEventos() {
        btnMenu.setOnClickListener(v -> toggleFiltro());

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaCesta.class)));
        btnFavoritos.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaFavorito.class)));
        btnInvernadero.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaInvernadero.class)));
        btnAjustes.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaConfiguracion.class)));

        btnDescargarPDF.setOnClickListener(v -> generarYDescargarPDF());
    }

    private void generarYDescargarPDF() {
        PdfDocument documento = new PdfDocument();
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = documento.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int x = 40, y = 60;
        int lineHeight = 22;

        paint.setTextSize(22);
        paint.setColor(Color.parseColor("#294FA3"));
        paint.setFakeBoldText(true);
        canvas.drawText("LOCAL MARKET — FACTURA", x, y, paint);
        y += lineHeight + 10;

        paint.setTextSize(11);
        paint.setColor(Color.DKGRAY);
        paint.setFakeBoldText(false);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        canvas.drawText("Pedido: #" + (pedidoId != null ? pedidoId : "---"), x, y, paint); y += lineHeight;
        canvas.drawText("Fecha: " + fecha, x, y, paint); y += lineHeight + 10;

        paint.setColor(Color.LTGRAY);
        canvas.drawLine(x, y, 555, y, paint); y += 15;

        paint.setTextSize(13); paint.setColor(Color.parseColor("#294FA3")); paint.setFakeBoldText(true);
        canvas.drawText("DATOS DEL COMPRADOR", x, y, paint); y += lineHeight + 4;

        paint.setTextSize(11); paint.setColor(Color.BLACK); paint.setFakeBoldText(false);
        canvas.drawText("Nombre:     " + (nombre    != null ? nombre    : "---"), x, y, paint); y += lineHeight;
        canvas.drawText("Email:      " + (email     != null ? email     : "---"), x, y, paint); y += lineHeight;
        canvas.drawText("Teléfono:   " + (telefono  != null ? telefono  : "---"), x, y, paint); y += lineHeight;
        canvas.drawText("Dirección:  " + (direccion != null ? direccion : "---"), x, y, paint); y += lineHeight + 10;

        paint.setColor(Color.LTGRAY);
        canvas.drawLine(x, y, 555, y, paint); y += 15;

        paint.setTextSize(13); paint.setColor(Color.parseColor("#294FA3")); paint.setFakeBoldText(true);
        canvas.drawText("PRODUCTOS", x, y, paint); y += lineHeight + 4;

        paint.setTextSize(11); paint.setColor(Color.BLACK); paint.setFakeBoldText(false);
        for (ProductoCesta p : Carrito.getInstancia().getProductosFactura()) {
            String linea = "• " + p.nombre + "  x" + p.cantidad +
                    "  →  " + String.format("%.2f €", p.precio * p.cantidad);
            canvas.drawText(linea, x, y, paint); y += lineHeight;
        }
        y += 10;

        paint.setColor(Color.LTGRAY);
        canvas.drawLine(x, y, 555, y, paint); y += 15;

        paint.setTextSize(13); paint.setColor(Color.parseColor("#294FA3")); paint.setFakeBoldText(true);
        canvas.drawText("RESUMEN DEL PEDIDO", x, y, paint); y += lineHeight + 4;

        paint.setTextSize(11); paint.setColor(Color.BLACK); paint.setFakeBoldText(false);
        canvas.drawText("Subtotal productos:", x, y, paint);
        canvas.drawText(String.format("%.2f €", subtotal), 450, y, paint); y += lineHeight;
        canvas.drawText("Gastos de envío:", x, y, paint);
        canvas.drawText(String.format("%.2f €", envio), 450, y, paint); y += lineHeight;
        canvas.drawText("IVA (6%):", x, y, paint);
        canvas.drawText(String.format("%.2f €", iva), 450, y, paint); y += lineHeight + 6;

        paint.setColor(Color.LTGRAY);
        canvas.drawLine(x, y, 555, y, paint); y += 12;

        paint.setTextSize(14); paint.setColor(Color.parseColor("#0B4DB8")); paint.setFakeBoldText(true);
        canvas.drawText("TOTAL A PAGAR:", x, y, paint);
        canvas.drawText(String.format("%.2f €", total), 430, y, paint); y += lineHeight + 20;

        paint.setTextSize(9); paint.setColor(Color.GRAY); paint.setFakeBoldText(false);
        canvas.drawText("Gracias por tu compra en Local Market. Este documento es tu factura oficial.", x, 800, paint);

        documento.finishPage(page);

        String nombreArchivo = "factura_" + (pedidoId != null ? pedidoId : "pedido") + ".pdf";
        File carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File archivo = new File(carpeta, nombreArchivo);

        try {
            documento.writeTo(new FileOutputStream(archivo));
            documento.close();
            Toast.makeText(this, "Factura guardada en Descargas:\n" + nombreArchivo,
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            documento.close();
            Toast.makeText(this, "Error al guardar: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}