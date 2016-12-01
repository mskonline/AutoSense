package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;
import org.autosense.services.BeaconService;

import java.util.Map;

public class ThresholdSpeedCapture extends AppCompatActivity {

    private TextView beaconNameTView, beaconReadings;

    private BeaconService beaconService;
    private AppConfig appConfig;
    private leDeviceCallback leDeviceCallback;

    private Map<String, String> thresholdSpeedReadings;

    private String measureSpeed, beaconName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold_speed_capture);

        beaconNameTView = (TextView) findViewById(R.id.op3_2_beaconName);
        beaconReadings = (TextView) findViewById(R.id.op3_2_speedReadings);

        beaconService = ((AutoSense) getApplication()).getBeaconService();
        appConfig = ((AutoSense) getApplication()).getAppConfig();
        thresholdSpeedReadings = ((AutoSense) getApplication()).getThresholdSpeedReadings();
        beaconName = appConfig.getBeaconName();

        leDeviceCallback = new leDeviceCallback();
        beaconService.startBeaconScan(leDeviceCallback);

        Bundle extras = getIntent().getExtras();
        measureSpeed = extras.getString("cSpeed");

        beaconNameTView.setText("Beacon : " + appConfig.getBeaconName());

        for(String key : thresholdSpeedReadings.keySet()) {
            beaconReadings.append(key + " MPH \t" + thresholdSpeedReadings.get(key) + "\n");
        }
    }

    private void updateReading(){
        beaconService.stopBeaconScan(leDeviceCallback);

        thresholdSpeedReadings.put(measureSpeed, "DETECTED");
        beaconReadings.append(measureSpeed + " MPH \t" + "DETECTED");
    }

    private void refresh(){

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
