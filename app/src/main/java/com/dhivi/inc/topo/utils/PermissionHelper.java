package com.dhivi.inc.topo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by manager on 03-11-2017.
 */

public class PermissionHelper {

    public static final int MANIFEST_PERMISSION_CAMERA = 1;
    public static final int MANIFEST_PERMISSION_SDCARD = 2;
    public static final int MANIFEST_PERMISSION_CALENDAR = 3;
    public static final int MANIFEST_PERMISSION_MICROPHONE = 4;
    public static final int MANIFEST_PERMISSION_LOCATION = 5;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static PermissionHelper permissionHelper = null;
    private Context ctx;

    private PermissionHelper(Context ctx) {
        //do the init here
        this.ctx = ctx;
    }

    public synchronized static PermissionHelper getInstance(Context ctx) {
        if (permissionHelper == null) {
            permissionHelper = new PermissionHelper(ctx);
        }
        return permissionHelper;
    }

    public boolean verifyCameraPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        boolean response;
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            response = false;
        } else {
            response = true;
        }
        return response;
    }

    public boolean verifySDCardPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        boolean response;
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            response = false;
        } else {
            response = true;
        }
        return response;
    }

    public boolean verifyCalendarPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        boolean response;
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            response = false;
        } else {
            response = true;
        }
        return response;
    }

    public boolean verifyLocationPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        boolean response;
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            response = false;
        } else {
            response = true;
        }
        return response;
    }

}

