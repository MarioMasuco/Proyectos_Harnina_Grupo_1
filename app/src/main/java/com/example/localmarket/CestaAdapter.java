package com.example.localmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CestaAdapter extends RecyclerView.Adapter<CestaAdapter.VH> {

    private final List<ProductoCesta> lista;

    public CestaAdapter(List<ProductoCesta> lista) {
        this.lista = lista;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cesta, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int position) {

        ProductoCesta p = lista.get(position);

        h.img.setImageResource(p.imagenRes);
        h.txtNombre.setText(p.nombre);
        h.txtInfo.setText(p.info);
        h.txtCantidad.setText("unidades: " + p.cantidad);

        double total = p.precio * p.cantidad;
        h.txtPrecio.setText(total + " €");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView img;
        TextView txtNombre, txtInfo, txtCantidad, txtPrecio;

        VH(View v) {
            super(v);
            img = v.findViewById(R.id.imgProducto);
            txtNombre = v.findViewById(R.id.txtNombre);
            txtInfo = v.findViewById(R.id.txtInfo);
            txtCantidad = v.findViewById(R.id.txtCantidad);
            txtPrecio = v.findViewById(R.id.txtPrecio);
        }
    }
}
