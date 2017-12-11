package com.ahmedghassen.socialwifi;


import android.Manifest;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;


public class FavouriteMapFragment extends Fragment {
    ConnectionManager con;
    LocationWifi p;
    private Gson gson;
    RequestQueue queue ;
    //SupportMapFragment mapFragment;
    MapView mapFragment;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public FavouriteMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_favouritemap, null, false);

        Bundle bundle = this.getArguments();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        String jsonMyObject = "";

        if (bundle != null) {
            jsonMyObject = bundle.getString("myObject");
        }
        p= new Gson().fromJson(jsonMyObject, LocationWifi.class);


        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());



        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.access_token));


        mapFragment = (MapView) root.findViewById(R.id.mapfav);
        mapFragment.onCreate(savedInstanceState);


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {


                mapboxMap.setStyleUrl(Style.MAPBOX_STREETS);

                // Set the camera's starting position
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(36.8984, 10.1897)) // set the camera's center position
                        .zoom(9)  // set the camera's zoom level
                        .tilt(20)  // set the camera's tilt
                        .build();

                // Move the camera to that position
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    LatLng sydney = new LatLng(Double.parseDouble(p.getLat()), Double.parseDouble(p.getLng()));
                    mapboxMap.addMarker(new MarkerViewOptions().position(sydney)
                            .title(p.getDesc()+"/"+p.getId())
                            .snippet(p.getWifi_pass()));


                mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {

                        View popup = null;
                        String ch = marker.getTitle();
                        String d = ch.substring(0,ch.indexOf("/"));
                        String idloc = ch.substring(ch.indexOf("/")+1,ch.length());
                        Log.d("Strings",d+"   "+idloc);


                        try {

                            // Getting view from the layout file info_window_layout
                            popup = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                            popup.setClickable(true);
                            // Getting reference to the TextView to set latitude
                            TextView wifiTxt = (TextView) popup.findViewById(R.id.titleWifi);
                            wifiTxt.setText(d);

                            TextView passTxt = (TextView) popup.findViewById(R.id.passworWifi);
                            passTxt.setText(marker.getSnippet());

                            ImageView heart = (ImageView)popup.findViewById(R.id.addfavourite);
                            Button connect = (Button)popup.findViewById(R.id.toconnect);
                            connect.setOnClickListener(v -> {

                                if (ContextCompat.checkSelfPermission(getActivity(),
                                        Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED
                                        &&
                                        ContextCompat.checkSelfPermission(getActivity(),
                                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    askForLocationPermissions();
                                } else {

                                    if (ExistingBSSID(wifiTxt.getText().toString())==true) {
                                        Toast.makeText(getContext(), "Wifi Connected :" +
                                                connectToWifi(wifiTxt.getText().toString(), passTxt.getText().toString()), Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getContext(),"Invailed Wifi",Toast.LENGTH_LONG).show();
                                    }
                                }

                            });
                            heart.setOnClickListener(v -> {
                                DelFavourite(idloc);

                            });

                        } catch (Exception ev) {
                            System.out.print(ev.getMessage());
                        }

                        return popup;
                    }
                });


            }
        });


        return root;
    }


    private void DelFavourite(String id){

        con = new ConnectionManager("delfavourite");

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        String s = con.getPath();
        String uri = s+String.format("&id_loc=%1$s",
                id);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri,
                response -> {
                    Toast.makeText(getActivity(),"Successful",Toast.LENGTH_LONG).show();
                },
                error -> {
                    Toast.makeText(getActivity(),"Failed",Toast.LENGTH_LONG).show();

                });
        Log.d("requet",myReq.toString());

        queue.add(myReq);
        Log.d("requet",myReq.toString());

    }



    public boolean connectToWifi(String SSID, String PASSWORD){
        try{
            WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
            //If Wifi is not enabled, enable it
            if (!mWifiManager.isWifiEnabled()) {
                Log.v("Log_TGS_Wifi", "Wifi is not enabled, enable it");
                mWifiManager.setWifiEnabled(true);
            }
            WifiConfiguration config = new WifiConfiguration();
            config.SSID = "\""+SSID+"\"";
            // if key is empty means it is open network-- I am considering like this
            if(PASSWORD.isEmpty()){
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else {
                config.preSharedKey = "\""+PASSWORD+"\"";
            }

            int networkId = mWifiManager.addNetwork(config);
            Log.d("netId",String.valueOf(networkId));
            // it will return -1 if the config is already saved..
            if(networkId == -1){
                networkId = getExistingNetworkId(config.SSID);
            }

            mWifiManager.saveConfiguration();

            mWifiManager.disconnect();
            // giving time to disconnect here.
            Thread.sleep(3*1000);
            mWifiManager.enableNetwork(networkId, true);
            mWifiManager.reconnect();
            Thread.sleep(3*1000);

            if (mWifiManager.isWifiEnabled()) {
                WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                    if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        Log.d("Wifi info : ", wifiInfo.toString());
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private int getExistingNetworkId(String SSID) {
        WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();

        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (SSID.equalsIgnoreCase(existingConfig.SSID)) {
                    mWifiManager.removeNetwork(existingConfig.networkId-1);
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

    public boolean ExistingBSSID (String BSSID) {

        WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        List<ScanResult> mScanResults = mWifiManager.getScanResults();

        for (ScanResult wifiInfo:mScanResults){
            Log.d("Wifi ssid : ", wifiInfo.SSID);
            if (wifiInfo.SSID.equals(BSSID))
                return true;
        }

        return false;
    }

    private void askForLocationPermissions() {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new android.support.v7.app.AlertDialog.Builder(getActivity())
                    .setTitle("Location permessions needed")
                    .setMessage("you need to allow this permission!")
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
//                                        //Do nothing
                        }
                    })
                    .show();

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    public static boolean isPermissionGranted(@NonNull String[] grantPermissions, @NonNull int[] grantResults,
                                              @NonNull String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Do you work
                } else {
                    Toast.makeText(getContext(), "Can not proceed! i need permission" , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
