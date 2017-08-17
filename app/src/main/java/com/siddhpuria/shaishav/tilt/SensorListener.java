package com.siddhpuria.shaishav.tilt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SensorListener.java
 * Listener class for various sensor events. This can be extended by registering listener methods
 * for different sensor hardware and adding its corresponding interpreter in SensorInterpreter.
 */

public class SensorListener implements SensorEventListener2 {

    private SensorManager sensorManager;
    Sensor rotationVectorSensor;
    private HashMap<Sensor, ArrayList<ModelObserver>> sensorObserversMap;


    public SensorListener(Context ctx) {
        sensorManager = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        sensorObserversMap = new HashMap<>();
        sensorObserversMap.put(rotationVectorSensor, new ArrayList<ModelObserver>());
    }

    public void registerSensorRotationVector(ModelObserver obs, int samplingFrequency) {
        sensorManager.registerListener(this, rotationVectorSensor, samplingFrequency);
        sensorObserversMap.get(rotationVectorSensor).add(obs);
    }

    public void unregisterSensorRotationVector() {
        sensorManager.unregisterListener(this, rotationVectorSensor);
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {

            for (ModelObserver obs : sensorObserversMap.get(rotationVectorSensor)) {
                obs.notifySensorEventUpdate(event);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
