package com.informationcollector.utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public final class LocationInfo extends BaseInfo {
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

    public LocationInfo(Context context) {
        super(context);
    }

    @Override
    public void output() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Dexter.withContext(this.context).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                LocationInfo.this.getLocation();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(LocationInfo.this.context, "需开启定位权限", Toast.LENGTH_SHORT).show();
            return;
        }
        String provider = null;
        // 获取定位服务
        locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        // 获取当前可用的位置控制器
        assert locationManager != null;
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            // GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
            Log.i("GPS位置控制器:", provider);
        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            // 网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
            Log.i("网络位置控制器", provider);
        } else {
            Log.e("位置控制器错误", "无法获取定位控制器，请开启定位权限");
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

    private void getLatAndLng(Location location) {
        Log.i("经度", String.valueOf(location.getLongitude()));
        Log.i("纬度", String.valueOf(location.getLatitude()));
    }
}