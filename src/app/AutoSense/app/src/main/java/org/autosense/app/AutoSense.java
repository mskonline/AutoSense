package org.autosense.app;

import android.app.Application;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;

import org.autosense.commons.AppConfig;
import org.autosense.services.BeaconService;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoSense extends Application {

    private AppConfig appConfig;
    private BeaconService beaconService;

    public boolean isInitialized = false;
    private Map<String, String> thresholdSpeedReadings;

    @Override
    public void onCreate(){

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            isInitialized = false;
            return;
        }

        isInitialized = setupApp();
    }

    private boolean setupApp(){
        appConfig = AppConfig.getInstance();
        thresholdSpeedReadings = new LinkedHashMap<String, String>();

        return true;
    }


    public void initializeBLEServices(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);

        if(beaconService == null)
            beaconService = new BeaconService(bluetoothManager);
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public BeaconService getBeaconService() {
        return beaconService;
    }

    public Map<String, String> getThresholdSpeedReadings() {
        return thresholdSpeedReadings;
    }

    public void setThresholdSpeedReadings(Map<String, String> thresholdSpeedReadings) {
        this.thresholdSpeedReadings = thresholdSpeedReadings;
    }
}
