package org.autosense.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;
import android.util.Log;

import org.autosense.commons.AppConfig;
import org.autosense.data.BeaconScanData;

import java.util.ArrayList;
import java.util.List;


public class BeaconService
{
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice beaconDevice;
    private leDeviceCallback leDeviceCallback;

    private BeaconScanData beaconScanData;

    private static final ParcelUuid URIBEACON_SERVICE_UUID =
            ParcelUuid.fromString("0000feaa-0000-1000-8000-00805f9b34fb");//"0000feaa-0000-1000-8000-00805f9b34fb");//"0000fed8-0000-1000-8000-00805f9b34fb");
    private boolean beaconFound = false;
    private boolean isInitialized = false;
    public long lastDectectTime;

    public int nthReading = 1;
    private byte operatingMode = 0;

    public BeaconService(BluetoothManager bluetoothManager){
        bluetoothAdapter = bluetoothManager.getAdapter();

        if(bluetoothAdapter == null) {
            Log.i("Error","Bluetooth adaptor not supported");
            return;
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if(bluetoothLeScanner == null) {
            Log.i("Error","No Bluetooth scanner");
            return;
        }

        isInitialized = true;
    }

    public void startBeaconScan(){
        if(!isInitialized){
            Log.i("Info","Not initialized");
            return;
        }


        lastDectectTime = System.currentTimeMillis();
        bluetoothLeScanner.startScan(leDeviceCallback);
    }

    public void startBeaconScan(ScanCallback callback){
        if(!isInitialized){
            Log.i("Info","Not initialized");
            return;
        }

        bluetoothLeScanner.startScan(callback);
    }


    public void startBeaconScan(int mode){
        if(!isInitialized){
            Log.i("Info","Not initialized");
            return;
        }

        ScanFilter beaconFilter =  new ScanFilter.Builder().setServiceUuid(URIBEACON_SERVICE_UUID).build();
        ArrayList<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
        //scanFilters.add(beaconFilter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bluetoothLeScanner.startScan(scanFilters, settings, leDeviceCallback);
    }

    public void stopBeaconScan(){
        bluetoothLeScanner.stopScan(leDeviceCallback);
        this.setBeaconFound(false);
    }

    class leDeviceCallback extends ScanCallback{

        private org.autosense.services.BeaconService beaconService;
        long interval;

        public leDeviceCallback(BeaconService beaconService){
            this.beaconService = beaconService;
        }

        public void filterBeacon(ScanResult result){
            List services = result.getScanRecord().getServiceUuids();

            if(services!= null && services.contains(URIBEACON_SERVICE_UUID)){
                long now = System.currentTimeMillis();

                switch (operatingMode){
                    case AppConfig.APP_MODE_MEASURE_BEACON_RSSI:
                        break;
                    case AppConfig.APP_MODE_RECORD_RSSI:
                        break;
                    case AppConfig.APP_MODE_SENSE_BEACON:
                        break;
                }

            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result){
            this.filterBeacon(result);
        }

        @Override
        public void onBatchScanResults (List<ScanResult> results){
            for (ScanResult r : results){
                Log.i("Scan Result", r.getDevice().getName());
            }
        }

        @Override
        public void onScanFailed(int errorCode){
            Log.e("Error","Scan failed : " + errorCode);
        }
    }

    public BluetoothDevice getBeaconDevice() {
        return beaconDevice;
    }

    public void setBeaconDevice(BluetoothDevice beaconDevice) {
        this.beaconDevice = beaconDevice;
    }

    public boolean isBeaconFound() {
        return beaconFound;
    }

    public void setBeaconFound(boolean beaconFound) {
        this.beaconFound = beaconFound;

        //Log.i("Info","Stopping scan");
        //bluetoothLeScanner.stopScan(leDeviceCallback);
        //this.connectToBeacon();
    }
}
