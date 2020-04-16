package com.informationcollector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

final class NetworkInformation extends BaseInformation {
    NetworkInformation(Context context) {
        super(context);
    }

    void output() {
        this.getNetOperator();
    }

    private void getNetOperator() {
        Dexter.withContext(this.context).withPermission(Manifest.permission.READ_PHONE_STATE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (ActivityCompat.checkSelfPermission(NetworkInformation.this.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                final SubscriptionManager sm = (SubscriptionManager) NetworkInformation.this.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                if (sm != null) {
                    final List<SubscriptionInfo> list = sm.getActiveSubscriptionInfoList();
                    Log.i("已激活SIM卡数量", String.valueOf(sm.getActiveSubscriptionInfoCount()));
                    Log.i("最大可激活SIM卡数量", String.valueOf(sm.getActiveSubscriptionInfoCountMax()));
                    for (SubscriptionInfo info : list) {
                        int idx = info.getSimSlotIndex() + 1;
                        Log.i("SIM卡" + idx + "名称", info.getDisplayName().toString());
                        Log.i("SIM卡" + idx + "网络运营商", info.getCarrierName().toString());
                        Log.i("SIM卡" + idx + "网络运营商国家代码", info.getCountryIso());
                        Log.i("SIM卡" + idx + "电话号码", info.getNumber());
                        Log.i("SIM卡" + idx + "是否允许漫游", String.valueOf(info.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE));
                        Log.i("SIM卡" + idx + "是否正在漫游", String.valueOf(sm.isNetworkRoaming(info.getSubscriptionId())));
                    }
                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                if (response.isPermanentlyDenied()) {
                    Toast.makeText(NetworkInformation.this.context, "需获取电话状态才可正常运行", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}
