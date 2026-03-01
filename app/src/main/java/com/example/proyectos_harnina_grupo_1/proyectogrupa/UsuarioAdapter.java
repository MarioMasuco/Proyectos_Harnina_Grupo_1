// Archivo: UsuarioAdapter.java
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

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private final Context context;
    // ¡CAMBIO CLAVE! El adapter ahora tiene su propia lista privada.
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onClickUsuario(Usuario usuario);
    }

    // El constructor ya no necesita la lista.
    public UsuarioAdapter(Context context, OnUsuarioClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position); // Usa la lista local
        if (usuario == null) return;

        // --- Foto de Perfil ---
        String urlImagen = usuario.getImagenUrl();
        if (urlImagen != null && !urlImagen.isEmpty()) {
            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.imagendefectousu) // Imagen mientras carga
                    .error(R.drawable.imagendefectousu)       // Imagen si hay error
                    .circleCrop()
                    .into(holder.imgFoto);
        } else {
            // Si no hay URL, muestra la imagen por defecto
            holder.imgFoto.setImageResource(R.drawable.imagendefectousu);
        }

        // --- Datos del Usuario ---
        holder.tvNombre.setText(usuario.getNombre() != null ? usuario.getNombre() : "N/A");
        holder.tvRol.setText(usuario.getRol() != null ? "Rol: " + usuario.getRol() : "Rol: N/A");
        holder.tvPermisos.setText(usuario.getPermisos() != null && !usuario.getPermisos().isEmpty()
                ? "Permisos: " + usuario.getPermisos()
                : "Permisos: Sin permisos");
        holder.tvId.setText("ID: " + (position + 1));

        // --- Imagen de Estado ---
        String estado = usuario.getEstado();
        if (estado != null) {
            switch (estado.toLowerCase()) {
                case "confirmar":
                    holder.imgEstado.setImageResource(R.drawable.estado_confirmar);
                    break;
                case "bloqueado":
                    holder.imgEstado.setImageResource(R.drawable.estado_bloqueado);
                    break;
                case "activo":
                default:
                    holder.imgEstado.setImageResource(R.drawable.estado_activo);
                    break;
            }
        } else {
            holder.imgEstado.setImageResource(R.drawable.estado_activo);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClickUsuario(usuario);
        });
    }

    @Override
    public int getItemCount() {
        // Devuelve el tamaño de la lista local del adapter
        return listaUsuarios.size();
    }

    // ¡MÉTODO CLAVE MODIFICADO!
    // Ahora recibe una lista nueva y la reemplaza por completo.
    public void actualizarLista(List<Usuario> nuevaLista) {
        // Crea una nueva lista para evitar problemas de referencia
        this.listaUsuarios = new ArrayList<>(nuevaLista);
        notifyDataSetChanged(); // Notifica que los datos han cambiado
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRol, tvPermisos, tvId;
        ImageView imgEstado, imgFoto;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.nombre);
            tvRol = itemView.findViewById(R.id.rol);
            tvPermisos = itemView.findViewById(R.id.permisos);
            tvId = itemView.findViewById(R.id.id);
            imgEstado = itemView.findViewById(R.id.estado);
            imgFoto = itemView.findViewById(R.id.foto);
        }
    }
}