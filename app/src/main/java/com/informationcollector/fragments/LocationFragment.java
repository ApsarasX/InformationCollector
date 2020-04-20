package com.informationcollector.fragments;

import android.Manifest;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.informationcollector.utils.info.HardwareInfo;
import com.informationcollector.utils.info.LocationInfo;
import com.informationcollector.utils.type.Tuple;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LocationFragment extends SharedFragment {
    @Override
    protected ArrayList<Tuple> getData() {
        Context ctx = getContext();
        ArrayList<Tuple> result = new ArrayList<>();
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Dexter.withContext(ctx).withPermissions(permissions).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    new LocationInfo(LocationFragment.this, ctx).getLocation();
                } else if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(ctx, "请到系统设置中开启定位权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ctx, "请开启定位权限", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
        return result;
    }
}