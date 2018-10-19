package com.dhivi.inc.topo.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.PermissionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manager on 03-11-2017.
 */

public class PermissionsActivity extends Activity {

    private static final String TAG = "PermissionsActivity";
    Context ctx;
    AlertDialog permissionDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ctx = this;
        /*if (checkCameraPermission()) {

        } else {
            displayMessageDialog(this, MANIFEST_PERMISSION_CAMERA, "Please enable camera permission");
        }*/


        /*if (Build.VERSION.SDK_INT >= 23) {
            System.out.println("Manimaran 11111 "+isApplicationSentToBackground(getApplicationContext()));
            showMultiplePermissions();
        } else
            finish();*/

        if (Build.VERSION.SDK_INT >= 23) {
            Logger.i(TAG, "Manimaran 11111");
            if (getIntent() != null && getIntent().getExtras() != null) {
                Logger.i(TAG, "Manimaran 22222");
                String from = getIntent().getStringExtra("from");
                int type = getIntent().getIntExtra("type", -1);
                if (type != -1) {
                    if (type == PermissionHelper.MANIFEST_PERMISSION_CAMERA) {
                        checkCameraPermission();
                    } else if (type == PermissionHelper.MANIFEST_PERMISSION_SDCARD) {
                        checkSdcardPermission();
                    } else if (type == PermissionHelper.MANIFEST_PERMISSION_LOCATION) {
                        checkLocationPermission();
                    } else if (type == PermissionHelper.MANIFEST_PERMISSION_CALENDAR) {
                        checkCalendarPermission();
                    }else if (type == PermissionHelper.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
                        showMultiplePermissions();
                    }
                } else {
                    finish();
                }
                /*String deeplinkvalue = getIntent().getData().getPath();
                if (deeplinkvalue.equalsIgnoreCase("/force"))
                    showMultiplePermissions();
                else
                    finish();*/
            } else {
                Logger.i(TAG, "Manimaran 33333");
                SharedPreferences pref = getSharedPreferences("emkit_info", 0);
                if (pref.getString("emkit_perm_dialog_display", null) == null) {
                    Logger.i(TAG, "Manimaran 44444");
                    /*if (!EmkitInstance.getInstance(this).isApplicationInForeground()) {
                        final SharedPreferences.Editor editor = pref.edit();
                        editor.putString("emkit_perm_dialog_display", "1");
                        editor.commit();
                        finish();
                    } else
                        showMultiplePermissions();*/
                } else
                    finish();
            }
        } else
            finish();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        finish();
    }

    protected void onNewIntent(Intent intent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void checkCameraPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        boolean response = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(
                    PermissionsActivity.this, new String[]{Manifest.permission.CAMERA},
                    PermissionHelper.MANIFEST_PERMISSION_CAMERA);

        } else {

        }
    }

    protected void checkCalendarPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        boolean response = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(
                    PermissionsActivity.this, new String[]{Manifest.permission.WRITE_CALENDAR},
                    PermissionHelper.MANIFEST_PERMISSION_CALENDAR);
            response = false;
        } else {
            response = true;
        }
    }

    protected void checkLocationPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        boolean response = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(
                    PermissionsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionHelper.MANIFEST_PERMISSION_LOCATION);
            response = false;
        } else {
            response = true;
        }
    }

    protected boolean checkMicrophonePermission() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        boolean response = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            response = false;
        } else {
            response = true;
        }
        return response;
    }

    protected void checkSdcardPermission() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        boolean response = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(
                    PermissionsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionHelper.MANIFEST_PERMISSION_SDCARD);
            response = false;
        } else {
            response = true;
        }
    }

    protected void displayMessageDialog(final Context context, final int type, final String displayMessage) {
        if (type == PermissionHelper.MANIFEST_PERMISSION_CAMERA) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ((permissionDialog != null) && permissionDialog.isShowing()) {
                        return;
                    }
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.app_name))
                            .setMessage(displayMessage)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int whichButton) {
                                    ActivityCompat.requestPermissions(
                                            PermissionsActivity.this, new String[]{Manifest.permission.CAMERA},
                                            1);
                                }
                            });

                    permissionDialog = dialog.create();
                    permissionDialog.setCancelable(false);
                    permissionDialog.show();
                }
            });
        } else if (type == PermissionHelper.MANIFEST_PERMISSION_SDCARD) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ((permissionDialog != null) && permissionDialog.isShowing()) {
                        return;
                    }
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                            .setTitle(getString(R.string.app_name))
                            .setMessage(displayMessage)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int whichButton) {
                                    ActivityCompat.requestPermissions(
                                            PermissionsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            1);
                                }
                            });

                    permissionDialog = dialog.create();
                    permissionDialog.setCancelable(false);
                    permissionDialog.show();
                }
            });
        }
    }


    private void showMultiplePermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Location");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) ;
        permissionsNeeded.add("Read and Write storage");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE)) ;
        permissionsNeeded.add("Acess the Network state");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS)) ;
        permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS)) ;
        permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE)) ;
        permissionsNeeded.add("Read Phone State");

        /*if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Reading phone state");*/

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                //String message = "You need to grant access to " + permissionsNeeded.get(0);
                /*for (int i = 1; i < permissionsList.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }*/


                /*showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        ActivityCompat.requestPermissions(PermissionsActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        finish();
                                        break;
                                }
                            }
                        });*/
                ActivityCompat.requestPermissions(PermissionsActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                        PermissionHelper.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
            ActivityCompat.requestPermissions(PermissionsActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    PermissionHelper.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        } else {
            finish();
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(new ContextThemeWrapper(PermissionsActivity.this, R.style.AboutDialog))
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .setCancelable(false)
                .create()
                .show();
        SharedPreferences pref = getSharedPreferences("emkit_info", 0);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString("emkit_perm_dialog_display", "1");
        editor.commit();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(PermissionsActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                /*perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);*/
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        /*&& perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED*/
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                  //  EmkitInstance.getInstance(PermissionsActivity.this).getUtilityExternal().startProximity();
                } else {
                    // Permission Denied
                    Toast.makeText(PermissionsActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            case PermissionHelper.MANIFEST_PERMISSION_CALENDAR: {

            }
            break;
            case PermissionHelper.MANIFEST_PERMISSION_MICROPHONE: {

            }
            break;
            case PermissionHelper.MANIFEST_PERMISSION_LOCATION: {
                //EmkitGPSTracker gps = EmkitGPSTracker.getInstance(this.getApplicationContext());
                //EmkitInstance.getInstance(this).reportUserLocation(this, PermissionsActivity.this,""+gps.getLatitude(),""+gps.getLongitude());
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
