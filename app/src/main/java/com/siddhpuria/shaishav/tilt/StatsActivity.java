package com.siddhpuria.shaishav.tilt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * StatsActivity.java
 * Show raw stats information when an experiment successfully finishes with the option to share text to various services.
 */
public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Intent intent = getIntent();
        final String statsData = intent.getStringExtra(MainActivity.EXPERIMENT_STATS);

        TextView dataView = (TextView) findViewById(R.id.stats_text_view);
        dataView.setText(statsData);

        Button shareButton = (Button) findViewById(R.id.stats_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, statsData);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


    }
}
