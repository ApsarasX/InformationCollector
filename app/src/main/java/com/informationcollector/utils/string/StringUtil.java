package com.informationcollector.utils.string;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class StringUtil {
    private static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static String getBoolStr(boolean value) {
        return value ? "是" : "否";
    }

    public static String getFormatDateTimeStr(long timestamp) {
        return fmt.format(timestamp);
    }
}
