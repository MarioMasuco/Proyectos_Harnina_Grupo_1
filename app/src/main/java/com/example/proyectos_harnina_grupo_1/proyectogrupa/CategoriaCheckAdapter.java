package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoriaCheckAdapter extends RecyclerView.Adapter<CategoriaCheckAdapter.CategoriaViewHolder> {

    private List<String> todasLasCategorias;
    private List<String> categoriasSeleccionadas;

    public CategoriaCheckAdapter(List<String> todasLasCategorias, List<String> categoriasSeleccionadas) {
        this.todasLasCategorias = todasLasCategorias;
        this.categoriasSeleccionadas = categoriasSeleccionadas != null ? categoriasSeleccionadas : new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos el CheckBox directamente por código, sin layout XML
        CheckBox checkBox = new CheckBox(parent.getContext());
        checkBox.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        checkBox.setPadding(16, 8, 16, 8);
        checkBox.setTextSize(16);
        return new CategoriaViewHolder(checkBox);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder holder, int position) {
        String categoria = todasLasCategorias.get(position);
        holder.checkBox.setText(categoria);

        // Evitar que el listener anterior dispare al reusar la vista
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(categoriasSeleccionadas.contains(categoria));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!categoriasSeleccionadas.contains(categoria)) {
                    categoriasSeleccionadas.add(categoria);
                }
            } else {
                categoriasSeleccionadas.remove(categoria);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todasLasCategorias != null ? todasLasCategorias.size() : 0;
    }

    public List<String> getCategoriasSeleccionadas() {
        return categoriasSeleccionadas;
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView;
        }
    }
}