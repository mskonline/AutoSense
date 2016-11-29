package org.autosense.commons;

import org.autosense.data.Beacon;

public class AppConfig {

    private static AppConfig appConfig;

    private String beaconName = "Beacon not set";
    private Beacon beacon;

    protected void AppConfig(){}

    public static AppConfig getInstance(){
        if(appConfig == null)
            appConfig = new AppConfig();

        return appConfig;
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
    }
}
