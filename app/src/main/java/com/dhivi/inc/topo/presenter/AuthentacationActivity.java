package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.SharedPrefManager;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.MessageClient;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.Authentication;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.TransparentOutlineProvider;
import com.dhivi.inc.topo.utils.Utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 10/26/2017.
 */

public class AuthentacationActivity extends Activity {

    String TAG = AuthentacationActivity.class.getSimpleName();

    @BindView(R.id.email_edittext)
    EditText email_edittext;
    @BindView(R.id.password_edittext)
    EditText password_edittext;
    @BindView(R.id.name_edittext)
    EditText name_edittext;
    @BindView(R.id.signup_email_edittext)
    EditText signup_email_edittext;
    @BindView(R.id.mobile_edittext)
    EditText mobile_edittext;
    @BindView(R.id.signup_password_edittext)
    EditText signup_password_edittext;
    @BindView(R.id.forgotemail_edittext)
    EditText forgotemail_edittext;
    @BindView(R.id.forgot_layout)
    RelativeLayout forgot_layout;
    @BindView(R.id.login_button)
    Button login_button;
    @BindView(R.id.signup_button)
    Button signup_button;
    @BindView(R.id.name_layout)
    RelativeLayout name_layout;
    @BindView(R.id.mobile_layout)
    RelativeLayout mobile_layout;
    @BindView(R.id.signup_email_layout)
    RelativeLayout signup_email_layout;
    @BindView(R.id.signup_password_layout)
    RelativeLayout signup_password_layout;
    @BindView(R.id.email_layout)
    RelativeLayout email_layout;
    @BindView(R.id.password_layout)
    RelativeLayout password_layout;
    @BindView(R.id.login_layout)
    LinearLayout login_layout;
    @BindView(R.id.sigup_layout)
    LinearLayout sigup_layout;


    APIInterface apiInterface;
    int signup = 0;
    int login = 0;
    int signupcall = 0;
    int logincall = 0;
    int resetcall = 0;
    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        forgot_layout.setVisibility(View.GONE);
        chaingingLogin();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Utility.startPermissionActivity(AuthentacationActivity.this, 124);

        Logger.i(TAG, "the fcm device key::" + StoreDetails.getFcmDeviceKey(AuthentacationActivity.this));

