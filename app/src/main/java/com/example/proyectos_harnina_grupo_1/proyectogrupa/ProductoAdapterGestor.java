package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

import java.util.List;

public class ProductoAdapterGestor extends RecyclerView.Adapter<ProductoAdapterGestor.ViewHolder> {

    private List<ItemProductoGestor> lista;
    private int posicionSeleccionada = -1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ItemProductoGestor item, int posicion);
    }

    public ProductoAdapterGestor(List<ItemProductoGestor> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, descripcion, precio;
        ImageView foto;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            descripcion = itemView.findViewById(R.id.descripcion);
            precio = itemView.findViewById(R.id.textView12);
            foto = itemView.findViewById(R.id.imageView14);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_producto_gestor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemProductoGestor item = lista.get(position);
        holder.nombre.setText(item.getNombre());
        holder.descripcion.setText(item.getDescripcion());
        holder.precio.setText(item.getPrecio());

        if (posicionSeleccionada == position) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            int anterior = posicionSeleccionada;
            posicionSeleccionada = holder.getAdapterPosition();
            notifyItemChanged(anterior);
            notifyItemChanged(posicionSeleccionada);
            listener.onItemClick(item, posicionSeleccionada);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public void agregarItem(ItemProductoGestor item) {
        lista.add(item);
        notifyItemInserted(lista.size() - 1);
    }

    public void modificarItem(ItemProductoGestor item, int posicion) {
        lista.set(posicion, item);
        notifyItemChanged(posicion);
    }

    public void eliminarItem(int posicion) {
        lista.remove(posicion);
        notifyItemRemoved(posicion);
        posicionSeleccionada = -1;
    }
}