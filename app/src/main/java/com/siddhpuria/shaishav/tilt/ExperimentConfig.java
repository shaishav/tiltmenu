package com.siddhpuria.shaishav.tilt;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ExperimentConfig.java
 * Handle configuration data to launch the experiment.
 */

public class ExperimentConfig {

    private JSONObject configData;
    public final static String PARTICIPANT_KEY = "ParticipantName";
    public final static String INTERACTION_KEY = "InteractionType";
    public final static String SENSITIVITY_KEY = "Sensitivity";
    public final static String AXIS_INVERT_KEY = "AxisInverted";

    public ExperimentConfig() {

        configData = new JSONObject();

        // initialize with default values
        try {
            configData.put(PARTICIPANT_KEY, "NoName");
            configData.put(INTERACTION_KEY, 2);
            configData.put(SENSITIVITY_KEY, 1);
            configData.put(AXIS_INVERT_KEY, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void setParticipantID(String val) {

        try {
            configData.put(PARTICIPANT_KEY, val);
        } catch (JSONException e) { e.printStackTrace(); }

    }

    public void setInteraction(int numZones) {

        try {
            configData.put(INTERACTION_KEY, numZones);
        } catch (JSONException e) { e.printStackTrace(); }

    }

    public int getNumZones() {

        try {
            return configData.getInt(INTERACTION_KEY);
        } catch (JSONException e) { e.printStackTrace(); return 0; }

    }


    public void setSensitivity(int factor) {

        try {
            configData.put(SENSITIVITY_KEY, factor);
        } catch (JSONException e) { e.printStackTrace(); }

    }

    public int getSensitivity() {

        try {
            return configData.getInt(SENSITIVITY_KEY);
        } catch (JSONException e) { e.printStackTrace(); return 0; }

    }

    public void setAxisInverted(boolean flag) {

        try {
            configData.put(AXIS_INVERT_KEY, flag);
        } catch (JSONException e) { e.printStackTrace(); }

    }

    public boolean getAxisInverted() {

        try {
            return configData.getBoolean(AXIS_INVERT_KEY);
        } catch (JSONException e) { e.printStackTrace(); return true; }

    }

    public String getStringRepresentation() {

        return configData.toString();

    }

}
