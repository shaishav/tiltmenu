package com.siddhpuria.shaishav.tilt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * MainActivity.java
 * Launcher activity of the app that displays experiment configuration options and launches the
 * activity for the experiment based on the configuration.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXPERIMENT_CONFIG = "com.shaishav.siddhpuria.mainactivity.experimentconfig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchExperimentButton = (Button) findViewById(R.id.start_experiment_button);
        launchExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchExperimentActivity();
            }
        });


    }

    /**
     * Launches the experiment activity based on the configuration options selected.
     */
    public void launchExperimentActivity() {
        Intent intent = new Intent(this, ExperimentActivity.class);
        String configString = getExperimentConfig();
        intent.putExtra(EXPERIMENT_CONFIG, configString);
        startActivity(intent);
    }

    /**
     * TODO gathers the configuration options in the MainActivity layout.
     */
    public String getExperimentConfig() {
        return "";
    }

}
