package com.ahmedghassen.socialwifi;


import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailLocFragment extends Fragment {

    private Gson gson;
    private MapboxMap mapboxMap ;
    MapView mapFragment;
    ConnectionManager con;
    RequestQueue queue ;
    Marker marky=null;
    LocationWifi p;
    public DetailLocFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_detail_loc, null, false);

         p = new LocationWifi();
        Bundle bundle = this.getArguments();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        String jsonMyObject = "";

        if (bundle != null) {
            jsonMyObject = bundle.getString("myObject");
        }
        p= new Gson().fromJson(jsonMyObject, LocationWifi.class);



        TextView ssid = (TextView) root.findViewById(R.id.ssiddet);
        TextView pw = (TextView)root.findViewById(R.id.pwdet);
        FloatingActionButton fab = (FloatingActionButton)root.findViewById(R.id.fabdet);

        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");

        ssid.setText(p.getDesc());
        pw.setText(p.getWifi_pass());

        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.access_token));


        mapFragment = (MapView) root.findViewById(R.id.detmaplayout);
        mapFragment.onCreate(savedInstanceState);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mapboxMap.setStyleUrl(Style.MAPBOX_STREETS);

                // Set the camera's starting position
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(p.getLng()), Double.parseDouble(p.getLng()))) // set the camera's center position
                        .zoom(9)  // set the camera's zoom level
                        .tilt(20)  // set the camera's tilt
                        .build();

                // Move the camera to that position
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                MarkerViewOptions mark = new MarkerViewOptions().position(new LatLng(Double.parseDouble(p.getLng()), Double.parseDouble(p.getLng())));
                mapboxMap.addMarker(mark);

            }
        });

        fab.setOnClickListener(v -> {
                addToFavourite();
                LocationsFragment locfrag = new LocationsFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_frame, locfrag, "init");
                transaction.commit();

        });

        return root;
    }


    private void addToFavourite(){

        con = new ConnectionManager("addfavourite");

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        SharedPreferences prefs = getActivity().getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String id_user = prefs.getString("fb_id",null);
        Log.d("id_user",id_user);
        String s = con.getPath();
        String uri = s+String.format("&id_user=%1$s&id_loc=%2$s",
                id_user,p.getId());

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

}
