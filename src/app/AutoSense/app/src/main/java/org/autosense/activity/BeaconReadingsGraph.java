package org.autosense.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeaconReadingsGraph extends AppCompatActivity {

    private AppConfig appConfig;
    private TextView beaconName, beaconReadings, scenarioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_readings_graph);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        beaconName = (TextView) findViewById(R.id.graph_beaconname);
        beaconReadings = (TextView) findViewById(R.id.graph_beaconreadings);
        scenarioText = (TextView) findViewById(R.id.scenarioText);

        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        List<Entry> entries = new ArrayList<Entry>();

        Random rand = new Random();
        for(int i = 0; i < 20; ++i){
            entries.add(new Entry(i, -(rand.nextInt(100))));
        }

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

        appConfig = ((AutoSense) getApplication()).getAppConfig();

        beaconName.setText("Beacon : " + appConfig.getBeaconName());
        beaconReadings.setText("Readings : " + "20");
        scenarioText.setText("Scenario : Walking down the Stairs");
    }

    class MyFillFormatter implements IFillFormatter {

        @Override
        public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
            return dataProvider.getYChartMin();
        }
    }
}
