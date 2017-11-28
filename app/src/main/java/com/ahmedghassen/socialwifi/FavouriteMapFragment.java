package com.ahmedghassen.socialwifi;


import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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


public class FavouriteMapFragment extends Fragment {
    ConnectionManager con;
    LocationWifi p;
    private Gson gson;
    RequestQueue queue ;
    //SupportMapFragment mapFragment;
    MapView mapFragment;

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

}
