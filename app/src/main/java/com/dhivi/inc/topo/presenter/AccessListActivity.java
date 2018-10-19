package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.AccessListAdapter;
import com.dhivi.inc.topo.adapter.RequestTopoListAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AccessList;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.Topos;
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
 * Created by User on 11/11/2017.
 */

public class AccessListActivity extends Activity {

    @BindView(R.id.access_recyclerview)
    RecyclerView access_recyclerview;
    @BindView(R.id.editaccess_layout)
    RelativeLayout editaccess_layout;
    @BindView(R.id.Estimated_time)
    TextView Estimated_time;
    @BindView(R.id.updateacess_button)
    Button updateacess_button;
    String[] timenames = {"3 HR", "12 HR", "24 HR", "Always"};
    String[] times = {"3", "12", "24", "Always"};
    String timeperoid = null;
    String topoAccessId = "";

    APIInterface apiInterface;
    String TAG = AccessListActivity.class.getSimpleName();
    AccessListAdapter accessListAdapter;
    String topoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_access);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);


        access_recyclerview.setHasFixedSize(true);
        access_recyclerview.setLayoutManager(new LinearLayoutManager(AccessListActivity.this));

        if (getIntent() != null) {
            topoName = getIntent().getStringExtra("topoName");
            Utility.showProgressDlg(AccessListActivity.this, null);
            getAccessToposMethoed();
        }

    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.cross_layout)
    void onHideLayout() {
        editaccess_layout.setVisibility(View.GONE);
    }

    @OnClick(R.id.estimate_layout)
    void estimateLayout() {
        final Dialog dialog = new Dialog(AccessListActivity.this);
        dialog.setContentView(R.layout.business_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.header_titie);
        text.setText(getResources().getString(R.string.businesstype));
        ListView busness_listview = (ListView) dialog.findViewById(R.id.busness_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.business_list_item, timenames);
        busness_listview.setAdapter(adapter);
        busness_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeperoid = times[position];
                Estimated_time.setText(timenames[position]);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.updateacess_button)
    void onUpdateAccessMethoed() {
        getAccessTopoUpdateMethoed();
    }

    void getAccessToposMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doAccessList("app", topoName, StoreDetails.getTopoUserId(AccessListActivity.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(AccessListActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(AccessListActivity.this), utility.getDeviceIpAdress(AccessListActivity.this), utility.getCurrentDate(), "fullrequestview");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(AccessListActivity.this, data.getMsg());
                    } else {

                        if (data.getAccessList() != null && data.getAccessList().size() > 0) {
                            accessListAdapter = new AccessListAdapter(itemSelectedListener, data.getAccessList());
                            access_recyclerview.setAdapter(accessListAdapter);
                        }

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(AccessListActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    AccessListAdapter.OnItemSelectedListener itemSelectedListener = new AccessListAdapter.OnItemSelectedListener() {
        @Override
        public void onItemDelete(final AccessList topo) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    new ContextThemeWrapper(AccessListActivity.this, R.style.AboutDialog));
            alertDialog.setTitle("");

            alertDialog.setMessage("Are sure want to delete?");
            alertDialog.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Utility.showProgressDlg(AccessListActivity.this, null);
                            getAccessToposDeleteMethoed(topo.getTopo_access_id());
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog1 = alertDialog.create();
            alertDialog1.show();


        }

        @Override
        public void onItemUpdate(AccessList topo) {
            editaccess_layout.setVisibility(View.VISIBLE);
            timeperoid = topo.getTopo_access_period();
            topoAccessId = topo.getTopo_access_id();
            if (!topo.getTopo_access_period().equals("Always"))
                Estimated_time.setText(topo.getTopo_access_period() + " HR");
            else
                Estimated_time.setText(topo.getTopo_access_period());

        }

    };

    void getAccessToposDeleteMethoed(String deleteId) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doAccessDelete("app", StoreDetails.getTopoUserId(AccessListActivity.this), deleteId, "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(AccessListActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(AccessListActivity.this), utility.getDeviceIpAdress(AccessListActivity.this), utility.getCurrentDate(), "fullrequestview");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(AccessListActivity.this, data.getMsg());
                    } else {

                        Utility.showProgressDlg(AccessListActivity.this, null);
                        getAccessToposMethoed();

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(AccessListActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    void getAccessTopoUpdateMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doAccessUpdate("app", StoreDetails.getTopoUserId(AccessListActivity.this), topoAccessId, timeperoid, "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(AccessListActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(AccessListActivity.this), utility.getDeviceIpAdress(AccessListActivity.this), utility.getCurrentDate(), "fullrequestview");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(AccessListActivity.this, data.getMsg());
                    } else {
                        editaccess_layout.setVisibility(View.GONE);
                        Utility.showProgressDlg(AccessListActivity.this, null);
                        getAccessToposMethoed();

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(AccessListActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

}
