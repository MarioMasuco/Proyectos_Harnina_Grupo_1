package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectos_harnina_grupo_1.R;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private Context context;
    private List<Pedido> listaPedidos;
    private OnPedidoClickListener listener;

    public interface OnPedidoClickListener {
        void onPedidoClick(Pedido pedido);
    }

    public PedidoAdapter(Context context, List<Pedido> listaPedidos, OnPedidoClickListener listener) {
        this.context = context;
        this.listaPedidos = listaPedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ¡IMPORTANTE! Inflamos tu layout personalizado.
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_pedidos, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        // Usamos los IDs de tu layout
        holder.tvTiendaNombre.setText(pedido.getTiendaNombre());
        holder.tvClienteNombre.setText(pedido.getClienteNombre());

        // Unimos la lista de productos en un solo string separado por comas
        if (pedido.getProductos() != null && !pedido.getProductos().isEmpty()) {
            String productosString = TextUtils.join(", ", pedido.getProductos());
            holder.tvProductosList.setText(productosString);
        } else {
            holder.tvProductosList.setText("Sin productos");
        }

        holder.tvCoste.setText(String.format(Locale.getDefault(), "%.2f€", pedido.getTotal()));
        holder.tvEstadoPedido.setText(pedido.getEstado());

        // Configuramos el clic en el item completo
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPedidoClick(pedido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    // El ViewHolder ahora tiene los TextViews de TU layout
    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTiendaNombre, tvClienteNombre, tvProductosList, tvCoste, tvEstadoPedido;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTiendaNombre = itemView.findViewById(R.id.nombretienda);
            tvClienteNombre = itemView.findViewById(R.id.nombrecliente);
            tvProductosList = itemView.findViewById(R.id.productoslist);
            tvCoste = itemView.findViewById(R.id.coste);
            tvEstadoPedido = itemView.findViewById(R.id.estadoPedido);
        }
    }
}