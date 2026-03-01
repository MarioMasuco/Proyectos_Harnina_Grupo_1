package com.example.proyectos_harnina_grupo_1.localmarket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectos_harnina_grupo_1.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PantallaInvernadero extends AppCompatActivity {

    private ImageView btnFavorito;
    private ImageView btnInvernadero;
    private ImageView btnAjuste;

    private RecyclerView recyclerSensores;
    private SensorAdapter adapter;
    private List<SensorItem> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_invernadero);

        inicializarVistas();
        configurarSensores();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnFavorito    = findViewById(R.id.btnFavorito);
        btnInvernadero = findViewById(R.id.btnInvernadero);
        btnAjuste      = findViewById(R.id.btnAjuste);

        recyclerSensores = findViewById(R.id.recyclerSensores);
        recyclerSensores.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarSensores() {

        sensorList = new ArrayList<>();

        sensorList.add(new SensorItem(
                "Presión atmosférica", "-", R.drawable.presion,
                "sensor.nodered_1e5a20d2b24a988a"
        ));
        sensorList.add(new SensorItem(
                "Temperatura", "-", R.drawable.temperatura,
                "sensor.nodered_401cd91308d88e24"
        ));
        sensorList.add(new SensorItem(
                "Humedad", "-", R.drawable.humedad,
                "sensor.nodered_bc5a1c3b0f79325b"
        ));
        sensorList.add(new SensorItem(
                "Luminosidad", "-", R.drawable.luz,
                "sensor.nodered_7542101674aa9aa0"
        ));
        sensorList.add(new SensorItem(
                "Temperatura tierra", "-", R.drawable.temptierra,
                "sensor.sensor_temperatura_invernadero"
        ));
        sensorList.add(new SensorItem(
                "Humedad tierra", "-", R.drawable.humetierra,
                "sensor.sensor_humedad_invernadero"
        ));

        adapter = new SensorAdapter(sensorList);
        recyclerSensores.setAdapter(adapter);

        for (SensorItem sensor : sensorList) {
            HomeAssistantApi.getSensorState(sensor.getEntityId(),
                    new HomeAssistantApi.SensorCallback() {

                        @Override
                        public void onSuccess(JSONObject json) {
                            Log.d("HA_API", json.toString());

                            String state = json.optString("state", "?");
                            JSONObject attrs = json.optJSONObject("attributes");
                            String unit = attrs != null
                                    ? attrs.optString("unit_of_measurement", "")
                                    : "";

                            String valorFinal = state + " " + unit;

                            runOnUiThread(() ->
                                    adapter.updateSensorValue(
                                            sensor.getEntityId(), valorFinal
                                    )
                            );
                        }

                        @Override
                        public void onError(IOException e, int code) {
                            runOnUiThread(() ->
                                    adapter.updateSensorValue(
                                            sensor.getEntityId(), "Error"
                                    )
                            );
                        }
                    });
        }
    }

    private void configurarEventos() {

        btnFavorito.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaFavorito.class))
        );

        btnInvernadero.setOnClickListener(v -> {
            Intent intent = new Intent(this, PantallaCatalogo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnAjuste.setOnClickListener(v ->
                startActivity(new Intent(this, PantallaConfiguracion.class))
        );
    }
}