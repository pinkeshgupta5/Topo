package com.dhivi.inc.topo.plugin.holder;

/**
 * Created by manager on 30-10-2017.
 */

public class Authentication {

    String topo_user_id;
    String tracking_login_key;
    String topo_user_country;

    public String getTopo_user_id() {
        return topo_user_id;
    }

    public void setTopo_user_id(String topo_user_id) {
        this.topo_user_id = topo_user_id;
    }

    public String getTracking_login_key() {
        return tracking_login_key;
    }

    public void setTracking_login_key(String tracking_login_key) {
        this.tracking_login_key = tracking_login_key;
    }

    public String getTopo_user_country() {
        return topo_user_country;
    }

    public void setTopo_user_country(String topo_user_country) {
        this.topo_user_country = topo_user_country;
    }
}
