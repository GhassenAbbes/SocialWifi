package com.ahmedghassen.socialwifi;





public class ConnectionManager {
    String path;
    public static String ip = "http://192.168.1.13/";


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