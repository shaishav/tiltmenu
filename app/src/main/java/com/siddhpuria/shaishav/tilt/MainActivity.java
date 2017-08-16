package com.siddhpuria.shaishav.tilt;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * MainActivity.java
 * Launcher activity of the app that displays experiment configuration options and launches the
 * activity for the experiment based on the configuration.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXPERIMENT_CONFIG = "com.shaishav.siddhpuria.mainactivity.experimentconfig";
    private ExperimentConfig experimentConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        experimentConfig = new ExperimentConfig();


        final EditText participantNameField = (EditText) findViewById(R.id.participant_id);
        participantNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String fieldValue = v.getText().toString();
                experimentConfig.setParticipantID(fieldValue);
                return false;
            }
        });

        final RadioButton twoZoneRadio = (RadioButton) findViewById(R.id.variant_twozone_radio);
        final RadioButton fourZoneRadio = (RadioButton) findViewById(R.id.variant_fourzone_radio);

        twoZoneRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    experimentConfig.setInteraction(2);
                } else {
                    fourZoneRadio.setChecked(!isChecked);
                }
            }
        });


        fourZoneRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    experimentConfig.setInteraction(4);
                } else {
                    twoZoneRadio.setChecked(!isChecked);
                }

            }
        });

        twoZoneRadio.setChecked(experimentConfig.getNumZones()==2);
        fourZoneRadio.setChecked(experimentConfig.getNumZones()==4);

        final SeekBar sensitivity = (SeekBar) findViewById(R.id.sensitivity_bar);
        sensitivity.setProgress(experimentConfig.getSensitivity());
        sensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                experimentConfig.setSensitivity(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { return; }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { return; }
        });

        final ToggleButton invertAxisToggle = (ToggleButton) findViewById(R.id.invert_axis_toggle);
        invertAxisToggle.setChecked(experimentConfig.getAxisInverted());
        invertAxisToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                experimentConfig.setAxisInverted(isChecked);
            }
        });


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
        intent.putExtra(EXPERIMENT_CONFIG, experimentConfig.getStringRepresentation());
        System.out.println("config: "+experimentConfig.getStringRepresentation());
        startActivity(intent);
    }

}
