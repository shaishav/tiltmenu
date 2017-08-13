package com.siddhpuria.shaishav.tilt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

/**
 * SensorListener.java
 * Listener class for various sensor events. This can be extended by registering listener methods
 * for different sensor hardware and adding its corresponding interpreter in SensorInterpreter.
 */

public class SensorListener implements SensorEventListener2 {

    private SensorManager sensorManager;
    Sensor rotationVectorSensor;

    public SensorListener(Context ctx) {
        sensorManager = (SensorManager) ctx.getSystemService(ctx.SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void registerSensorRotationVector(int samplingFrequency) {
        sensorManager.registerListener(this, rotationVectorSensor, samplingFrequency);
    }

    public void unregisterSensorRotationVector() {
        sensorManager.unregisterListener(this, rotationVectorSensor);
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            System.out.println(+event.values[0]+" "+event.values[1]+" "+event.values[2]+" ");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
