package com.esprit.socialwifi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LENOVO-Z510 on 10/11/2017.
 */

public class LocationWifi {

    private int id_loc;
    @SerializedName("desc_loc")
    private String ssid;
    private String wifi_pass;
    private String lat;
    private String lng;
    private String img;
    private String mac;

    public LocationWifi(int id,String ssid , String wifi_pass , String lat , String lng , String img , String mac) {
        this.id_loc = id;
        this.ssid = ssid;
        this.wifi_pass = wifi_pass;
        this.lat = lat;
        this.lng  = lng;
        this.img = img;
        this.mac = mac;
    }

    public LocationWifi() {
    }

    public int getId() {
        return id_loc;
    }

    public String getSsid() {
        return ssid;
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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setId(int id) {
        this.id_loc = id;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
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
                "id_loc=" + id_loc +
                ", desc='" + ssid + '\'' +
                ", wifi_pass='" + wifi_pass + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", img='" + img + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
