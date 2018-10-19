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

public class HelpActivity extends Activity {

    @BindView(R.id.comments_edittext)
    EditText comments_edittext;

    int helpcall = 0;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.submit_button)
    void updateComments() {
        if (Utility.isEmpty(comments_edittext)) {
            Utility.errorDialog(HelpActivity.this, "Please Enter Your Comments");
        } else {

            if (helpcall == 0) {
                helpcall = 1;
                Utility.showProgressDlg(HelpActivity.this, null);
                updatehelpMethoed();
            }

        }
    }

    void updatehelpMethoed() {
        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doHelp("app", StoreDetails.getTopoUserId(HelpActivity.this), comments_edittext.getText().toString(), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(HelpActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(HelpActivity.this), utility.getDeviceIpAdress(HelpActivity.this), utility.getCurrentDate(), "help");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                helpcall = 0;
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i("", "update Response::" + data.getMsg());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(HelpActivity.this, data.getMsg());
                    } else {
                        Utility.errorDialog(HelpActivity.this, data.getMsg(), HelpActivity.this);
                    }
                } else {
                    Utility.errorDialog(HelpActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                helpcall = 0;
                call.cancel();
            }
        });
    }
}

