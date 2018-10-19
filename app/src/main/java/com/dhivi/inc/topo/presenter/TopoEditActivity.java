package com.dhivi.inc.topo.presenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/5/2017.
 */

public class TopoEditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    APIInterface apiInterface;
    String TAG = TopoEditActivity.class.getSimpleName();
    String topoName = "";
    String lat = null, lon = "";
    String radioValue = "";
    String isbusiness = "";
    String businesstype = "";
    String workingdays = "";
    String WorkingtimeTo = "";
    String WorkingFrom = "";
    String workingcontactnumber = "";
    String workingcontactname = "";

    String flatValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edittopo);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        initMapView();
        if (getIntent() != null) {
            topoName = getIntent().getStringExtra("topoName");
            if (topoName != null) {
                getViewTopoMethoed();
            }
        }

    }

    void initMapView() {

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        View locationButton = mapFragment.getView().findViewById(2);

        locationButton.setVisibility(View.GONE);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 20, 400);
        mapFragment.getMapAsync(this);


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
                lat = "" + latlangObj.latitude;
                lon = "" + latlangObj.longitude;
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlangObj, 15f));
            }

            @Override
            public void onError(Status status) {


            }
        });
        Utility.loadFonts(TopoEditActivity.this);
        EditText editText = ((EditText) places.getView().findViewById(R.id.place_autocomplete_search_input));
        editText.setTextSize(18.0f);
        editText.setHint("Search Your Location");
        editText.setTypeface(Utility.fontTitlereguler);
    }

    //** Bound actions
    Geocoder geocoder;
    List<Address> addresses;

    @OnClick(R.id.editlocation_button)
    void callingUpdateocation() {

        try {
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();


            if (address != null && !address.equals("") && postalCode != null && !postalCode.equals("")) {
                Intent i = new Intent(TopoEditActivity.this, EditTopoActivity.class);
                i.putExtra("lat", "" + lat);
                i.putExtra("lon", "" + lon);
                i.putExtra("topoName", topoName);
                i.putExtra("address", address);
                i.putExtra("city", city);
                i.putExtra("country", country);
                i.putExtra("postalCode", postalCode);
                i.putExtra("radioValue", radioValue);
                i.putExtra("flatValue", flatValue);
                i.putExtra("isbusiness", isbusiness);
                i.putExtra("businesstype", businesstype);
                i.putExtra("workingdays", workingdays);
                i.putExtra("WorkingtimeTo", WorkingtimeTo);
                i.putExtra("WorkingFrom", WorkingFrom);
                i.putExtra("workingcontactname", workingcontactname);
                i.putExtra("workingcontactnumber", workingcontactnumber);


                startActivity(i);
            } else {
                Utility.errorDialog(TopoEditActivity.this, "Unable to read location. Try Again");
            }

        } catch (Exception e) {
            Utility.errorDialog(TopoEditActivity.this, "Unable to read location. Try Again");
            e.printStackTrace();
        }

    }


    void getViewTopoMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doTopoView("app", StoreDetails.getTopoUserId(TopoEditActivity.this), topoName, "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(TopoEditActivity.this), "", "", StoreDetails.getLoginkey(TopoEditActivity.this), utility.getDeviceIpAdress(TopoEditActivity.this), utility.getCurrentDate(), "viewtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                AppResponseData data = response.body();
                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(TopoEditActivity.this, data.getMsg());
                    } else {
                        if (data.getTopoView() != null && data.getTopoView().size() > 0) {
                            lat = data.getTopoView().get(0).getTopo_lat();
                            lon = data.getTopoView().get(0).getTopo_lon();
                            radioValue = data.getTopoView().get(0).getTopo_privacy();
                            isbusiness = data.getTopoView().get(0).getTopo_is_business();
                            businesstype = data.getTopoView().get(0).getTopo_business_category();
                            workingdays = data.getTopoView().get(0).getTopo_working_days();
                            WorkingFrom = data.getTopoView().get(0).getTopo_working_hours_from();
                            WorkingtimeTo = data.getTopoView().get(0).getTopo_working_hours_to();
                            workingcontactname = data.getTopoView().get(0).getTopo_business_name();
                            workingcontactnumber = data.getTopoView().get(0).getTopo_contact_number();
                            flatValue = data.getTopoView().get(0).getTopo_flat_no();
                            if (lat != null && !lat.equals("")) {
                                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                            }
                        }
                    }

                    Logger.i(TAG, "forgot Response::" + data.getMsg());
                } else {
                    Utility.errorDialog(TopoEditActivity.this, "Unable to handle request");
                }
            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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


        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                LatLng mCenterLatLong = cameraPosition.target;
                mGoogleMap.setMapType(mGoogleMap.MAP_TYPE_NORMAL);
                lat = "" + mCenterLatLong.latitude;
                lon = "" + mCenterLatLong.longitude;
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
               /* if (TP != null) {
                    Logger.i(TAG, "the markers removing in onMapReady..");
                    TP.remove();
                }
                TP = mGoogleMap.addMarker(new MarkerOptions().
                        position(mCenterLatLong).title("Car Parking").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));*/
                //Picasso.with(EmkitMyCarParkingActivity.this).load(R.drawable.emkit_mycar).resize(150, 150).into(new EmkitPicassoMarker(Tp1));
                if (ActivityCompat.checkSelfPermission(TopoEditActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });
    }
}
