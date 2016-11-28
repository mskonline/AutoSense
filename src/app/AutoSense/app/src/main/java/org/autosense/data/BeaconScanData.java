package org.autosense.data;

import java.util.LinkedHashMap;
import java.util.Map;


public class BeaconScanData {

    private Map<Long, Integer> data;

    public BeaconScanData(){
        data = new LinkedHashMap<Long, Integer>();
    }

    public void addDataPoint(Long time, int rssi){
        data.put(time, rssi);
    }

    public void addDataPoint(int rssi){
        Long cTime = System.currentTimeMillis();
        data.put(cTime, rssi);
    }

    public Map<Long, Integer> getData(){
        return data;
    }
}
