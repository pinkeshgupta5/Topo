package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.ArrivingToposAdapter;
import com.dhivi.inc.topo.adapter.InstantToposAdapter;
import com.dhivi.inc.topo.adapter.MyTopoListAdapter;
import com.dhivi.inc.topo.adapter.PagerAdapter;
import com.dhivi.inc.topo.adapter.TopoRequestAdapter;
import com.dhivi.inc.topo.fragments.InstantTopos;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.ArrivingTopos;
import com.dhivi.inc.topo.plugin.holder.TopoRequests;
import com.dhivi.inc.topo.plugin.holder.Topos;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by User on 11/23/2017.
 */

public class NewHomeActivity extends FragmentActivity {

    @BindView(R.id.instanttopo_recyclerview)
    RecyclerView instanttopo_recyclerview;
    @BindView(R.id.toporequest_recyclerview)
    RecyclerView toporequest_recyclerview;
    @BindView(R.id.arrivingtopo_recyclerview)
    RecyclerView arrivingtopo_recyclerview;
    @BindView(R.id.savedtopo_recyclerview)
    RecyclerView savedtopo_recyclerview;
    @BindView(R.id.mytopo_recyclerview)
    RecyclerView mytopo_recyclerview;
    @BindView(R.id.mytopo_textview)
    TextView mytopo_textview;
    @BindView(R.id.saved_textview)
    TextView saved_textview;
    @BindView(R.id.toporequest_textview)
    TextView toporequest_textview;
    @BindView(R.id.instatnt_textview)
    TextView instatnt_textview;
    @BindView(R.id.arriving_textview)
    TextView arriving_textview;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.pager_layout)
    RelativeLayout pager_layout;
    @BindView(R.id.profile_imageview)
    ImageView profile_imageview;
    @BindView(R.id.name_textview)
    TextView name_textview;
    @BindView(R.id.ph_textview)
    TextView ph_textview;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;

    @BindView(R.id.profile_layout)
    RelativeLayout profile_layout;
    @BindView(R.id.gesture_layout)
    LinearLayout gesture_layout;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;
    @BindView(R.id.dragView)
    LinearLayout dragView;


    String TAG = NewHomeActivity.class.getSimpleName();
    APIInterface apiInterface;
    InstantToposAdapter instantToposAdapter;
    TopoRequestAdapter topoRequestAdapter;
    ArrivingToposAdapter arrivingToposAdapter;
    MyTopoListAdapter myTopoListAdapter;
    public final static int PAGES = 3;
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;
    public PagerAdapter adapter;
    float value = 0;
    DisplayMetrics displaymetrics;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_newhome);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        activity = NewHomeActivity.this;
        initView();
        Utility.startPermissionActivity(NewHomeActivity.this, 124);
        if (StoreDetails.getContactsKey(NewHomeActivity.this) == null || StoreDetails.getContactsKey(NewHomeActivity.this).equals("")) {
            new Utility().processPhoneData(NewHomeActivity.this);
        }


        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mLayout.setPanelHeight(mapHeight);
    }

    @OnClick(R.id.namelayout)
    void callingProfile() {
        Intent i = new Intent(NewHomeActivity.this, EditProfileActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.profile_imageview)
    void callingImageProfile() {
        Intent i = new Intent(NewHomeActivity.this, EditProfileActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.search_imageview)
    void callingSearchView() {
        Intent i = new Intent(NewHomeActivity.this, SearchActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.help_layout)
    void callingHelp() {
        Intent i = new Intent(NewHomeActivity.this, HelpActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.feedback_layout)
    void callingFeedback() {
        Intent i = new Intent(NewHomeActivity.this, FeedBackActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.privacy_layout)
    void callingPrivacy() {
        Intent i = new Intent(NewHomeActivity.this, WebViewActivity.class);
        i.putExtra("weburl", getString(R.string.privacy_url));
        startActivity(i);
    }

    @OnClick(R.id.terms_layout)
    void callingTerms() {
        Intent i = new Intent(NewHomeActivity.this, WebViewActivity.class);
        i.putExtra("weburl", getString(R.string.terms_url));
        startActivity(i);
    }


    void initView() {
        Utility.showProgressDlg(NewHomeActivity.this, null);


        instanttopo_recyclerview.setHasFixedSize(true);
        instanttopo_recyclerview.setLayoutManager(new LinearLayoutManager(NewHomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        toporequest_recyclerview.setHasFixedSize(true);
        toporequest_recyclerview.setLayoutManager(new LinearLayoutManager(NewHomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        arrivingtopo_recyclerview.setHasFixedSize(true);
        arrivingtopo_recyclerview.setLayoutManager(new LinearLayoutManager(NewHomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        savedtopo_recyclerview.setHasFixedSize(true);
        savedtopo_recyclerview.setLayoutManager(new LinearLayoutManager(NewHomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mytopo_recyclerview.setHasFixedSize(true);
        mytopo_recyclerview.setLayoutManager(new LinearLayoutManager(NewHomeActivity.this, LinearLayoutManager.HORIZONTAL, false));


        PagerAdapter adapter = new PagerAdapter(this, this.getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        viewpager.setPageTransformer(false, adapter);
        viewpager.setCurrentItem(FIRST_PAGE);
        viewpager.setOffscreenPageLimit(3);
        displaymetrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        value = getResources().getDisplayMetrics().density;

        System.out.println("Balaji Value :: " + value);

        value *= 200;

        value *= -1;

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        viewpager.setPageMargin((int) value);


        if (getIntent() != null) {
            String deeplink = getIntent().getStringExtra("deeplink");
            if (deeplink != null && !deeplink.equals("")) {
                if (deeplink.contains("viewtopo")) {
                    String[] split = deeplink.split(":");
                    Intent i = new Intent(NewHomeActivity.this, ViewTopoActivity.class);
                    i.putExtra("topoName", split[1]);
                    startActivity(i);
                    finish();
                } else if (deeplink.contains("viewinstanttopo")) {
                    String[] split = deeplink.split(":");
                    Intent i = new Intent(NewHomeActivity.this, ViewInstantTopoActivity.class);
                    i.putExtra("instatntId", split[1]);
                    startActivity(i);
                    finish();
                } else if (deeplink.contains("navigation")) {
                    String[] split = deeplink.split(":");
                    Intent i = new Intent(NewHomeActivity.this, LiveTrackingActivity.class);
                    i.putExtra("topoid", split[1]);
                    i.putExtra("topotype", split[2]);
                    startActivity(i);
                    finish();
                } else if (deeplink.contains("accessrequest")) {
                    String[] split = deeplink.split(":");
                    Intent i = new Intent(NewHomeActivity.this, RequestTopoActivity.class);
                    i.putExtra("requestsid", split[1]);
                    startActivity(i);
                }
            } else {
                getRequestToposMethoed();
            }
        } else {
            getRequestToposMethoed();
        }
    }


    void getRequestToposMethoed() {

        Utility utility = new Utility();
        //Call<AppResponseData> call = apiInterface.doHome("app", StoreDetails.getTopoUserId(NewHomeActivity.this), "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(NewHomeActivity.this), "", "", "", StoreDetails.getLoginkey(NewHomeActivity.this), utility.getDeviceIpAdress(NewHomeActivity.this), utility.getCurrentDate(), "home");
        Call<AppResponseData> call = apiInterface.doHome("app", StoreDetails.getTopoUserId(NewHomeActivity.this), "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(NewHomeActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(NewHomeActivity.this), utility.getDeviceIpAdress(NewHomeActivity.this), utility.getCurrentDate(), "home");

        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());


                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(NewHomeActivity.this, data.getMsg());
                    } else {

                        int check = 0;
                        if (data.getRequests() != null && data.getRequests().size() > 0) {
                            topoRequestAdapter = new TopoRequestAdapter(listener, data.getRequests());
                            toporequest_recyclerview.setAdapter(topoRequestAdapter);
                        } else {
                            toporequest_textview.setVisibility(View.GONE);
                            toporequest_recyclerview.setVisibility(View.GONE);
                        }
                        if (data.getInstantTopos() != null && data.getInstantTopos().size() > 0) {
                            instantToposAdapter = new InstantToposAdapter(selectedListener, data.getInstantTopos());
                            instanttopo_recyclerview.setAdapter(instantToposAdapter);
                        } else {
                            instatnt_textview.setVisibility(View.GONE);
                            instanttopo_recyclerview.setVisibility(View.GONE);
                        }
                        if (data.getArrivings() != null && data.getArrivings().size() > 0) {
                            arrivingToposAdapter = new ArrivingToposAdapter(arryivinglistener, data.getArrivings());
                            arrivingtopo_recyclerview.setAdapter(arrivingToposAdapter);
                        } else {
                            arriving_textview.setVisibility(View.GONE);
                            arrivingtopo_recyclerview.setVisibility(View.GONE);
                        }
                        if (data.getTopos() != null && data.getTopos().size() > 0) {
                            check = 1;
                            myTopoListAdapter = new MyTopoListAdapter(toposListener, data.getTopos());
                            mytopo_recyclerview.setAdapter(myTopoListAdapter);
                        } else {
                            mytopo_textview.setVisibility(View.GONE);
                            mytopo_recyclerview.setVisibility(View.GONE);
                        }
                        if (data.getSavedToposList() != null && data.getSavedToposList().size() > 0) {
                            myTopoListAdapter = new MyTopoListAdapter(toposListener, data.getSavedToposList());
                            savedtopo_recyclerview.setAdapter(myTopoListAdapter);
                        } else {
                            saved_textview.setVisibility(View.GONE);
                            savedtopo_recyclerview.setVisibility(View.GONE);
                        }


                        if (check == 0) {
                            animationView.setVisibility(View.VISIBLE);
                        }

                        if (data.getProfileView() != null) {
                            //topopin_textview.setText(data.getProfileView().getTopo_b_pin());
                            name_textview.setText(data.getProfileView().getTopo_user_name());
                            //ph_textview.setText(data.getProfileView().getTopo_user_mobile());
                            ph_textview.setText("PIN " + data.getProfileView().getTopo_b_pin());
                            if (data.getProfileView().getTopo_image_url() != null && !data.getProfileView().getTopo_image_url().equals("")) {
                                Picasso.with(NewHomeActivity.this).load(getString(R.string.profile_image) + data.getProfileView().getTopo_image_url()).error(R.drawable.defalut_profile).transform(new CircleTransform()).into(profile_imageview);
                            } else {
                                Picasso.with(NewHomeActivity.this).load(R.drawable.defalut_profile).into(profile_imageview);
                            }
                        }

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(NewHomeActivity.this, "Unable to handle request");
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


    InstantToposAdapter.OnItemSelectedListener selectedListener = new InstantToposAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(InstantTopos instantTopo) {
            Intent i = new Intent(NewHomeActivity.this, ViewInstantTopoActivity.class);
            i.putExtra("instatntId", instantTopo.getTopo_instant_id());
            startActivity(i);
        }
    };
    TopoRequestAdapter.OnItemSelectedListener listener = new TopoRequestAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(TopoRequests topoRequests) {
            Intent i = new Intent(NewHomeActivity.this, RequestTopoActivity.class);
            i.putExtra("requestsid", topoRequests.getTopo_request_id());
            startActivity(i);
        }
    };
    ArrivingToposAdapter.OnItemSelectedListener arryivinglistener = new ArrivingToposAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(ArrivingTopos topo) {
            Intent i = new Intent(NewHomeActivity.this, LiveTrackingActivity.class);
            i.putExtra("topoid", topo.getTopo_id());
            i.putExtra("topotype", topo.getIs_instant_topo());
            startActivity(i);
        }
    };
    MyTopoListAdapter.OnItemSelectedListener toposListener = new MyTopoListAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(Topos topos) {
            Intent i = new Intent(NewHomeActivity.this, ViewTopoActivity.class);
            i.putExtra("topoName", topos.getTopo_name());
            i.putExtra("from", "notfinish");
            startActivity(i);
        }
    };


}
