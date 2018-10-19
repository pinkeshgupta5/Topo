package com.dhivi.inc.topo.presenter;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dhivi.inc.topo.MapServices;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.CabsAdapter;
import com.dhivi.inc.topo.adapter.NearByAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.CabsData;
import com.dhivi.inc.topo.plugin.holder.NearByData;
import com.dhivi.inc.topo.plugin.holder.TopoInstantCreate;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 11/5/2017.
 */

public class ViewInstantTopoActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.title_textview)
    TextView title_textview;
    @BindView(R.id.favtopo_imageview)
    ImageView favtopo_imageview;
    @BindView(R.id.map_imageview)
    ImageView map_imageview;
    @BindView(R.id.navigation_imageview)
    ImageView navigation_imageview;
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
    APIInterface apiInterface;
    String TAG = ViewInstantTopoActivity.class.getSimpleName();
    String instatntId = "";
    String lat = null, lon = "";
    int favid = 0;
    String topoId = "";
    String totopoId = "";
    String topoPhysicalAddress = "";
    String firsturl = "https://maps.googleapis.com/maps/api/staticmap?&markers=icon:https://s3.ap-south-1.amazonaws.com/edispatch/Topo/Assets/img/companyImg/m_v5.png%7C";
    String secondurl = "&key=AIzaSyAvIsgCbwpTfO4ad3F6taz3kQydYeGv5mY&zoom=15&size=360x400&format=png&maptype=roadmap&style=feature:administrative%7Celement:geometry.fill%7Ccolor:0xd6e2e6&style=feature:administrative%7Celement:geometry.stroke%7Ccolor:0xcfd4d5&style=feature:administrative%7Celement:labels.text.fill%7Ccolor:0x7492a8&style=feature:administrative.neighborhood%7Celement:labels.text.fill%7Clightness:25&style=feature:landscape%7Celement:geometry.fill%7Cvisibility:on&style=feature:landscape.natural%7Celement:labels.text.fill%7Ccolor:0x7492a8&style=feature:poi%7Celement:labels.icon%7Csaturation:-100&style=feature:poi%7Celement:labels.text.fill%7Ccolor:0x588ca4&style=feature:poi.park%7Celement:geometry.fill%7Ccolor:0xa9de83&style=feature:poi.park%7Celement:geometry.stroke%7Ccolor:0xbae6a1&style=feature:poi.sports_complex%7Celement:geometry.fill%7Ccolor:0xc6e8b3&style=feature:poi.sports_complex%7Celement:geometry.stroke%7Ccolor:0xbae6a1&style=feature:road%7Celement:labels.icon%7Csaturation:-45%7Clightness:10%7Cvisibility:on&style=feature:road%7Celement:labels.text.fill%7Ccolor:0x41626b";
    String thirdurl = "&style=feature:road.arterial%7Celement:geometry.fill%7Ccolor:0xffffff&style=feature:road.highway%7Celement:geometry.fill%7Ccolor:0xc1d1d6&style=feature:road.highway%7Celement:geometry.stroke%7Ccolor:0xa6b5bb&style=feature:road.highway%7Celement:labels.icon%7Cvisibility:on&style=feature:road.local%7Celement:geometry.fill%7Ccolor:0xffffff&style=feature:transit%7Celement:labels.icon%7Csaturation:-70&style=feature:transit.line%7Celement:geometry.fill%7Ccolor:0xb4cbd4&style=feature:transit.line%7Celement:labels.text.fill%7Ccolor:0x588ca4&style=feature:transit.station%7Celement:labels.text.fill%7Ccolor:0x008cb5%7Cvisibility:on&style=feature:transit.station.airport%7Celement:geometry.fill%7Csaturation:-100%7Clightness:-5&style=feature:water%7Celement:geometry.fill%7Ccolor:0xa6cbe3";
    NearByAdapter adapter;
    protected static final int REQUEST_LOCATION = 0x1;
    GoogleApiClient googleApiClient = null;
    double currentlat = 0;
    double currentlong = 0;
    int latlong = 0;
    String uberurl = "https://m.uber.com/ul/?action=setPickup&client_id=QDtYUikKvCQ0UmRnEe08pKZ1xl4gZCYB&pickup=my_location&dropoff[formatted_address]=%1$s&dropoff[latitude]=%2$s&dropoff[longitude]=%3$s";
    String olaurl = "https://olawebcdn.com/assets/ola-universal-link.html?&category=compact&landing_page=bk&bk_act=rn&drop_lat=%1$s&drop_lng=%2$s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_viewinstanttopo);
        ButterKnife.bind(this);
        favid = 0;
        cabs_recycler.setHasFixedSize(true);
        cabs_recycler.setLayoutManager(new LinearLayoutManager(ViewInstantTopoActivity.this, LinearLayoutManager.HORIZONTAL, false));
        nearme_recycler.setHasFixedSize(true);
        nearme_recycler.setLayoutManager(new LinearLayoutManager(ViewInstantTopoActivity.this));
        apiInterface = APIClient.getClient().create(APIInterface.class);
        if (getIntent() != null) {
            instatntId = getIntent().getStringExtra("instatntId");
            if (instatntId != null) {
                Utility.showProgressDlg(ViewInstantTopoActivity.this, null);
                getViewTopoMethoed();
            }
        }

        onCabClick();
        providingLocationReuired();
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

    @OnClick(R.id.delete_layout)
    void callingDeleteMetheode() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(ViewInstantTopoActivity.this, R.style.AboutDialog));
        alertDialog.setTitle("");

        alertDialog.setMessage("Are you sure want to delete?");
        alertDialog.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Utility.showProgressDlg(ViewInstantTopoActivity.this, null);
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
            if (lat != null)
                setNavigateTopoMethoed();

        } else {
            if (Utility.topoId.equals(topoId)) {
                setStopNavigateTopoMethoed();
            } else {
                setNavigateTopoMethoed();
            }
        }

    }

    @OnClick(R.id.back_layout)
    void onBack() {
        finish();
    }

    @OnClick(R.id.forward_layout)
    void callingMovingMethoed() {
        if (topoId != null && !topoId.equals("")) {
            Intent i = new Intent(ViewInstantTopoActivity.this, SHowingContactTopsActivity.class);
            i.putExtra("instantTopoId", topoId);
            startActivity(i);
        }
    }

    @OnClick(R.id.share_layout)
    void callingShareMethoed() {
        if (topoId != null && !topoId.equals("")) {
            doStatTopoMethoed("instatnt","share");
            String shareurl = "https://www.topoapp.in/viewTopo.php?type=instant&id=" + topoId;
            shareTextUrl(shareurl);
        }
    }


    void getViewTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doInstantTopoView("app", instatntId, "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewInstantTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewInstantTopoActivity.this), utility.getDeviceIpAdress(ViewInstantTopoActivity.this), utility.getCurrentDate(), "viewtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                Logger.i(TAG, "forgot Response::" + data.getMsg());
                if (data.getResult().toLowerCase().equals("failed")) {
                    Utility.errorDialog(ViewInstantTopoActivity.this, data.getMsg());
                } else {


                    if (data.getNearby() != null && data.getNearby().size() > 0) {
                        adapter = new NearByAdapter(itemSelectedListener, data.getNearby());
                        nearme_recycler.setAdapter(adapter);
                    }

                    if (data.getCabs() != null && data.getCabs().size() > 0) {
                        CabsAdapter adapter = new CabsAdapter(selectedListener, data.getCabs());
                        cabs_recycler.setAdapter(adapter);
                    }

                    if (data.getTopoInstatnView() != null && data.getTopoInstatnView().size() > 0) {
                        lat = data.getTopoInstatnView().get(0).getTopo_instant_lat();
                        lon = data.getTopoInstatnView().get(0).getTopo_instant_lon();
                        topoPhysicalAddress = data.getTopoInstatnView().get(0).getTopo_instant_address();
                        topoId = data.getTopoInstatnView().get(0).getTopo_instant_id();
                        totopoId = data.getTopoInstatnView().get(0).getTopo_instant_user_id();
                        title_textview.setText(data.getTopoInstatnView().get(0).getTopo_instant_name());
                        if (data.getTopoInstatnView().get(0).getIsNavigating().equals("Yes")) {
                            navigation_imageview.setImageResource(R.drawable.navigate_cancle);
                        } else {
                            navigation_imageview.setImageResource(R.drawable.navigate);
                        }
                        setDetails(topoId);
                        if (lat != null && !lat.equals("")) {
                            //  String src = "https://maps.googleapis.com/maps/api/staticmap?zoom=15&size=400x400&maptype=roadmap&markers=icon:https://s3.ap-south-1.amazonaws.com/edispatch/Topo/Assets/img/companyImg/5.png%7C"+data.getTopoInstatnView().get(0).getTopo_instant_lat()+","+data.getTopoInstatnView().get(0).getTopo_instant_lon()+"&key=AIzaSyAvIsgCbwpTfO4ad3F6taz3kQydYeGv5mY&style=feature:all|element:labels|visibility:off";
                            // String src = firsturl + lat + "," + lon + secondurl + thirdurl;
                            String src = String.format(getString(R.string.mapurl_full), data.getTopoInstatnView().get(0).getTopo_instant_user_id(), data.getTopoInstatnView().get(0).getTopo_img_url());
                            Picasso.with(ViewInstantTopoActivity.this).load(src).into(map_imageview);

                        }

                    }
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
            doStatTopoMethoed("instatnt","nearme");
            LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            LatLng source = new LatLng(Double.parseDouble(data.getLat()), Double.parseDouble(data.getLon()));
            openGoogleDirections(source, destination);
        }
    };
    CabsAdapter.OnItemSelectedListener selectedListener = new CabsAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(CabsData cabsData) {
            if (cabsData.getTopo_cab_provider().toLowerCase().equals("uber")) {
                doStatTopoMethoed("instant","uber");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(uberurl, topoPhysicalAddress, lat, lon)));
                startActivity(browserIntent);
            } else if (cabsData.getTopo_cab_provider().toLowerCase().equals("ola")) {
                doStatTopoMethoed("instatnt","ola");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(olaurl, lat, lon)));
                startActivity(browserIntent);
            }
        }
    };

    void setStopNavigateTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.dostopnavigation("app", StoreDetails.getTopoUserId(getApplicationContext()), Utility.navigationId, utility.topoId, lat, lon, "yes", "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(getApplicationContext()), "", "", StoreDetails.getLoginkey(getApplicationContext()), utility.getDeviceIpAdress(getApplicationContext()), utility.getCurrentDate(), "updatenavigation");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                AppResponseData data = response.body();
                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ViewInstantTopoActivity.this, data.getMsg());
                    } else {
                        navigation_imageview.setImageResource(R.drawable.navigate);
                        Utility.navigation = 0;
                        Utility.navigationId = "";
                        Utility.topoId = "";
                        Utility.topoType = "";
                        stopService(new Intent(ViewInstantTopoActivity.this, MapServices.class));

                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewInstantTopoActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    void setNavigateTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doStratNavigation("app", instatntId, StoreDetails.getTopoUserId(ViewInstantTopoActivity.this), totopoId, topoId, "" + currentlat, "" + currentlong, "yes", "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewInstantTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewInstantTopoActivity.this), utility.getDeviceIpAdress(ViewInstantTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ViewInstantTopoActivity.this, data.getMsg());
                    } else {
                        navigation_imageview.setImageResource(R.drawable.navigate_cancle);
                        Utility.navigation = 1;
                        Utility.navigationId = data.getStartNavigation().getNavigationId();
                        Utility.topoId = topoId;
                        Utility.topoType = "yes";
                        LatLng destination = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                        LatLng source = new LatLng(currentlat, currentlong);
                        openGoogleDirections(source, destination);
                        adddetails();
                        startService(new Intent(ViewInstantTopoActivity.this, MapServices.class));
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewInstantTopoActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
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

    void setdeleteTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doInstantTopoDelete("app", StoreDetails.getTopoUserId(ViewInstantTopoActivity.this), instatntId, "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewInstantTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewInstantTopoActivity.this), utility.getDeviceIpAdress(ViewInstantTopoActivity.this), utility.getCurrentDate(), "deletetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(ViewInstantTopoActivity.this, data.getMsg());
                    } else {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                new ContextThemeWrapper(ViewInstantTopoActivity.this, R.style.AboutDialog));
                        alertDialog.setTitle("");

                        alertDialog.setMessage(data.getMsg());
                        alertDialog.setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finishAffinity();
                                    }
                                });
                        AlertDialog alertDialog1 = alertDialog.create();
                        alertDialog1.show();
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(ViewInstantTopoActivity.this, "Unable to handle request");
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


            if (latlong == 1) {
                latlong = 0;
                setNavigateTopoMethoed();
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
        } catch (java.lang.SecurityException ex) {
            Logger.i(TAG, "fail to request location update, ignore" + ex);
        } catch (IllegalArgumentException ex) {
            Logger.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
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
            setNavigateTopoMethoed();

        }
        return false;

    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(ViewInstantTopoActivity.this)
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
                                        ViewInstantTopoActivity.this, REQUEST_LOCATION);
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
            if (Utility.topoType.equals("yes")) {
                if (topoId.equals(Utility.topoId)) {
                    navigation_imageview.setImageResource(R.drawable.navigate_cancle);
                }
            }
        } else {
            navigation_imageview.setImageResource(R.drawable.navigate);
        }
    }

    void doStatTopoMethoed(String item,String action) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoStats("app",item,action, topoId, StoreDetails.getTopoUserId(ViewInstantTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(ViewInstantTopoActivity.this), "", "", StoreDetails.getLoginkey(ViewInstantTopoActivity.this), utility.getDeviceIpAdress(ViewInstantTopoActivity.this), utility.getCurrentDate(), "savetopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }
}
