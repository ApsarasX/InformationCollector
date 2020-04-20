package com.informationcollector.fragments;

import android.Manifest;
import android.content.Context;
import android.widget.Toast;

import com.informationcollector.utils.info.SensorInfo;
import com.informationcollector.utils.type.Tuple;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class SensorFragment extends SharedFragment {

    private SensorInfo sensorInfo;

    @Override
    protected ArrayList<Tuple> getData() {
        Context ctx = getContext();
        ArrayList<Tuple> result = new ArrayList<>();
        String[] permissions;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissions = new String[]{Manifest.permission.BODY_SENSORS, Manifest.permission.ACTIVITY_RECOGNITION};
        } else {
            permissions = new String[]{Manifest.permission.BODY_SENSORS};
        }
        Dexter.withContext(ctx).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    sensorInfo = new SensorInfo(SensorFragment.this, ctx);
                    sensorInfo.getSensorInfo();
                } else if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(ctx, "请到系统设置中开启传感器权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "请开启传感器权限", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.sensorInfo != null) {
            this.sensorInfo.stopListener();
        }
    }
}
