package com.siddhpuria.shaishav.tilt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.ArrayList;

/**
 * ExperimentActivity.java
 * This assembles the tilt-menu interaction specifically to the configuration passed.
 */
public class ExperimentActivity extends AppCompatActivity implements ModelObserver, ExperimentView.HitObserver {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private View mContentView;
    private View mControlsView;

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    private SensorListener sensorListener;
    private SensorInterpreter sensorInterpreter;

    private ExperimentConfig experimentConfig;
    private ExperimentView experimentView;
    private ExperimentStats experimentStats;

    private int currentLevel = 0;
    private int taskIndex = 0;
    private long currentTaskStartTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String configString = intent.getStringExtra(MainActivity.EXPERIMENT_CONFIG);
        experimentConfig = new ExperimentConfig(configString);
        experimentStats = new ExperimentStats(experimentConfig);

        setContentView(R.layout.activity_experiment);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        experimentView = new ExperimentView(this, experimentConfig);
        experimentView.registerForHits(this);
        mContentView = experimentView;

        setContentView(mContentView);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorInterpreter.recalibrateCenter();
                currentLevel = 0;
                showMenusAtCurrentLevel();
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        sensorListener = new SensorListener(this);
        sensorListener.registerSensorRotationVector(this, SensorManager.SENSOR_DELAY_GAME);
        sensorInterpreter = new SensorInterpreter(experimentConfig);
        showMenusAtCurrentLevel();
        updateExperimentView();
        currentTaskStartTime = System.currentTimeMillis();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        sensorListener.unregisterSensorRotationVector();
        super.onDestroy();
    }

    /**
     * Receive sensor updates from the subject
     */
    @Override
    public void notifySensorEventUpdate(SensorEvent event) {

        SensorInterpreter.InterpretFrame frame = sensorInterpreter.interpretRotationVectorData(event);
        experimentView.updatePointerPosition(frame.screenOffsetX, frame.screenOffsetY);

    }

    @Override
    public void notifyHit(int menuIndex) {
        currentLevel = currentLevel == 0 ? menuIndex : (currentLevel * 10) + menuIndex;
        showMenusAtCurrentLevel();
    }

    private void showMenusAtCurrentLevel() {

        ArrayList<String> newOpts = new ArrayList<>();

        for (int i = 1; i <= experimentConfig.getNumZones(); i++) {
            int key = (currentLevel * 10) + i;
            String menuValue = experimentConfig.menuOptionAt(key);
            if (menuValue.isEmpty()) {
                String hit = Integer.toString(currentLevel);
                String expected = experimentConfig.getExpectedHitAtIndex(taskIndex);
                boolean success = hit.equals(expected) ? true : false;
                long deltaTime = System.currentTimeMillis() - currentTaskStartTime;
                experimentStats.addTaskResult(experimentConfig.getTaskStringAtIndex(taskIndex),success, deltaTime);
                currentTaskStartTime = System.currentTimeMillis();
                taskIndex++;
                currentLevel = 0;
                updateExperimentView();
                return;
            }
            newOpts.add(menuValue);
        }

        String[] optionsArray = newOpts.toArray(new String[newOpts.size()]);
        ((ExperimentView) mContentView).menuZone.setOptions(optionsArray);

    }

    private void updateExperimentView() {

        if (taskIndex >= experimentConfig.getTotalTasks()) {
            cleanupAndFinish();
        } else {
            experimentView.setTaskLevelString("Tasks: " + Integer.toString(taskIndex) + "/" + experimentConfig.getTotalTasks());
            experimentView.setTaskString("Current Task: " + experimentConfig.getTaskStringAtIndex(taskIndex));
            showMenusAtCurrentLevel();
        }

    }

    private void cleanupAndFinish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.EXPERIMENT_STATS, experimentStats.getStringRepresentation());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
