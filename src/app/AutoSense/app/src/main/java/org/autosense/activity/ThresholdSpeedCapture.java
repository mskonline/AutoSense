package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;
import org.autosense.services.BeaconService;

import java.util.Map;

public class ThresholdSpeedCapture extends AppCompatActivity {

    private TextView beaconNameTView, beaconURLTView, beaconReadings;
    private Button stopBtn;

    private BeaconService beaconService;
    private AppConfig appConfig;
    private leDeviceCallback leDeviceCallback;

    private Map<String, String> thresholdSpeedReadings;

    private String measureSpeed, beaconName;
    boolean isDetected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold_speed_capture);

        beaconNameTView = (TextView) findViewById(R.id.op3_2_beaconName);
        beaconURLTView = (TextView) findViewById(R.id.op3_2_beaconURL);
        beaconReadings = (TextView) findViewById(R.id.op3_2_speedReadings);

        stopBtn = (Button) findViewById(R.id.op3_2_stopBtn);

        beaconService = ((AutoSense) getApplication()).getBeaconService();
        appConfig = ((AutoSense) getApplication()).getAppConfig();
        thresholdSpeedReadings = ((AutoSense) getApplication()).getThresholdSpeedReadings();
        beaconName = appConfig.getBeaconName();

        leDeviceCallback = new leDeviceCallback();
        beaconService.startBeaconScan(leDeviceCallback);

        Bundle extras = getIntent().getExtras();
        measureSpeed = extras.getString("cSpeed");

        beaconNameTView.setText("Beacon : " + appConfig.getBeaconName());
        beaconURLTView.setText("URL : " + appConfig.getBeaconURL());

        for(String key : thresholdSpeedReadings.keySet()) {
            beaconReadings.append(key + " MPH \t\t" + thresholdSpeedReadings.get(key) + "\n");
        }


        stopBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(isDetected){
                    finish();
                } else {
                    beaconService.stopBeaconScan(leDeviceCallback);

                    thresholdSpeedReadings.put(measureSpeed, "NOT DETECTED");
                    beaconReadings.append(measureSpeed + " MPH" + getString(R.string.tab) + "NOT DETECTED");
                }
            }
        });
    }

    private void updateReading(){
        beaconService.stopBeaconScan(leDeviceCallback);

        thresholdSpeedReadings.put(measureSpeed, "DETECTED");
        beaconReadings.append(measureSpeed + " MPH "+ getString(R.string.tab) + "DETECTED");

        isDetected = true;
        stopBtn.setText("Back");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        beaconService.stopBeaconScan(leDeviceCallback);
    }

    class leDeviceCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            String deviceName = result.getDevice().getName();

            if(deviceName == null || deviceName == ""){
                deviceName = result.getDevice().getAddress();
            }

            if(beaconName.equals(deviceName)){
                updateReading();
            }
        }
    }
}
