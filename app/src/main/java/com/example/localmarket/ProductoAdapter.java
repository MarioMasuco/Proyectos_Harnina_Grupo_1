package com.example.localmarket;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.VH> {

    private final List<Producto> lista;

    public ProductoAdapter(List<Producto> lista) {
        this.lista = lista;
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
        h.txtInfo.setText(p.categoria);
        h.txtPrecio.setText(p.precio + " €");
        h.imgProducto.setImageResource(p.imagenRes);

        if (p.disponible) {
            h.btnAccion.setText("Comprar");
            h.btnAccion.setEnabled(true);
            h.btnAccion.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#0B4DB8"))
            );
        } else {
            h.btnAccion.setText("Agotado");
            h.btnAccion.setEnabled(false);
            h.btnAccion.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(Color.parseColor("#B00020"))
            );
        }

        // 🔥 CLICK EN EL PRODUCTO
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PantallaProducto.class);
            intent.putExtra("producto", p);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombre, txtInfo, txtPrecio;
        Button btnAccion;

        VH(View v) {
            super(v);
            imgProducto = v.findViewById(R.id.imgProducto);
            txtNombre   = v.findViewById(R.id.txtNombre);
            txtInfo     = v.findViewById(R.id.txtInfo);
            txtPrecio   = v.findViewById(R.id.txtPrecio);
            btnAccion   = v.findViewById(R.id.btnAccion);
        }
    }
}
