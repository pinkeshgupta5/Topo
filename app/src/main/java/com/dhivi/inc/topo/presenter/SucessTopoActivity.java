package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 12/24/2017.
 */

public class SucessTopoActivity extends Activity {


    String topoName = "";
    String from = null;
    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sucesstopo);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        if (getIntent() != null) {
            topoName = getIntent().getStringExtra("topoName");
            from = getIntent().getStringExtra("from");
        }

    }

    @OnClick(R.id.cross_layout)
    void crossClick() {
        Intent i = new Intent(SucessTopoActivity.this, ViewTopoActivity.class);
        i.putExtra("topoName", topoName);
        i.putExtra("from", from);
        startActivity(i);
    }

    @OnClick(R.id.share_button)
    void shareTopo() {
        doStatTopoMethoed("firsttopocreation","share");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Going to dilip home easyly...");
        share.putExtra(Intent.EXTRA_TEXT, "https://www.topoapp.in/campaign.php?id="+ StoreDetails.getTopoUserId(SucessTopoActivity.this)+"&cid=2");
        startActivity(Intent.createChooser(share, "Share link!"));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        crossClick();
    }

    void doStatTopoMethoed(String item,String action) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoStats("app",item,action, "0", StoreDetails.getTopoUserId(SucessTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(SucessTopoActivity.this), "", "", StoreDetails.getLoginkey(SucessTopoActivity.this), utility.getDeviceIpAdress(SucessTopoActivity.this), utility.getCurrentDate(), "savetopo");
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
