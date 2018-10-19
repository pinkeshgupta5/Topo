package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.MyTopoListAdapter;
import com.dhivi.inc.topo.adapter.RequestTopoListAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
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
 * Created by manager on 31-10-2017.
 */

public class RequestTopoActivity extends Activity {

    @BindView(R.id.name_textview)
    TextView name_textview;
    @BindView(R.id.ph_textview)
    TextView ph_textview;
    @BindView(R.id.messages_res_textview)
    TextView messages_res_textview;
    @BindView(R.id.mytopo_recyclerview)
    RecyclerView mytopo_recyclerview;
    RequestTopoListAdapter requestTopoListAdapter;
    String requestsid = null;
    String TAG = RequestTopoActivity.class.getSimpleName();
    APIInterface apiInterface;
    String topoId = "";
    String timeperoid = null;
    String fromTopoUserId = "";
    @BindView(R.id.materialSeekBar)
    SeekBar materialSeekBar;
    @BindView(R.id.first_segment)
    TextView first_segment;
    @BindView(R.id.second_segment)
    TextView second_segment;
    @BindView(R.id.third_segment)
    TextView third_segment;
    @BindView(R.id.fourth_segment)
    TextView fourth_segment;
    @BindView(R.id.estimate_layout)
    RelativeLayout estimate_layout;
    @BindView(R.id.Estimated_time)
    TextView Estimated_time;
    String[] timenames = {"3 HR", "12 HR", "24 HR", "Always"};
    String[] times = {"3", "12", "24", "Always"};
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_toporequest);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        mytopo_recyclerview.setHasFixedSize(true);
        mytopo_recyclerview.setLayoutManager(new LinearLayoutManager(RequestTopoActivity.this));

        if (getIntent() != null) {
            requestsid = getIntent().getStringExtra("requestsid");
            if (requestsid != null) {
                Utility.showProgressDlg(RequestTopoActivity.this, null);
                getFullRequestToposMethoed();
            }
        }


        materialSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int number = progress;
                switch (number) {
                    case 0:
                        timeperoid = "3";
                        first_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.black));
                        second_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        third_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        fourth_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        break;
                    case 25:
                        timeperoid = "12";
                        first_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        second_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.black));
                        third_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        fourth_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        break;
                    case 50:
                        timeperoid = "24";
                        first_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        second_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        third_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.black));
                        fourth_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        break;
                    case 100:
                        timeperoid = "always";
                        first_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        second_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        third_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.list_textcolor));
                        fourth_segment.setTextColor(ContextCompat.getColor(RequestTopoActivity.this, R.color.black));
                        break;
                    default:
                        System.out.println("");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

       /* myTopoListAdapter = new MyTopoListAdapter(selectedListener);
        mytopo_recyclerview.setAdapter(myTopoListAdapter);*/
    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.delete_layout)
    void onDelete() {
        //confirmation reqired.....
        errorDialog(RequestTopoActivity.this, "Are you sure want to delete?");

    }

    @OnClick(R.id.estimate_layout)
    void estimateLayout() {
        final Dialog dialog = new Dialog(RequestTopoActivity.this);
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

    @OnClick(R.id.sendtopo_button)
    void callingMovingMethoed() {
        if (topoId != null && !topoId.equals("")) {
            if (flag) {
                if (timeperoid == null) {
                    Utility.errorDialog(RequestTopoActivity.this, "Please Sleect Accessing Period");
                } else {
                    Utility.showProgressDlg(RequestTopoActivity.this, null);
                    sendingInstatntToposMethoed();
                }
            } else {
                Utility.showProgressDlg(RequestTopoActivity.this, null);
                sendingInstatntToposMethoed();
            }

        } else {
            Utility.errorDialog(RequestTopoActivity.this, "Please select topo");
        }
    }

    @OnClick(R.id.sharevia_button)
    void callingShareMethoed() {
        if (topoId != null && !topoId.equals("")) {
            String shareurl = "https://www.dhivitopo.com/type=instant&id=" + topoId;
            shareTextUrl(shareurl);
        }
    }

    void getFullRequestToposMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doRequestView("app", StoreDetails.getTopoUserId(RequestTopoActivity.this), requestsid, "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(RequestTopoActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(RequestTopoActivity.this), utility.getDeviceIpAdress(RequestTopoActivity.this), utility.getCurrentDate(), "fullrequestview");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(RequestTopoActivity.this, data.getMsg());
                    } else {

                        if (data.getTopos() != null && data.getTopos().size() > 0) {
                            requestTopoListAdapter = new RequestTopoListAdapter(selectedListener, data.getTopos());
                            mytopo_recyclerview.setAdapter(requestTopoListAdapter);
                        }
                        if (data.getRequests() != null && data.getRequests().size() > 0) {
                            name_textview.setText(data.getRequests().get(0).getTopo_username());
                            ph_textview.setText(data.getRequests().get(0).getTopo_user_mobile());
                            messages_res_textview.setText(data.getRequests().get(0).getTopo_request_message());
                            fromTopoUserId = data.getRequests().get(0).getFrom_topo_user_id();
                        }

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(RequestTopoActivity.this, "Unable to handle request");
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

    MyTopoListAdapter.OnItemSelectedListener selectedListener = new MyTopoListAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Topos name) {
            if (name.getTopo_privacy().equals("Private")) {
                flag = true;
                estimate_layout.setVisibility(View.VISIBLE);
            } else {
                flag = false;
                estimate_layout.setVisibility(View.GONE);
            }
            topoId = name.getTopo_id();
        }
    };

    void setdeleteTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doRequestDelete("app", StoreDetails.getTopoUserId(RequestTopoActivity.this), requestsid, "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(RequestTopoActivity.this), "", "", StoreDetails.getLoginkey(RequestTopoActivity.this), utility.getDeviceIpAdress(RequestTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(RequestTopoActivity.this, data.getMsg());
                    } else {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                new ContextThemeWrapper(RequestTopoActivity.this, R.style.AboutDialog));
                        alertDialog.setTitle("");

                        alertDialog.setMessage(data.getMsg());
                        alertDialog.setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        Intent i = new Intent(RequestTopoActivity.this, NewHomeActivity.class);
                                        startActivity(i);
                                        finishAffinity();
                                    }
                                });
                        AlertDialog alertDialog1 = alertDialog.create();
                        alertDialog1.show();
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(RequestTopoActivity.this, "Unable to handle request");
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

    private void shareTextUrl(String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Going to dilip home easyly...");
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    void sendingInstatntToposMethoed() {

        Utility utility = new Utility();

        Call<AppResponseData> call = apiInterface.doSendTopoRequest("app", requestsid, topoId, fromTopoUserId, StoreDetails.getTopoUserId(RequestTopoActivity.this), timeperoid, "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(RequestTopoActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(RequestTopoActivity.this), utility.getDeviceIpAdress(RequestTopoActivity.this), utility.getCurrentDate(), "sendinginstantttopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {


                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());

                    Utility.hideProgressDlg();
                    if (data.getResult().toLowerCase().equals("failed")) {

                        Utility.errorDialog(RequestTopoActivity.this, data.getMsg());

                    } else {
                        Utility.errorDialog(RequestTopoActivity.this, data.getMsg(), RequestTopoActivity.this);
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(RequestTopoActivity.this, "Unable to handle request");
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

    public void errorDialog(Context con, String error) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(con, R.style.AboutDialog));
        alertDialog.setTitle("");

        alertDialog.setMessage(error);
        alertDialog.setPositiveButton(con.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utility.showProgressDlg(RequestTopoActivity.this, null);
                        setdeleteTopoMethoed();
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton(con.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }
}