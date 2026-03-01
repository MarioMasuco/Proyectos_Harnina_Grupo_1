package com.example.proyectos_harnina_grupo_1.proyectogrupa;

import android.content.Context;
import android.util.Log;
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

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private final Context context;
    private List<Producto> listaProductos = new ArrayList<>();
    private OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onClickProducto(Producto producto);
    }

    public ProductoAdapter(Context context, OnProductoClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        if (producto == null) return;

        // 1. Asignar el nombre del producto
        holder.tvNombre.setText(producto.getNombre() != null ? producto.getNombre() : "Sin nombre");

        // 2. Asignar la DESCRIPCIÓN al TextView de la descripción (tvDescripcion)
        String descripcion = producto.getDescripcion();
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            holder.tvDescripcion.setText(descripcion);
        } else {
            holder.tvDescripcion.setText("Sin descripción");
        }

        // 3. Asignar las CATEGORÍAS al TextView de las categorías (tvCategorias)
        List<String> cats = producto.getCategorias();
        if (cats != null && !cats.isEmpty()) {
            // Une las categorías con una coma y un espacio: "Herramientas, Materiales"
            String textoCategorias = String.join(", ", cats);
            holder.tvCategorias.setText("Categorías: " + textoCategorias);
        } else {
            holder.tvCategorias.setText("Sin categorías");
        }

        // 4. Asignar el precio y el stock
        holder.tvPrecio.setText(producto.getPrecio() + "€  |  Stock: " + producto.getStock());

        // 5. Asignar el icono de estado (disponible o no)
        if (producto.isDisponible()) {
            holder.imgEstado.setImageResource(R.drawable.estado_activo);
        } else {
            holder.imgEstado.setImageResource(R.drawable.estado_bloqueado);
        }

        // 6. Cargar la imagen del producto con Glide
        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            Glide.with(context)
                    .load(producto.getImagenUrl())
                    .placeholder(R.drawable.imagendefectousu)
                    .error(R.drawable.imagendefectousu)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.imagendefectousu);
        }

        // 7. Configurar el click en el item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClickProducto(producto);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public void actualizarLista(List<Producto> nuevaLista) {
        // Añade logs para depuración
        Log.d("ProductoAdapter", "Actualizando lista. Tamaño anterior: " + listaProductos.size() +
                ", Tamaño nuevo: " + (nuevaLista != null ? nuevaLista.size() : 0));

        listaProductos.clear();
        if (nuevaLista != null) {
            listaProductos.addAll(nuevaLista);
        }

        // Forzar la actualización completa
        notifyDataSetChanged();

        // Verificar que la lista se ha actualizado correctamente
        Log.d("ProductoAdapter", "Lista actualizada. Nuevo tamaño: " + listaProductos.size());
    }

    /**
     * ViewHolder que contiene las referencias a todas las vistas del item.
     */
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio;
        TextView tvCategorias; // Referencia al TextView para las categorías (ID: permisos)
        ImageView imgEstado, imgProducto;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Enlazar cada variable con su ID en el layout XML
            tvNombre = itemView.findViewById(R.id.nombre);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvPrecio = itemView.findViewById(R.id.id);
            tvCategorias = itemView.findViewById(R.id.permisos); // ID del TextView de categorías
            imgEstado = itemView.findViewById(R.id.estado);
            imgProducto = itemView.findViewById(R.id.fotoTienda);
        }
    }
}