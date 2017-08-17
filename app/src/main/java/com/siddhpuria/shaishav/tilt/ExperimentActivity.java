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
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private SensorListener sensorListener;
    private SensorInterpreter sensorInterpreter;

    private ExperimentConfig experimentConfig;
    private ExperimentView experimentView;

    private int currentLevel = 0;
    private int taskIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String configString = intent.getStringExtra(MainActivity.EXPERIMENT_CONFIG);
        experimentConfig = new ExperimentConfig(configString);

        setContentView(R.layout.activity_experiment);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;
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

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        sensorListener = new SensorListener(this);
        sensorListener.registerSensorRotationVector(this, SensorManager.SENSOR_DELAY_GAME);
        sensorInterpreter = new SensorInterpreter(experimentConfig);
        showMenusAtCurrentLevel();
        updateExperimentView();

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

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
            System.out.println(menuValue);
            if (menuValue.isEmpty()) {
                // no more menu
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

    public void updateExperimentView() {

        experimentView.setTaskLevelString("Tasks: "+Integer.toString(taskIndex)+"/"+experimentConfig.getTotalTasks());
        experimentView.setTaskString("Current Task: "+experimentConfig.getTaskStringAtIndex(taskIndex));
        showMenusAtCurrentLevel();

    }
}
