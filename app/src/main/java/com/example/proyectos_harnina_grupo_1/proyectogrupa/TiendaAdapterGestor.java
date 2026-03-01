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

public class TiendaAdapterGestor extends RecyclerView.Adapter<TiendaAdapterGestor.ViewHolder> {

    private List<ItemTiendaGestor> lista;
    private int posicionSeleccionada = -1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ItemTiendaGestor item, int posicion);
        void onConfirmarClick(ItemTiendaGestor item, int posicion);

    }

    public TiendaAdapterGestor(List<ItemTiendaGestor> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, descripcion, textViewPropietario;
        ImageView imageViewConfirmar, imageViewFoto;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            descripcion = itemView.findViewById(R.id.descripcion);
            textViewPropietario = itemView.findViewById(R.id.textView12);
            imageViewConfirmar = itemView.findViewById(R.id.imageView13);
            imageViewFoto = itemView.findViewById(R.id.imageView14);

            android.util.Log.d("ADAPTER", "nombre: " + nombre);
            android.util.Log.d("ADAPTER", "descripcion: " + descripcion);
            android.util.Log.d("ADAPTER", "propietario: " + textViewPropietario);
            android.util.Log.d("ADAPTER", "confirmar: " + imageViewConfirmar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_tienda_gestor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemTiendaGestor item = lista.get(position);

        holder.nombre.setText(item.getNombre());
        holder.descripcion.setText(item.getDescripcion());
        holder.textViewPropietario.setText(item.getPropietario());

        // Resaltar seleccionado
        if (posicionSeleccionada == position) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Seleccionar item
        holder.itemView.setOnClickListener(v -> {
            int anterior = posicionSeleccionada;
            posicionSeleccionada = holder.getAdapterPosition();
            notifyItemChanged(anterior);
            notifyItemChanged(posicionSeleccionada);
            listener.onItemClick(item, posicionSeleccionada);
        });

        // Botón confirmar del item
        holder.imageViewConfirmar.setOnClickListener(v -> {
            listener.onConfirmarClick(item, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Métodos para los botones de la pantalla
    public void agregarItem(ItemTiendaGestor item) {
        lista.add(item);
        notifyItemInserted(lista.size() - 1);
    }

    public void modificarItem(ItemTiendaGestor itemModificado, int posicion) {
        lista.set(posicion, itemModificado);
        notifyItemChanged(posicion);
    }

    public void eliminarItem(int posicion) {
        lista.remove(posicion);
        notifyItemRemoved(posicion);
        posicionSeleccionada = -1;
    }
}
