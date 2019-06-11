package com.wwdablu.soumya.campdf.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public final class CameraHelper {

    private static CameraHelper sInstance;

    private Context mContext;

    private CameraHelper(@NonNull Context context) {
        mContext = context;
    }

    public static CameraHelper getInstance(@NonNull Context context) {

        if(sInstance == null) {
            sInstance = new CameraHelper(context);
        }

        return sInstance;
    }

    public boolean hasCameraHardware() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public boolean hasCameraPermission() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.CAMERA);
    }
}
