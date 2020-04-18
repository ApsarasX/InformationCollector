package com.informationcollector;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getAllInformation(View view) {
        Context ctx = getApplicationContext();
        new HardwareInformation(ctx).output();
        new SystemInformation(ctx).output();
        new NetworkInformation(ctx).output();
        new SensorInformation(ctx).output();
        new LocationInformation(ctx).output();
    }
}
