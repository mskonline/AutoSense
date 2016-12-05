package org.autosense.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;

public class MeasureThresholdSpeed extends AppCompatActivity {

    private TextView beaconName;
    private EditText speed;
    private Button startButton;

    private AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_threshold_speed);

        beaconName = (TextView) findViewById(R.id.op3BeaconName);
        startButton = (Button) findViewById(R.id.op3startbtn);
        speed = (EditText) findViewById(R.id.op3SpeedEditText);

        appConfig = ((AutoSense) getApplication()).getAppConfig();
        beaconName.setText(appConfig.getBeaconName());

        startButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MeasureThresholdSpeed.this, ThresholdSpeedCapture.class);
                i.putExtra("cSpeed", speed.getText().toString());
                startActivity(i);
            }
        });
    }
}
