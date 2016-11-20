package org.autosense.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import org.autosense.activity.MainTestActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class BeaconService
{
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice beaconDevice;
    private BluetoothGatt beaconGatt;
    private MainTestActivity activity;

    private leDeviceCallback leDeviceCallback;
    private Handler handler;

    private static final long SCAN_PERIOD = 25000;
    private static final ParcelUuid URIBEACON_SERVICE_UUID =
            ParcelUuid.fromString("0000feaa-0000-1000-8000-00805f9b34fb");//"0000feaa-0000-1000-8000-00805f9b34fb");//"0000fed8-0000-1000-8000-00805f9b34fb");

    private boolean beaconFound = false;
    private boolean isInitialized = false;

    public long lastDectectTime;

    Timer timer;
    public int nthReading = 1;

    public BeaconService(BluetoothManager bluetoothManager, Activity activity){
        this.activity = (MainTestActivity)activity;

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

        leDeviceCallback = new leDeviceCallback(this);

        isInitialized = true;
    }

    public void startBeaconScan(){
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

        nthReading = 0;
        handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!beaconFound) {
                    Log.i("Info", "Stopping scan");
                    bluetoothLeScanner.stopScan(leDeviceCallback);
                }

            }
        }, SCAN_PERIOD);*/

        Log.i("Info", "Starting scan");
        lastDectectTime = System.currentTimeMillis();
        bluetoothLeScanner.startScan(scanFilters,settings,leDeviceCallback);
    }

    public void stopBeaconScan(){
        bluetoothLeScanner.stopScan(leDeviceCallback);

        /*if(isBeaconFound()){
            timer.cancel();
            beaconGatt.disconnect();
        }*/

        this.setBeaconFound(false);

        Log.i("info","Stopped");
    }

    public void connectToBeacon(){
        leDeviceGattCallback beaconGattCallback = new leDeviceGattCallback();

        if(beaconDevice != null){
            //Log.i("Info", beaconDevice.getAddress());
            //Log.i("Info","Connecting to the Device");
            beaconGatt = beaconDevice.connectGatt(activity, false, beaconGattCallback);
        } else{
            Log.i("Info", "No Beacon device");
        }
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
                interval =  now - beaconService.lastDectectTime;
                beaconService.lastDectectTime = now;
                Log.i("Info","Device found : " + interval + " ms");
                //beaconService.bluetoothLeScanner.flushPendingScanResults(this);

                if(!beaconService.isBeaconFound()){
                    String beacon = result.getDevice().getName();

                    if(beacon == null)
                        beacon = result.getDevice().getAddress();

                    beaconService.activity.addBeacon(beacon);
                    beaconService.setBeaconFound(true);
                }
                //Log.i("Info", "Device name : " + result.getDevice().getName());
                //String message = new String(result.getScanRecord().getServiceData().get(URIBEACON_SERVICE_UUID));
                //Log.i("Info","Data : " + message);
                Log.i("Info","RSSI : " + result.getRssi());
                //beaconService.setBeaconDevice(result.getDevice());
                //beaconService.setBeaconFound(true);
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result){
            //Log.i("Info",result.toString());
            //beaconService.bluetoothLeScanner.flushPendingScanResults(this);
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

    class leDeviceGattCallback extends BluetoothGattCallback{

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if(status == gatt.GATT_SUCCESS)
                Log.i("Info","Gatt success");
            else
                Log.i("Info","Gatt failed");

            if (newState == BluetoothProfile.STATE_CONNECTED && status == gatt.GATT_SUCCESS) {
                Log.i("Info", "Beacon Connected");

                timer = new Timer();
                BeaconRSSITimer beaconRSSITimer = new BeaconRSSITimer();

                timer.scheduleAtFixedRate(beaconRSSITimer, 0, 500);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("Info", "Beacon disconnected");
            } else if (status != gatt.GATT_SUCCESS){
                Log.i("Info", "Beacon not Connected");
            }
        }

        @Override
        public void onReadRemoteRssi (BluetoothGatt gatt,
                                      int rssi,
                                      int status){

            Log.i("Reading","" + nthReading + "\t" + rssi);
            ++nthReading;
        }

    }

    class BeaconRSSITimer extends TimerTask{
        //int readingsCount = 0;

        public void run(){
            //++readingsCount;
            beaconGatt.readRemoteRssi();

            /*if(readingsCount == 50){
                timer.cancel();
                beaconGatt.disconnect();
            }*/
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
