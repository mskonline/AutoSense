package org.autosense.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;
import org.autosense.services.BeaconService;

public class MeasureInMotion extends AppCompatActivity
{
    private AppConfig appConfig;
    private BeaconService beaconService;

    private Button startButton, stopButton, graphButton, resetButton;

    private int numReadings = 0;
    private TextView readingTView;

    private leDeviceCallback ledeviceCallBack;
    private InputMethodManager imm;

    private long startTime;
    private float stopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_in_motion);

        appConfig = ((AutoSense)getApplication()).getAppConfig();
        beaconService = ((AutoSense) getApplication()).getBeaconService();
        String Name = appConfig.getBeaconName();

        TextView beaconName = (TextView) findViewById(R.id.beaconName);
        readingTView = (TextView) findViewById(R.id.op2_readings);
        beaconName.setText("Beacon Name  :  " + Name);

        clickCallBack();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void clickCallBack()
    {
        startButton = (Button) findViewById(R.id.op2_start);
        stopButton = (Button) findViewById(R.id.op2_stop);
        graphButton = (Button) findViewById(R.id.graph);
        resetButton = (Button) findViewById(R.id.op2_reset);

        final EditText scenario = (EditText) findViewById(R.id.op2_scenario);
        ledeviceCallBack = new leDeviceCallback();

        startButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                imm.hideSoftInputFromWindow(scenario.getWindowToken(), 0);
                scenario.clearFocus();
                graphButton.requestFocus();

                if(!scenario.getText().toString().equalsIgnoreCase("")){
                    /* All set. Let's start the experiment */
                    startTime = System.currentTimeMillis();

                    beaconService.startBeaconScan(ledeviceCallBack);
                    showMessage("Scanning started");

                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    resetButton.setEnabled(true);
                } else {
                    showMessage("Please enter scenario");
                }
            }
        });

        stopButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                beaconService.stopBeaconScan(ledeviceCallBack);
                showMessage("Scanning stopped");

                calculateStopTime();

                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);
            }
        });

        resetButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                beaconService.stopBeaconScan(ledeviceCallBack);

                resetMetrics();
                showMessage("Reset done");

                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(false);
            }
        });

        graphButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v) {
                beaconService.stopBeaconScan(ledeviceCallBack);

                if(appConfig.getData().size() > 0){
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    resetButton.setEnabled(true);

                    Intent i= new Intent(MeasureInMotion.this, BeaconReadingsGraph.class);
                    i.putExtra("scenario", scenario.getText().toString());
                    i.putExtra("numReadings", "" + numReadings);

                    calculateStopTime();

                    i.putExtra("stopTime", stopTime);
                    startActivity(i);
                } else {
                    showMessage("No data available");
                }
            }
        });
    }

    private void calculateStopTime(){
        stopTime = (System.currentTimeMillis() - startTime) / 1000f;
    }

    private void resetMetrics(){
        appConfig.clearBeaconData();
        numReadings = 0;
        startTime = 0;
        readingTView.setText("0");
    }

    private void showMessage(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        appConfig.clearBeaconData();
    }

    class leDeviceCallback extends ScanCallback {

        float currentTimeInSecs;

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String deviceName = result.getDevice().getName();

            if (deviceName == null || deviceName == "") {
                deviceName = result.getDevice().getAddress();
            }

            if (appConfig.getBeaconName().equals(deviceName)) {

                currentTimeInSecs = (System.currentTimeMillis() - startTime) / 1000f;
                ++numReadings;

                appConfig.setEntry(currentTimeInSecs, result.getRssi());
                readingTView.setText("" + numReadings);
            }
        }
    }
}