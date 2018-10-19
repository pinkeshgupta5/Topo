package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/3/2017.
 */

public class ChangePasswordActivity extends Activity {

    @BindView(R.id.currentpassword_edittext)
    EditText currentpassword_edittext;
    @BindView(R.id.newpassword_edittext)
    EditText newpassword_edittext;
    @BindView(R.id.confirmpassword_edittext)
    EditText confirmpassword_edittext;

    int passwordcall = 0;
    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_changepwd);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);


    }

    @OnClick(R.id.back_layout)
    void onBack()
    {
        finish();
    }

    @OnClick(R.id.updatepassword_button)
    void updatePassword()
    {
        if (Utility.isEmpty(currentpassword_edittext)) {
            Utility.errorDialog(ChangePasswordActivity.this, "Please Enter Current Password");
        } else if (Utility.isEmpty(newpassword_edittext)) {
            Utility.errorDialog(ChangePasswordActivity.this, "Please Enter New Password");
        } else if (Utility.isEmpty(confirmpassword_edittext)) {
            Utility.errorDialog(ChangePasswordActivity.this, "Please Enter New Confirm Password");
        } else if (!confirmpassword_edittext.getText().toString().equals(newpassword_edittext.getText().toString())) {
            Utility.errorDialog(ChangePasswordActivity.this, "Please Enter Same Confirm password");
        } else {

            if(passwordcall==0)
            {
                passwordcall =1;
                Utility.showProgressDlg(ChangePasswordActivity.this,null);
                updatePasswordMethoed();
            }

        }
    }

    void updatePasswordMethoed()
    {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doChangePassword("app", StoreDetails.getTopoUserId(ChangePasswordActivity.this),currentpassword_edittext.getText().toString(),newpassword_edittext.getText().toString(),"ime1","ime2",utility.getDeviceName(),"andrpod",utility.getDeviceModel(),"browsername","browserversion","yes",utility.getVersionNumber(ChangePasswordActivity.this),"tracklat","tracllon",StoreDetails.getLoginkey(ChangePasswordActivity.this),utility.getDeviceIpAdress(ChangePasswordActivity.this),utility.getCurrentDate(), "changepassword");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                passwordcall = 0;
                AppResponseData data = response.body();
                Logger.i("","update Response::"+data.getMsg());
                if(data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(ChangePasswordActivity.this, data.getMsg());
                }else
                {
                    Utility.errorDialog(ChangePasswordActivity.this, data.getMsg(),ChangePasswordActivity.this);
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                passwordcall = 0;
                call.cancel();
            }
        });
    }

}

