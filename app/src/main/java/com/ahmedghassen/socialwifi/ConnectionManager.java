package com.ahmedghassen.socialwifi;

/**
 * Created by LENOVO-Z510 on 10/11/2017.
 */

public class ConnectionManager {
    String path;



    public ConnectionManager(){
        String p="http://172.19.1.5/";
        path=p+"android/services.php?action=";
    }
    public ConnectionManager(String a){
        String p="http://192.168.1.7/";
        path=p+"android/services.php?action="+a;
    }

    public String getPath() {
        return path;
    }
}