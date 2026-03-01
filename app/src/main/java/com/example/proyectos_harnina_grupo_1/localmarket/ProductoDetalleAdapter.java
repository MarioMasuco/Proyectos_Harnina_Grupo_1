package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectos_harnina_grupo_1.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoDetalleAdapter
        extends RecyclerView.Adapter<ProductoDetalleAdapter.ProductoVH> {

    private final Producto producto;
    private final List<Comentario> comentarios = new ArrayList<>();

    private final FirebaseFirestore db;
    private final String uid;
    private final String nombreUsuario;

    public ProductoDetalleAdapter(Producto producto) {
        this.producto = producto;
        this.db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            this.nombreUsuario = email != null ? email.split("@")[0] : "Usuario";
        } else {
            this.uid = null;
            this.nombreUsuario = "Anónimo";
        }
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

        // 🖼️ Imagen
        if (producto.imagenUrl != null && !producto.imagenUrl.isEmpty()) {
            Glide.with(h.imgProducto.getContext())
                    .load(producto.imagenUrl)
                    .placeholder(R.drawable.ic_tomate)
                    .into(h.imgProducto);
        } else {
            h.imgProducto.setImageResource(producto.imagenRes);
        }

        h.txtNombre.setText(producto.nombre);
        h.txtPrecio.setText(producto.precio + " €");
        h.txtDescripcion.setText(producto.descripcion);
        h.txtCantidad.setText(String.valueOf(h.cantidad));

        // ✅ categoria es List<String> — mostrar la primera
        if (producto.categoria != null && !producto.categoria.isEmpty()) {
            h.txtCategoria.setText(producto.categoria.get(0));
        } else {
            h.txtCategoria.setText("");
        }

        h.btnMas.setOnClickListener(v -> {
            if (h.cantidad < producto.stock) {
                h.cantidad++;
                h.txtCantidad.setText(String.valueOf(h.cantidad));
            } else {
                Snackbar snackbar = Snackbar.make(
                        v.getRootView().findViewById(android.R.id.content),
                        "⚠  Stock máximo: " + producto.stock + " unidades",
                        Snackbar.LENGTH_LONG
                );
                View snackView = snackbar.getView();
                snackView.setBackgroundColor(Color.parseColor("#B00020"));
                TextView txt = snackView.findViewById(
                        com.google.android.material.R.id.snackbar_text);
                txt.setTextColor(Color.WHITE);
                txt.setTextSize(16f);
                txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar.show();
            }
        });

        h.btnMenos.setOnClickListener(v -> {
            if (h.cantidad > 1) {
                h.cantidad--;
                h.txtCantidad.setText(String.valueOf(h.cantidad));
            }
        });

        ComentarioAdapter comentarioAdapter = new ComentarioAdapter(comentarios);
        h.recyclerComentarios.setLayoutManager(
                new LinearLayoutManager(h.itemView.getContext()));
        h.recyclerComentarios.setAdapter(comentarioAdapter);
        cargarComentarios(comentarioAdapter);

        h.btnComentar.setOnClickListener(v -> {
            String texto = h.edtComentario.getText().toString().trim();
            if (texto.isEmpty()) return;
            if (uid == null) {
                Toast.makeText(v.getContext(), "Inicia sesión para comentar",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            guardarComentarioEnFirestore(texto, comentarioAdapter,
                    h.edtComentario, v.getContext());
        });

        h.btnComprar.setOnClickListener(v -> {
            Carrito.getInstancia().agregarProducto(producto, h.cantidad);
            Context context = v.getContext();
            context.startActivity(new Intent(context, PantallaCesta.class));
        });
    }

    private void cargarComentarios(ComentarioAdapter adapter) {
        if (producto.id == null || producto.id.isEmpty()) return;
        db.collection("productos").document(producto.id).collection("comentarios")
                .orderBy("timestamp", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(qs -> {
                    comentarios.clear();
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        String autor = doc.getString("autor");
                        String texto = doc.getString("texto");
                        if (autor != null && texto != null)
                            comentarios.add(new Comentario(autor, texto));
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void guardarComentarioEnFirestore(String texto, ComentarioAdapter adapter,
                                              EditText edtComentario, Context context) {
        if (producto.id == null || producto.id.isEmpty()) return;
        Map<String, Object> datos = new HashMap<>();
        datos.put("autor",     nombreUsuario);
        datos.put("texto",     texto);
        datos.put("timestamp", System.currentTimeMillis());
        db.collection("productos").document(producto.id).collection("comentarios").add(datos)
                .addOnSuccessListener(ref -> {
                    comentarios.add(new Comentario(nombreUsuario, texto));
                    adapter.notifyItemInserted(comentarios.size() - 1);
                    edtComentario.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return 1; }

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
            imgProducto         = v.findViewById(R.id.imgProducto);
            txtNombre           = v.findViewById(R.id.txtNombreProducto);
            txtCategoria        = v.findViewById(R.id.txtCategoria);
            txtPrecio           = v.findViewById(R.id.txtPrecio);
            txtDescripcion      = v.findViewById(R.id.txtDescripcion);
            btnMas              = v.findViewById(R.id.btnMas);
            btnMenos            = v.findViewById(R.id.btnMenos);
            txtCantidad         = v.findViewById(R.id.txtCantidad);
            btnComprar          = v.findViewById(R.id.btnComprar);
            edtComentario       = v.findViewById(R.id.edtComentario);
            btnComentar         = v.findViewById(R.id.btnComentar);
            recyclerComentarios = v.findViewById(R.id.recyclerComentarios);
        }
    }
}