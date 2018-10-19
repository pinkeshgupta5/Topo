package com.dhivi.inc.topo.presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dhivi.inc.topo.R;
import com.dhivi.inc.topo.adapter.LiveTrackAdater;
import com.dhivi.inc.topo.adapter.TopoRequestAdapter;
import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.plugin.holder.ArrivingTopos;
import com.dhivi.inc.topo.plugin.holder.LiveUsers;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by User on 11/5/2017.
 */

public class LiveTrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.liveTrack_recyclerview)
    RecyclerView liveTrack_recyclerview;
    private GoogleMap mGoogleMap;
    APIInterface apiInterface;
    String TAG = LiveTrackingActivity.class.getSimpleName();
    String topoid = null, topotype = "";
    String lat = null, lon = "";
    LiveTrackAdater liveTrackAdater;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;
    static public Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_livetrack);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        initMapView();
        if (getIntent() != null) {
            topoid = getIntent().getStringExtra("topoid");
            topotype = getIntent().getStringExtra("topotype");
            if (topoid != null) {
                Utility.showProgressDlg(LiveTrackingActivity.this, null);
                getLiveTrackMethoed();
                doingRefresh();
            }
        }

    }

    void initMapView() {
        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);

        liveTrack_recyclerview.setHasFixedSize(true);
        liveTrack_recyclerview.setLayoutManager(new LinearLayoutManager(LiveTrackingActivity.this));

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        View locationButton = mapFragment.getView().findViewById(2);

        locationButton.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 20, 20);
        mapFragment.getMapAsync(this);
    }

    void getLiveTrackMethoed() {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doLiveTrackView("app", StoreDetails.getTopoUserId(LiveTrackingActivity.this), topoid, topotype, "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(LiveTrackingActivity.this), "", "", StoreDetails.getLoginkey(LiveTrackingActivity.this), utility.getDeviceIpAdress(LiveTrackingActivity.this), utility.getCurrentDate(), "viewtopo");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {
                Utility.hideProgressDlg();
                AppResponseData data = response.body();

                if (data != null) {
                    if (data.getResult().toLowerCase().equals("failed")) {
                        Utility.errorDialog(LiveTrackingActivity.this, data.getMsg());
                    } else {
                        if (data.getLiveUsers() != null && data.getLiveUsers().size() > 0) {
                            liveTrackAdater = new LiveTrackAdater(listener, data.getLiveUsers());
                            liveTrack_recyclerview.setAdapter(liveTrackAdater);

                            LatLng latLng = new LatLng(Double.parseDouble(data.getLiveUsers().get(0).getTopo_lat()), Double.parseDouble(data.getLiveUsers().get(0).getTopo_long()));
                            Marker TP = mGoogleMap.addMarker(new MarkerOptions().
                                    position(latLng).title(data.getLiveUsers().get(0).getTopo_user_name()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.marker))));

                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                        }
                        if (data.getLiveTopo() != null) {
                            LatLng latLng = new LatLng(Double.parseDouble(data.getLiveTopo().get(0).getTopo_lat()), Double.parseDouble(data.getLiveTopo().get(0).getTopo_lon()));
                            Marker TP = mGoogleMap.addMarker(new MarkerOptions().
                                    position(latLng).title(data.getLiveTopo().get(0).getTopo_name().toUpperCase()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.marker))));

                        }
                    }
                } else {
                    Utility.errorDialog(LiveTrackingActivity.this, "Unable to handle request");
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

    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {

        mMarkerImageView.setImageResource(resId);
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

        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LiveTrackAdater.OnItemSelectedListener listener = new LiveTrackAdater.OnItemSelectedListener() {
        @Override
        public void onItemSelected(LiveUsers topo) {
            LatLng latLng = new LatLng(Double.parseDouble(topo.getTopo_lat()), Double.parseDouble(topo.getTopo_long()));
            Marker TP = mGoogleMap.addMarker(new MarkerOptions().
                    position(latLng).title(topo.getTopo_user_name()).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.marker))));

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        }
    };


    public void doingRefresh() {
        Logger.i(TAG, "it is coming here");
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.i(TAG, "it is coming here1");
                Logger.i(TAG, "doingRefresh is calling");
                getLiveTrackMethoed();
            }
        }, 0, 1000 * 30);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }
}