        login_button.setOutlineProvider(new TransparentOutlineProvider(0.86f));
        signup_button.setOutlineProvider(new TransparentOutlineProvider(0.86f));
    }

    void chaingingLogin() {

        name_edittext.setText("");
        mobile_edittext.setText("");
        login_button.setBackgroundResource(R.drawable.whiteroundedbg);
        login_button.setTextColor(getResources().getColor(R.color.login_textcolor));
        signup_button.setBackgroundColor(getResources().getColor(R.color.transperent));
        signup_button.setTextColor(getResources().getColor(R.color.login_textcolor));

        name_layout.setVisibility(View.GONE);
        mobile_layout.setVisibility(View.GONE);
        signup_email_layout.setVisibility(View.GONE);
        signup_password_layout.setVisibility(View.GONE);

        email_layout.setVisibility(View.VISIBLE);
        password_layout.setVisibility(View.VISIBLE);

        email_edittext.requestFocus();

        /*Animation leftSwipe = AnimationUtils.loadAnimation(AuthentacationActivity.this, R.anim.animate_left);
        login_layout.startAnimation(leftSwipe);*/


    }

    void chaingingSignUp() {

        email_edittext.setText("");
        password_edittext.setText("");
        signup_button.setBackgroundResource(R.drawable.whiteroundedbg);
        signup_button.setTextColor(getResources().getColor(R.color.login_textcolor));
        login_button.setBackgroundColor(getResources().getColor(R.color.transperent));
        login_button.setTextColor(getResources().getColor(R.color.login_textcolor));

        name_layout.setVisibility(View.VISIBLE);
        mobile_layout.setVisibility(View.VISIBLE);
        signup_email_layout.setVisibility(View.VISIBLE);
        signup_password_layout.setVisibility(View.VISIBLE);

        email_layout.setVisibility(View.GONE);
        password_layout.setVisibility(View.GONE);

       /* Animation RightSwipe = AnimationUtils.loadAnimation(AuthentacationActivity.this, R.anim.animate_right);
        sigup_layout.startAnimation(RightSwipe);*/

        name_edittext.requestFocus();

        login = 0;
    }

    //** Bound actions
    @OnClick(R.id.login_button)
    void callingLogin() {
        chaingingLogin();

        if (signup == 0) {
            if (Utility.isEmpty(email_edittext)) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Email");
            } else if (!validate(email_edittext.getText().toString())) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Valid Email");
            } else if (Utility.isEmpty(password_edittext)) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Pssword");
            } else {
                if (logincall == 0) {
                    logincall = 1;
                    Utility.showProgressDlg(AuthentacationActivity.this, null);
                    Logger.i(TAG, "the fcm device key::" + SharedPrefManager.getInstance(AuthentacationActivity.this).getDeviceToken());
                    loginMethoed();
                }

            }
        } else {
            signup = 0;
            if (login == 0) {
                login = 1;
            } else {
                if (Utility.isEmpty(email_edittext)) {
                    Utility.errorDialog(AuthentacationActivity.this, "Please Enter Email");
                } else if (!validate(email_edittext.getText().toString())) {
                    Utility.errorDialog(AuthentacationActivity.this, "Please Enter Valid Email");
                } else if (Utility.isEmpty(password_edittext)) {
                    Utility.errorDialog(AuthentacationActivity.this, "Please Enter Pssword");
                } else {
                    if (logincall == 0) {
                        logincall = 1;
                        Utility.showProgressDlg(AuthentacationActivity.this, null);
                        loginMethoed();
                    }

                }
            }
        }

    }

    @OnClick(R.id.signup_button)
    void callingSignUp() {
        chaingingSignUp();

        if (signup == 0) {
            signup = 1;
        } else {
            if (Utility.isEmpty(signup_email_edittext)) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Email");
            } else if (!validate(signup_email_edittext.getText().toString())) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Valid Email");
            } else if (Utility.isEmpty(name_edittext)) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Email");
            } else if (Utility.isEmpty(mobile_edittext)) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Mobile Number");
            } else if (Utility.isEmpty(signup_password_edittext)) {
                Utility.errorDialog(AuthentacationActivity.this, "Please Enter Pssword");
            } else {
                if (signupcall == 0) {
                    signupcall = 1;
                    Utility.showProgressDlg(AuthentacationActivity.this, null);
                    signUpMethoed();
                }

            }
        }
    }

    @OnClick(R.id.forgotpwd_layout)
    void showingForgotPwd() {
        forgot_layout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.cross_layout)
    void hideForgotPwd() {
        forgot_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.reset_button)
    void callingResetPwd() {
        if (Utility.isEmpty(forgotemail_edittext)) {
            Utility.errorDialog(AuthentacationActivity.this, "Please Enter Email");
        } else {
            if (resetcall == 0) {
                resetcall = 1;
                Utility.showProgressDlg(AuthentacationActivity.this, null);
                resetUpMethoed();
            }

        }
    }


    void loginMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSignin("app", SharedPrefManager.getInstance(AuthentacationActivity.this).getDeviceToken(), email_edittext.getText().toString(), password_edittext.getText().toString(), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(AuthentacationActivity.this), "", "", "", utility.getDeviceIpAdress(AuthentacationActivity.this), utility.getCurrentDate(), "signin");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "login Response::" + data.getMsg());
                    logincall = 0;
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(AuthentacationActivity.this, data.getMsg());
                    } else {
                        if (data.getSingin() != null) {
                            StoreDetails.setLoginKey(AuthentacationActivity.this, data.getSingin().getTracking_login_key());
                            StoreDetails.setTopoUserId(AuthentacationActivity.this, data.getSingin().getTopo_user_id());
                            StoreDetails.setUserContry(AuthentacationActivity.this, data.getSingin().getTopo_user_country());
                            Intent i = new Intent(AuthentacationActivity.this, NewHomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                } else {
                    Utility.errorDialog(AuthentacationActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                logincall = 0;
                call.cancel();
            }
        });
    }

    void signUpMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSignUp("app", SharedPrefManager.getInstance(AuthentacationActivity.this).getDeviceToken(), name_edittext.getText().toString(), signup_email_edittext.getText().toString(), mobile_edittext.getText().toString(), "", "", "", signup_password_edittext.getText().toString(), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(AuthentacationActivity.this), "", "", "", utility.getDeviceIpAdress(AuthentacationActivity.this), utility.getCurrentDate(), "signup");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                signupcall = 0;
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "signup Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(AuthentacationActivity.this, data.getMsg());
                    } else {
                        if (data.getSignup() != null) {
                            StoreDetails.setLoginKey(AuthentacationActivity.this, data.getSignup().getTracking_login_key());
                            StoreDetails.setTopoUserId(AuthentacationActivity.this, data.getSignup().getTopo_user_id());
                            StoreDetails.setUserContry(AuthentacationActivity.this, data.getSignup().getTopo_user_country());
                            Intent i = new Intent(AuthentacationActivity.this, NewHomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                } else {
                    Utility.errorDialog(AuthentacationActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Logger.i("", "error" + t.getMessage());
                Utility.hideProgressDlg();
                signupcall = 0;
                call.cancel();
            }
        });
    }

    void resetUpMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doReset("app", forgotemail_edittext.getText().toString(), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(AuthentacationActivity.this), "", "", "", utility.getDeviceIpAdress(AuthentacationActivity.this), utility.getCurrentDate(), "forgotpwd");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                resetcall = 0;
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed"))
                        Utility.errorDialog(AuthentacationActivity.this, data.getMsg());
                    else
                        Utility.errorDialog(AuthentacationActivity.this, data.getMsg());

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(AuthentacationActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                resetcall = 0;
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    public boolean validate(final String hex) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }


}