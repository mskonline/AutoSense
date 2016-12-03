package org.autosense.commons;

import com.github.mikephil.charting.data.Entry;

import org.autosense.data.Beacon;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {

    private static AppConfig appConfig;

    public List<Entry> entries = new ArrayList<Entry>();

    private boolean isBeaconSet = false;
    private String beaconName;
    private String beaconURL;
    private Beacon beacon;

    protected void AppConfig(){}

    public static AppConfig getInstance(){
        if(appConfig == null)
            appConfig = new AppConfig();

        return appConfig;
    }

    public void clearBeaconData(){
        entries.clear();
    }

    public void setEntry(int i, int rssi) {
        entries.add(new Entry(i + 1, rssi));
    }

    public List<Entry> getData() {
        return entries;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public void setBeaconName(String beaconName) {
        this.beaconName = beaconName;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
        isBeaconSet = true;
    }

    public String getBeaconURL() {
        return beaconURL;
    }

    public void setBeaconURL(String beaconURL) {
        this.beaconURL = beaconURL;
    }

    public boolean isBeaconSet() {
        return isBeaconSet;
    }

    public void setBeaconSet(boolean beaconSet) {
        isBeaconSet = beaconSet;
    }
}
