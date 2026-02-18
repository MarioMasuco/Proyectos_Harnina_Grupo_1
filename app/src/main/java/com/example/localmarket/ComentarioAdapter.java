package com.example.localmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComentarioAdapter
        extends RecyclerView.Adapter<ComentarioAdapter.ComentarioVH> {

    private final List<Comentario> lista;

    public ComentarioAdapter(List<Comentario> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ComentarioVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ComentarioVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioVH h, int position) {
        Comentario c = lista.get(position);
        h.txtUsuario.setText(c.usuario + ":");
        h.txtComentario.setText(c.texto);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // ================= VIEW HOLDER =================
    static class ComentarioVH extends RecyclerView.ViewHolder {

        TextView txtUsuario, txtComentario;

        ComentarioVH(@NonNull View v) {
            super(v);
            txtUsuario = v.findViewById(R.id.txtUsuario);
            txtComentario = v.findViewById(R.id.txtComentario);
        }
    }
}
