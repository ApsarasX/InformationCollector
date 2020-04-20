package com.informationcollector.fragments;

import android.Manifest;
import android.content.Context;
import android.widget.Toast;

import com.informationcollector.utils.info.HardwareInfo;
import com.informationcollector.utils.info.NetworkInfo;
import com.informationcollector.utils.type.Tuple;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public final class OperatorFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        Context ctx = getContext();
        ArrayList<Tuple> result = new ArrayList<>();
        Dexter.withContext(ctx).withPermission(Manifest.permission.READ_PHONE_STATE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                OperatorFragment.this.addDataList(HardwareInfo.getIMEI(ctx));
                OperatorFragment.this.addDataList(NetworkInfo.getSIMInformation(ctx));
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                String toastStr = "请开启获取电话状态权限";
                if (response.isPermanentlyDenied()) {
                    toastStr = "请到系统设置中开启获取电话状态权限";
                }
                Toast.makeText(ctx, toastStr, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest request, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
        return result;
    }
}
