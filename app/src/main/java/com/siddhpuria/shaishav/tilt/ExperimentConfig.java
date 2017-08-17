package com.siddhpuria.shaishav.tilt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ExperimentConfig.java
 * Handle configuration data to launch the experiment.
 */

public class ExperimentConfig {

    private JSONObject configData;
    public final static String PARTICIPANT_KEY = "ParticipantName";
    public final static String INTERACTION_KEY = "NumZones";
    public final static String SENSITIVITY_KEY = "Sensitivity";
    public final static String AXIS_INVERT_KEY = "AxisInverted";

    public static JSONObject TwoWayMenu;
    public static ArrayList<String> TwoWayTasks;

    public static JSONObject FourWayMenu;
    public static ArrayList<String> FourWayTasks;


    public ExperimentConfig() {

        BuildTwoWayMenu();
        BuildFourWayMenu();

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

    public static void BuildTwoWayMenu() {

        TwoWayMenu = new JSONObject();

        try {
            TwoWayMenu.put("levels", "4");

            TwoWayMenu.put("1", "Actions");
                TwoWayMenu.put("11", "Send");
                    TwoWayMenu.put("111", "Stats");
                        TwoWayMenu.put("1111", "Location");
                        TwoWayMenu.put("1112", "Pulse");
                    TwoWayMenu.put("112", "Communication");
                        TwoWayMenu.put("1121", "Wave");
                        TwoWayMenu.put("1122", "Text");
                TwoWayMenu.put("12", "Settings");
                    TwoWayMenu.put("121", "Systems");
                        TwoWayMenu.put("1211", "Bluetooth");
                        TwoWayMenu.put("1212", "GPS");
                    TwoWayMenu.put("122", "Modes");
                        TwoWayMenu.put("1221", "Do not disturb");
                        TwoWayMenu.put("1222", "Sleep");

            TwoWayMenu.put("2", "Info");
                TwoWayMenu.put("21", "Calendar");
                    TwoWayMenu.put("211", "View");
                        TwoWayMenu.put("2111", "Today");
                        TwoWayMenu.put("2112", "Tomorrow");
                    TwoWayMenu.put("212", "Change");
                        TwoWayMenu.put("2121", "Add Event");
                        TwoWayMenu.put("2122", "Cancel Event");
                TwoWayMenu.put("22", "Health");
                        TwoWayMenu.put("221", "Cardiac");
                            TwoWayMenu.put("2211", "Pulse");
                            TwoWayMenu.put("2212", "O2");
                        TwoWayMenu.put("222", "Energy");
                            TwoWayMenu.put("2221", "Calories");
                            TwoWayMenu.put("2222", "Steps");

        } catch (JSONException e) { e.printStackTrace(); }

        TwoWayTasks = new ArrayList<>(Arrays.asList("1112", "1122", "1221", "1222", "2111", "2112", "2211", "2221"));


    }

    public String menuOptionAt(int level) {

        String key = Integer.toString(level);
        return menuOptionAt(key);

    }

    public String menuOptionAt(String level) {

        String key = level;
        try {
            if (getNumZones() == 2) {
                if (key.length() > Integer.valueOf(TwoWayMenu.getString("levels"))) {
                    return "";
                }

                return TwoWayMenu.getString(key);
            } else if (getNumZones() == 4) {
                if (key.length() > Integer.valueOf(FourWayMenu.getString("levels"))) {
                    return "";
                }

                return FourWayMenu.getString(key);
            }
        } catch (JSONException e) { e.printStackTrace(); }

        return "";

    }

    public String getTaskStringAtIndex(int taskIndex) {

        String key = getNumZones() == 2 ? TwoWayTasks.get(taskIndex) : getNumZones() == 4 ? FourWayTasks.get(taskIndex) : "";

        if (key.length() == 0) {
            return "";
        }

        String taskString = "";
        for (int i = 1; i < key.length(); i++) {
            taskString += menuOptionAt(key.substring(0,i)) +" -> ";
        }
        taskString += menuOptionAt(key);

        return taskString;
    }

    public String getExpectedHitAtIndex(int taskIndex) {
        return getNumZones() == 2 ? TwoWayTasks.get(taskIndex) : getNumZones() == 4 ? FourWayTasks.get(taskIndex) : "";
    }

    public int getTotalTasks() {

        int totalTasks = getNumZones() == 2 ? TwoWayTasks.size() : getNumZones() == 4 ? FourWayTasks.size() : 0;
        return totalTasks;

    }

    public static void BuildFourWayMenu() {

        FourWayMenu = new JSONObject();

        try {

            FourWayMenu.put("levels", "2");

            FourWayMenu.put("1", "Send");
                FourWayMenu.put("11", "Location");
                FourWayMenu.put("12", "Pulse");
                FourWayMenu.put("13", "Wave");
                FourWayMenu.put("14", "Text");

            FourWayMenu.put("2", "Settings");
                FourWayMenu.put("21", "Bluetooth");
                FourWayMenu.put("22", "GPS");
                FourWayMenu.put("23", "Do not disturb");
                FourWayMenu.put("24", "Sleep");

            FourWayMenu.put("3", "Calendar");
                FourWayMenu.put("31", "Today");
                FourWayMenu.put("32", "Tomorrow");
                FourWayMenu.put("33", "Add Event");
                FourWayMenu.put("34", "Cancel Event");

            FourWayMenu.put("4", "Health");
                FourWayMenu.put("41", "Pulse");
                FourWayMenu.put("42", "O2");
                FourWayMenu.put("43", "Calories");
                FourWayMenu.put("44", "Steps");

            FourWayTasks = new ArrayList<>(Arrays.asList("12", "14", "23", "24", "31", "32", "41", "43"));

        } catch (JSONException e) { e.printStackTrace(); }

    }

    public ExperimentConfig(String stringRepr) {

        try {
            System.out.println("stringRepr "+stringRepr);
            configData = new JSONObject(stringRepr);
        } catch (JSONException e) { e.printStackTrace(); }

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
