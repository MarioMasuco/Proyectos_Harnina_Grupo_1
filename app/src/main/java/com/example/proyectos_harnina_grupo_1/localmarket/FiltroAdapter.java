package com.example.proyectos_harnina_grupo_1.localmarket;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

import java.util.ArrayList;
import java.util.List;

public class FiltroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<FiltroItem> lista;

    private Double minPrecio = null;
    private Double maxPrecio = null;

    public FiltroAdapter(List<FiltroItem> lista) {
        this.lista = lista;
    }

    public Double getMinPrecio() { return minPrecio; }
    public Double getMaxPrecio() { return maxPrecio; }

    public List<String> getCategoriasSeleccionadas() {
        List<String> seleccionadas = new ArrayList<>();
        for (FiltroItem f : lista) {
            if (f.seleccionado &&
                    (f.tipo == FiltroItem.SUBCATEGORIA || f.tipo == FiltroItem.CATEGORIA)) {
                seleccionadas.add(f.texto);
            }
        }
        return seleccionadas;
    }

    public List<String> getTiendasSeleccionadas() {
        List<String> seleccionadas = new ArrayList<>();
        for (FiltroItem f : lista) {
            if (f.seleccionado && f.tipo == FiltroItem.TIENDA) {
                seleccionadas.add(f.texto);
            }
        }
        return seleccionadas;
    }

    public String getCategoriaSeleccionada() {
        List<String> cats = getCategoriasSeleccionadas();
        return cats.isEmpty() ? null : cats.get(0);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (FiltroItem f : lista) if (f.visible) count++;
        return count;
    }

    private FiltroItem getVisibleItem(int position) {
        int index = -1;
        for (FiltroItem f : lista) {
            if (f.visible) index++;
            if (index == position) return f;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return getVisibleItem(position).tipo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == FiltroItem.PRECIO) {
            return new PrecioVH(inflater.inflate(R.layout.item_precio, parent, false));
        }
        return new TextoVH(inflater.inflate(R.layout.item_texto, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FiltroItem item = getVisibleItem(position);
        if (item == null) return;

        if (item.tipo == FiltroItem.PRECIO) {
            PrecioVH h = (PrecioVH) holder;
            h.edtMin.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                public void onTextChanged(CharSequence s, int a, int b, int c) {}
                public void afterTextChanged(Editable s) {
                    minPrecio = s.toString().isEmpty() ? null : Double.parseDouble(s.toString());
                }
            });
            h.edtMax.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                public void onTextChanged(CharSequence s, int a, int b, int c) {}
                public void afterTextChanged(Editable s) {
                    maxPrecio = s.toString().isEmpty() ? null : Double.parseDouble(s.toString());
                }
            });
            return;
        }

        TextoVH vh = (TextoVH) holder;
        TextView tv = vh.textView;
        String textoMostrado = (item.tipo == FiltroItem.CATEGORIA || item.tipo == FiltroItem.SUBCATEGORIA)
                && !item.texto.isEmpty()
                ? item.texto.substring(0, 1).toUpperCase() + item.texto.substring(1)
                : item.texto;
        tv.setText(textoMostrado);
        tv.setRotation(0);

        if (item.tipo == FiltroItem.HEADER) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundColor(0xFFE0E0E0);
            tv.setTextColor(0xFF000000);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tv.setOnClickListener(null);
            return;
        }

        tv.setTypeface(null, Typeface.NORMAL);
        tv.setTextColor(0xFF444444);

        if (item.seleccionado) {
            tv.setBackgroundColor(0xFFD0E8FF);
            tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            tv.setCompoundDrawablePadding(12);
        } else {
            tv.setBackgroundColor(0x00000000);
            if (item.tipo == FiltroItem.CATEGORIA && tieneSubcategorias(item)) {
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.flecha, 0);
                tv.setCompoundDrawablePadding(16);
            } else {
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        tv.setOnClickListener(v -> {

            if (item.tipo == FiltroItem.CATEGORIA && tieneSubcategorias(item)) {
                item.expandido = !item.expandido;
                boolean activar = false;
                for (FiltroItem f : lista) {
                    if (f == item) { activar = true; continue; }
                    if (activar && f.tipo == FiltroItem.SUBCATEGORIA) f.visible = item.expandido;
                    if (activar && f.tipo != FiltroItem.SUBCATEGORIA) break;
                }
                notifyDataSetChanged();
                return;
            }

            if (item.tipo == FiltroItem.SUBCATEGORIA ||
                    item.tipo == FiltroItem.TIENDA ||
                    (item.tipo == FiltroItem.CATEGORIA && !tieneSubcategorias(item))) {

                item.seleccionado = !item.seleccionado; // toggle
                notifyDataSetChanged();
            }
        });
    }

    private boolean tieneSubcategorias(FiltroItem categoria) {
        boolean encontrada = false;
        for (FiltroItem f : lista) {
            if (f == categoria) { encontrada = true; continue; }
            if (encontrada && f.tipo == FiltroItem.SUBCATEGORIA) return true;
            if (encontrada && f.tipo != FiltroItem.SUBCATEGORIA) break;
        }
        return false;
    }

    static class TextoVH extends RecyclerView.ViewHolder {
        TextView textView;
        TextoVH(View v) { super(v); textView = (TextView) v; }
    }

    static class PrecioVH extends RecyclerView.ViewHolder {
        EditText edtMin, edtMax;
        PrecioVH(View v) {
            super(v);
            edtMin = v.findViewById(R.id.edtMinPrecio);
            edtMax = v.findViewById(R.id.edtMaxPrecio);
        }
    }
}