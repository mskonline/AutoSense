package org.autosense.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;

public class MainActivity extends AppCompatActivity {
    private AppConfig appconfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Options
        String[] options = new String[]{
                "Measure RSSI",
                "Measure RSSI when in motion",
                "Measure threshold speed"
        };

        // build adapter
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);

        // Configure list view
        ListView list=(ListView) findViewById(R.id.viewid);
        list.setAdapter(adapter);
        appconfig=((AutoSense)getApplication()).getAppConfig();

        // Callback
        itemClickCallBack();

        // Check Bluetooth Permissions
        checkBLEPermissions();
    }

    private void itemClickCallBack() {
        ListView list = (ListView) findViewById(R.id.viewid);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int i, long l) {
                Intent intent;
                if(!appconfig.isBeaconSet()) {
                    Toast.makeText(viewClicked.getContext(), "Select a Beacon", Toast.LENGTH_SHORT).show();
                } else if(i == 0) {
                    intent = new Intent(viewClicked.getContext(), MeasureRSSI.class);
                    startActivity(intent);
                } else if(i == 1) {
                    intent = new Intent(viewClicked.getContext(), MeasureInMotion.class);
                    startActivity(intent);
                } else if(i == 2){
                    intent = new Intent(viewClicked.getContext(), MeasureThresholdSpeed.class);
                    startActivity(intent);
                }
            }
        });
    }

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private void checkBLEPermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                this.initializeBLEService();
            }
        } else {
            this.initializeBLEService();
        }
    }

    private void initializeBLEService(){
        Log.i("Info","initializeBLEService()");
        ((AutoSense) getApplication()).initializeBLEServices();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.initializeBLEService();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        switch (item.getItemId()) {
            case R.id.select_beacon:
                i = new Intent(MainActivity.this, BeaconSelect.class);
                startActivity(i);
                return true;
            case R.id.about_autosense:
                i = new Intent(MainActivity.this, AboutAutoSense.class);
                startActivity(i);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
