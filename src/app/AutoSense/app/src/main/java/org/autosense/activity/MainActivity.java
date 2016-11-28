package org.autosense.activity;

import android.Manifest;
import android.bluetooth.BluetoothManager;
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

import org.autosense.R;
import org.autosense.app.AutoSense;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.checkBLEPermissions();
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
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        ((AutoSense) getApplication()).initializeBLEServices(bluetoothManager);
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
        inflater.inflate(R.menu.topmenu, menu);
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
}
