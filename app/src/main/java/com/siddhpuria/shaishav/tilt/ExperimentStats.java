package com.siddhpuria.shaishav.tilt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ExperimentStats.java
 * Used for logging and reporting experiment results
 */

public class ExperimentStats {

    private JSONObject statsObject;
    private JSONArray tasksArray;
    private int incorrectTasks = 0;
    private int correctTasks = 0;

    private static final String TOTAL_TASKS_KEY = "Total Tasks";
    private static final String CORRECT_TASKS_KEY = "Correct Tasks";
    private static final String INCORRECT_TASKS_KEY = "Incorrect Tasks";
    private static final String TASK_DETAIL_KEY = "Task Details";
    private static final String TASK_NAME_KEY = "Task";
    private static final String TASK_RESULT_KEY = "Result";
    private static final String TASK_TIME_KEY = "Time (ms)";


    public ExperimentStats(ExperimentConfig config) {
        try {
            statsObject = new JSONObject();
            tasksArray = new JSONArray();
            statsObject.put(TOTAL_TASKS_KEY, config.getTotalTasks());
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public String getStringRepresentation() {
        summarize();
        return statsObject.toString();
    }

    public void addTaskResult(String task, boolean correct, long time) {

        correctTasks = correct ? correctTasks+1 : correctTasks;
        incorrectTasks = correct ? incorrectTasks : incorrectTasks+1;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK_NAME_KEY, task);
            jsonObject.put(TASK_RESULT_KEY, correct ? "Correct" : "Incorrect");
            jsonObject.put(TASK_TIME_KEY, time);

            tasksArray.put(tasksArray.length(), jsonObject);
        } catch (JSONException e) { e.printStackTrace(); }
    }

    public void summarize() {
        try {

            statsObject.put(CORRECT_TASKS_KEY, correctTasks);
            statsObject.put(INCORRECT_TASKS_KEY, incorrectTasks);
            statsObject.put(TASK_DETAIL_KEY, tasksArray);
        } catch (JSONException e) { e.printStackTrace(); }

    }

}
