package org.autosense.activity;

import android.Manifest;

import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.autosense.R;
import org.autosense.services.BeaconService;

import java.util.ArrayList;
import java.util.List;


public class MainTestActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BeaconService beaconService;
    private ListView beaconList;
    public List<String> beacons;
    public BeaconsAdapter beaconListAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        beacons = new ArrayList<String>();
        beaconList = (ListView) findViewById(R.id.beconList);

        beaconListAdaptor = new BeaconsAdapter();
        beaconList.setAdapter(beaconListAdaptor);

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Log.i("Feature","Not Supported");
            finish();
        } else
            Log.i("Feature","Supported");

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        //beaconService = new BeaconService(bluetoothManager, this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("permission", "no coarse location permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    public void addBeacon(String beaconAddr){
        beacons.add(beaconAddr);
        beaconListAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "coarse location permission granted");
                    beaconService.startBeaconScan();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Start:
                beaconService.startBeaconScan();
                return true;
            case R.id.Stop:
                beaconService.stopBeaconScan();
                beacons.clear();
                beaconListAdaptor.notifyDataSetChanged();
                return true;
            default:
                return false;
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
