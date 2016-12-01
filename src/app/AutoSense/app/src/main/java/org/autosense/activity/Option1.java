package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;
import org.autosense.services.BeaconService;

public class Option1 extends AppCompatActivity {

    private AppConfig appConfig;
    private BeaconService beaconService;
    private leDeviceCallback leDeviceCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option1);

        beaconService = ((AutoSense) getApplication()).getBeaconService();
        appConfig = ((AutoSense) getApplication()).getAppConfig();



        leDeviceCallback = new leDeviceCallback();
        beaconService.startBeaconScan(leDeviceCallback);

    }

    class leDeviceCallback extends ScanCallback {
        public void onScanResult(int callbackType, ScanResult result) {

            TextView rssivalue;
            String deviceName = result.getDevice().getName();

            if (deviceName == null || deviceName == "") {
                deviceName = result.getDevice().getAddress();
            }

            if (appConfig.getBeaconName().equals(deviceName)) // Scanning for required beacon
            {
                rssivalue = (TextView) findViewById(R.id.RSSIvalue);
                rssivalue.setText("RSSI     :    " + result.getRssi());  //getting RSSI value of required beacon
            }
        }
    }
}
