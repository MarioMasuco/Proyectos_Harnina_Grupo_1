package com.example.proyectos_harnina_grupo_1.localmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectos_harnina_grupo_1.R;

import java.util.List;

public class CestaAdapter extends RecyclerView.Adapter<CestaAdapter.CestaVH> {

    private final List<ProductoCesta> productos;
    private OnTotalCambiadoListener listener;

    public interface OnTotalCambiadoListener {
        void onTotalCambiado(double nuevoTotal);
    }

    public CestaAdapter(List<ProductoCesta> productos) {
        this.productos = productos;
    }

    public void setOnTotalCambiadoListener(OnTotalCambiadoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CestaVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cesta, parent, false);
        return new CestaVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CestaVH h, int position) {

        ProductoCesta p = productos.get(position);

        if (p.imagenUrl != null && !p.imagenUrl.isEmpty()) {
            Glide.with(h.imgProducto.getContext())
                    .load(p.imagenUrl)
                    .placeholder(R.drawable.ic_tomate)
                    .into(h.imgProducto);
        } else {
            h.imgProducto.setImageResource(p.imagenRes);
        }

        h.txtNombre.setText(p.nombre);
        h.txtInfo.setText(p.info != null ? p.info : "");
        h.txtUnidades.setText("Unidades: " + p.cantidad);
        h.txtPrecio.setText(String.format("%.2f €", p.precio * p.cantidad));

        h.btnEliminar.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;

            Carrito.getInstancia().eliminarProducto(productos.get(pos));
            productos.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, productos.size());

            if (listener != null) {
                listener.onTotalCambiado(Carrito.getInstancia().getTotal());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class CestaVH extends RecyclerView.ViewHolder {

        ImageView imgProducto, btnEliminar;
        TextView txtNombre, txtInfo, txtUnidades, txtPrecio;

        CestaVH(@NonNull View v) {
            super(v);
            imgProducto = v.findViewById(R.id.imgProducto);
            btnEliminar = v.findViewById(R.id.btnEliminar);
            txtNombre   = v.findViewById(R.id.txtNombre);
            txtInfo     = v.findViewById(R.id.txtInfo);
            txtUnidades = v.findViewById(R.id.txtUnidades);
            txtPrecio   = v.findViewById(R.id.txtPrecio);
        }
    }
}