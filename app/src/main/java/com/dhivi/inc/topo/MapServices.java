package com.dhivi.inc.topo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.dhivi.inc.topo.plugin.APIClient;
import com.dhivi.inc.topo.plugin.APIInterface;
import com.dhivi.inc.topo.plugin.holder.AppResponseData;
import com.dhivi.inc.topo.utils.Logger;
import com.dhivi.inc.topo.utils.StoreDetails;
import com.dhivi.inc.topo.utils.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Manoj on 11/4/2017.
 */

public class MapServices extends Service {

    private static final String TAG = MapServices.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    APIInterface apiInterface;

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
            Toast.makeText(getApplicationContext(), "onLocation Changed" + location.getLatitude(), Toast.LENGTH_LONG).show();
            updateNavigationMethoed("" + location.getLatitude(), "" + location.getLongitude());

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

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        Logger.e(TAG, "onCreate");
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

    @Override
    public void onDestroy() {
        Logger.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Logger.i(TAG, "fail to remove location listners, ignore" + ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Logger.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    void updateNavigationMethoed(String lat, String lon) {

        Utility utility = new Utility();
        Call<AppResponseData> call = apiInterface.doupdatenavigation("app", StoreDetails.getTopoUserId(getApplicationContext()), Utility.navigationId, utility.topoId, lat, lon, Utility.topoType , "", "", utility.getDeviceName(), "android", utility.getDeviceModel(), "", "", "yes", utility.getVersionNumber(getApplicationContext()), "", "", StoreDetails.getLoginkey(getApplicationContext()), utility.getDeviceIpAdress(getApplicationContext()), utility.getCurrentDate(), "updatenavigation");
        call.enqueue(new Callback<AppResponseData>() {
            @Override
            public void onResponse(Call<AppResponseData> call, Response<AppResponseData> response) {

                AppResponseData data = response.body();
                Logger.i(TAG, "forgot Response::" + data.getMsg());
                if (data.getResult().toLowerCase().equals("failed")) {
                    stopSelf();
                    Utility.navigation = 0;
                    Utility.navigationId = "";
                    Utility.topoId = "";
                    Utility.topoType = "";
                } else {

                }


            }

            @Override
            public void onFailure(Call<AppResponseData> call, Throwable t) {

                Logger.i(TAG, "forgot Response failure::" + t.toString());
                call.cancel();
            }
        });
    }


}
