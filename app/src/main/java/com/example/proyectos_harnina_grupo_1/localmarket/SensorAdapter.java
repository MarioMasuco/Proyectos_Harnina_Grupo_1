package com.example.proyectos_harnina_grupo_1.localmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    private final List<SensorItem> sensorList;

    public SensorAdapter(List<SensorItem> sensorList) {
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SensorItem sensor = sensorList.get(position);

        holder.txtNombreSensor.setText(sensor.getNombre());
        holder.txtInfoSensor.setText(sensor.getValor());

        holder.imgSensor.setImageResource(sensor.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    public void updateSensorValue(String entityId, String valor) {
        for (int i = 0; i < sensorList.size(); i++) {
            if (sensorList.get(i).getEntityId().equals(entityId)) {
                sensorList.get(i).setValor(valor);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSensor;
        TextView txtNombreSensor;
        TextView txtInfoSensor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSensor = itemView.findViewById(R.id.imgSensor);
            txtNombreSensor = itemView.findViewById(R.id.txtNombreSensor);
            txtInfoSensor = itemView.findViewById(R.id.txtInfoSensor);
        }
    }
}