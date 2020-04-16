package com.informationcollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Properties;

final class SystemInformation extends BaseInformation {
    SystemInformation(Context context) {
        super(context);
    }

    void output() {
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
        try {
            this.getBaseband();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBaseband() throws Exception {
        @SuppressLint("PrivateApi") Class<?> cl = Class.forName("android.os.SystemProperties");
        Method m = cl.getMethod("get", String.class);
        String version = (String) m.invoke(null, "gsm.version.baseband");
        version = TextUtils.isEmpty(version) ? Build.getRadioVersion() : version;
        version = version.split(",")[0];
        Log.i("基带版本", version);
    }
}
