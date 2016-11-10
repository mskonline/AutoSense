package org.autosense.activity;

import android.app.Activity;
import android.os.Bundle;

import org.autosense.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // sample commit
        setContentView(R.layout.activity_main);
    }
}
