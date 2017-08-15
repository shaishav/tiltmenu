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

            double yprMag = Math.sqrt(Math.pow(ypr[0], 2)+Math.pow(ypr[1], 2)+Math.pow(ypr[2], 2));

//            ypr[0] /= yprMag;
//            ypr[1] /= yprMag;
//            ypr[2] /= yprMag;

//            System.out.printf("x %f y %f z %f\n", x, y, z);
//            System.out.printf("yaw %f pitch %f roll %f\n", Math.toDegrees(ypr[0]), Math.toDegrees(ypr[1]), Math.toDegrees(ypr[2]));
            float pitchDegrees = (float) Math.toDegrees(ypr[1]);
            float rollDegrees = (float) Math.toDegrees(ypr[2]);

            float sensorXdelta = -inversion * (rollDegrees - centerFrame.inputSensorX);
            float currentX = 25 * (sensorXdelta + centerFrame.screenOffsetX);

            float sensorYdelta = inversion * (pitchDegrees - centerFrame.inputSensorY); //500 * (event.values[1] - lastFrame.inputSensorY);
            float currentY = 25 * (sensorYdelta + centerFrame.screenOffsetY);

//            System.out.printf("direction angle %f\n", Math.toDegrees(Math.atan(ypr[1]/ypr[2])));

            interpretFrames.add(new InterpretFrame(event.timestamp, event.values[0], event.values[1], currentX, currentY));
        }

        return interpretFrames.get(interpretFrames.size()-1);

    }

    public void rotVectorToYPR(float[] inputRotationVector, float[] outputYPR) {

        float[] R = new float[9];
        SensorManager.getRotationMatrixFromVector(R, inputRotationVector);
        SensorManager.getOrientation(R, outputYPR);

    }

    public void setShouldInvert(boolean flag) {

        inversion = flag ? -1 : 1;

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
