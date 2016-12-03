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

public class MeasureRSSI extends AppCompatActivity {

    private AppConfig appConfig;
    private BeaconService beaconService;
    private leDeviceCallback leDeviceCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_rssi);

        beaconService = ((AutoSense) getApplication()).getBeaconService();
        appConfig = ((AutoSense) getApplication()).getAppConfig();

        leDeviceCallback = new leDeviceCallback();
        beaconService.startBeaconScan(leDeviceCallback);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        beaconService.stopBeaconScan(leDeviceCallback);
    }

    class leDeviceCallback extends ScanCallback {
        public void onScanResult(int callbackType, ScanResult result) {
            TextView rssivalue;
            String deviceName = result.getDevice().getName();

            if (deviceName == null || deviceName == "") {
                deviceName = result.getDevice().getAddress();
            }

            // Scanning for required beacon
            if (appConfig.getBeaconName().equals(deviceName)) {
                rssivalue = (TextView) findViewById(R.id.RSSIvalue);

                //getting RSSI value of required beacon
                rssivalue.setText("RSSI : " + result.getRssi() + "dBm");
            }
        }
    }
}
