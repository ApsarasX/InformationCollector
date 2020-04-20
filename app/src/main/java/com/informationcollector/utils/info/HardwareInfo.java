package com.informationcollector.utils.info;

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
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.informationcollector.utils.string.StringUtil;
import com.informationcollector.utils.type.Tuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class HardwareInfo {
    public static ArrayList<Tuple> getGeneralInfo() {
        ArrayList<Tuple> result = new ArrayList<>();
        result.add(new Tuple("主板名称", Build.BOARD));
        result.add(new Tuple("系统引导程序版本", Build.BOOTLOADER));
        result.add(new Tuple("系统定制商", Build.BRAND));
        result.add(new Tuple("设备参数", Build.DEVICE));
        result.add(new Tuple("屏幕参数", Build.DISPLAY));
        result.add(new Tuple("设备硬件名称", Build.HARDWARE));
        result.add(new Tuple("设备版本号", Build.ID));
        result.add(new Tuple("设备制造商", Build.MANUFACTURER));
        result.add(new Tuple("设备型号", Build.MODEL));
        result.add(new Tuple("设备产品名称", Build.PRODUCT));
        return result;
    }

    public static ArrayList<Tuple> getCPUInfo() {
        ArrayList<Tuple> result = new ArrayList<>();
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(":\\s+", 2);
                if (array[0].startsWith("Hardware")) {
                    result.add(new Tuple("CPU型号", array[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles((dirname, filename) -> Pattern.matches("cpu[0-9]", filename));
            if (files != null) {
                int coresCount = files.length;
                result.add(new Tuple("CPU核数", String.valueOf(coresCount)));
            }
        } catch (SecurityException e) {
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
            result.add(new Tuple("CPU ABI(32位)", sb.toString()));
            sb.delete(0, sb.length());
            for (String abi : Build.SUPPORTED_64_BIT_ABIS) {
                sb.append(abi).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            result.add(new Tuple("CPU ABI(64位)", sb.toString()));
            sb.delete(0, sb.length());
            for (String abi : Build.SUPPORTED_ABIS) {
                sb.append(abi).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            result.add(new Tuple("CPU ABI", sb.toString()));
        }
        {
            Map<String, String> fileMap = new HashMap<String, String>() {
                {
                    put("CPU最大频率", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
                    put("CPU最小频率", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
                    put("CPU实时频率", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
                }
            };
            fileMap.forEach((k, v) -> {
                try (BufferedReader br = new BufferedReader(new FileReader(v))) {
                    String text = br.readLine();
                    String freqStr = text.trim();
                    double freq = Double.parseDouble(freqStr) / 1000;
                    result.add(new Tuple(k, freq + "MHz"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        try (BufferedReader br = new BufferedReader(new FileReader("/sys/class/thermal/thermal_zone9/subsystem/thermal_zone9/temp"))) {
            String temp = br.readLine();
            String temperature = TextUtils.isEmpty(temp) ? null : temp.length() >= 5 ? (Double.parseDouble(temp) / 1000) + "" : temp;
            result.add(new Tuple("CPU温度", temperature + "℃"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressLint("DefaultLocale")
    public static ArrayList<Tuple> getMemoryInfo(Context context) {
        ArrayList<Tuple> result = new ArrayList<>();
        {
            ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(mi);
            }
            double availRam = (double) mi.availMem / 1e9;
            double totalRam = (double) mi.totalMem / 1e9;
            result.add(new Tuple("RAM全部内存", String.format("%.2fGB", totalRam)));
            result.add(new Tuple("RAM可用内存", String.format("%.2fGB", availRam)));
        }
        {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            long totalBlocks = stat.getBlockCountLong();
            double totalRom = (double) (totalBlocks * blockSize) / 1e9;
            double availRom = (double) (availableBlocks * blockSize) / 1e9;
            result.add(new Tuple("ROM全部内存", String.format("%.2fGB", totalRom)));
            result.add(new Tuple("ROM可用内存", String.format("%.2fGB", availRom)));
        }
        return result;
    }

    public static ArrayList<Tuple> getDisplayInfo(Context context) {
        ArrayList<Tuple> result = new ArrayList<>();
        WindowManager wm = (WindowManager) context.getSystemService(Service.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getRealMetrics(dm);
            result.add(new Tuple("设备屏幕分辨率", dm.widthPixels + "x" + dm.heightPixels));
            result.add(new Tuple("设备屏幕像素比", Float.toString(dm.density)));
            result.add(new Tuple("设备屏幕DPI", Integer.toString(dm.densityDpi)));
        }
        return result;
    }

    @SuppressLint("PrivateApi")
    public static ArrayList<Tuple> getBatteryInformation(Context context) {
        ArrayList<Tuple> result = new ArrayList<>();
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryStatus != null) {
            {
                boolean present = batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
                result.add(new Tuple("电池是否存在", StringUtil.getBoolStr(present)));
            }
            {
                Object mPowerProfile;
                double batteryCapacity = 0;
                try {
                    mPowerProfile = Class.forName("com.android.internal.os.PowerProfile").getConstructor(Context.class).newInstance(context);
                    batteryCapacity = (double) Class.forName("com.android.internal.os.PowerProfile").getMethod("getBatteryCapacity").invoke(mPowerProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result.add(new Tuple("电池容量", batteryCapacity + "mAh"));
            }
            {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                double batteryLevel = -1;
                if (level != -1 && scale != -1) {
                    batteryLevel = (double) level / (double) scale;
                }
                result.add(new Tuple("电量百分比", batteryLevel * 100 + "%"));
            }
            {
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                String statusStr = null;
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusStr = "充电中";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusStr = "放电中";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusStr = "未充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusStr = "已充满";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        statusStr = "未知";
                        break;
                }
                if (statusStr != null) {
                    result.add(new Tuple("电池状态", statusStr));
                }
            }
            {
                int healthStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                String statusStr = null;
                switch (healthStatus) {
                    case BatteryManager.BATTERY_HEALTH_COLD:
                        statusStr = "过冷";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        statusStr = "没电";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        statusStr = "良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        statusStr = "电压过高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        statusStr = "过热";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        statusStr = "未知错误";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        statusStr = "未知";
                        break;
                }
                if (statusStr != null) {
                    result.add(new Tuple("电池健康状态", statusStr));
                }
            }
            {
                int plugStatus = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                String statusStr = null;
                switch (plugStatus) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        statusStr = "充电器(交流电)";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        statusStr = "USB";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                        statusStr = "无线";
                        break;
                    default:
                        break;
                }
                if (statusStr != null) {
                    result.add(new Tuple("充电方式", statusStr));
                }
            }
            String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            if (technology != null) {
                result.add(new Tuple("电池技术", technology));
            }
            result.add(new Tuple("电池温度", temperature / 10 + "℃"));
            if (voltage > 1000) {
                result.add(new Tuple("电池电压", voltage / 1000f + "V"));
            } else {
                result.add(new Tuple("电池电压", voltage + "V"));
            }
        }
        return result;
    }

    public static ArrayList<Tuple> getIMEI(Context context) {
        ArrayList<Tuple> result = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return result;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        if (tm != null) {
            int phoneCnt = tm.getPhoneCount();
            result.add(new Tuple("设备卡槽数量", String.valueOf(phoneCnt)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                result.add(new Tuple("设备IMEI", "Android Q及以上版本系统无法获取"));
                return result;
            }
            if (phoneCnt > 1) {
                for (int i = 0; i < phoneCnt; ++i) {
                    result.add(new Tuple("设备IMEI" + (i + 1), tm.getImei(i)));
                }
            } else {
                result.add(new Tuple("设备IMEI", tm.getImei()));
            }
        }
        return result;
    }
}
