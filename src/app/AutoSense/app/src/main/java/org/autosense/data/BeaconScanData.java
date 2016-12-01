package org.autosense.data;

import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BeaconScanData implements Serializable {

    List<Entry> entries = new ArrayList<Entry>();

   public void setEntry(int i, int rssi)
   {
       entries.add(new Entry(i+1,rssi));
   }

    public List<Entry> getData()
    {
        return entries;
    }

}
