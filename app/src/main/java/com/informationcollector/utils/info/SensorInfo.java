package com.informationcollector.utils.info;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.informationcollector.fragments.SharedFragment;
import com.informationcollector.utils.type.Tuple;

import java.util.List;

public final class SensorInfo {
    private Context context;
    private SharedFragment fragment;
    private SensorManager sensorManager;

    public SensorInfo(SharedFragment fragment, Context context) {
        this.context = context;
        this.fragment = fragment;
    }

    // 获取所有传感器的信息
    public void getSensorInfo() {
        // 获取传感器管理器
        this.sensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
        if (sensorManager != null) {
            // 获取全部传感器列表
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor item : sensors) {
                // 注册各传感器监听信息
                sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(item.getType()), SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    // 关闭监听传感器
    public void stopListener() {
        if (this.sensorManager != null) {
            sensorManager.unregisterListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ALL));
        }
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // 当sensor值变化时触发这个回调方法
            int len = sensorEvent.values.length;
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < len; i++) {
                msg.append(sensorEvent.values[i]).append('\n');
            }
            msg.deleteCharAt(msg.length() - 1);
            String sensorTypeStr;
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    sensorTypeStr = "加速度";
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorTypeStr = "环境温度";
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    sensorTypeStr = "地磁旋转矢量";
                    break;
                case Sensor.TYPE_GRAVITY:
                    sensorTypeStr = "重力";
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensorTypeStr = "陀螺仪";
                    break;
                case Sensor.TYPE_HEART_BEAT:
                    sensorTypeStr = "心跳";
                    break;
                case Sensor.TYPE_HEART_RATE:
                    sensorTypeStr = "心率监测";
                    break;
                case Sensor.TYPE_LIGHT:
                    sensorTypeStr = "光线";
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sensorTypeStr = "线性加速度";
                    break;
                case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                    sensorTypeStr = "低延迟离体监测";
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorTypeStr = "磁场";
                    break;
                case Sensor.TYPE_MOTION_DETECT:
                    sensorTypeStr = "运动检测";
                    break;
                case Sensor.TYPE_POSE_6DOF:
                    sensorTypeStr = "姿态";
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensorTypeStr = "压力";
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sensorTypeStr = "距离";
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    sensorTypeStr = "相对湿度";
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    sensorTypeStr = "旋转矢量";
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    sensorTypeStr = "运动触发";
                    break;
                case Sensor.TYPE_STATIONARY_DETECT:
                    sensorTypeStr = "静止检测";
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    sensorTypeStr = "步进计数";
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    sensorTypeStr = "步进监测";
                    break;
                default:
                    return;
            }
            SensorInfo.this.fragment.updateSingleData(new Tuple(sensorTypeStr, msg.toString()));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

}