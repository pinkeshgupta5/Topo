package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.MultiSelectedAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.CreateTopo;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/1/2017.
 */

public class CreateTopoActivity extends Activity {


    @BindView(R.id.topoid_edittext)
    EditText topoid_edittext;
    @BindView(R.id.flat_edittext)
    EditText flat_edittext;
    @BindView(R.id.businessname_edittext)
    EditText businessname_edittext;
    @BindView(R.id.businessnumber_edittext)
    EditText businessnumber_edittext;
    @BindView(R.id.public_radiobutton)
    RadioButton public_radiobutton;
    @BindView(R.id.private_radiobutton)
    RadioButton private_radiobutton;
    @BindView(R.id.privacy_switch)
    Switch privacy_switch;
    @BindView(R.id.business_switch)
    Switch business_switch;
    @BindView(R.id.business_layout)
    LinearLayout business_layout;
    @BindView(R.id.businesstype_textview)
    TextView businesstype_textview;
    @BindView(R.id.businessdays_textview)
    TextView businessdays_textview;
    @BindView(R.id.businesshours_textview)
    TextView businesshours_textview;
    int apicall = 0;
    APIInterface apiInterface;
    String TAG = CreateTopoActivity.class.getSimpleName();
    String lat = "", lon = "", address = "", city = "", country = "", postalCode = "";
    String radioValue = "Public";
    String[] businessTypes = {"ATM", "Bank", "Bar", "Beauty salon", "Business center", "Cafe", "Car service", "Cinema", "Clothing store", "Dental clinic", "Educational institutue", "Electronic store", "Gym", "Hospital", "Hotel", "Museum", "Night club", "Office", "Petrol station", "Pharmacy", "Physician", "Point of interest", "Restaurant", "Shopping mall", "Supermarket", "Other"};
    String[] workingsdays = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    String[] workinghoursfrom = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    String[] workinghoursto = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    String businesstype = null;
    String workingdays = null;
    String WorkingtimeTo = null;
    String WorkingFrom = null;
    ArrayList<String> dayslist = new ArrayList<>();
    String isbusiness = "No";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_createtopo);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        if (getIntent() != null) {
            lat = getIntent().getStringExtra("lat");
            lon = getIntent().getStringExtra("lon");
            address = getIntent().getStringExtra("address");
            city = getIntent().getStringExtra("city");
            country = getIntent().getStringExtra("country");
            postalCode = getIntent().getStringExtra("postalCode");
        }

        topoid_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 0 || i == EditorInfo.IME_ACTION_NEXT) {
                    avialbilityTopo();
                    flat_edittext.requestFocus();
                    return true;
                }
                return false;
            }
        });
        flat_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 0 || i == EditorInfo.IME_ACTION_DONE) {
                    createTopo();
                    return true;
                }
                return false;
            }
        });

        public_radiobutton.setChecked(false);
        private_radiobutton.setChecked(true);

        privacy_switch.setChecked(true);
        privacy_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioValue = "Public";
                    public_radiobutton.setChecked(true);
                    private_radiobutton.setChecked(false);
                } else {
                    radioValue = "Private";
                    public_radiobutton.setChecked(false);
                    private_radiobutton.setChecked(true);
                }
            }
        });


        business_layout.setVisibility(View.GONE);
        business_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isbusiness = "Yes";
                    business_layout.setVisibility(View.VISIBLE);
                } else {
                    isbusiness = "No";
                    business_layout.setVisibility(View.GONE);
                }
            }
        });

    }

    //** Bound actions
    @OnClick(R.id.createtopo_button)
    void callingCreateTopo() {

        createTopo();
    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.businesstype_layout)
    void showinfBusinessTypesList() {
        final Dialog dialog = new Dialog(CreateTopoActivity.this);
        dialog.setContentView(R.layout.business_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.header_titie);
        text.setText(getResources().getString(R.string.businesstype));
        ListView busness_listview = (ListView) dialog.findViewById(R.id.busness_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.business_list_item, businessTypes);
        busness_listview.setAdapter(adapter);
        busness_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                businesstype = businessTypes[position];
                businesstype_textview.setText(businessTypes[position]);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.businessdays_layout)
    void showinfBusinessDaysList() {
        // if(dayslist!=null){dayslist.clear();}
        final Dialog dialog = new Dialog(CreateTopoActivity.this);
        dialog.setContentView(R.layout.multiselection_layout);
        TextView text = (TextView) dialog.findViewById(R.id.header_titie);
        text.setText(getResources().getString(R.string.workingdays));
        Button ok_button = (Button) dialog.findViewById(R.id.ok_button);
        RecyclerView multiselection_recycler = (RecyclerView) dialog.findViewById(R.id.multiselection_recycler);
        multiselection_recycler.setHasFixedSize(true);
        multiselection_recycler.setLayoutManager(new LinearLayoutManager(CreateTopoActivity.this));
        MultiSelectedAdapter adapter = new MultiSelectedAdapter(listener, workingsdays, dayslist);
        multiselection_recycler.setAdapter(adapter);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayslist != null && dayslist.size() > 0) {
                    StringBuilder commaSepValueBuilder = new StringBuilder();
                    for (int i = 0; i < dayslist.size(); i++) {
                        commaSepValueBuilder.append(dayslist.get(i));
                        if (i != dayslist.size() - 1) {
                            commaSepValueBuilder.append(", ");
                        }
                    }

                    workingdays = commaSepValueBuilder.toString();
                    businessdays_textview.setText(workingdays);
                    dialog.dismiss();
                } else {
                    Utility.errorDialog(CreateTopoActivity.this, "Select any Day");
                }

            }
        });
        dialog.show();

    }

    @OnClick(R.id.businesshours_layout)
    void showinfBusinessHoursList() {
        final Dialog dialog = new Dialog(CreateTopoActivity.this);
        dialog.setContentView(R.layout.business_dialog);
        final TextView text = (TextView) dialog.findViewById(R.id.header_titie);
        text.setText("From");
        text.setTextColor(getResources().getColor(R.color.red));
        final ListView busness_listview = (ListView) dialog.findViewById(R.id.busness_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.business_list_item, workinghoursfrom);
        busness_listview.setAdapter(adapter);
        final ListView toListview = (ListView) dialog.findViewById(R.id.workinghours_to_listview);
        ArrayAdapter<String> toadapter = new ArrayAdapter<String>(this, R.layout.business_list_item, workinghoursto);
        toListview.setAdapter(toadapter);
        busness_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                busness_listview.setVisibility(View.GONE);
                toListview.setVisibility(View.VISIBLE);
                WorkingFrom = workinghoursfrom[position];
                text.setTextColor(getResources().getColor(R.color.green));
                text.setText("To");
            }
        });
        toListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toListview.setVisibility(View.GONE);
                WorkingtimeTo = workinghoursto[position];
                businesshours_textview.setText("From " + WorkingFrom + " To " + WorkingtimeTo);
                dialog.hide();
            }
        });

        dialog.show();

    }


    void createTopo() {
        if (isbusiness.equals("Yes")) {
            if (Utility.isEmpty(topoid_edittext)) {
                Utility.errorDialog(CreateTopoActivity.this, "Choose Your Topo Id");
            } else if (Utility.isEmpty(businessnumber_edittext)) {
                Utility.errorDialog(CreateTopoActivity.this, "Enter Your Business Name");
            } else {
                if (apicall == 0) {
                    apicall = 1;
                    Utility.showProgressDlg(CreateTopoActivity.this, null);
                    createTopoMethoed();
                }

            }
        } else {
            if (Utility.isEmpty(topoid_edittext)) {
                Utility.errorDialog(CreateTopoActivity.this, "Choose Your Topo Id");
            } else {
                if (apicall == 0) {
                    apicall = 1;
                    Utility.showProgressDlg(CreateTopoActivity.this, null);
                    createTopoMethoed();
                }

            }
        }
    }

    void createTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSaveTopo(businessname_edittext.getText().toString(), isbusiness, businesstype, workingdays, WorkingFrom, WorkingtimeTo, businessnumber_edittext.getText().toString(), "app", flat_edittext.getText().toString(), postalCode, city, country, address, lon, lat, topoid_edittext.getText().toString(), radioValue, StoreDetails.getTopoUserId(CreateTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(CreateTopoActivity.this), "", "", StoreDetails.getLoginkey(CreateTopoActivity.this), utility.getDeviceIpAdress(CreateTopoActivity.this), utility.getCurrentDate(), "createtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                Utility.hideProgressDlg();
                apicall = 0;
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(CreateTopoActivity.this, data.getMsg());
                    } else {
                        if (data.getTopoCreate() != null) {
                            if (StoreDetails.getNumberOfTopos(CreateTopoActivity.this) != 0) {

                                Intent i = new Intent(CreateTopoActivity.this, ViewTopoActivity.class);
                                i.putExtra("topoName", data.getTopoCreate().getTopo_name());
                                i.putExtra("from", "finish");
                                startActivity(i);
                            } else {
                                StoreDetails.setNumberOfTopos(CreateTopoActivity.this, 1);
                                Intent i = new Intent(CreateTopoActivity.this, SucessTopoActivity.class);
                                i.putExtra("topoName", data.getTopoCreate().getTopo_name());
                                i.putExtra("from", "finish");
                                startActivity(i);
                            }
                        }
                    }
                } else {

                    Utility.errorDialog(CreateTopoActivity.this, "unable to creatre topo");
                }


            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                apicall = 0;
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    void avialbilityTopo() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoAvailability("app", topoid_edittext.getText().toString(), StoreDetails.getTopoUserId(CreateTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(CreateTopoActivity.this), "", "", StoreDetails.getLoginkey(CreateTopoActivity.this), utility.getDeviceIpAdress(CreateTopoActivity.this), utility.getCurrentDate(), "createtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                apicall = 0;
                AppResponseData data = response.body();
                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(CreateTopoActivity.this, data.getMsg());
                    } else {

                    }
                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(CreateTopoActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                apicall = 0;
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    MultiSelectedAdapter.OnItemSelectedListener listener = new MultiSelectedAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(String name) {
            dayslist.add(name);
        }

        @Override
        public void onItemRemoved(String name) {
            if (dayslist.size() > 0) {
                if (dayslist.contains(name)) {
                    dayslist.remove(dayslist.indexOf(name));
                }
            }
        }
    };
}
