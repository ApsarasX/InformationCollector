package com.informationcollector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Locale;

final class SensorInformation extends BaseInformation {
    public SensorInformation(Context context) {
        super(context);
    }

    void output() {
//        Log.i("加速度传感器", String.valueOf(Sensor.TYPE_ACCELEROMETER));
//        Log.i("陀螺仪传感器", String.valueOf(Sensor.TYPE_GYROSCOPE));
//        Log.i("环境光仪传感器", String.valueOf(Sensor.TYPE_LIGHT));
//        Log.i("电磁场传感器", String.valueOf(Sensor.TYPE_MAGNETIC_FIELD));
//        Log.i("方向传感器", String.valueOf(Sensor.TYPE_ROTATION_VECTOR)); //获取旋转矩阵和欧拉角
//        Log.i("压力传感器", String.valueOf(Sensor.TYPE_PRESSURE));
//        Log.i("距离传感器", String.valueOf(Sensor.TYPE_PROXIMITY));
//        Log.i("温度传感器", String.valueOf(Sensor.TYPE_AMBIENT_TEMPERATURE));
        getSensorInfor();
    }

    // 获取所有传感器的信息
    private void getSensorInfor(){
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        for (Sensor item : sensors){
            strLog.append("TYPE：" + item.getType() + " ");
            strLog.append("NAME：" + item.getName() + " ");
            strLog.append("VERSION：" + item.getVersion() + " ");
            strLog.append("VENDOR：" + item.getVendor() + " ");
            strLog.append("Maximum Range：" + item.getMaximumRange() + " ");
            strLog.append("Minimum Delay：" + item.getMinDelay() + " ");
            strLog.append("POWER：" + item.getPower() + " ");
            strLog.append("RESOLUTION：" + item.getResolution() + "\r\n");

            // 注册各传感器监听信息
            sensorManager.registerListener(SensorListener, sensorManager.getDefaultSensor(item.getType()), SensorManager.SENSOR_DELAY_UI);
        }
        Log.i("传感器信息：", strLog.toString());
    }

    // 关闭监听传感器
    private void stopListener(int type){
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(SensorListener, sensorManager.getDefaultSensor(type));
    }

    final SensorEventListener SensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // 当sensor值变化时触发这个回调方法
            int len = sensorEvent.values.length;
            String msg = "";
            for (int i=0; i<len; i++){
                msg += String.valueOf(sensorEvent.values[i]) + " ";
            }
            Log.i(sensorEvent.sensor.getName() + "：",  msg);
            stopListener(sensorEvent.sensor.getType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}
