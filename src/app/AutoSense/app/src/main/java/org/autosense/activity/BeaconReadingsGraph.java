package org.autosense.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.autosense.R;
import org.autosense.app.AutoSense;
import org.autosense.commons.AppConfig;

import java.util.List;

public class BeaconReadingsGraph extends AppCompatActivity {

    private AppConfig appConfig;
    private TextView beaconName,
                     beaconReadings,
                     scenarioText,
                     beaconURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_readings_graph);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        beaconName = (TextView) findViewById(R.id.graph_beaconname);
        beaconReadings = (TextView) findViewById(R.id.graph_beaconreadings);
        scenarioText = (TextView) findViewById(R.id.scenarioText);
        beaconURL = (TextView) findViewById(R.id.graph_beaconURL);
        Button backBtn = (Button) findViewById(R.id.graph_backBtn);

        backBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        appConfig = ((AutoSense) getApplication()).getAppConfig();

        Bundle extras = getIntent().getExtras();
        scenarioText.setText(extras.getString("scenario"));
        beaconReadings.setText("Readings : " + extras.getString("numReadings"));
        beaconName.setText("Beacon : " + appConfig.getBeaconName());
        beaconURL.setText("URL : " + appConfig.getBeaconURL());

        float stopTime = extras.getFloat("stopTime");

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getXAxis().setAxisMinimum(0.0f);
        chart.getXAxis().setAxisMaximum(stopTime);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        List<Entry> entries = appConfig.getData();

        LineDataSet dataSet = new LineDataSet(entries, "RSSI");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setDrawCircleHole(false);
        dataSet.setCircleRadius(3f);

        dataSet.setFillFormatter(new MyFillFormatter());
        dataSet.setDrawFilled(true);


        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.invalidate();
    }

    class MyFillFormatter implements IFillFormatter {

        @Override
        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
            return dataProvider.getYChartMin();
        }
    }
}
