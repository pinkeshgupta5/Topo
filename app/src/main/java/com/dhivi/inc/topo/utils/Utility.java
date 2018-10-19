package com.dhivi.inc.topo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.presenter.EditProfileActivity;
import com.dhivi.inc.topo.presenter.PermissionsActivity;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Manoj on 4/19/2017.
 */

public class Utility {

    public static Typeface fontTitlereguler;


    private final static String TAG = "Utility";
    static Context mcon;
    private static CustomDialog ringProgressDialog;
    private static Dialog progressCustomDlg;
    public static int navigation = 0;
    public static String navigationId = "";
    public static String topoId = "";
    public static String topoType = "";


    public static void errorDialog(Context con, String error) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(con, R.style.AboutDialog));
        alertDialog.setTitle("");

        alertDialog.setMessage(error);
        alertDialog.setPositiveButton(con.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }

    public static void errorDialog(Context con, String error,final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(con, R.style.AboutDialog));
        alertDialog.setTitle("");

        alertDialog.setMessage(error);
        alertDialog.setPositiveButton(con.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        activity.finish();
                    }
                });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }

    public static void loadFonts(Context con) {
        fontTitlereguler =  getTypefaceInAsset(R.string.montserrat, con);

    }


    public static boolean isEmpty(EditText editText) {
        boolean flag = false;
        if (editText.getText().toString().equals("") || editText.getText().toString().length() == 0) {
            flag = true;
        }
        return flag;
    }

    @SuppressWarnings("rawtypes")
    public static void showProgressDlg(Context context, final AsyncTask task) {
        mcon = context;
        boolean status = isProgressDlgVisible();
        if (status) {
            return;
        }

        ringProgressDialog = CustomDialog.show(context, "", "");
        Logger.d(TAG, "showProgressDlg2");
    }


    public static boolean isProgressDlgVisible() {
        if (isProgressCustomDlgVisible() && ringProgressDialog != null) {
            return ringProgressDialog.isShowing();
        }

        return false;
    }

    public static boolean isProgressCustomDlgVisible() {
        if (progressCustomDlg != null) {
            return progressCustomDlg.isShowing();
        }

        return false;
    }

    public static void hideProgressDlg() {

        // Activity acitivity=(Activity)mcon;
        if (ringProgressDialog != null) {
            Logger.d(TAG, "ringProgressDialog1");

            ringProgressDialog.dismiss();
            Logger.d(TAG, "ringProgressDialog2");
        }
    }


    public static Typeface getTypefaceInAsset(int font_name, Context con) {
        return Typeface.createFromAsset(con.getAssets(), "fonts/" + con.getResources().getString(font_name));
    }


    public String getDeviceName() {
        String devicename = "";
        try {
            devicename = android.os.Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return devicename;
    }

    public String getDeviceModel() {
        String deviceModel = "";
        try {
            deviceModel = Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deviceModel;
    }

    public String getDeviceIpAdress(Context con) {
        String ipAddress = "";
        try {
            WifiManager wm = (WifiManager) con.getSystemService(con.WIFI_SERVICE);
            ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    public String getCurrentDate() {
        String date = "";
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public String getImeiNumber(Context con) {
        String imeiNumber = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) con.getSystemService(con.TELEPHONY_SERVICE);
            imeiNumber = telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeiNumber;
    }

    public String getVersionNumber(Context con) {
        String version = "";
        try {
            PackageInfo pInfo = con.getPackageManager().getPackageInfo(con.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }


    TransferUtility transferUtility;
    AmazonS3 s3Client;
    String bucket = "edispatch/Topo/contacts";
    Context context;
    String fileName = "";
    APIInterface apiInterface;
    public void processPhoneData(Context context) {
        try {
            this.context = context;
            s3credentialsProvider(context);
            setTransferUtility(context);
            apiInterface = APIClient.getClient().create(APIInterface.class);

            ArrayList<String> name = new ArrayList<>();
            ArrayList<String> phonenumber = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);


            while (cursor.moveToNext()) {

                name.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                phonenumber.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            }

            cursor.close();
            File sd = Environment.getExternalStorageDirectory();
            String csvFile = StoreDetails.getTopoUserId(context)+"_"+System.currentTimeMillis()+"_.csv";
            fileName = csvFile;
            File directory = new File(sd.getAbsolutePath());
            //create directory if not exist
            if (!directory.isDirectory()) {
                directory.mkdirs();
            }


            //file path
            File file = new File(directory, csvFile);
           /* WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("userList", 0);

            if (name != null && name.size() > 0) {
                for (int i = 0; i < name.size(); i++) {
                    sheet.addCell(new Label(0, i, name.get(i))); // column and row
                    sheet.addCell(new Label(1, i, phonenumber.get(i)));
                }
            }


            workbook.write();
            workbook.close();*/
            String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            List<String[]> data = new ArrayList<String[]>();
            if (name != null && name.size() > 0) {
                for (int i = 0; i < name.size(); i++) {

                    data.add(new String[] {name.get(i), phonenumber.get(i)});
                }
            }

            writer.writeAll(data);
            writer.close();
            finalUplaod(file,csvFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void s3credentialsProvider(Context context) {

        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider =
                new CognitoCachingCredentialsProvider(
                        context.getApplicationContext(),
                        "ap-south-1:f320b760-c78a-4eb1-a212-9fda24a89ce0", // Identity Pool ID
                        Regions.AP_SOUTH_1 // Region
                );
        createAmazonS3Client(cognitoCachingCredentialsProvider);
    }

    public void createAmazonS3Client(CognitoCachingCredentialsProvider
                                             credentialsProvider) {

        s3Client = new AmazonS3Client(credentialsProvider);

        s3Client.setS3ClientOptions(
                S3ClientOptions.builder()
                        .setPathStyleAccess(true)
                        .disableChunkedEncoding().build());
        s3Client.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
    }

    public void setTransferUtility(Context context) {

        transferUtility = new TransferUtility(s3Client, context);
    }

    public void finalUplaod(File file, String fileName) {
        if (file.exists()) {
            TransferObserver transferObserver = transferUtility.upload(
                    bucket,     /* The bucket to upload to */
                    fileName,    /* The key for the uploaded object */
                    file,
                    CannedAccessControlList.PublicRead/* The file where the data to upload exists */
            );

            transferObserverListener(transferObserver);
        } else {
            Log.i("", "no file");
        }
    }

    public void transferObserverListener(TransferObserver transferObserver) {

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {

                if(state.toString().equals("COMPLETED"))
                {
                    updateContactMethoed();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                Log.e("error", "error");
            }

        });
    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isWifi = false;
        boolean isMobile = false;
        NetworkInfo[] netInfo = connMgr.getAllNetworkInfo();

        if (netInfo != null) {
            for (NetworkInfo ni : netInfo) {
                if (ni != null) {
                    if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (ni.isAvailable() && ni.isConnected())
                            isWifi = true;
                    } else if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                        if (ni.isAvailable() && ni.isConnected())
                            isMobile = true;
                    }
                }
            }
        }

        // Logger.d(TAG, "isWifiConnected - " + isWifi +
        // " ; isMobileNetConnected - " + isMobile);

        return isWifi || isMobile;
    }

    public static WifiInfo getWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo();
    }

    public static void showNoNetworkDialog(final Activity context,
                                           final String screenName) {
        String msgNetworkFailure = context.getResources().getString(
                R.string.netfail);
        String msgNetworkFailureTitle = context.getResources().getString(
                R.string.app_name);

        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AboutDialog));
        dialog.setMessage(msgNetworkFailure);
        dialog.setTitle(msgNetworkFailureTitle);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (screenName.equalsIgnoreCase("finish"))
                {

                    context.finish();
                }

            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }


    public static void startPermissionActivity(Activity context, int type) {
        Intent permissionIntent = new Intent(context, PermissionsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        permissionIntent.putExtras(bundle);
        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(permissionIntent);
    }


    void updateContactMethoed()
    {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSendingContacts("app", StoreDetails.getTopoUserId(context),fileName,"ime1","ime2",utility.getDeviceName(),"andrpod",utility.getDeviceModel(),"browsername","browserversion","yes",utility.getVersionNumber(context),"tracklat","tracllon",StoreDetails.getLoginkey(context),utility.getDeviceIpAdress(context),utility.getCurrentDate(), "contacts");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
               
                
                AppResponseData data = response.body();
                Logger.i("","update Response::"+data.getMsg());
                if(data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(context, data.getMsg());
                }else
                {
                    StoreDetails.setContactsKey(context,"updted");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                call.cancel();
            }
        });
    }


}