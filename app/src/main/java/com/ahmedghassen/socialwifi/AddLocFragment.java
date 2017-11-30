package com.ahmedghassen.socialwifi;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLocFragment extends Fragment {

    private Gson gson;
    private MapboxMap mapboxMap ;
    MapView mapFragment;
    ConnectionManager con;
    RequestQueue queue ;
    Marker marky=null;

    public AddLocFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_add_loc, null, false);
        EditText ssid = (EditText) root.findViewById(R.id.ssidadd);
        EditText pw = (EditText)root.findViewById(R.id.pwadd);
        Button ajouter = (Button)root.findViewById(R.id.ajouter);

        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();


        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.access_token));


        mapFragment = (MapView) root.findViewById(R.id.addmaplayout);
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

                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                             MarkerViewOptions mark = new MarkerViewOptions().position(point);
                            mapboxMap.addMarker(mark);
                            marky = new MarkerView(new MarkerViewOptions().position(point));
                    }
                });

                mapboxMap.setInfoWindowAdapter ((Marker marker) -> {
                    marker.setTitle("Marker Deleted!");
                    marky=null;
                    marker.remove();
                    return null;
                });
            }
        });

        ajouter.setOnClickListener(v -> {
            if ( TextUtils.isEmpty(ssid.getText())||TextUtils.isEmpty(pw.getText())||marky==null)
                Toast.makeText(getActivity(),"You must complete the missing fields!",Toast.LENGTH_LONG).show();
            else {
                con = new ConnectionManager("addloc");

                queue = Volley.newRequestQueue(getActivity().getApplicationContext());


                String s = con.getPath();
                String uri = s + String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s",
                        ssid.getText().toString(),
                        pw.getText().toString(),
                        Double.toString(marky.getPosition().getLatitude()),
                        Double.toString(marky.getPosition().getLongitude()));

                StringRequest myReq = new StringRequest(Request.Method.GET,
                        uri,
                        response -> {
                            Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG).show();
                        },
                        error -> {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();

                        });
                Log.d("requet", myReq.toString());

                queue.add(myReq);
                Log.d("requet", myReq.toString());

                LocationsFragment locfrag = new LocationsFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_frame, locfrag, "init");
                transaction.commit();
            }
        });

        return root;
    }

}
