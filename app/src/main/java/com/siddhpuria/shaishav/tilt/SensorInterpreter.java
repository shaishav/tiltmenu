package com.siddhpuria.shaishav.tilt;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * SensorInterpreter.java
 * Interpreter class to facilitate conversion from raw sensor data to screen coord
 */

public class SensorInterpreter {

    public ArrayList<InterpretFrame> interpretFrames = new ArrayList<>();
    private int inversion = -1;
    private float tiltGain = 25.0f;
    private ExperimentConfig experimentConfig;

    public SensorInterpreter(ExperimentConfig config) {

        experimentConfig = config;
        tiltGain = (experimentConfig.getSensitivity()+10);
        inversion = config.getAxisInverted() ? 1 : -1;

    }

    // convert from raw rotation vector to yaw-pitch-roll, normalize, add tilt gain, invert (or not)
    public InterpretFrame interpretRotationVectorData(SensorEvent event) {

        if (interpretFrames.isEmpty()) {
            float[] ypr = new float[3];
            rotVectorToYPR(event.values, ypr);
            float pitchDegrees = (float) Math.toDegrees(ypr[1]);
            float rollDegrees = (float) Math.toDegrees(ypr[2]);

            interpretFrames.add(new InterpretFrame(event.timestamp, rollDegrees, pitchDegrees, 0.0f, 0.0f));
        } else {
            InterpretFrame centerFrame =  interpretFrames.get(0);

            float[] ypr = new float[3];
            rotVectorToYPR(event.values, ypr);

            float pitchDegrees = (float) Math.toDegrees(ypr[1]);
            float rollDegrees = (float) Math.toDegrees(ypr[2]);

            float sensorXdelta = -inversion * (rollDegrees - centerFrame.inputSensorX);
            float currentX = tiltGain * (sensorXdelta + centerFrame.screenOffsetX);

            float sensorYdelta = inversion * (pitchDegrees - centerFrame.inputSensorY);
            float currentY = tiltGain * (sensorYdelta + centerFrame.screenOffsetY);

            interpretFrames.add(new InterpretFrame(event.timestamp, event.values[0], event.values[1], currentX, currentY));
        }

        return interpretFrames.get(interpretFrames.size()-1);

    }

    // use the Android libraries to convert the rotation vector from quaternions to its yaw-pitch-roll
    public void rotVectorToYPR(float[] inputRotationVector, float[] outputYPR) {

        float[] R = new float[9];
        SensorManager.getRotationMatrixFromVector(R, inputRotationVector);
        SensorManager.getOrientation(R, outputYPR);

    }

    // recalibrate for center by clearing the frames list (i.e. the reference frame)
    public void recalibrateCenter() {

        interpretFrames.clear();

    }

    public class InterpretFrame {

        public InterpretFrame(long ts, float sensorX, float sensorY, float offsetX, float offsetY) {
            timestamp = ts;
            inputSensorX = sensorX;
            inputSensorY = sensorY;
            screenOffsetX = offsetX;
            screenOffsetY = offsetY;
        }

        public long timestamp;
        public float inputSensorX;
        public float inputSensorY;
        public float screenOffsetX;
        public float screenOffsetY;

    }

}
