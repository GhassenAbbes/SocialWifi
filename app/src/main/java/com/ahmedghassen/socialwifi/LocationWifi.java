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
    private String img;
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

    public String getImg() {
        return img;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setWifi_pass(String wifi_pass) {
        this.wifi_pass = wifi_pass;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
    public void setImg(String img) {
        this.img = img;
    }





    @Override
    public String toString() {
        return "LocationWifi{" +
                "id=" + id +
                ", desc='" + desc + '\'' +
                ", wifi_pass='" + wifi_pass + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
