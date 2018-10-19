package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
 * Created by User on 11/13/2017.
 */

public class ShowingDirectTopoContacts extends Activity {

    @BindView(R.id.topousers_recyclerview)
    RecyclerView topousers_recyclerview;
    MyTopoUsersAdapter usersAdapter;
    String TAG = ShowingDirectTopoContacts.class.getSimpleName();
    APIInterface apiInterface;
    String TopoId;
    String topouserId;
    String topoPrivacy = null;
    ArrayList<String> userIds = new ArrayList<>();
    int totalcalls = 0;
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
    @BindView(R.id.segmented_time_intervels)
    LinearLayout segmented_time_intervels;
    @BindView(R.id.Estimated_time)
    TextView Estimated_time;
    @BindView(R.id.estimate_layout)
    RelativeLayout estimate_layout;
    @BindView(R.id.search_edittext)
    EditText search_edittext;
    @BindView(R.id.noresults)
    TextView noresults;
    String timeperoid = null;
    String[] timenames = {"3 HR", "12 HR", "24 HR", "Always"};
    String[] times = {"3", "12", "24", "Always"};
    ArrayList<FetchContacts> fetchContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_directcontacts);
        ButterKnife.bind(this);

        topousers_recyclerview.setHasFixedSize(true);
        topousers_recyclerview.setLayoutManager(new LinearLayoutManager(ShowingDirectTopoContacts.this));

        apiInterface = APIClient.getClient().create(APIInterface.class);
        Utility.showProgressDlg(ShowingDirectTopoContacts.this, null);
        getRequestToposMethoed();

        if (getIntent() != null) {
            TopoId = getIntent().getStringExtra("TopoId");
            topouserId = getIntent().getStringExtra("topouserId");
            topoPrivacy = getIntent().getStringExtra("topoPrivacy");

            if (topoPrivacy != null) {
                if (topoPrivacy.equals("Public")) {
                    timeperoid = "Always";
                    estimate_layout.setVisibility(View.GONE);
                    segmented_time_intervels.setVisibility(View.GONE);
                    materialSeekBar.setVisibility(View.GONE);
                }
            }
        }


        materialSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int number = progress;
                switch (number) {
                    case 0:
                        timeperoid = "3";
                        first_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.black));
                        second_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        third_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        fourth_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        break;
                    case 25:
                        timeperoid = "12";
                        first_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        second_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.black));
                        third_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        fourth_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        break;
                    case 50:
                        timeperoid = "24";
                        first_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        second_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        third_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.black));
                        fourth_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        break;
                    case 100:
                        timeperoid = "always";
                        first_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        second_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        third_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.list_textcolor));
                        fourth_segment.setTextColor(ContextCompat.getColor(ShowingDirectTopoContacts.this, R.color.black));
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

    @OnClick(R.id.estimate_layout)
    void estimateLayout() {
        final Dialog dialog = new Dialog(ShowingDirectTopoContacts.this);
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

    @OnClick(R.id.sendinstatnttopo_button)
    void sendingInstantTopos() {

        if (userIds.size() > 0) {
            if (timeperoid == null) {
                Utility.errorDialog(ShowingDirectTopoContacts.this, "Please Sleect Accessing Period");
            } else {
                totalcalls = 0;
                Utility.showProgressDlg(ShowingDirectTopoContacts.this, null);
                sendingInstatntToposMethoed(userIds.get(totalcalls));
            }
        }
    }


    void getRequestToposMethoed() {

        Utility utility = new Utility();

        Call<AppResponseData> call = apiInterface.dofetchContacts("app", StoreDetails.getTopoUserId(ShowingDirectTopoContacts.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(ShowingDirectTopoContacts.this), "tracklat", "tracllon", StoreDetails.getLoginkey(ShowingDirectTopoContacts.this), utility.getDeviceIpAdress(ShowingDirectTopoContacts.this), utility.getCurrentDate(), "mytopousers");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ShowingDirectTopoContacts.this, data.getMsg());
                    } else {
                        if (data.getFetchContacts() != null && data.getFetchContacts().size() > 0) {
                            if (fetchContacts != null) {
                                fetchContacts.clear();
                            }
                            fetchContacts = data.getFetchContacts();
                            Logger.i("", "data contacts size" + data.getFetchContacts().size());
                            usersAdapter = new MyTopoUsersAdapter(selectedListener, data.getFetchContacts());
                            topousers_recyclerview.setAdapter(usersAdapter);


                            if (!topouserId.equals(StoreDetails.getTopoUserId(ShowingDirectTopoContacts.this))) {
                                estimate_layout.setVisibility(View.GONE);
                                segmented_time_intervels.setVisibility(View.GONE);
                                materialSeekBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ShowingDirectTopoContacts.this, "Unable to handle request");
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


    void sendingInstatntToposMethoed(final String userid) {

        Utility utility = new Utility();
      /*  if (topouserId.equals(StoreDetails.getTopoUserId(ShowingDirectTopoContacts.this))) {
            timeperoid = "always";
        }*/
        Call<AppResponseData> call = apiInterface.doSendTopo("app", TopoId, userid, StoreDetails.getTopoUserId(ShowingDirectTopoContacts.this), timeperoid, "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(ShowingDirectTopoContacts.this), "tracklat", "tracllon", StoreDetails.getLoginkey(ShowingDirectTopoContacts.this), utility.getDeviceIpAdress(ShowingDirectTopoContacts.this), utility.getCurrentDate(), "sendinginstantttopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                totalcalls++;

                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        if (totalcalls == userIds.size()) {
                            //  userIds.clear();
                            Utility.hideProgressDlg();
                        }
                        errorDialog(ShowingDirectTopoContacts.this, data.getMsg());

                    } else {
                        if (totalcalls == userIds.size()) {
                            // userIds.clear();
                            Utility.hideProgressDlg();
                            errorDialog(ShowingDirectTopoContacts.this, data.getMsg());
                        } else {
                            sendingInstatntToposMethoed(userIds.get(totalcalls));
                        }
                    }


                    Logger.i(TAG, "forgot Response::" + data.getMsg());
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
                                sendingInstatntToposMethoed(userIds.get(totalcalls));
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

