package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;
import org.autosense.commons.Utils;
import org.autosense.services.BeaconService;
import org.autosense.data.Beacon;

import java.util.ArrayList;
import java.util.List;

public class BeaconSelect extends AppCompatActivity {

    private ListView beaconList;
    private Button actionButton, refreshButton;

    private List<String> beaconNameList;
    private List<Beacon> beaconObjList;

    private BeaconsAdapter beaconListAdaptor;

    private BeaconService beaconService;
    private AppConfig appConfig;
    private leDeviceCallback leDeviceCallback;

    public boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_select);

        beaconList = (ListView) findViewById(R.id.beacons_list);
        actionButton = (Button) findViewById(R.id.button_stopBeaconListing);
        refreshButton = (Button) findViewById(R.id.button_refreshBeaconListing);

        beaconNameList = new ArrayList<String>();
        beaconObjList = new ArrayList<Beacon>();

        beaconListAdaptor = new BeaconsAdapter();
        beaconList.setAdapter(beaconListAdaptor);

        beaconService = ((AutoSense) getApplication()).getBeaconService();
        appConfig = ((AutoSense) getApplication()).getAppConfig();

        leDeviceCallback = new leDeviceCallback();
        beaconService.startBeaconScan(leDeviceCallback);

        actionButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isScanning){
                    beaconService.stopBeaconScan(leDeviceCallback);
                    actionButton.setText("Rescan");
                    isScanning = false;
                } else {
                    beaconService.startBeaconScan(leDeviceCallback);
                    actionButton.setText("Stop Scan");
                    isScanning = true;
                }
            }
        });

        refreshButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                beaconNameList.clear();
                beaconObjList.clear();
                beaconListAdaptor.notifyDataSetChanged();
            }
        });

        beaconList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String beaconName = beaconNameList.get(position);
                Beacon beacon = beaconObjList.get(position);

                appConfig.setBeaconName(beaconName);
                appConfig.setBeacon(beacon);
                appConfig.setBeaconURL(beacon.getData());

                beaconService.stopBeaconScan(leDeviceCallback);

                Toast.makeText(getApplicationContext(), "Beacon - " + beaconName + " selected", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(BeaconSelect.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateBeaconList(String deviceName, Beacon beacon){
        int i = beaconNameList.indexOf(deviceName);
        boolean refresh = false;

        if(i == -1) {
            beaconNameList.add(deviceName);
            beaconObjList.add(beacon);
            refresh = true;
        } else {
            if(beaconObjList.get(i).getRssi() != beacon.getRssi()){
                beaconObjList.get(i).setRssi(beacon.getRssi());
                refresh = true;
            }
        }

        if(refresh)
            beaconListAdaptor.notifyDataSetChanged();
    }

    class leDeviceCallback extends ScanCallback {

        List<ParcelUuid> uuids;

        @Override
        public void onScanResult(int callbackType, ScanResult result){
            Log.d("Scan Result", result.toString());

            String deviceName = result.getDevice().getName();

            if(deviceName == null || deviceName == ""){
                deviceName = result.getDevice().getAddress();
            }

            Beacon beacon = new Beacon();
            beacon.setDevice(result.getDevice());
            beacon.setRssi(result.getRssi());

            uuids = result.getScanRecord().getServiceUuids();
            String data;

            try{
                data = Utils.decode(result.getScanRecord().getServiceData(uuids.get(0)));
            } catch (Exception e){
                data = "URL not available";
            }

            beacon.setData(data);
            updateBeaconList(deviceName, beacon);
        }
    }

    public class BeaconsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return beaconNameList.size();
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
            row = inflater.inflate(R.layout.list_item_beacon, parent, false);
            TextView beaconName, beaconStrength, beaconUrl;

            beaconName = (TextView) row.findViewById(R.id.beaconName);
            beaconStrength = (TextView) row.findViewById(R.id.beaconStrength);
            beaconUrl = (TextView) row.findViewById(R.id.beaconUrl);

            try {
                beaconName.setText(beaconNameList.get(position));
                beaconStrength.setText("" + beaconObjList.get(position).getRssi());
                beaconUrl.setText(beaconObjList.get(position).getData());
            }catch (Exception e){
            }

            return row;
        }
    }
}
