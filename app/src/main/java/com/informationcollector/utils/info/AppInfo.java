package com.informationcollector.utils.info;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.informationcollector.utils.string.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AppInfo {
    public static ArrayList<Map<String, String>> getAppListInfo(Context context) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        for (PackageInfo packInfo : list) {
            Map<String, String> itemMap = new HashMap<>();
            ApplicationInfo appInfo = packInfo.applicationInfo;
            itemMap.put("app_package_name", packInfo.packageName);
            itemMap.put("app_version", packInfo.versionName);
            itemMap.put("app_name", appInfo.loadLabel(pm).toString());
            itemMap.put("app_target_sdk", String.valueOf(appInfo.targetSdkVersion));
            itemMap.put("app_min_sdk", String.valueOf(appInfo.minSdkVersion));
            itemMap.put("app_last_update_time", StringUtil.getFormatDateTimeStr(packInfo.lastUpdateTime));
            itemMap.put("app_first_install_time", StringUtil.getFormatDateTimeStr(packInfo.firstInstallTime));
            itemMap.put("app_system", StringUtil.getBoolStr((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM));
            itemMap.put("last_update_time", String.valueOf(packInfo.lastUpdateTime));
            result.add(itemMap);
        }
        result.sort((o1, o2) -> {
            if (!Objects.equals(o1.get("app_system"), o2.get("app_system"))) {
                if (Objects.equals(o1.get("app_system"), "否")) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                String time1 = Objects.requireNonNull(o1.get("last_update_time"));
                String time2 = Objects.requireNonNull(o2.get("last_update_time"));
                if (time1.length() != time2.length()) {
                    return time2.length() - time1.length();
                }
                return -time1.compareTo(time2);
            }
        });
        return result;
    }
}
