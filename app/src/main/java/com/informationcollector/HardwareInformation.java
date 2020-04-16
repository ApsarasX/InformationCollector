package com.informationcollector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

final class HardwareInformation extends BaseInformation {

    HardwareInformation(Context context) {
        super(context);
    }

    void output() {
        Log.i("主板版本", Build.BOARD);
        Log.i("设备引导程序版本", Build.BOOTLOADER);
        Log.i("设备品牌", Build.BRAND);
        Log.i("设备工业设计名称", Build.DEVICE);
        Log.i("设备显示的版本", Build.DISPLAY);
        Log.i("设备硬件名称", Build.HARDWARE);
        Log.i("设备版本号", Build.ID);
        Log.i("设备制造商", Build.MANUFACTURER);
        Log.i("设备型号", Build.MODEL);
        Log.i("设备产品名称", Build.PRODUCT);
        this.getIMEI();
        StringBuilder sb = new StringBuilder();
        for (String abi : Build.SUPPORTED_32_BIT_ABIS) {
            sb.append(abi);
        }
        Log.i("设备支持的32位ABI", sb.toString());
        sb.delete(0, sb.length());
        for (String abi : Build.SUPPORTED_64_BIT_ABIS) {
            sb.append(abi);
        }
        Log.i("设备支持的64位ABI", sb.toString());
        sb.delete(0, sb.length());
        for (String abi : Build.SUPPORTED_ABIS) {
            sb.append(abi);
        }
        Log.i("设备支持的ABI", sb.toString());
        this.getScreenInformation();
        this.getBatteryInformation();
    }

    private void getIMEI() {
        Dexter.withContext(this.context).withPermission(Manifest.permission.READ_PHONE_STATE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                HardwareInformation.this.getIMEIReally();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    Toast.makeText(HardwareInformation.this.context, "需获取电话状态才可正常运行", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    // Android Q以下才会调用
    private void getIMEIReally() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TelephonyManager tm = (TelephonyManager) this.context.getSystemService(Service.TELEPHONY_SERVICE);
        if (tm != null) {
            int phoneCnt = tm.getPhoneCount();
            Log.i("设备卡槽数量", String.valueOf(phoneCnt));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.i("设备IMEI", "Android Q及以上版本系统无法获取");
                return;
            }
            if (phoneCnt > 1) {
                for (int i = 0; i < phoneCnt; ++i) {
                    Log.i("设备IMEI" + (i + 1), tm.getImei(i));
                }
            } else {
                Log.i("设备IMEI", tm.getImei());
            }
        }
    }

    private void getScreenInformation() {
        WindowManager wm = (WindowManager) this.context.getSystemService(Service.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getRealMetrics(dm);
            Log.i("设备屏幕分辨率", dm.widthPixels + "*" + dm.heightPixels);
            Log.i("设备屏幕像素比", Float.toString(dm.density));
            Log.i("设备屏幕DPI", Integer.toString(dm.densityDpi));
        }
    }

    @SuppressLint("PrivateApi")
    private void getBatteryInformation() {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryStatus != null) {
            {
                boolean present = batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
                Log.i("电池是否存在", String.valueOf(present));
            }
            {
                Object mPowerProfile;
                double batteryCapacity = 0;
                try {
                    mPowerProfile = Class.forName("com.android.internal.os.PowerProfile").getConstructor(Context.class).newInstance(this.context);
                    batteryCapacity = (double) Class.forName("com.android.internal.os.PowerProfile").getMethod("getBatteryCapacity").invoke(mPowerProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("电池容量", batteryCapacity + "mAh");
            }
            {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                double batteryLevel = -1;
                if (level != -1 && scale != -1) {
                    batteryLevel = (double) level / (double) scale;
                }
                Log.i("电量百分比", batteryLevel * 100 + "%");
            }
            {
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String statusStr = "undefined";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusStr = "charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusStr = "discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusStr = "full";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusStr = "not charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        statusStr = "unknown";
                        break;
                    default:
                        break;
                }
                Log.i("电池状态", statusStr);
            }
            {
                int healthStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                String statusStr = "undefined";
                switch (healthStatus) {
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        statusStr = "cold";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        statusStr = "dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        statusStr = "good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        statusStr = "overVoltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        statusStr = "overheat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        statusStr = "unknown";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        statusStr = "unspecified";
                        break;
                    default:
                        break;
                }
                Log.i("电池健康状态", statusStr);
            }
            {
                int plugStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                String statusStr = "undefined";
                switch (plugStatus) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        statusStr = "ac";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        statusStr = "usb";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        statusStr = "wireless";
                        break;
                    default:
                        break;
                }
                Log.i("充电方式", statusStr);
            }
            String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            if (technology != null) {
                Log.i("电池技术", technology);
            }
            Log.i("电池温度", temperature / 10 + "℃");
            if (voltage > 1000) {
                Log.i("电池电压", voltage / 1000f + "V");
            } else {
                Log.i("电池电压", voltage + "V");
            }
        }
    }
}
