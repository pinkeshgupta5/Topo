package com.dhivi.inc.topo.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.fragments.InstantTopos;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.TopoInstantCreate;
import com.dhivi.inc.topo.utils.Logger;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/2/2017.
 */

public class InstatntTopoActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MapActivity";
    private GoogleMap mGoogleMap;
    private LatLng mDummyLatLng;// = new LatLng(33.6667, 73.1667);
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;
    private String ImageUrl = "https://s3.amazonaws.com/uifaces/faces/twitter/jsa/128.jpg";
    double lat = 0;
    double lon = 0;
    APIInterface apiInterface;
    int shaeviacall = 0;
    int sendtopocall = 0;
    int latlong = 0;
    protected static final int REQUEST_LOCATION = 0x1;
    GoogleApiClient googleApiClient = null;
    Geocoder geocoder;
    List<Address> addresses;
    String address = "", city = "", country = "", postalCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_insatanttopo);

        ButterKnife.bind(this);
        initViews();
        apiInterface = APIClient.getClient().create(APIInterface.class);

    }

    private void initViews() {

        lat = 0;
        lon = 0;
        latlong = 0;
        PlaceAutocompleteFragment places = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();

        places.setFilter(typeFilter);
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                mGoogleMap.setMapType(mGoogleMap.MAP_TYPE_NORMAL);
                LatLng latlangObj = place.getLatLng();
                lat = latlangObj.latitude;
                lon = latlangObj.longitude;
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlangObj, 15f));
            }

            @Override
            public void onError(Status status) {


            }
        });

        Utility.loadFonts(InstatntTopoActivity.this);
        EditText editText = ((EditText) places.getView().findViewById(R.id.place_autocomplete_search_input));
        editText.setTextSize(18.0f);
        editText.setHint("Search Your Location");
        editText.setTypeface(Utility.fontTitlereguler);

        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        View locationButton = mapFragment.getView().findViewById(2);

        locationButton.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 20, 400);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady() called with");
        mGoogleMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyles));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        setUpMapIfNeeded();

        providingLocationReuired();
        //   MapsInitializer.initialize(this);
        //   addCustomMarker();
    }

    //** Bound actions
    @OnClick(R.id.sharevia_button)
    void callingshareviaMethoed() {
        if (shaeviacall == 0) {

            try {
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();


                if (address != null && !address.equals("") && postalCode != null && !postalCode.equals("")) {
                    shaeviacall = 1;
                    Utility.showProgressDlg(InstatntTopoActivity.this, null);
                    saveInstatntTopo("share");
                } else {
                    Utility.errorDialog(InstatntTopoActivity.this, "Unable to read location. Try Again");
                }

            } catch (Exception e) {
                Utility.errorDialog(InstatntTopoActivity.this, "Unable to read location. Try Again");
                e.printStackTrace();
            }


        }
    }

    @OnClick(R.id.sendtopo_button)
    void callingsendtopoMethoed() {
        if (sendtopocall == 0) {

            try {
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();


                if (address != null && !address.equals("") && postalCode != null && !postalCode.equals("")) {
                    sendtopocall = 1;
                    Utility.showProgressDlg(InstatntTopoActivity.this, null);
                    saveInstatntTopo("send");
                } else {
                    Utility.errorDialog(InstatntTopoActivity.this, "Unable to read location. Try Again");
                }

            } catch (Exception e) {
                Utility.errorDialog(InstatntTopoActivity.this, "Unable to read location. Try Again");
                e.printStackTrace();
            }

        }
    }

    private void addCustomMarker() {
        Log.d(TAG, "addCustomMarker()");
        if (mGoogleMap == null) {
            return;
        }

        // adding a marker with image from URL using glide image loading library
        Glide.with(getApplicationContext()).
                load(ImageUrl)
                .asBitmap()
                .fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(mDummyLatLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDummyLatLng, 13f));


                    }
                });


    }

    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

    private void setUpMapIfNeeded() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }

        noLocation();
        // Check if we were successful in obtaining the map.
        if (mGoogleMap != null) {

            mGoogleMap.setMyLocationEnabled(true);
         /*   mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {
                    // TODO Auto-generated method stub
                    if (lat == 0) {
                        lat = arg0.getLatitude();
                        lon = arg0.getLongitude();
                        mDummyLatLng = new LatLng(lat, lon);
                        Marker TP = mGoogleMap.addMarker(new MarkerOptions().
                                position(mDummyLatLng).title("dilip"));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDummyLatLng, 15f));
                        //mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                        TP.setDraggable(true);
                    }

                }
            });*/

        }
    }


    void saveInstatntTopo(final String value) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doInstantSaveTopo("app", "", postalCode, city, country, address, "" + lon, "" + lat, StoreDetails.getTopoUserId(InstatntTopoActivity.this), "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(InstatntTopoActivity.this), "", "", StoreDetails.getLoginkey(InstatntTopoActivity.this), utility.getDeviceIpAdress(InstatntTopoActivity.this), utility.getCurrentDate(), "createtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();
                if (data != null) {
                    Logger.i(TAG, "login Response::" + data.getMsg());
                    TopoInstantCreate instantCreate = data.getTopoInstantCreate();
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(InstatntTopoActivity.this, data.getMsg());
                    } else {


                        if (value.equals("send")) {
                            sendtopocall = 0;
                            if (instantCreate.getTopo_instant_id() != null && !instantCreate.getTopo_instant_id().equals("")) {
                                Intent i = new Intent(InstatntTopoActivity.this, SHowingContactTopsActivity.class);
                                i.putExtra("instantTopoId", instantCreate.getTopo_instant_id());
                                startActivity(i);
                            }

                        } else {
                            shaeviacall = 0;
                            if (instantCreate.getTopo_instant_id() != null && !instantCreate.getTopo_instant_id().equals("")) {
                                String shareurl = "https://www.topoapp.in/viewTopo.php?type=instant&id=" + instantCreate.getTopo_instant_id();
                                shareTextUrl(shareurl);
                            }
                        }
                    }
                } else {
                    Utility.errorDialog(InstatntTopoActivity.this, "Unable to handle request");
                }

            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {
                Utility.hideProgressDlg();
                shaeviacall = 0;
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

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(InstatntTopoActivity.this)
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
                                        InstatntTopoActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }

    // check whether gps is enabled
    public boolean noLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //  buildAlertMessageNoGps();

            enableLoc();
            return true;
        }
        return false;

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
            if (mGoogleMap != null) {
                if (latlong == 0) {
                    latlong = 1;
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    mDummyLatLng = new LatLng(lat, lon);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDummyLatLng, 15f));
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
}


