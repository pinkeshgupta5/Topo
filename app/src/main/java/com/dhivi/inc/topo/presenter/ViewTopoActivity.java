package com.dhivi.inc.topo.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.MapServices;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.CabsAdapter;
import com.dhivi.inc.topo.adapter.MyTopoListAdapter;
import com.dhivi.inc.topo.adapter.NearByAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.CabsData;
import com.dhivi.inc.topo.plugin.holder.NearByData;
import com.dhivi.inc.topo.plugin.holder.TopoInstantCreate;
import com.dhivi.inc.topo.utils.CircleTransform;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.PermissionUtils;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/5/2017.
 */

public class ViewTopoActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.title_textview)
    TextView title_textview;
    @BindView(R.id.flat_textview)
    TextView flat_textview;
    @BindView(R.id.woringdays_textview)
    TextView woringdays_textview;
    @BindView(R.id.woringhours_textview)
    TextView woringhours_textview;
    @BindView(R.id.favtopo_imageview)
    ImageView favtopo_imageview;
    @BindView(R.id.map_imageview)
    ImageView map_imageview;
    @BindView(R.id.navigation_imageview)
    ImageView navigation_imageview;
    @BindView(R.id.delete_layout)
    RelativeLayout delete_layout;
    @BindView(R.id.edit_layout)
    RelativeLayout edit_layout;
    @BindView(R.id.favtopo_layout)
    LinearLayout favtopo_layout;
    @BindView(R.id.cardlayout)
    CardView cardlayout;
    @BindView(R.id.errror_textview)
    TextView errror_textview;
    @BindView(R.id.business_textview)
    TextView business_textview;
    @BindView(R.id.cabs_recycler)
    RecyclerView cabs_recycler;
    @BindView(R.id.nearme_recycler)
    RecyclerView nearme_recycler;
    @BindView(R.id.cabs_view)
    View cabs_view;
    @BindView(R.id.nearby_view)
    View nearby_view;
    @BindView(R.id.cabs_recycler_layout)
    RelativeLayout cabs_recycler_layout;
    @BindView(R.id.info_layout)
    RelativeLayout info_layout;
    APIInterface apiInterface;
    String TAG = ViewTopoActivity.class.getSimpleName();
    String topoName = "";
    String topoPhysicalAddress = "";
    String lat = null, lon = "";
    double currentlat = 0;
    double currentlong = 0;
    int favid = 0;
    String topoId = "";
    String topouserId = "";
    String totopoId = "";
    String topoPrivacy = "";
    String from = null;
    int latlong = 0;
    int googlelatlong = 0;
    String errortopoid = "";
    String errormsg = "";
    NearByAdapter adapter;
    String UBER_PACKAGE = "com.ubercab";
    String UBER_CLIENT_ID = "lyyHRqhMv38XfGpC6620XAcDSrJBgCDn";
    String firsturl = "https://maps.googleapis.com/maps/api/staticmap?&markers=icon:https://s3.ap-south-1.amazonaws.com/edispatch/Topo/Assets/img/companyImg/m_v5.png%7C";
    String secondurl = "&key=AIzaSyAvIsgCbwpTfO4ad3F6taz3kQydYeGv5mY&zoom=15&size=360x400&format=png&maptype=roadmap&style=feature:administrative%7Celement:geometry.fill%7Ccolor:0xd6e2e6&style=feature:administrative%7Celement:geometry.stroke%7Ccolor:0xcfd4d5&style=feature:administrative%7Celement:labels.text.fill%7Ccolor:0x7492a8&style=feature:administrative.neighborhood%7Celement:labels.text.fill%7Clightness:25&style=feature:landscape%7Celement:geometry.fill%7Cvisibility:on&style=feature:landscape.natural%7Celement:labels.text.fill%7Ccolor:0x7492a8&style=feature:poi%7Celement:labels.icon%7Csaturation:-100&style=feature:poi%7Celement:labels.text.fill%7Ccolor:0x588ca4&style=feature:poi.park%7Celement:geometry.fill%7Ccolor:0xa9de83&style=feature:poi.park%7Celement:geometry.stroke%7Ccolor:0xbae6a1&style=feature:poi.sports_complex%7Celement:geometry.fill%7Ccolor:0xc6e8b3&style=feature:poi.sports_complex%7Celement:geometry.stroke%7Ccolor:0xbae6a1&style=feature:road%7Celement:labels.icon%7Csaturation:-45%7Clightness:10%7Cvisibility:on&style=feature:road%7Celement:labels.text.fill%7Ccolor:0x41626b";
    String thirdurl = "&style=feature:road.arterial%7Celement:geometry.fill%7Ccolor:0xffffff&style=feature:road.highway%7Celement:geometry.fill%7Ccolor:0xc1d1d6&style=feature:road.highway%7Celement:geometry.stroke%7Ccolor:0xa6b5bb&style=feature:road.highway%7Celement:labels.icon%7Cvisibility:on&style=feature:road.local%7Celement:geometry.fill%7Ccolor:0xffffff&style=feature:transit%7Celement:labels.icon%7Csaturation:-70&style=feature:transit.line%7Celement:geometry.fill%7Ccolor:0xb4cbd4&style=feature:transit.line%7Celement:labels.text.fill%7Ccolor:0x588ca4&style=feature:transit.station%7Celement:labels.text.fill%7Ccolor:0x008cb5%7Cvisibility:on&style=feature:transit.station.airport%7Celement:geometry.fill%7Csaturation:-100%7Clightness:-5&style=feature:water%7Celement:geometry.fill%7Ccolor:0xa6cbe3";
    String uberurl = "https://m.uber.com/ul/?action=setPickup&client_id=QDtYUikKvCQ0UmRnEe08pKZ1xl4gZCYB&pickup=my_location&dropoff[formatted_address]=%1$s&dropoff[latitude]=%2$s&dropoff[longitude]=%3$s";
    String olaurl = "https://olawebcdn.com/assets/ola-universal-link.html?&category=compact&landing_page=bk&bk_act=rn&drop_lat=%1$s&drop_lng=%2$s";


    protected static final int REQUEST_LOCATION = 0x1;
    GoogleApiClient googleApiClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewtopo);
        ButterKnife.bind(this);
        favid = 0;
        cabs_recycler.setHasFixedSize(true);
        cabs_recycler.setLayoutManager(new LinearLayoutManager(ViewTopoActivity.this, LinearLayoutManager.HORIZONTAL, false));
        nearme_recycler.setHasFixedSize(true);
        nearme_recycler.setLayoutManager(new LinearLayoutManager(ViewTopoActivity.this));
        apiInterface = APIClient.getClient().create(APIInterface.class);
        boolean result = PermissionUtils.checkPermission(ViewTopoActivity.this);
        //  initMapView();
        if (getIntent() != null) {
            topoName = getIntent().getStringExtra("topoName");
            from = getIntent().getStringExtra("from");
            if (topoName != null) {
                Utility.showProgressDlg(ViewTopoActivity.this, null);
                getViewTopoMethoed();
            }
        }

        if (from == null) {
            from = "notfinish";
        }

        providingLocationReuired();
        latlong = 0;
        googlelatlong = 0;
        onCabClick();
    }

    @OnClick(R.id.cabs_layout)
    void onCabClick() {
        cabs_view.setVisibility(View.VISIBLE);
        cabs_recycler_layout.setVisibility(View.VISIBLE);
        nearby_view.setVisibility(View.GONE);
        nearme_recycler.setVisibility(View.GONE);
    }

    @OnClick(R.id.nearby_layout)
    void onNearbyClick() {
        cabs_view.setVisibility(View.GONE);
        cabs_recycler_layout.setVisibility(View.GONE);
        nearby_view.setVisibility(View.VISIBLE);
        nearme_recycler.setVisibility(View.VISIBLE);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.back_layout)
    void onBack() {
        Intent i = new Intent(ViewTopoActivity.this, NewHomeActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.favtopo_layout)
    void callingFavMetheods() {
        if (favid == 0) {
            Utility.showProgressDlg(ViewTopoActivity.this, null);
            setFavTopoMethoed();
        } else {
            Utility.showProgressDlg(ViewTopoActivity.this, null);
            setUnFavTopoMethoed();
        }

    }

    @OnClick(R.id.delete_layout)
    void callingDeleteMetheode() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(ViewTopoActivity.this, R.style.AboutDialog));
        alertDialog.setTitle("");

        alertDialog.setMessage("Are you sure want to delete?");
        alertDialog.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utility.showProgressDlg(ViewTopoActivity.this, null);
                        setdeleteTopoMethoed();
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();


    }

    @OnClick(R.id.navigation_layout)
    void callingNavigateMethoed() {
        if (Utility.navigation == 0) {
            if (topouserId.equals(StoreDetails.getTopoUserId(ViewTopoActivity.this))) {

                googlelatlong = 1;
                noLocation();

            } else {
                if (lat != null) {

                    latlong = 1;
                    noLocation();

                }
            }
        } else {
            if (Utility.topoId.equals(topoId)) {
                setStopNavigateTopoMethoed();
            } else {
                if (topouserId.equals(StoreDetails.getTopoUserId(ViewTopoActivity.this))) {

                    googlelatlong = 1;
                    noLocation();

                } else {
                    if (lat != null) {

                        latlong = 1;
                        noLocation();

                    }
                }
            }

        }


    }

    @OnClick(R.id.edit_layout)
    void callingEditMethoed() {
        Intent i = new Intent(ViewTopoActivity.this, TopoEditActivity.class);
        i.putExtra("topoName", topoName);
        startActivity(i);
    }

    @OnClick(R.id.forward_layout)
    void callingMovingMethoed() {
        if (topoId != null && !topoId.equals("")) {
            Intent i = new Intent(ViewTopoActivity.this, ShowingDirectTopoContacts.class);
            i.putExtra("TopoId", topoId);
            i.putExtra("topouserId", topouserId);
            i.putExtra("topoPrivacy", topoPrivacy);
            startActivity(i);
        }
    }

    @OnClick(R.id.share_layout)
    void callingShareMethoed() {

        if (topoId != null && !topoId.equals("")) {
            doStatTopoMethoed("topo","share");
            String shareurl = "https://www.topoapp.in/viewTopo.php?type=topo&id=" + topoId;
            shareTextUrl(shareurl);
        }
    }

    @OnClick(R.id.info_layout)
    void callingInfoLayout() {
        if (topoId != null && !topoId.equals("")) {
            Intent i = new Intent(ViewTopoActivity.this, AccessListActivity.class);
            i.putExtra("topoName", topoName);
            startActivity(i);
        }
    }

    @OnClick(R.id.sendrequest_button)
    void callingsendRequest() {
        sendingRequestToposMethoed();
    }


    void sendingRequestToposMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doRequestTopo("app", StoreDetails.getTopoUserId(ViewTopoActivity.this), "0", errormsg, errortopoid, "ime1", "ime2", utility.getDeviceName(), "andrpod", utility.getDeviceModel(), "browsername", "browserversion", "yes", utility.getVersionNumber(ViewTopoActivity.this), "tracklat", "tracllon", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "sendinginstantttopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "data response" + response.body().toString());

                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                    } else {
                        Utility.errorDialog(ViewTopoActivity.this, data.getMsg(), ViewTopoActivity.this);
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewTopoActivity.this, "Unable to handle request");
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

    void getViewTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoView("app", StoreDetails.getTopoUserId(ViewTopoActivity.this), topoName, "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "viewtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        if (data.getMsg().equals("No Access") || data.getMsg().equals("Access expired")) {
                            errortopoid = data.getTopo_id();
                            errormsg = "looking for acees to " + data.getTopo_namee();
                            errror_textview.setText(data.getMsg());
                            cardlayout.setVisibility(View.VISIBLE);
                        } else {
                            Utility.errorDialog(ViewTopoActivity.this, data.getMsg(), ViewTopoActivity.this);
                        }
                    } else {

                        if (data.getNearby() != null && data.getNearby().size() > 0) {
                            adapter = new NearByAdapter(itemSelectedListener, data.getNearby());
                            nearme_recycler.setAdapter(adapter);
                        }

                        if (data.getCabs() != null && data.getCabs().size() > 0) {
                            CabsAdapter adapter = new CabsAdapter(selectedListener, data.getCabs());
                            cabs_recycler.setAdapter(adapter);
                        }
                        if (data.getTopoView() != null && data.getTopoView().size() > 0) {
                            lat = data.getTopoView().get(0).getTopo_lat();
                            lon = data.getTopoView().get(0).getTopo_lon();
                            topoId = data.getTopoView().get(0).getTopo_id();
                            totopoId = data.getTopoView().get(0).getTopo_user_id();
                            topoPrivacy = data.getTopoView().get(0).getTopo_privacy();
                            title_textview.setText(data.getTopoView().get(0).getTopo_name());
                            topouserId = data.getTopoView().get(0).getTopo_user_id();
                            topoPhysicalAddress = data.getTopoView().get(0).getTopo_address();
                            //   String src = firsturl + lat + "," + lon + secondurl + thirdurl;
                            String src = String.format(getString(R.string.mapurl_full), data.getTopoView().get(0).getTopo_user_id(), data.getTopoView().get(0).getTopo_img_url());
                            Logger.i("", "my topos list map url::" + src);
                            Picasso.with(ViewTopoActivity.this).load(src).into(map_imageview);

                            if (data.getTopoView().get(0).getSaved().equals("Yes")) {
                                favid = 1;
                                favtopo_imageview.setImageResource(R.drawable.favtopos);
                            }
                            setDetails(topoId);
                            if (!topouserId.equals(StoreDetails.getTopoUserId(ViewTopoActivity.this))) {
                                info_layout.setVisibility(View.GONE);
                                edit_layout.setVisibility(View.GONE);
                                delete_layout.setVisibility(View.GONE);
                                favtopo_layout.setVisibility(View.VISIBLE);
                            } else {
                                favtopo_layout.setVisibility(View.GONE);
                            }

                            if (data.getTopoView().get(0).getTopo_privacy().equals("Public")) {
                                info_layout.setVisibility(View.GONE);
                            }

                            if (data.getTopoView().get(0).getTopo_flat_no() != null && !data.getTopoView().get(0).getTopo_flat_no().equals("")) {
                                flat_textview.setText("Flat/Door No: " + data.getTopoView().get(0).getTopo_flat_no());
                                flat_textview.setVisibility(View.VISIBLE);
                            } else {
                                flat_textview.setVisibility(View.GONE);
                            }
                            if (data.getTopoView().get(0).getTopo_business_name() != null && !data.getTopoView().get(0).getTopo_business_name().equals("")) {
                                business_textview.setText(data.getTopoView().get(0).getTopo_business_name());
                                business_textview.setVisibility(View.VISIBLE);
                            } else {
                                business_textview.setVisibility(View.GONE);
                            }
                            if (data.getTopoView().get(0).getTopo_working_days() != null && !data.getTopoView().get(0).getTopo_working_days().equals("")) {
                                woringdays_textview.setText(data.getTopoView().get(0).getTopo_working_days());
                                woringdays_textview.setVisibility(View.VISIBLE);
                            } else {
                                woringdays_textview.setVisibility(View.GONE);
                            }
                            if (data.getTopoView().get(0).getTopo_working_hours_from() != null && !data.getTopoView().get(0).getTopo_working_hours_from().equals("")) {
                                woringhours_textview.setText(data.getTopoView().get(0).getTopo_working_hours_from() + " To " + data.getTopoView().get(0).getTopo_working_hours_to());
                                woringhours_textview.setVisibility(View.VISIBLE);
                            } else {
                                woringhours_textview.setVisibility(View.GONE);
                            }

                            if (data.getTopoView().get(0).getIsNavigating().equals("no")) {
                                navigation_imageview.setImageResource(R.drawable.navigate);
                                Utility.navigation = 0;
                                Utility.navigationId = "";
                                Utility.topoId = "";
                                Utility.topoType = "";
                                stopService(new Intent(ViewTopoActivity.this, MapServices.class));
                            }

                        }
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewTopoActivity.this, "Unable to handle request");
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

    void setFavTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doAddToTopos("app", topoName, StoreDetails.getTopoUserId(ViewTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                    } else {
                        favid = 1;
                        favtopo_imageview.setImageResource(R.drawable.favtopos);
                        Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewTopoActivity.this, "Unable to handle request");
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

    NearByAdapter.OnItemSelectedListener itemSelectedListener = new NearByAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(NearByData data) {
            doStatTopoMethoed("topo","nearme");
            LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            LatLng source = new LatLng(Double.parseDouble(data.getLat()), Double.parseDouble(data.getLon()));
            openGoogleDirections(source, destination);
        }
    };
    CabsAdapter.OnItemSelectedListener selectedListener = new CabsAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(CabsData cabsData) {
            if (cabsData.getTopo_cab_provider().toLowerCase().equals("uber")) {
                doStatTopoMethoed("topo","uber");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(uberurl, topoPhysicalAddress, lat, lon)));
                startActivity(browserIntent);
            } else if (cabsData.getTopo_cab_provider().toLowerCase().equals("ola")) {
                doStatTopoMethoed("topo","ola");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(olaurl, lat, lon)));
                startActivity(browserIntent);
            }
        }
    };


    void setNavigateTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doStratNavigation("app", topoName, StoreDetails.getTopoUserId(ViewTopoActivity.this), totopoId, topoId, "" + currentlat, "" + currentlong, "no", "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                    } else {
                        if (data.getStartNavigation().getNavigationId() != null && !data.getStartNavigation().getNavigationId().equals("")) {
                            startService(new Intent(ViewTopoActivity.this, MapServices.class));
                            navigation_imageview.setImageResource(R.drawable.navigate_cancle);
                            Utility.navigation = 1;
                            Utility.navigationId = data.getStartNavigation().getNavigationId();
                            Utility.topoId = topoId;
                            Utility.topoType = "no";
                            LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                            LatLng source = new LatLng(currentlat, currentlong);
                            openGoogleDirections(source, destination);
                            adddetails();
                        }

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewTopoActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    void setStopNavigateTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.dostopnavigation("app", StoreDetails.getTopoUserId(getApplicationContext()), Utility.navigationId, utility.topoId, lat, lon, "no", "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(getApplicationContext()), "", "", StoreDetails.getLoginkey(getApplicationContext()), utility.getDeviceIpAdress(getApplicationContext()), utility.getCurrentDate(), "updatenavigation");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                AppResponseData data = response.body();

                if (data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                } else {
                    navigation_imageview.setImageResource(R.drawable.navigate);
                    Utility.navigation = 0;
                    Utility.navigationId = "";
                    Utility.topoId = "";
                    Utility.topoType = "";
                    stopService(new Intent(ViewTopoActivity.this, MapServices.class));

                }

                Logger.i(TAG, "forgot Response::" + data.getMsg());
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    void setUnFavTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doDeleteFromTopos("app", topoName, StoreDetails.getTopoUserId(ViewTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                } else {
                    favid = 0;
                    favtopo_imageview.setImageResource(R.drawable.topos);
                    Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                }

                Logger.i(TAG, "forgot Response::" + data.getMsg());
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }


    void setdeleteTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoDelete("app", topoName, StoreDetails.getTopoUserId(ViewTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(ViewTopoActivity.this, data.getMsg());
                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            new ContextThemeWrapper(ViewTopoActivity.this, R.style.AboutDialog));
                    alertDialog.setTitle("");

                    alertDialog.setMessage(data.getMsg());
                    alertDialog.setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent i = new Intent(ViewTopoActivity.this, NewHomeActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                }
                            });
                    AlertDialog alertDialog1 = alertDialog.create();
                    alertDialog1.show();
                }

                Logger.i(TAG, "forgot Response::" + data.getMsg());
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
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Going to dilip home easyly...");
        share.putExtra(Intent.EXTRA_TEXT, url);

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    @Override
    public void onBackPressed() {

        /*if (from.equals("finish")) {
            Intent i = new Intent(ViewTopoActivity.this, HomeActivity.class);
            startActivity(i);
            finishAffinity();
        } else {
            super.onBackPressed();
        }*/
        Intent i = new Intent(ViewTopoActivity.this, NewHomeActivity.class);
        startActivity(i);
        finish();
    }

    private void openGoogleDirections(LatLng origin, LatLng dest) {
        String str_origin = origin.latitude + "," + origin.longitude;
        String str_dest = dest.latitude + "," + dest.longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + str_origin + "&daddr=" + str_dest));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Logger.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Logger.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            currentlat = location.getLatitude();
            currentlong = location.getLongitude();

            if (googlelatlong == 1) {
                googlelatlong = 0;
                LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                LatLng source = new LatLng(currentlat, currentlong);
                openGoogleDirections(source, destination);
            } else {
                if (latlong == 1) {
                    latlong = 0;
                    setNavigateTopoMethoed();
                }
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            Logger.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private LocationManager mLocationManager = null;

    private void initializeLocationManager() {
        Logger.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    void providingLocationReuired() {
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Logger.i(TAG, "fail to request location update, ignore" + ex);
        } catch (IllegalArgumentException ex) {
            Logger.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Logger.i(TAG, "fail to request location update, ignore" + ex);
        } catch (IllegalArgumentException ex) {
            Logger.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    // check whether gps is enabled
    public boolean noLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  buildAlertMessageNoGps();

            enableLoc();
            return true;
        } else {
            if (googlelatlong == 1) {
                googlelatlong = 0;
                LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                LatLng source = new LatLng(currentlat, currentlong);
                openGoogleDirections(source, destination);
            } else {
                latlong = 0;
                setNavigateTopoMethoed();
            }

        }
        return false;

    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(ViewTopoActivity.this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            //  Timber.v("Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        ViewTopoActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void adddetails() {
        SharedPreferences pref = getSharedPreferences("dhiviexpress", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("navigation", Utility.navigation);
        editor.putString("navigationId", Utility.navigationId);
        editor.putString("topoId", Utility.topoId);
        editor.putString("topoType", Utility.topoType);
        editor.commit();
    }

    void setDetails(String topoId) {
        SharedPreferences pref = getSharedPreferences("dhiviexpress", 0);
        int navigation = pref.getInt("navigation", 0);
        if (navigation == 1) {
            Utility.navigation = navigation;
            Utility.navigationId = pref.getString("navigationId", "");
            Utility.topoId = pref.getString("topoId", "");
            Utility.topoType = pref.getString("topoType", "");
            if (Utility.topoType.equals("no")) {
                if (topoId.equals(Utility.topoId)) {
                    navigation_imageview.setImageResource(R.drawable.navigate_cancle);
                }
            }
        } else {
            navigation_imageview.setImageResource(R.drawable.navigate);
        }
    }

    /*void callingUber() {
        if (new Utility().isPackageInstalled(UBER_PACKAGE, ViewTopoActivity.this)) {

            //openLink("uber://?client_id=" + UBER_CLIENT_ID + "&action=setPickup&dropoff[latitude]=" + latitude + "&dropoff[longitude]=" + longitude + "");

        } else {
            Intent intent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ubercab"));
            startActivity(intent);
        }
    }*/

    public void openLink(String link) {
        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
        playStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        playStoreIntent.setData(Uri.parse(link));
        startActivity(playStoreIntent);
    }

    /*public void googleMapRide() {
        //please write here to open the map app....
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }*/



    void doStatTopoMethoed(String item,String action) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoStats("app",item,action, topoName, StoreDetails.getTopoUserId(ViewTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewTopoActivity.this), utility.getDeviceIpAdress(ViewTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

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