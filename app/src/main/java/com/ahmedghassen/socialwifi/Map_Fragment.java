package com.ahmedghassen.socialwifi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


}
