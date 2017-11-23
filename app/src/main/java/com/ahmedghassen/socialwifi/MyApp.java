package com.ahmedghassen.socialwifi;

/**
 * Created by Ahmed on 28/10/2017.
 */

import android.app.Application;

public class MyApp extends Application{

    private boolean connected = false;


    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

}