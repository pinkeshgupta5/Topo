package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.MyTopoListAdapter;
import com.dhivi.inc.topo.adapter.SearchResultAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.Topos;
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
 * Created by User on 11/17/2017.
 */

public class SearchActivity extends Activity {

    @BindView(R.id.search_edittext)
    EditText search_edittext;
    @BindView(R.id.savedtopo_recyclerview)
    RecyclerView savedtopo_recyclerview;
    @BindView(R.id.noresults)
    TextView noresults;
    SearchResultAdapter myTopoListAdapter;
    APIInterface apiInterface;
    String TAG = SearchActivity.class.getSimpleName();
    ArrayList<Topos> searchresultTopos = new ArrayList<Topos>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        initView();
    }

    void initView() {
        Utility.showProgressDlg(SearchActivity.this, null);
        getSearchdefalutToposMethoed();

        savedtopo_recyclerview.setHasFixedSize(true);
        savedtopo_recyclerview.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("") && s.toString().length() > 0) {
                    ArrayList<Topos> searchResult = new ArrayList<>();
                    if (searchresultTopos != null && searchresultTopos.size() > 0) {
                        for (int i = 0; i < searchresultTopos.size(); i++) {
                            if (searchresultTopos.get(i).getTopo_name().toLowerCase().contains(s.toString())) {
                                searchResult.add(searchresultTopos.get(i));
                            }
                        }
                        if (searchResult != null && searchResult.size() > 0) {
                            noresults.setVisibility(View.GONE);
                            myTopoListAdapter = new SearchResultAdapter(selectedListener, searchResult);
                            savedtopo_recyclerview.setAdapter(myTopoListAdapter);
                        } else {
                            noresults.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (searchresultTopos != null && searchresultTopos.size() > 0) {
                        noresults.setVisibility(View.GONE);
                        myTopoListAdapter = new SearchResultAdapter(selectedListener, searchresultTopos);
                        savedtopo_recyclerview.setAdapter(myTopoListAdapter);
                    } else {
                        noresults.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick(R.id.search_imageview)
    void onSearchClicked() {
        Utility.showProgressDlg(SearchActivity.this, null);
        getSearchResultToposMethoed();
    }

    void getSearchdefalutToposMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSearchTopos("app", StoreDetails.getTopoUserId(SearchActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(SearchActivity.this), "", "", StoreDetails.getLoginkey(SearchActivity.this), utility.getDeviceIpAdress(SearchActivity.this), utility.getCurrentDate(), "home");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    if (searchresultTopos != null) {
                        searchresultTopos.clear();
                    }

                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(SearchActivity.this, data.getMsg());
                    } else {

                        if (data.getSearchDefalut() != null && data.getSearchDefalut().size() > 0) {
                            noresults.setVisibility(View.GONE);
                            searchresultTopos = data.getSearchDefalut();
                            myTopoListAdapter = new SearchResultAdapter(selectedListener, data.getSearchDefalut());
                            savedtopo_recyclerview.setAdapter(myTopoListAdapter);
                        } else {
                            noresults.setVisibility(View.VISIBLE);
                        }
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(SearchActivity.this, "Unable to handle request");
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

    void getSearchResultToposMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doSearchResultTopos("app", StoreDetails.getTopoUserId(SearchActivity.this), search_edittext.getText().toString(), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(SearchActivity.this), "", "", StoreDetails.getLoginkey(SearchActivity.this), utility.getDeviceIpAdress(SearchActivity.this), utility.getCurrentDate(), "home");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(SearchActivity.this, data.getMsg());
                    } else {

                        if (data.getSearchTopo() != null && data.getSearchTopo().size() > 0) {
                            noresults.setVisibility(View.GONE);
                            myTopoListAdapter = new SearchResultAdapter(selectedListener, data.getSearchTopo());
                            savedtopo_recyclerview.setAdapter(myTopoListAdapter);
                        } else {
                            noresults.setVisibility(View.VISIBLE);
                        }
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(SearchActivity.this, "Unable to handle request");
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


    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    MyTopoListAdapter.OnItemSelectedListener selectedListener = new MyTopoListAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Topos topos) {
            Intent i = new Intent(SearchActivity.this, ViewTopoActivity.class);
            i.putExtra("topoName", topos.getTopo_name());
            i.putExtra("from", "notfinish");
            startActivity(i);
        }
    };

}
