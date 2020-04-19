package com.informationcollector.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class HardwareInfo extends BaseInfo {

    public HardwareInfo(Context context) {
        super(context);
    }

    @Override
    public void output() {
        Dexter.withContext(this.context).withPermission(Manifest.permission.READ_PHONE_STATE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                HardwareInfo.this.getGeneralInformation();
                HardwareInfo.this.getIMEI();
                HardwareInfo.this.getScreenInformation();
                HardwareInfo.this.getBatteryInformation();
                HardwareInfo.this.getCPUInformation();
                HardwareInfo.this.getMemoryInformation();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    Toast.makeText(HardwareInfo.this.context, "需开启获取电话状态权限", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void getGeneralInformation() {
        Log.i("主板名称", Build.BOARD);
        Log.i("系统引导程序版本", Build.BOOTLOADER);
        Log.i("系统定制商", Build.BRAND);
        Log.i("设备参数", Build.DEVICE);
        Log.i("屏幕参数", Build.DISPLAY);
        Log.i("设备硬件名称", Build.HARDWARE);
        Log.i("设备版本号", Build.ID);
        Log.i("设备制造商", Build.MANUFACTURER);
        Log.i("设备型号", Build.MODEL);
        Log.i("设备产品名称", Build.PRODUCT);
    }

    private void getIMEI() {
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

    private void getCPUInformation() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(":\\s+", 2);
                if (array[0].startsWith("Hardware")) {
                    Log.i("CPU型号", array[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        {
            StringBuilder sb = new StringBuilder();
            for (String abi : Build.SUPPORTED_32_BIT_ABIS) {
                sb.append(abi).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            Log.i("CPU ABI(32位)", sb.toString());
            sb.delete(0, sb.length());
            for (String abi : Build.SUPPORTED_64_BIT_ABIS) {
                sb.append(abi).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            Log.i("CPU ABI(64位)", sb.toString());
            sb.delete(0, sb.length());
            for (String abi : Build.SUPPORTED_ABIS) {
                sb.append(abi).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            Log.i("CPU ABI", sb.toString());
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles((dirname, filename) -> Pattern.matches("cpu[0-9]", filename));
            if (files != null) {
                int coresCount = files.length;
                Log.i("CPU核数", String.valueOf(coresCount));
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        {
            Map<String, String> map = new HashMap<String, String>() {
                {
                    put("CPU最大频率", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
                    put("CPU最小频率", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
                    put("CPU实时频率", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
                }
            };
            map.forEach((k, v) -> {
                try (BufferedReader br = new BufferedReader(new FileReader(v))) {
                    String text = br.readLine();
                    String freqStr = text.trim();
                    double freq = Double.parseDouble(freqStr) / 1000;
                    Log.i(k, freq + "MHz");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        try (BufferedReader br = new BufferedReader(new FileReader("/sys/class/thermal/thermal_zone9/subsystem/thermal_zone9/temp"))) {
            String temp = br.readLine();
            String temperature = TextUtils.isEmpty(temp) ? null : temp.length() >= 5 ? (Double.parseDouble(temp) / 1000) + "" : temp;
            Log.i("CPU温度", temperature + "℃");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getMemoryInformation() {
        {
            ActivityManager am = (ActivityManager) this.context.getSystemService(Service.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(mi);
            }
            double availRam = (double) mi.availMem / 1e9;
            double totalRam = (double) mi.totalMem / 1e9;
            Log.i("RAM全部内存", String.format("%.2fGB", totalRam));
            Log.i("RAM可用内存", String.format("%.2fGB", availRam));
        }
        {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            long totalBlocks = stat.getBlockCountLong();
            double totalRom = (double) (totalBlocks * blockSize) / 1e9;
            double availRom = (double) (availableBlocks * blockSize) / 1e9;
            Log.i("ROM全部内存", String.format("%.2fGB", totalRom));
            Log.i("ROM可用内存", String.format("%.2fGB", availRom));
        }
    }
}
