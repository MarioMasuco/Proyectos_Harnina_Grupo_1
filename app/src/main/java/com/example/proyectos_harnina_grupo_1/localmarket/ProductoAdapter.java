package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectos_harnina_grupo_1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.VH> {

    private final List<Producto> lista = new ArrayList<>();
    private final FirebaseFirestore db;
    private final String uid;

    public ProductoAdapter(List<Producto> lista) {
        this.lista.addAll(lista);
        this.db  = FirebaseFirestore.getInstance();
        this.uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int position) {
        Producto p = lista.get(position);

        h.txtNombre.setText(p.nombre);
        h.txtPrecio.setText(p.precio + " €");

        // ✅ categoria es List<String> — mostrar la primera
        if (p.categoria != null && !p.categoria.isEmpty()) {
            h.txtInfo.setText(p.categoria.get(0));
        } else {
            h.txtInfo.setText("");
        }

        // 🖼️ Imagen
        if (p.imagenUrl != null && !p.imagenUrl.isEmpty()) {
            Glide.with(h.imgProducto.getContext())
                    .load(p.imagenUrl)
                    .placeholder(R.drawable.ic_tomate)
                    .into(h.imgProducto);
        } else {
            h.imgProducto.setImageResource(p.imagenRes);
        }

        actualizarIconoFavorito(h.btnFavorito, p.favorito);

        h.btnFavorito.setOnClickListener(v -> {
            if (uid == null) {
                Toast.makeText(v.getContext(),
                        "Inicia sesión para guardar favoritos",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            p.favorito = !p.favorito;
            actualizarIconoFavorito(h.btnFavorito, p.favorito);
            guardarFavoritoEnFirestore(p, v);
        });

        if (p.stock > 0) {
            h.btnAccion.setText("Comprar");
            h.btnAccion.setEnabled(true);
            h.btnAccion.setAlpha(1f);
            h.btnAccion.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#0B4DB8")));
        } else {
            h.btnAccion.setText("Agotado");
            h.btnAccion.setEnabled(false);
            h.btnAccion.setAlpha(0.5f);
            h.btnAccion.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#B00020")));
        }

        h.btnAccion.setOnClickListener(v -> {
            if (p.stock <= 0) return;
            p.stock--;
            notifyItemChanged(position);
            Intent intent = new Intent(v.getContext(), PantallaProducto.class);
            intent.putExtra("producto", p);
            v.getContext().startActivity(intent);
        });
    }

    private void actualizarIconoFavorito(ImageView btn, boolean esFavorito) {
        btn.setImageResource(esFavorito
                ? R.drawable.corazon_rojo
                : R.drawable.favori);
    }

    private void guardarFavoritoEnFirestore(Producto p, View v) {
        if (p.id == null || p.id.isEmpty()) return;

        if (p.favorito) {
            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre",      p.nombre);
            datos.put("categoria",   p.categoria); // ✅ guarda el List tal cual
            datos.put("descripcion", p.descripcion);
            datos.put("precio",      p.precio);
            datos.put("stock",       p.stock);
            datos.put("imagenUrl",   p.imagenUrl != null ? p.imagenUrl : "");

            db.collection("usuarios")
                    .document(uid)
                    .collection("favoritos")
                    .document(p.id)
                    .set(datos)
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(),
                                    "Error al guardar favorito",
                                    Toast.LENGTH_SHORT).show());
        } else {
            db.collection("usuarios")
                    .document(uid)
                    .collection("favoritos")
                    .document(p.id)
                    .delete()
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(),
                                    "Error al eliminar favorito",
                                    Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgProducto, btnFavorito;
        TextView txtNombre, txtInfo, txtPrecio;
        Button btnAccion;

        VH(View v) {
            super(v);
            imgProducto = v.findViewById(R.id.imgProducto);
            btnFavorito = v.findViewById(R.id.btnFavorito);
            txtNombre   = v.findViewById(R.id.txtNombre);
            txtInfo     = v.findViewById(R.id.txtInfo);
            txtPrecio   = v.findViewById(R.id.txtPrecio);
            btnAccion   = v.findViewById(R.id.btnAccion);
        }
    }

    public void actualizarLista(List<Producto> nuevaLista) {
        lista.clear();
        lista.addAll(nuevaLista);
        notifyDataSetChanged();
    }
}