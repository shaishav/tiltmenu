package com.siddhpuria.shaishav.tilt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    public void launchExperimentActivity() {
        Intent intent = new Intent(this, ExperimentActivity.class);
        String configString = getExperimentConfig();
        intent.putExtra(EXPERIMENT_CONFIG, configString);
        startActivity(intent);
    }

    public String getExperimentConfig() {
        return "";
    }

}
