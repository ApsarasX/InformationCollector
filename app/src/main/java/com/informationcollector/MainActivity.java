package com.informationcollector;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.informationcollector.utils.HardwareInfo;
import com.informationcollector.utils.LocationInfo;
import com.informationcollector.utils.NetworkInfo;
import com.informationcollector.utils.SensorInfo;
import com.informationcollector.utils.SystemInfo;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getAllInformation(View view) {
        Context ctx = getApplicationContext();
        new HardwareInfo(ctx).output();
        new SystemInfo(ctx).output();
        new NetworkInfo(ctx).output();
        new SensorInfo(ctx).output();
        new LocationInfo(ctx).output();
    }
}
