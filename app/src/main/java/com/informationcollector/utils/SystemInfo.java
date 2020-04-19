package com.informationcollector.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public final class SystemInfo extends BaseInfo {
    public SystemInfo(Context context) {
        super(context);
    }

    @Override
    public void output() {
        Log.i("系统标签", Build.TAGS);
        Log.i("系统构建时间", Long.toString(Build.TIME));
        Log.i("系统构建类型", Build.TYPE);
        Log.i("设备主机地址", Build.HOST);
        Log.i("系统用户名", Build.USER);
        Log.i("系统版本", Build.VERSION.RELEASE);
        Log.i("系统SDK版本", Integer.toString(Build.VERSION.SDK_INT));
        Log.i("系统开发代号", Build.VERSION.CODENAME);
        Log.i("系统源码控制版本号", Build.VERSION.INCREMENTAL);
        Log.i("系统安全补丁版本", Build.VERSION.SECURITY_PATCH);
        Log.i("系统构建的唯一标识", Build.FINGERPRINT);
        Log.i("系统语言", Locale.getDefault().toString());
        Properties properties = System.getProperties();
        Log.i("Java运行时名称", properties.getProperty("java.runtime.name"));
        Log.i("Java运行时版本", properties.getProperty("java.runtime.version"));
        Log.i("JVM名称", properties.getProperty("java.vm.name"));
        Log.i("JVM版本", properties.getProperty("java.vm.version"));
        Log.i("内核名称", properties.getProperty("os.name"));
        Log.i("内核版本", properties.getProperty("os.version"));
        Log.i("内核架构", properties.getProperty("os.arch"));
        Log.i("android zlib版本", properties.getProperty("android.zlib.version"));
        Log.i("android openssl版本", properties.getProperty("android.openssl.version"));
        this.getBaseband();
        this.getAppListInformation();
    }

    private void getBaseband() {
        try {
            @SuppressLint("PrivateApi") Class<?> cl = Class.forName("android.os.SystemProperties");
            Method m = cl.getMethod("get", String.class);
            String version = (String) m.invoke(null, "gsm.version.baseband");
            version = TextUtils.isEmpty(version) ? Build.getRadioVersion() : version;
            version = version.split(",")[0];
            Log.i("基带版本", version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAppListInformation() {
        PackageManager pm = this.context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        for (PackageInfo packInfo : list) {
            ApplicationInfo appInfo = packInfo.applicationInfo;
            Log.i("应用包名", packInfo.packageName);
            Log.i("应用版本", packInfo.versionName);
            Log.i("应用名", appInfo.loadLabel(pm).toString());
            Log.i("目标SDK", String.valueOf(appInfo.targetSdkVersion));
            Log.i("最小SDK", String.valueOf(appInfo.minSdkVersion));
            Log.i("最近更新时间", f.format(packInfo.lastUpdateTime));
            Log.i("首次安装时间", f.format(packInfo.firstInstallTime));
            Log.i("是否为系统应用", String.valueOf((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM));
        }
    }
}
