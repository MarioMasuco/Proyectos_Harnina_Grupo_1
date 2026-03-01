package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

import java.util.List;

public class PedidoAdapterGestor extends RecyclerView.Adapter<PedidoAdapterGestor.ViewHolder> {

    private List<ItemPedidoGestor> lista;
    private int posicionSeleccionada = -1;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ItemPedidoGestor item, int posicion);
    }

    public PedidoAdapterGestor(List<ItemPedidoGestor> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView idPedido, nombreCliente, productos, coste, estado;

        public ViewHolder(View itemView) {
            super(itemView);
            idPedido = itemView.findViewById(R.id.textView1);
            nombreCliente = itemView.findViewById(R.id.textView2);
            productos = itemView.findViewById(R.id.recuperar);
            coste = itemView.findViewById(R.id.textView);
            estado = itemView.findViewById(R.id.textView11);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_pedidos_gestor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemPedidoGestor item = lista.get(position);
        holder.idPedido.setText(item.getIdPedido());
        holder.nombreCliente.setText(item.getNombreCliente());
        holder.productos.setText(item.getProductos());
        holder.coste.setText(item.getCoste());
        holder.estado.setText(item.getEstado());

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

    public void agregarItem(ItemPedidoGestor item) {
        lista.add(item);
        notifyItemInserted(lista.size() - 1);
    }

    public void modificarItem(ItemPedidoGestor item, int posicion) {
        lista.set(posicion, item);
        notifyItemChanged(posicion);
    }

    public void eliminarItem(int posicion) {
        lista.remove(posicion);
        notifyItemRemoved(posicion);
        posicionSeleccionada = -1;
    }
}