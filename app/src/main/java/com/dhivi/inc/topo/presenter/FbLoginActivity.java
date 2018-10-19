package com.dhivi.inc.topo.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;


import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.SharedPrefManager;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.MessageClient;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by User on 12/7/2017.
 */

public class FbLoginActivity extends Activity {


    APIInterface apiInterface;
    APIInterface messageInterface;
    String phoneNumberString;
    String countryCode;
    @BindView(R.id.otp_layout)
    RelativeLayout otp_layout;
    @BindView(R.id.countrycodeedittext)
    EditText countrycodeedittext;
    @BindView(R.id.countrycodespinner)
    Spinner countrycodespinner;
    @BindView(R.id.mobileedittext)
    EditText mobileedittext;
    @BindView(R.id.name_edittext)
    EditText name_edittext;
    @BindView(R.id.update_layout)
    RelativeLayout update_layout;
    @BindView(R.id.verifyotp_layout)
    RelativeLayout verifyotp_layout;
    @BindView(R.id.verifyotp_edittext)
    EditText verifyotp_edittext;
    String genarateRandomeNumber;
    int fbcall = 0;
    int otpcall = 0;
    //String countrycode = "";
    ArrayList<String> list = new ArrayList<>();
    String locale = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fblogin);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        messageInterface = MessageClient.getClient().create(APIInterface.class);
        Utility.startPermissionActivity(FbLoginActivity.this, 124);
        Utility.showProgressDlg(FbLoginActivity.this, null);


        //locale = getResources().getConfiguration().locale.getCountry();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            locale = tm.getNetworkCountryIso();
            String mPhoneNumber = tm.getLine1Number();
            mobileedittext.setText(mPhoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getCountrycodeData();
        if (StoreDetails.getLoginkey(FbLoginActivity.this) != null && !StoreDetails.getLoginkey(FbLoginActivity.this).equals("")) {
            if (StoreDetails.getUsernameValue(FbLoginActivity.this) != null && !StoreDetails.getUsernameValue(FbLoginActivity.this).equals("")) {
                otp_layout.setVisibility(View.VISIBLE);
                update_layout.setVisibility(View.GONE);
            } else {
                otp_layout.setVisibility(View.GONE);
                update_layout.setVisibility(View.VISIBLE);
            }
        } else {
            otp_layout.setVisibility(View.VISIBLE);
            update_layout.setVisibility(View.GONE);
        }


        Random random = new Random();
        genarateRandomeNumber = String.format("%04d", random.nextInt(10000));

        countrycodespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCode = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.otp_button)
    void getOtp() {
        if (Utility.isEmpty(mobileedittext)) {
            Utility.errorDialog(FbLoginActivity.this, "Please enter mobile number");
        } else if (Utility.isEmpty(countrycodeedittext)) {
            Utility.errorDialog(FbLoginActivity.this, "Please enter country code");
        } else {
            if (fbcall == 0) {
                fbcall = 1;
                doStatTopoMethoed("sendotp",mobileedittext.getText().toString());
                Utility.showProgressDlg(FbLoginActivity.this, null);
                // fbLoginMethoed();
                usergetOtpMethoed();

            }
        }
    }

    @OnClick(R.id.resendotp)
    void getResendOtp()
    {
        doStatTopoMethoed("resendotp",mobileedittext.getText().toString());
        Utility.showProgressDlg(FbLoginActivity.this, null);
        ressendOtpMethoed();
    }

    @OnClick(R.id.cross_layout)
    void hideOtpLayout() {
        verifyotp_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.updateuser_button)
    void getUserUpdate() {
        if (Utility.isEmpty(name_edittext)) {
            Utility.errorDialog(FbLoginActivity.this, "Please Enter Email");
        } else {
            if (fbcall == 0) {
                fbcall = 1;
                Utility.showProgressDlg(FbLoginActivity.this, null);
                userUpdateMethoed();

            }
        }
    }

    @OnClick(R.id.verifyotp_button)
    void getVerifyOtp() {
        if (Utility.isEmpty(verifyotp_edittext)) {
            Utility.errorDialog(FbLoginActivity.this, "Please Enter OTP");
        } else {
            if (genarateRandomeNumber.equals(verifyotp_edittext.getText().toString())) {
                if (otpcall == 0) {
                    otpcall = 1;
                    Utility.showProgressDlg(FbLoginActivity.this, null);
                    fbLoginMethoed();
                }
            } else {
                Utility.errorDialog(FbLoginActivity.this, "Please Enter Valid OTP");
            }
        }
    }

    void fbLoginMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doFbOauth("app", SharedPrefManager.getInstance(FbLoginActivity.this).getDeviceToken(), mobileedittext.getText().toString(), countrycodeedittext.getText().toString(), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(FbLoginActivity.this), "", "", "", utility.getDeviceIpAdress(FbLoginActivity.this), utility.getCurrentDate(), "signin");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                otpcall = 0;
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "login Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(FbLoginActivity.this, data.getMsg());
                    } else {
                        if (data.getFbOauth() != null) {
                            StoreDetails.setLoginKey(FbLoginActivity.this, data.getFbOauth().getTracking_login_key());
                            StoreDetails.setTopoUserId(FbLoginActivity.this, data.getFbOauth().getTopo_user_id());
                            StoreDetails.setUserContry(FbLoginActivity.this, data.getFbOauth().getTopo_user_country());
                            StoreDetails.setNumberOfTopos(FbLoginActivity.this, data.getFbOauth().getNoOfTopos());
                            if (data.getFbOauth().getAskName().equals("Yes")) {
                                otp_layout.setVisibility(View.GONE);
                                verifyotp_layout.setVisibility(View.GONE);
                                update_layout.setVisibility(View.VISIBLE);
                            } else {
                                StoreDetails.setUsernameValue(FbLoginActivity.this, "inserted");
                                if (data.getFbOauth().getNoOfTopos() == 0) {
                                    Intent i = new Intent(FbLoginActivity.this, EmptyToposActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Intent i = new Intent(FbLoginActivity.this, NewHomeActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                    }
                } else {
                    Utility.errorDialog(FbLoginActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                otpcall = 0;
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }

    void userUpdateMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doUserNameUpdate("app", StoreDetails.getTopoUserId(FbLoginActivity.this), name_edittext.getText().toString(), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(FbLoginActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(FbLoginActivity.this), utility.getDeviceIpAdress(FbLoginActivity.this), utility.getCurrentDate(), "editprofile");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                fbcall = 0;
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(FbLoginActivity.this, data.getMsg());
                    } else {
                        StoreDetails.setUsernameValue(FbLoginActivity.this, "inserted");
                        Intent i = new Intent(FbLoginActivity.this, EmptyToposActivity.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Utility.errorDialog(FbLoginActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                fbcall = 0;
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }

    void usergetOtpMethoed() {
        Logger.i("", "usergetOtpMethoed" + countrycodeedittext.getText().toString());
        Call<AppResponseData> call = messageInterface.doSendOtp(countrycodeedittext.getText().toString() + mobileedittext.getText().toString(), genarateRandomeNumber + "%20is%20OTP%20for%20Topo%20-%20Digital%20addressing%20%26%20Location%20Sharing%20App", genarateRandomeNumber);
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                fbcall = 0;
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "usergetOtpMethoed Response::" + data.getType());
                    if (data.getType().equals("success")) {
                        verifyotp_layout.setVisibility(View.VISIBLE);
                    } else {

                    }
                } else {
                    Utility.errorDialog(FbLoginActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                fbcall = 0;
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }


    public void getCountrycodeData() {
        Call<AppResponseData> call = apiInterface.doUserCountriesList("app");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    if (data.getCounriesList() != null && data.getCounriesList().size() > 0) {

                        int position = 0;
                        list = new ArrayList<String>();
                        ArrayList<String> countycodes = new ArrayList<>();
                        for (int i = 0; i < data.getCounriesList().size(); i++) {
                            list.add(data.getCounriesList().get(i).getCalling_code());
                            countycodes.add(data.getCounriesList().get(i).getCca2().toLowerCase());
                        }
                        if (locale == null) {
                            countrycodeedittext.setText("91");
                        } else {
                            if (countycodes.contains(locale)) {
                                position = countycodes.indexOf(locale);
                            }
                            countrycodeedittext.setText(list.get(position));
                        }


                        ArrayAdapter<String> countrycodeAdapter = new ArrayAdapter<String>(FbLoginActivity.this, android.R.layout.simple_expandable_list_item_1, list);
                        countrycodespinner.setAdapter(countrycodeAdapter);
                        countrycodespinner.setSelection(position);


                    } else {
                        Utility.errorDialog(FbLoginActivity.this, "No Country code list");
                    }
                } else {
                    Utility.errorDialog(FbLoginActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }


    void ressendOtpMethoed() {
        Logger.i("", "ressendOtpMethoed" + countrycodeedittext.getText().toString());
        Call<AppResponseData> call = messageInterface.doResendOtp(countrycodeedittext.getText().toString() + mobileedittext.getText().toString());
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                fbcall = 0;
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "usergetOtpMethoed Response::" + data.getType());
                    if (data.getType().equals("success")) {
                        verifyotp_layout.setVisibility(View.VISIBLE);
                    } else {

                    }
                } else {
                    Utility.errorDialog(FbLoginActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                fbcall = 0;
                Utility.hideProgressDlg();
                call.cancel();
            }
        });
    }

    void doStatTopoMethoed(String item,String action) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoStats("app",item,action,"", "", "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(FbLoginActivity.this), "", "", StoreDetails.getLoginkey(FbLoginActivity.this), utility.getDeviceIpAdress(FbLoginActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                call.cancel();
            }
        });
    }
}
