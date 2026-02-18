package com.example.localmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductoDetalleAdapter
        extends RecyclerView.Adapter<ProductoDetalleAdapter.ProductoVH> {

    private final Producto producto;
    private final List<Comentario> comentarios = new ArrayList<>();

    public ProductoDetalleAdapter(Producto producto) {
        this.producto = producto;
    }

    @NonNull
    @Override
    public ProductoVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_detalle, parent, false);
        return new ProductoVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoVH h, int position) {

        // ===== DATOS DEL PRODUCTO =====
        h.imgProducto.setImageResource(producto.imagenRes);
        h.txtNombre.setText(producto.nombre);
        h.txtCategoria.setText(producto.categoria); // categoría / info
        h.txtPrecio.setText(producto.precio + " €");
        h.txtDescripcion.setText(producto.descripcion);

        // ===== CANTIDAD =====
        h.txtCantidad.setText(String.valueOf(h.cantidad));

        h.btnMas.setOnClickListener(v -> {
            h.cantidad++;
            h.txtCantidad.setText(String.valueOf(h.cantidad));
        });

        h.btnMenos.setOnClickListener(v -> {
            if (h.cantidad > 1) {
                h.cantidad--;
                h.txtCantidad.setText(String.valueOf(h.cantidad));
            }
        });

        // ===== COMENTARIOS =====
        ComentarioAdapter comentarioAdapter =
                new ComentarioAdapter(comentarios);

        h.recyclerComentarios.setLayoutManager(
                new LinearLayoutManager(h.itemView.getContext()));
        h.recyclerComentarios.setAdapter(comentarioAdapter);

        h.btnComentar.setOnClickListener(v -> {
            String texto = h.edtComentario.getText().toString().trim();
            if (!texto.isEmpty()) {
                comentarios.add(new Comentario("Usuario", texto));
                comentarioAdapter.notifyItemInserted(comentarios.size() - 1);
                h.edtComentario.setText("");
            }
        });

        // ===== COMPRAR =====
        h.btnComprar.setOnClickListener(v -> {
            // Aquí puedes añadir lógica real de compra
        });
    }

    @Override
    public int getItemCount() {
        return 1; // SOLO UN PRODUCTO
    }

    // ================= VIEW HOLDER =================
    static class ProductoVH extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombre, txtCategoria, txtPrecio, txtDescripcion, txtCantidad;
        ImageButton btnMas, btnMenos;
        Button btnComprar, btnComentar;
        EditText edtComentario;
        RecyclerView recyclerComentarios;

        int cantidad = 1;

        ProductoVH(@NonNull View v) {
            super(v);

            imgProducto = v.findViewById(R.id.imgProducto);
            txtNombre = v.findViewById(R.id.txtNombreProducto);
            txtCategoria = v.findViewById(R.id.txtCategoria);
            txtPrecio = v.findViewById(R.id.txtPrecio);
            txtDescripcion = v.findViewById(R.id.txtDescripcion);

            btnMas = v.findViewById(R.id.btnMas);
            btnMenos = v.findViewById(R.id.btnMenos);
            txtCantidad = v.findViewById(R.id.txtCantidad);

            btnComprar = v.findViewById(R.id.btnComprar);

            edtComentario = v.findViewById(R.id.edtComentario);
            btnComentar = v.findViewById(R.id.btnComentar);
            recyclerComentarios = v.findViewById(R.id.recyclerComentarios);
        }
    }
}
