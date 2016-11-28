package org.autosense.app;

import android.app.Application;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.util.Log;

import org.autosense.commons.AppConfig;
import org.autosense.services.BeaconService;

public class AutoSense extends Application {

    private AppConfig appConfig;
    private BeaconService beaconService;

    public boolean isInitialized = false;

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
        appConfig.setOperatingMode(AppConfig.APP_MODE_MEASURE_BEACON_RSSI);

        return true;
    }


    public void initializeBLEServices(BluetoothManager bluetoothManager){
        if(beaconService == null)
            beaconService = new BeaconService(bluetoothManager);

        Log.i("Info", "BLE Services Initialized");
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public BeaconService getBeaconService() {
        return beaconService;
    }
}
