package com.ahmedghassen.socialwifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by LENOVO-Z510 on 10/11/2017.
 */

public class ConnectionManager {
    String path;
    public static String ip = "http://192.168.1.4/";


    public ConnectionManager(){
        path=ip+"android/services.php?action=";
    }
    public ConnectionManager(String a){
        path=ip+"android/services.php?action="+a;
    }

    public String getPath() {
        return path;
    }


}