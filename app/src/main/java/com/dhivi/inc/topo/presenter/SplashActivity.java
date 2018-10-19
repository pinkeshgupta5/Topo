package com.dhivi.inc.topo.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.PermissionUtils;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Manoj on 10/26/2017.
 */

public class SplashActivity extends Activity {

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.gifimageview)
    ImageView gifimageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spalsh);
        printKeyHash(SplashActivity.this);
        ButterKnife.bind(this);
        webview.loadUrl("file:///android_asset/splash.html");

       // GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifimageview);
        //Glide.with(this).load(R.raw.splash).into(imageViewTarget);

        int SPLASH_TIME_OUT = 3000;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (Utility.isNetworkAvailable(SplashActivity.this)) {
                    onTimerFinish();
                } else {
                    Utility.showNoNetworkDialog(SplashActivity.this, "finish");
                }

            }
        }, SPLASH_TIME_OUT);

    }

    private void onTimerFinish() {
        if (StoreDetails.getLoginkey(SplashActivity.this) != null && !StoreDetails.getLoginkey(SplashActivity.this).equals("")) {
            if (StoreDetails.getUsernameValue(SplashActivity.this) != null && !StoreDetails.getUsernameValue(SplashActivity.this).equals("")) {
                if (StoreDetails.getNumberOfTopos(SplashActivity.this) == 0) {
                    Intent i = new Intent(SplashActivity.this, EmptyToposActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, NewHomeActivity.class);
                    startActivity(i);
                    finish();
                }
            } else {
                if (StoreDetails.getTutorialData(SplashActivity.this) != null && !StoreDetails.getTutorialData(SplashActivity.this).equals("")) {
                    Intent i = new Intent(SplashActivity.this, FbLoginActivity.class);
                    startActivity(i);
                    finish();
                }else
                {
                    Intent i = new Intent(SplashActivity.this, TutorialActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        } else {
                if (StoreDetails.getTutorialData(SplashActivity.this) != null && !StoreDetails.getTutorialData(SplashActivity.this).equals("")) {
                    Intent i = new Intent(SplashActivity.this, FbLoginActivity.class);
                    startActivity(i);
                    finish();
                }else
                {
                    Intent i = new Intent(SplashActivity.this, TutorialActivity.class);
                    startActivity(i);
                    finish();
                }

        }
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));


                // String key = new String(Base64.encodeBytes(md.digest()));
                Logger.i("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

}
