package org.autosense.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;

import android.util.Log;

import java.util.ArrayList;

public class BeaconService {
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;

    private boolean isInitialized = false;

    public BeaconService(BluetoothManager bluetoothManager) {
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Log.i("Error", "Bluetooth adaptor not supported");
            return;
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothLeScanner == null) {
            Log.d("Error", "No Bluetooth scanner");
            return;
        }

        isInitialized = true;
    }

    public void startBeaconScan(ScanCallback callback) {
        if (!isInitialized) {
            Log.d("Info", "Not initialized");
            return;
        }

        ArrayList<ScanFilter> scanFilters = new ArrayList<ScanFilter>();

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bluetoothLeScanner.startScan(scanFilters, settings, callback);
    }

    public void stopBeaconScan(ScanCallback callback){
        bluetoothLeScanner.stopScan(callback);
    }
}
