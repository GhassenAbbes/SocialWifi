package com.ahmedghassen.socialwifi;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DELL on 30/12/2017.
 */

public class Wifi {
    private String id_loc;
    private String ssid;
    private String wifi_pass;
    private String lat;
    private String lng;
    private String img;
    private String mac;

    public Wifi() {
    }

    public Wifi(String id_loc, String ssid, String wifi_pass, String lat, String lng, String img, String mac) {
        this.id_loc = id_loc;
        this.ssid = ssid;
        this.wifi_pass = wifi_pass;
        this.lat = lat;
        this.lng = lng;
        this.img = img;
        this.mac = mac;
    }

    public String getId_loc() {
        return id_loc;
    }

    public void setId_loc(String id_loc) {
        this.id_loc = id_loc;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getWifi_pass() {
        return wifi_pass;
    }

    public void setWifi_pass(String wifi_pass) {
        this.wifi_pass = wifi_pass;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "id_loc='" + id_loc + '\'' +
                ", ssid='" + ssid + '\'' +
                ", wifi_pass='" + wifi_pass + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", img='" + img + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
