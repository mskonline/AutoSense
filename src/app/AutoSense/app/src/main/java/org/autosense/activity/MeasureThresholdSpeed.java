package org.autosense.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;

public class MeasureThresholdSpeed extends AppCompatActivity {

    private TextView beaconName;
    private AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_threshold_speed);

        beaconName = (TextView) findViewById(R.id.op3BeaconName);

        appConfig = ((AutoSense) getApplication()).getAppConfig();

        beaconName.setText(appConfig.getBeaconName());
    }
}
