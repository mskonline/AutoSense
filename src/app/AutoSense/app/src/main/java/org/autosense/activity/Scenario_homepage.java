package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;
import org.autosense.data.BeaconScanData;
import org.autosense.services.BeaconService;


import java.util.ArrayList;
import java.util.List;

public class Scenario_homepage extends AppCompatActivity
{

    private AppConfig appConfig;
    private BeaconService beaconService;
    private BeaconScanData beaconScanData=new BeaconScanData();
    int i=0;
    //private List list;
    private leDeviceCallback ledeviceCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario_homepage);

        // getting beacon beacon name
        TextView beaconName;


        appConfig=((AutoSense)getApplication()).getAppConfig();
        beaconService = ((AutoSense) getApplication()).getBeaconService();
        //ledeviceCallback = new ledeviceCallback();
        //beaconService.startBeaconScan(ledeviceCallback);
        String Name = appConfig.getBeaconName();

        beaconName = (TextView) findViewById(R.id.beaconName);
        beaconName.setText("Beacon Name  :  "+ Name);
        clickCallBack();

    }

    private void clickCallBack()
    {
        Button startButton = (Button) findViewById(R.id.start);
        Button stopButton =(Button) findViewById(R.id.stop);
        Button graphButton =(Button) findViewById(R.id.graph) ;
        final EditText scenario=(EditText) findViewById(R.id.scenario);
        startButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                ledeviceCallBack = new leDeviceCallback();
                beaconService.startBeaconScan(ledeviceCallBack);

            }
        }  );

        stopButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                beaconService.stopBeaconScan(ledeviceCallBack);
            }

        });

        graphButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent i= new Intent(Scenario_homepage.this,BeaconReadingsGraph.class);
                i.putExtra("beaconData",beaconScanData);
                i.putExtra("Scenario",scenario.getText());
                startActivity(i);

            }
        });



    }


    class leDeviceCallback extends ScanCallback {

        public void onScanResult(int callbackType, ScanResult result) {

            //List list;


            String deviceName = result.getDevice().getName();

            if (deviceName == null || deviceName == "") {
                deviceName = result.getDevice().getAddress();
            }

            if (appConfig.getBeaconName().equals(deviceName)) // Scanning for required beacon
            {

                beaconScanData.setEntry(++i,result.getRssi());

            }
        }

    }
}

