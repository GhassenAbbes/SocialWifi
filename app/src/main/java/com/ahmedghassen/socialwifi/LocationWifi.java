package com.ahmedghassen.socialwifi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LENOVO-Z510 on 10/11/2017.
 */

public class LocationWifi {
    @SerializedName("id_loc")
    private int id;
    @SerializedName("desc_loc")
    private String desc;
    private String wifi_pass;
    private String lat;
    private String lng;

    public LocationWifi() {
    }

    public int getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public String getWifi_pass() {
        return wifi_pass;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}
