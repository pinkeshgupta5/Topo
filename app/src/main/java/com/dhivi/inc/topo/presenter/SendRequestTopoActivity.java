package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.MyTopoUsersAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.FetchContacts;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 11/15/2017.
 */

public class SendRequestTopoActivity extends Activity {

    @BindView(R.id.topousers_recyclerview)
    RecyclerView topousers_recyclerview;
    @BindView(R.id.message_edittext)
    EditText message_edittext;
    @BindView(R.id.search_edittext)
    EditText search_edittext;
    @BindView(R.id.noresults)
    TextView noresults;
    MyTopoUsersAdapter usersAdapter;
    String TAG = SendRequestTopoActivity.class.getSimpleName();
    APIInterface apiInterface;
    String TopoId;
    String topouserId;
    ArrayList<String> userIds = new ArrayList<>();
    int totalcalls = 0;
    ArrayList<FetchContacts> fetchContacts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sentoporequest);
        ButterKnife.bind(this);

        topousers_recyclerview.setHasFixedSize(true);
        topousers_recyclerview.setLayoutManager(new LinearLayoutManager(SendRequestTopoActivity.this));

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Utility.showProgressDlg(SendRequestTopoActivity.this, null);
        getRequestToposMethoed();

        if (getIntent() != null) {
            TopoId = getIntent().getStringExtra("TopoId");
            topouserId = getIntent().getStringExtra("topouserId");
        }


        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("") && s.toString().length() > 0) {
                    ArrayList<FetchContacts> searchResult = new ArrayList<>();
                    if (fetchContacts != null && fetchContacts.size() > 0) {
                        for (int i = 0; i < fetchContacts.size(); i++) {
                            if (fetchContacts.get(i).getTopo_user_name().toLowerCase().contains(s.toString())) {
                                searchResult.add(fetchContacts.get(i));
                            }
                        }
                        if (searchResult != null && searchResult.size() > 0) {
                            noresults.setVisibility(View.GONE);
                            usersAdapter = new MyTopoUsersAdapter(selectedListener, searchResult);
                            topousers_recyclerview.setAdapter(usersAdapter);
                            topousers_recyclerview.setVisibility(View.VISIBLE);
                            noresults.setVisibility(View.GONE);
                        } else {
                            noresults.setVisibility(View.VISIBLE);
                            topousers_recyclerview.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (fetchContacts != null && fetchContacts.size() > 0) {
                        topousers_recyclerview.setVisibility(View.VISIBLE);
                        usersAdapter = new MyTopoUsersAdapter(selectedListener, fetchContacts);
                        topousers_recyclerview.setAdapter(usersAdapter);
                        noresults.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick(R.id.sendrequesttopo_button)
    void sendingRequesttTopos() {

        if (userIds.size() > 0) {
            if (Utility.isEmpty(message_edittext)) {
                Utility.errorDialog(SendRequestTopoActivity.this, "Please Enter Message");
            } else {
                totalcalls = 0;
                Utility.showProgressDlg(SendRequestTopoActivity.this, null);
                sendingRequestToposMethoed(userIds.get(totalcalls));
            }
        }
    }


    void getRequestToposMethoed() {

        Utility utility = new Utility();

        Call<AppResponseData> call = apiInterface.dofetchContacts("app", StoreDetails.getTopoUserId(SendRequestTopoActivity.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(SendRequestTopoActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(SendRequestTopoActivity.this), utility.getDeviceIpAdress(SendRequestTopoActivity.this), utility.getCurrentDate(), "mytopousers");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(SendRequestTopoActivity.this, data.getMsg());
                    } else {
                        if (data.getFetchContacts() != null && data.getFetchContacts().size() > 0) {
                            if (fetchContacts != null) {
                                fetchContacts.clear();
                            }
                            fetchContacts = data.getFetchContacts();
                            Logger.i("", "data contacts size" + data.getFetchContacts().size());
                            usersAdapter = new MyTopoUsersAdapter(selectedListener, data.getFetchContacts());
                            topousers_recyclerview.setAdapter(usersAdapter);
                        }
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(SendRequestTopoActivity.this, "Unable to handle request");
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

    MyTopoUsersAdapter.OnItemSelectedListener selectedListener = new MyTopoUsersAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(String name) {
            userIds.add(name);
        }

        @Override
        public void onItemRemoved(String name) {
            if (userIds.size() > 0) {
                if (userIds.contains(name)) {
                    userIds.remove(userIds.indexOf(name));
                }
            }
        }
    };


    void sendingRequestToposMethoed(final String userid) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doRequestTopo("app", StoreDetails.getTopoUserId(SendRequestTopoActivity.this), userid, message_edittext.getText().toString(), "0", "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(SendRequestTopoActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(SendRequestTopoActivity.this), utility.getDeviceIpAdress(SendRequestTopoActivity.this), utility.getCurrentDate(), "sendinginstantttopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                totalcalls++;

                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        if (totalcalls == userIds.size()) {
                            // userIds.clear();
                            message_edittext.setText("");
                            Utility.hideProgressDlg();
                        }
                        errorDialog(SendRequestTopoActivity.this, data.getMsg());

                    } else {
                        if (totalcalls == userIds.size()) {
                            message_edittext.setText("");
                            // userIds.clear();
                            Utility.hideProgressDlg();
                            errorDialog(SendRequestTopoActivity.this, data.getMsg());
                        } else {
                            sendingRequestToposMethoed(userIds.get(totalcalls));
                        }
                    }


                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(SendRequestTopoActivity.this, "Unable to handle request");
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
                        if (totalcalls == userIds.size()) {
                            finish();
                        } else {
                            try {
                                sendingRequestToposMethoed(userIds.get(totalcalls));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
    }

}


