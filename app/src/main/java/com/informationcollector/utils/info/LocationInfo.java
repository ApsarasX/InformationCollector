package com.informationcollector.utils.info;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.informationcollector.fragments.SharedFragment;
import com.informationcollector.utils.type.Tuple;

import java.util.ArrayList;
import java.util.List;

public final class LocationInfo {
    private Context context;

    private LocationManager locationManager;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // 获取位置
                LocationInfo.this.getLatAndLng(location);
                if (locationListener != null) {
                    locationManager.removeUpdates(locationListener);
                    locationListener = null;
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private SharedFragment fragment;

    public LocationInfo(SharedFragment fragment, Context context) {
        this.context = context;
        this.fragment = fragment;
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String provider = null;
        // 获取定位服务
        locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        // 获取当前可用的位置控制器
        assert locationManager != null;
        List<String> list = locationManager.getProviders(true);
        String providerLabel = null;
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            // GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
            providerLabel = "GPS";
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            // 网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
            providerLabel = "网络";
        }
        if (providerLabel != null) {
            this.fragment.addSingleData(new Tuple("位置控制器", providerLabel));
        }
        if (provider != null) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                this.getLatAndLng(location);
            } else {
                // 加监听等待获取
                this.locationManager.requestLocationUpdates(provider, 3000, 1, locationListener);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void getLatAndLng(Location location) {
        Log.i("经度", String.valueOf(location.getLongitude()));
        Log.i("纬度", String.valueOf(location.getLatitude()));
        ArrayList<Tuple> result = new ArrayList<Tuple>() {
            {
                add(new Tuple("经度", String.format("%.5f", location.getLongitude())));
                add(new Tuple("纬度", String.format("%.5f", location.getLatitude())));
            }
        };
        this.fragment.addDataList(result);
    }
}