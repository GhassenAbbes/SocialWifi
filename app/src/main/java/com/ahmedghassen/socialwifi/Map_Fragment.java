package com.ahmedghassen.socialwifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.mapbox.mapboxsdk.maps.MapboxMap.OnInfoWindowClickListener;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Map_Fragment extends Fragment implements OnInfoWindowClickListener
{

    private ImageView imageLoc;
    private int GALLERY = 1, CAMERA = 2;

    private static final String TAG = "LocationPickerActivity";

    private Gson gson;
    static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
    private static final String STATE_IN_PERMISSION = "inPermission";
    private static final int REQUEST_PERMS = 1337;
    private boolean needsInit = false;
    private boolean isInPermission = false;
    //GoogleMap m;

    private Mapbox mapView;
    private MapboxMap mapboxMap ;
    private com.mapbox.mapboxsdk.annotations.Marker droppedMarker;
    private ImageView hoveringMarker;
    Dialog dialog ;
    MarkerView marky;
    ConnectionManager con;
    RequestQueue queue ;
    //SupportMapFragment mapFragment;
    MapView mapFragment;
    String indexloc="";
    List<LocationWifi> listlocations;
    LocationWifi loca = new LocationWifi();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_map_, null, false);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddLocFragment addfrag = new AddLocFragment() ;
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_frame, addfrag, "init");
                transaction.commit();
            }
        });

        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();


        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.access_token));


        mapFragment = (MapView) root.findViewById(R.id.map);
        mapFragment.onCreate(savedInstanceState);
        fetchLocations();


        return root;
    }



    @Override
    public boolean onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity().getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
        return false;
    }


    private void fetchLocations() {
        StringRequest request = new StringRequest(Request.Method.GET, con.getPath(), onPostsLoaded, onPostsError);
        queue.add(request);

    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {


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

                    String ch=response;


                    listlocations = Arrays.asList(gson.fromJson(response, LocationWifi[].class));
                    Log.i("PostActivity", listlocations.size() + " posts loaded.");
                    Log.d("string ",ch);

                    int i =0;
                    for (LocationWifi loc : listlocations) {
                        LatLng sydney = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLng()));
                        mapboxMap.addMarker(new MarkerViewOptions().position(sydney)
                                .title(loc.getDesc() + "/"+i)
                                .snippet(loc.getWifi_pass())
                        );
                        i++;
                    }

                    mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public View getInfoWindow(@NonNull Marker marker) {

                            View popup = null;
                            String ch="";
                            String d="";

                                ch = marker.getTitle();
                                d = ch.substring(0, ch.indexOf("/"));
                                indexloc = ch.substring(ch.indexOf("/") + 1, ch.length());
                                Log.d("Strings", d + "   " + indexloc);


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

                                loca = listlocations.get(Integer.valueOf(indexloc));

                                heart.setOnClickListener(v -> {
                                    addToFavourite(Integer.toString(loca.getId()));

                                });

                                ImageView imgWifi = (ImageView)popup.findViewById(R.id.clientPic);

                                imgWifi.setClickable(true);
                                imgWifi.setOnClickListener(v -> {

                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    Fragment fragment = new DetailLocFragment();
                                    Bundle bundle2 = new Bundle();
                                    bundle2.putString("myObject", new Gson().toJson(loca));

                                    fragment.setArguments(bundle2);

                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.content_frame, fragment, "SC");
                                    transaction.addToBackStack("fav");
                                    transaction.commit();
                                });

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
                                Picasso.with(getActivity())
                                        .load(loca.getImg())
                                        .into(imgWifi);

                            } catch (Exception ev) {
                                System.out.print(ev.getMessage());
                            }

                            return popup;
                        }
                    });

                }

            });

        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    private void addToFavourite(String id){

        con = new ConnectionManager("addfavourite");

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        SharedPreferences prefs = getActivity().getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String id_user = prefs.getString("fb_id",null);
        Log.d("id_user",id_user);
        String s = con.getPath();
        String uri = s+String.format("&id_user=%1$s&id_loc=%2$s",
                id_user,id);

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
