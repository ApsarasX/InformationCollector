package com.informationcollector.utils;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

public final class SensorInfo extends BaseInfo {
    private SensorManager sensorManager = null;

    public SensorInfo(Context context) {
        super(context);
    }

    @Override
    public void output() {
        getSensorInfo();
    }

    // 获取所有传感器的信息
    private void getSensorInfo() {
        // 获取传感器管理器
        this.sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获取全部传感器列表
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor item : sensors) {
                // 注册各传感器监听信息
                sensorManager.registerListener(SensorListener, sensorManager.getDefaultSensor(item.getType()), SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    // 关闭监听传感器
    private void stopListener(int type) {
        if (this.sensorManager != null) {
            sensorManager.unregisterListener(SensorListener, sensorManager.getDefaultSensor(type));
        }
    }

    private final SensorEventListener SensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // 当sensor值变化时触发这个回调方法
            int len = sensorEvent.values.length;
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < len; i++) {
                msg.append(sensorEvent.values[i]).append(" ");
            }
            Log.i(sensorEvent.sensor.getStringType() + "：", msg.toString());
            stopListener(sensorEvent.sensor.getType());
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

}