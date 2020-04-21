package com.informationcollector.utils.info;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import com.informationcollector.utils.string.StringUtil;
import com.informationcollector.utils.type.Tuple;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

public final class SystemInfo {

    public static ArrayList<Tuple> getOSInfo() {
        ArrayList<Tuple> result = new ArrayList<>();
        Properties properties = System.getProperties();
        result.add(new Tuple("系统标签", Build.TAGS));
        result.add(new Tuple("系统构建时间", StringUtil.getFormatDateTimeStr(Build.TIME)));
        result.add(new Tuple("系统构建类型", Build.TYPE));
        result.add(new Tuple("设备主机地址", Build.HOST));
        result.add(new Tuple("系统用户名", Build.USER));
        result.add(new Tuple("系统版本", "Android " + Build.VERSION.RELEASE));
        result.add(new Tuple("系统SDK版本", Integer.toString(Build.VERSION.SDK_INT)));
        result.add(new Tuple("系统开发代号", Build.VERSION.CODENAME));
        result.add(new Tuple("系统源码控制版本号", Build.VERSION.INCREMENTAL));
        result.add(new Tuple("系统安全补丁版本", Build.VERSION.SECURITY_PATCH));
        result.add(new Tuple("系统构建的唯一标识", Build.FINGERPRINT));
        result.add(new Tuple("系统语言", Locale.getDefault().toString()));
        result.add(new Tuple("内核名称", properties.getProperty("os.name")));
        result.add(new Tuple("内核版本", properties.getProperty("os.version")));
        result.add(new Tuple("内核架构", properties.getProperty("os.arch")));
        try {
            @SuppressLint("PrivateApi") Class<?> cl = Class.forName("android.os.SystemProperties");
            Method m = cl.getMethod("get", String.class);
            String version = (String) m.invoke(null, "gsm.version.baseband");
            version = TextUtils.isEmpty(version) ? Build.getRadioVersion() : version;
            version = version.split(",")[0];
            result.add(new Tuple("基带版本", version));
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.add(new Tuple("Java运行时名称", properties.getProperty("java.runtime.name")));
        result.add(new Tuple("Java运行时版本", properties.getProperty("java.runtime.version")));
        result.add(new Tuple("JVM名称", properties.getProperty("java.vm.name")));
        result.add(new Tuple("JVM版本", properties.getProperty("java.vm.version")));
        result.add(new Tuple("Android Zlib版本", properties.getProperty("android.zlib.version")));
        result.add(new Tuple("Android OpenSSL版本", properties.getProperty("android.openssl.version")));
        return result;
    }
}
