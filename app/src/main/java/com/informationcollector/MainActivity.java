package com.informationcollector;

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_READ_PHONE_STATE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.getPhoneStateReally();
            } else {
                Toast.makeText(this, "应用需要获取电话状态才可正常运行", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getDeviceInformation(View view) {
        this.getLanguageInformation();
        this.getPhoneState();
        this.getBuildInformation();
        this.getMACAddress();
    }

    private void getLanguageInformation() {
        Log.i("LANGUAGE", Locale.getDefault().getLanguage());
    }

    private void getPhoneState() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                Toast.makeText(this, "需获取电话状态才可正常运行", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        } else {
            this.getPhoneStateReally();
        }
    }

    private void getPhoneStateReally() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        if (tm != null) {
            try {
                Log.i("IMEI", tm.getImei());
            } catch (SecurityException ignored) {
                Log.i("IMEI", "无权限获取");
            }
        }
    }

    private void getBuildInformation() {
        // VERSION相关
        Log.i("RELEASE", Build.VERSION.RELEASE);
        Log.i("BASE_OS", Build.VERSION.BASE_OS);
        Log.i("CODENAME", Build.VERSION.CODENAME);
        Log.i("INCREMENTAL", Build.VERSION.INCREMENTAL);
        Log.i("SECURITY_PATCH", Build.VERSION.SECURITY_PATCH);
        Log.i("PREVIEW_SDK_INT", Integer.toString(Build.VERSION.PREVIEW_SDK_INT));
        Log.i("SDK_INT", Integer.toString(Build.VERSION.SDK_INT));

        Log.i("ID", Build.ID);
        Log.i("DISPLAY", Build.DISPLAY);
        Log.i("PRODUCT", Build.PRODUCT);
        Log.i("DEVICE", Build.DEVICE);
        Log.i("BOARD", Build.BOARD);
        Log.i("MANUFACTURER", Build.MANUFACTURER);
        Log.i("BRAND", Build.BRAND);
        Log.i("MODEL", Build.MODEL);
        Log.i("BOOTLOADER", Build.BOOTLOADER);
        Log.i("HARDWARE", Build.HARDWARE);
        Log.i("FINGERPRINT", Build.FINGERPRINT);
        Log.i("HOST", Build.HOST);
        Log.i("TAGS", Build.TAGS);
        Log.i("TYPE", Build.TYPE);
        Log.i("USER", Build.USER);
        Log.i("TIME", Long.toString(Build.TIME));

        // 获取 SUPPORTED_ABIS
        StringBuilder sb = new StringBuilder();
        for (String abi : Build.SUPPORTED_ABIS) {
            sb.append(abi);
        }
        Log.i("SUPPORTED_ABIS", sb.toString());
        sb.delete(0, sb.length());
        // 获取 SUPPORTED_32_BIT_ABIS
        for (String abi : Build.SUPPORTED_32_BIT_ABIS) {
            sb.append(abi);
        }
        Log.i("SUPPORTED_32_BIT_ABIS", sb.toString());
        sb.delete(0, sb.length());
        // 获取 SUPPORTED_64_BIT_ABIS
        for (String abi : Build.SUPPORTED_64_BIT_ABIS) {
            sb.append(abi);
        }
        Log.i("SUPPORTED_64_BIT_ABIS", sb.toString());
    }

    private void getMACAddress() {
        // 获取MAC地址
    }
}
