package com.esprit.socialwifi;





public class ConnectionManager {
    String path;
    public static String ip = "http://41.226.11.243:10080/socialwifi";


    public ConnectionManager(){
        path=ip+"/android/services.php?action=";
    }
    public ConnectionManager(String a){
        path=ip+"/android/services.php?action="+a;
    }

    public String getPath() {
        return path;
    }


}