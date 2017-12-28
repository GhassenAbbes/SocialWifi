package com.ahmedghassen.socialwifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.places.PlaceManager;
import com.facebook.places.internal.LocationPackageRequestParams;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Map_Fragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        OnInfoWindowClickListener,
        DirectionCallback
{




    private ImageView imageLoc;
    private int GALLERY = 1, CAMERA = 2;

    private static final String TAG = "LocationPickerActivity";
    String ch="";
    private Gson gson;
    static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
    private static final String STATE_IN_PERMISSION = "inPermission";
    private static final int REQUEST_PERMS = 1337;
    private boolean needsInit = false;
    private boolean isInPermission = false;
    //GoogleMap m;

    //private Mapbox mapView;
   // private com.mapbox.mapboxsdk.annotations.Marker droppedMarker;
    MapView mMapView;
    private GoogleMap googleMap;

    private ImageView hoveringMarker;
    Dialog dialog ;
    Marker marky;
    ConnectionManager con;
    RequestQueue queue ;
    //SupportMapFragment mapFragment;

    String indexloc="";
    List<LocationWifi> listlocations;
    LocationWifi loca = new LocationWifi();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;boolean locationfound=false;
    private String serverKey = "AIzaSyCw125M5v_sa7jtKYAdYFVXYASws5RPvT4";
    private LatLng origin = new LatLng(37.7849569, -122.4068855);
    private LatLng destination = new LatLng(37.7814432, -122.4460177);
    GsonBuilder gsonBuilder;
    // ADD LOCATION VARIABLES

    String myurl = con.ip+"AndroidUploadImage/uploadImage.php";
    String imagePath="null";

    /////////////////////////////
    @Override
    public boolean onMarkerClick(Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
        getDeviceLocation();
        Log.d("current location",currentLocation.getLatitude()+""+currentLocation.getLongitude());
        //origin= new LatLng( 36.170544, 10.170545);
        origin= new LatLng( currentLocation.getLatitude(),currentLocation.getLongitude());
        destination = marker.getPosition();
        requestDirection();

        return false;
    }



    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        private View popup=null;
        private LayoutInflater inflater=null;

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {

            String ch="";
            String d="";

            ch = marker.getTitle();
            d = ch.substring(0, ch.indexOf("/"));
            String indexloc = ch.substring(ch.indexOf("/") + 1, ch.length());
            Log.d("Strings", d + "   " + indexloc);


            try {

                // Getting view from the layout file info_window_layout
                popup = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                popup.setClickable(true);
                // Getting reference to the TextView to set latitude
                TextView wifiTxt = popup.findViewById(R.id.titleWifi);
                wifiTxt.setText(d);

                TextView passTxt = popup.findViewById(R.id.passworWifi);
                passTxt.setText(marker.getSnippet());

                ImageView heart = popup.findViewById(R.id.addfavourite);

                loca = listlocations.get(Integer.valueOf(indexloc));

                heart.setOnClickListener(v -> {
                    addToFavourite(Integer.toString(loca.getId()));

                });

                ImageView imgWifi = popup.findViewById(R.id.clientPic);

                imgWifi.setClickable(true);
                imgWifi.setOnClickListener(v -> {


                });

                Button connect = popup.findViewById(R.id.toconnect);
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
    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_map_, null, false);
        root.setScrollContainer(false);
        FloatingActionButton fab = root.findViewById(R.id.fabadd);

        /*fab.setOnClickListener(view -> {

            FragmentManager manager = getActivity().getSupportFragmentManager();
            Fragment fragment = new AddLocFragment();


            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, "SC");
            transaction.addToBackStack("fav");
            transaction.commit();
        });*/

        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();


        CheckGooglePlayServices();
        mMapView =  root.findViewById(R.id.map);

        mMapView.onCreate(savedInstanceState);
        getLocationPermission();

        //Add Location
            fab.setOnClickListener(view -> {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_loc, null);
                final EditText ssid = mView.findViewById(R.id.ssidadd_d);
                final EditText pw = mView.findViewById(R.id.pwadd_d);
                final Button ajouter = mView.findViewById(R.id.ajouter_d);
                final Button cancel = mView.findViewById(R.id.canceladd_d);
                cancel.setOnClickListener(view1 -> {
                    dialog.hide();
                });

                final ImageView imageLoc =  mView.findViewById(R.id.addlocimage_d);
                imageLoc.setOnClickListener(v -> showPictureDialog());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                    WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (mWifiManager.isWifiEnabled()) {
                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                        if (wifiInfo != null) {
                            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                            if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                                ssid.setText(wifiInfo.getSSID().replace("\"",""));
                                ajouter.setOnClickListener(v -> {


                                    if ( TextUtils.isEmpty(ssid.getText())||TextUtils.isEmpty(pw.getText()))
                                        Toast.makeText(getActivity(),"You must complete the missing fields!",Toast.LENGTH_LONG).show();
                                    else {

                                        if (ContextCompat.checkSelfPermission(getActivity(),
                                                Manifest.permission.ACCESS_FINE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED
                                                &&
                                                ContextCompat.checkSelfPermission(getActivity(),
                                                        Manifest.permission.ACCESS_COARSE_LOCATION)
                                                        != PackageManager.PERMISSION_GRANTED) {
                                            askForLocationPermissions();
                                        } else {
                                            Log.d("OUr BSSID ", wifiInfo.getBSSID());
                                            if (ExistingBSSID(wifiInfo.getBSSID())==true) {


                                                if( connectToWifi(ssid.getText().toString(), pw.getText().toString()))
                                                {
                                                    getDeviceLocation();
                                                    Log.d("Image Path ",Double.toString(currentLocation.getLatitude()));

                                                    con = new ConnectionManager("addloc");

                                                    queue = Volley.newRequestQueue(getActivity().getApplicationContext());


                                                    Log.d("Image Path ", imagePath);

                                                    String s = con.getPath();
                                                    String uri = s + String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s&img=%5$s&mac=%6$s",
                                                            ssid.getText().toString().replace(" ","_"),
                                                            pw.getText().toString(),
                                                            Double.toString(currentLocation.getLatitude()),
                                                            Double.toString(currentLocation.getLongitude()),
                                                            imagePath,
                                                            wifiInfo.getBSSID()
                                                    );



                                                    // Request a string response
                                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {

                                                                    // Result handling
                                                                    Toast.makeText(root.getContext(), ""+response, Toast.LENGTH_SHORT).show();

                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {

                                                            // Error handling
                                                            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                            error.printStackTrace();

                                                        }
                                                    });
                                                    queue.add(stringRequest);
                                                    Log.d("requet", stringRequest.toString());

                                                    dialog.hide();
                                                }
                                                else {
                                                    Toast.makeText(getContext(),"Invailed password",Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                Toast.makeText(getContext(),"Invailed Wifi",Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    }
                                });
                            }
                        }
                    }


            });
        return root;
    }



    @Override
    public void onInfoWindowClick(Marker marker) {

        FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment fragment = new DetailLocFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("myObject", new Gson().toJson(loca));

        fragment.setArguments(bundle2);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment, "SC");
        transaction.addToBackStack("fav");
        transaction.commit();

    }


    private void fetchLocations() {
        StringRequest request = new StringRequest(Request.Method.GET, con.getPath(), onPostsLoaded, onPostsError);
        queue.add(request);

    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
             ch=response;
            mMapView.onResume();
             listlocations = Arrays.asList(gson.fromJson(ch, LocationWifi[].class));
            Log.i("PostActivity", listlocations.size() + " posts loaded.");
            Log.d("string ",ch);

            int i =0;
            for (LocationWifi loc : listlocations) {
                LatLng sydney = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLng()));
                googleMap.addMarker(new MarkerOptions().position(sydney)
                        .title(loc.getDesc() + "/"+i)
                        .snippet(loc.getWifi_pass())
                );
                i++;
            }
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
    public void onMapReady(GoogleMap mMap) {



            //mapboxMap.setStyleUrl(Style.MAPBOX_STREETS);

            // Set the camera's starting position
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(36.8984, 10.1897)) // set the camera's center position
                    .zoom(9)  // set the camera's zoom level
                    .tilt(20)  // set the camera's tilt
                    .build();

            // Move the camera to that position
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

            if (mLocationPermissionsGranted) {
                getDeviceLocation();

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }


                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            googleMap = mMap;

              fetchLocations();

        }




    private boolean getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location!");
                        currentLocation = (Location) task.getResult();

                       /* moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                DEFAULT_ZOOM);*/
                       locationfound = true;
                       Log.d("locationfound",Boolean.toString(locationfound));
                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
        return locationfound;
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        mMapView.getMapAsync(this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }



    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }














    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    public void requestDirection() {
        //Snackbar.make(btnRequestDirection, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        //Snackbar.make(btnRequestDirection, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Searching for directions", Toast.LENGTH_SHORT).show();

        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);
            googleMap.addMarker(new MarkerOptions().position(origin));
            googleMap.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
            setCameraWithCoordinationBounds(route);

            //btnRequestDirection.setVisibility(View.GONE);
        } else {
           // Snackbar.make(btnRequestDirection, direction.getStatus(), Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "No directions found!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        //Snackbar.make(btnRequestDirection, t.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }




    //ADD LOCATION FUNCTIONS

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == GALLERY)
                onSelectFromGalleryResult(data);
            else if (requestCode == CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                imageLoc.setImageBitmap(bitmap);
                uploaduserimage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d( "File Path: ",thumbnail.toString());
        imageLoc.setImageBitmap(thumbnail);
        uploaduserimage(thumbnail);

    }


    public void uploaduserimage(Bitmap bitmap){


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("Myresponse",""+response);
                imagePath = response;
                Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart",""+error);
                Toast.makeText(getActivity().getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                String images = getStringImage(bitmap);
                Log.i("Mynewsam",""+images);
                param.put("image",images);
                new ConnectionManager();
                param.put("server",ConnectionManager.ip);
                return param;
            }
        };

        requestQueue.add(stringRequest);


    }

    public String getStringImage(Bitmap bitmap){
        Log.i("MyHitesh",""+bitmap);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }










    ///////////////////////////////////


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {

        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {

        mMapView.onLowMemory();
        super.onLowMemory();
    }
}
