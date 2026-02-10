package com.example.localmarket;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FiltroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<FiltroItem> lista;

    public FiltroAdapter(List<FiltroItem> lista) {
        this.lista = lista;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (FiltroItem f : lista) {
            if (f.visible) count++;
        }
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
            return new VH(inflater.inflate(R.layout.item_precio, parent, false));
        }
        return new VH(inflater.inflate(R.layout.item_texto, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FiltroItem item = getVisibleItem(position);
        if (item == null) return;

        if (item.tipo == FiltroItem.PRECIO) return;

        TextView tv = (TextView) holder.itemView;
        tv.setText(item.texto);
        tv.setRotation(0); // 🔑 NUNCA rotar el texto

        // ===== HEADER =====
        if (item.tipo == FiltroItem.HEADER) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundColor(0xFFE0E0E0);
            tv.setTextColor(0xFF000000);
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return;
        }

        // ===== NORMAL =====
        tv.setTypeface(null, Typeface.NORMAL);
        tv.setTextColor(0xFF444444);
        tv.setBackgroundColor(
                item.seleccionado ? 0xFFD0E8FF : 0x00000000
        );

        // ===== FLECHA SOLO EN CATEGORÍAS =====
        if (item.tipo == FiltroItem.CATEGORIA && tieneSubcategorias(item)) {

            tv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    item.expandido
                            ? R.drawable.flecha
                            : R.drawable.flecha,
                    0
            );
            tv.setCompoundDrawablePadding(16);

        } else {
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        // ===== CLICK =====
        tv.setOnClickListener(v -> {

            // EXPANDIR / COLAPSAR
            if (item.tipo == FiltroItem.CATEGORIA && tieneSubcategorias(item)) {

                item.expandido = !item.expandido;

                boolean activar = false;
                for (FiltroItem f : lista) {
                    if (f == item) {
                        activar = true;
                        continue;
                    }
                    if (activar && f.tipo == FiltroItem.SUBCATEGORIA) {
                        f.visible = item.expandido;
                    }
                    if (activar && f.tipo != FiltroItem.SUBCATEGORIA) break;
                }

                notifyDataSetChanged();
                return;
            }

            // SELECCIÓN
            if (item.tipo == FiltroItem.SUBCATEGORIA ||
                    item.tipo == FiltroItem.TIENDA ||
                    (item.tipo == FiltroItem.CATEGORIA && !tieneSubcategorias(item))) {

                for (FiltroItem f : lista) {
                    if (f.tipo == FiltroItem.CATEGORIA ||
                            f.tipo == FiltroItem.SUBCATEGORIA) {
                        f.seleccionado = false;
                    }
                    if (item.tipo == FiltroItem.TIENDA && f.tipo == FiltroItem.TIENDA) {
                        f.seleccionado = false;
                    }
                }

                item.seleccionado = true;
                notifyDataSetChanged();
            }
        });
    }

    private boolean tieneSubcategorias(FiltroItem categoria) {
        boolean encontrada = false;
        for (FiltroItem f : lista) {
            if (f == categoria) {
                encontrada = true;
                continue;
            }
            if (encontrada && f.tipo == FiltroItem.SUBCATEGORIA) return true;
            if (encontrada && f.tipo != FiltroItem.SUBCATEGORIA) break;
        }
        return false;
    }

    static class VH extends RecyclerView.ViewHolder {
        VH(View v) { super(v); }
    }
}
