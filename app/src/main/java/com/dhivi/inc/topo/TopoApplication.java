package com.dhivi.inc.topo;

import android.support.multidex.MultiDex;
import android.support.multidex .MultiDexApplication;


/**
 * Created by User on 11/18/2017.
 */

public class TopoApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }

}
