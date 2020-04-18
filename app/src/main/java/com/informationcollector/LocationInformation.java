package com.informationcollector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.Locale;

final class LocationInformation extends BaseInformation{
    public LocationInformation(Context context) {
        super(context);
    }

    void output(){
        getLocation();
    }

    // 获取定位信息
    private static LocationManager locationManager;
    private static Context mContext;

    public static void onActivityStoped(){
        if (locationListener != null){
            locationManager.removeUpdates(locationListener);
            locationListener = null;
        }
        Log.i("定位关闭：", "onActivityStoped");
    }

    public static void getAddress(Location location){
        // 显示经纬度
        String string = "纬度：" + location.getLatitude() + "，经度：" + location.getLongitude();
        Log.i("经纬度：", string);
    }

    private static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // 获取位置
                getAddress(location);
                onActivityStoped();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void getLocation(){
        String [] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Dexter.withContext(this.context).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("onPermissionsChecked: ", "没有开启定位权限，请开启定位权限");
                    return;
                }
                mContext = context;
                String provider = null;
                // 获取定位服务
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                // 获取当前可用的位置控制器
                List<String> list = locationManager.getProviders(true);
                if (list.contains(LocationManager.GPS_PROVIDER)) {
                    // GPS位置控制器
                    provider = LocationManager.GPS_PROVIDER;
                    Log.i("GPS位置控制器:", provider);
                } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                    // 网络位置控制器
                    provider = LocationManager.NETWORK_PROVIDER;
                    Log.i("网络位置控制器:", provider);
                } else {
                    Log.i("location error：", "无法获取定位控制器，请开启定位权限");
                }
                if (provider != null) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        // 直接获取定位
                        getAddress(location);
                    } else {
                        // 加监听等待获取
                        locationManager.requestLocationUpdates(provider, 3000, 1, locationListener);
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
        }).check();
    }
}
