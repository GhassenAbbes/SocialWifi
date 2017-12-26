package com.ahmedghassen.socialwifi;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * A simple {@link Fragment} subclass.
 */
public class AddLocFragment extends Fragment implements OnMapReadyCallback {
    private ImageView imageLoc;
    private int GALLERY = 1, CAMERA = 2;
    EditText ssid;

    String myurl = "http://172.19.12.34/AndroidUploadImage/uploadImage.php";
    String imagePath="null";
    private static final String TAG = "LocationPickerActivity";

    private Gson gson;
    private GoogleMap mapboxMap ;
    MapView mapFragment;
    ConnectionManager con;
    RequestQueue queue ;
    Marker marky=null;
    String wifiinfos;
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    EditText pw;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Location currentLocation;
    GoogleMap meMap;

    public AddLocFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_add_loc, null, false);

        ssid = (EditText) root.findViewById(R.id.ssidadd);
        pw = (EditText)root.findViewById(R.id.pwadd);
        Button ajouter = (Button)root.findViewById(R.id.ajouter);
        imageLoc = (ImageView) root.findViewById(R.id.addlocimage);
        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();



        imageLoc.setOnClickListener(v -> showPictureDialog());


        mapFragment = (MapView) root.findViewById(R.id.addmaplayout);
        mapFragment.onCreate(savedInstanceState);

        mapFragment.getMapAsync(this);


          WifiManager mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    ssid.setText(wifiInfo.getSSID().replace("\"",""));
                    ajouter.setOnClickListener(v -> {

                        if ( TextUtils.isEmpty(ssid.getText())||TextUtils.isEmpty(pw.getText())||marky==null)
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
                                        con = new ConnectionManager("addloc");

                                        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                                        getDeviceLocation();
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

                                        LocationsFragment locfrag = new LocationsFragment();
                                        FragmentManager manager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction transaction = manager.beginTransaction();
                                        transaction.replace(R.id.content_frame, locfrag, "init");
                                        transaction.commit();
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


        return root;
    }

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
                param.put("server",getString(R.string.serverip));
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
            Log.d("Wifi ssid : ", wifiInfo.BSSID);
            if (wifiInfo.BSSID.equals(BSSID))
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // mapboxMap.setStyleUrl(Style.MAPBOX_STREETS);

        // Set the camera's starting position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(36.8984, 10.1897)) // set the camera's center position
                .zoom(9)  // set the camera's zoom level
                .tilt(20)  // set the camera's tilt
                .build();

        // Move the camera to that position
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        meMap = mapboxMap;


    }

    private void getDeviceLocation(){
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

                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

}
