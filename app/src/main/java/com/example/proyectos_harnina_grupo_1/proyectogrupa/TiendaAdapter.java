package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectos_harnina_grupo_1.R;

import java.util.ArrayList;
import java.util.List;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.TiendaViewHolder> {

    private final Context context;
    private List<Tienda> listaTiendas = new ArrayList<>(); // <-- Lista interna
    private OnTiendaClickListener listener;

    public interface OnTiendaClickListener {
        void onClickTienda(Tienda tienda);
    }

    public TiendaAdapter(Context context, OnTiendaClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_tienda, parent, false);
        return new TiendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaViewHolder holder, int position) {
        Tienda tienda = listaTiendas.get(position);
        if (tienda == null) return;

        // Asignar textos
        holder.tvNombre.setText(tienda.getNombre() != null ? tienda.getNombre() : "Sin nombre");
        holder.tvDescripcion.setText(tienda.getDescripcion() != null ? tienda.getDescripcion() : "Sin descripción");
        holder.tvPropietario.setText("Gestor: " + (tienda.getGestorId() != null ? tienda.getGestorId() : "Sin asignar"));

        // Cargar imagen con Glide
        String urlImagen = tienda.getImagenUrl();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.imagendefectousu) // Imagen mientras carga
                    .error(R.drawable.imagendefectousu)       // Imagen si hay error
                    .into(holder.imgTienda);
        } else {
            // Si no hay URL, muestra la imagen por defecto
            holder.imgTienda.setImageResource(R.drawable.imagendefectousu);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClickTienda(tienda);
        });
    }

    @Override
    public int getItemCount() {
        return listaTiendas.size();
    }

    // <-- MÉTODO CLAVE PARA ACTUALIZAR LA LISTA -->
    public void actualizarLista(List<Tienda> nuevaLista) {
        listaTiendas.clear();
        if (nuevaLista != null) {
            listaTiendas.addAll(nuevaLista);
        }
        notifyDataSetChanged();
    }

    public static class TiendaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPropietario;
        ImageView imgTienda; // <-- Nuevo ImageView
        ImageView imgEstado;

        public TiendaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.nombre);
            tvDescripcion = itemView.findViewById(R.id.permisos);
            tvPropietario = itemView.findViewById(R.id.id);
            imgTienda = itemView.findViewById(R.id.fotoTienda); // <-- Vincular
            imgEstado = itemView.findViewById(R.id.estado);
        }
    }
}