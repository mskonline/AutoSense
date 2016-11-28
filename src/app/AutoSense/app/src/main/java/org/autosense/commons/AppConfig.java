package org.autosense.commons;

import android.bluetooth.BluetoothDevice;

public class AppConfig {

    public static final byte APP_MODE_MEASURE_BEACON_RSSI = 0;
    public static final byte APP_MODE_RECORD_RSSI = 1;
    public static final byte APP_MODE_SENSE_BEACON = 2;

    private byte operatingMode;
    private static AppConfig appConfig;

    private BluetoothDevice device;

    protected void AppConfig(){}

    public static AppConfig getInstance(){
        if(appConfig == null)
            appConfig = new AppConfig();

        return appConfig;
    }

    public byte getOperatingMode() {
        return operatingMode;
    }

    public void setOperatingMode(byte operatingMode) {
        this.operatingMode = operatingMode;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
