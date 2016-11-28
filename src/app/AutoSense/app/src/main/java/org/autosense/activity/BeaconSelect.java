package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.services.BeaconService;

import java.util.ArrayList;
import java.util.List;

public class BeaconSelect extends AppCompatActivity {

    private ListView beaconList;
    private Button stopButton;
    private List<String> beacons;
    private BeaconsAdapter beaconListAdaptor;

    private BeaconService beaconService;
    private leDeviceCallback leDeviceCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_select);

        beaconList = (ListView) findViewById(R.id.beacons_list);
        stopButton = (Button) findViewById(R.id.button_stopBeaconListing);

        beacons = new ArrayList<String>();
        beaconListAdaptor = new BeaconsAdapter();
        beaconList.setAdapter(beaconListAdaptor);

        beaconService = ((AutoSense) getApplication()).getBeaconService();

        leDeviceCallback = new leDeviceCallback();
        //beaconService.startBeaconScan(leDeviceCallback);

        stopButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                beaconService.stopBeaconScan();
            }
        });
    }

    private void updateBeaconList(String deviceName){
        if(!beacons.contains(deviceName)) {
            beacons.add(deviceName);
            beaconListAdaptor.notifyDataSetChanged();
        }
    }

    class leDeviceCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result){
            Log.i("Scan Result",result.toString());
            String deviceName = result.getDevice().getName();

            if(deviceName != null && deviceName != ""){
                updateBeaconList(deviceName);
            } else {
                String deviceAddress = result.getDevice().getAddress();

                updateBeaconList(deviceAddress);
            }
        }
    }

    public class BeaconsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return beacons.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.beaconitem, parent, false);
            TextView beaconAddr;

            beaconAddr = (TextView) row.findViewById(R.id.beaconAddr);

            try{
                beaconAddr.setText(beacons.get(position));
            }catch (Exception e){
            }

            return row;
        }
    }
}
