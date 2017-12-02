package com.ahmedghassen.socialwifi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
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
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMarkerClickListener;

import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
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

import static android.view.Gravity.*;


public class Map_Fragment extends Fragment implements OnInfoWindowClickListener
{

    private ImageView imageLoc;
    private int GALLERY = 1, CAMERA = 2;

    private static final String TAG = "LocationPickerActivity";

    ArrayList<LocationWifi> listlocations = new ArrayList<LocationWifi>();
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
    String idloc="";
    String imgloc="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_map_, null, false);

        /*Bundle bundle = this.getArguments();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        String jsonMyObject = "";
        if (bundle != null) {
            jsonMyObject = bundle.getString("locations");
            Log.d("bundle", jsonMyObject);
            List<LocationWifi> alist = Arrays.asList(gson.fromJson(jsonMyObject, LocationWifi[].class));
            listlocations = new ArrayList<LocationWifi>(alist);
        }*/

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fabadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

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
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
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

   /* private void addMarker(MapboxMap map, double lat, double lon,
                           String title, String snippet) {
        map.addMarker(new MarkerViewOptions()
                .position(new LatLng(lat,lon))
                .title(title)
                .snippet(snippet)

        );
    }*/

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
                    if (!ch.equals("[]")) {

                        List<LocationWifi> listlocations = Arrays.asList(gson.fromJson(response, LocationWifi[].class));
                        Log.i("PostActivity", listlocations.size() + " posts loaded.");
                        Log.d("string ",ch);
                        for (LocationWifi loc : listlocations) {
                            LatLng sydney = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLng()));
                            mapboxMap.addMarker(new MarkerViewOptions().position(sydney)
                                    .title(loc.getDesc() + "/" + loc.getId() + "+" + loc.getImg())
                                    .snippet(loc.getWifi_pass())
                                    );
                        }
                    }
                    else {
                        Toast.makeText(getActivity(),"No WIFIS available :(",Toast.LENGTH_LONG).show();

                    }

                   /* mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                           *//* MarkerViewOptions mark = new MarkerViewOptions().position(point);
                            mapboxMap.addMarker(mark);
                            marky = new MarkerView(new MarkerViewOptions().position(point));
*//*
                            dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.popupadd);
                            dialog.setTitle("Add Your WIFI Access Point");

                            Log.d("marker", "click");
                            // set the custom dialog components - text, image and button
                            EditText desc = (EditText) dialog.findViewById(R.id.desc_loc_add);
                            EditText pw = (EditText) dialog.findViewById(R.id.pw_loc_add);
                            imageLoc = (ImageView) dialog.findViewById(R.id.pic_locc);

                            imageLoc.setOnClickListener(v -> showPictureDialog());


                            Button dialogButton = (Button) dialog.findViewById(R.id.add_loc);


                            // if button is clicked, close the custom dialog
                            dialogButton.setOnClickListener(v -> {


                                if ( TextUtils.isEmpty(desc.getText())||TextUtils.isEmpty(pw.getText()))
                                    Toast.makeText(getActivity(),"The description and password can not be empty!",Toast.LENGTH_LONG).show();
                                else {
                                    MarkerViewOptions mark = new MarkerViewOptions().position(point) .title(desc.getText().toString())
                                            .snippet(pw.getText().toString());
                                    mapboxMap.addMarker(mark);

                                    marky = new MarkerView(new MarkerViewOptions().position(point));

                                    con = new ConnectionManager("addloc");

                                    queue = Volley.newRequestQueue(getActivity().getApplicationContext());


                                    String s = con.getPath();
                                    String uri = s + String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s",
                                            desc.getText().toString(),
                                            pw.getText().toString(),
                                            Double.toString(point.getLatitude()),
                                            Double.toString(point.getLongitude()));

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

                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });*/

                    mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public View getInfoWindow(@NonNull Marker marker) {

                            View popup = null;
                            String ch="";
                            String d="";
                            if (marker.getTitle().contains("/")) {
                                ch = marker.getTitle();
                                d = ch.substring(0, ch.indexOf("/"));
                                idloc = ch.substring(ch.indexOf("/") + 1, ch.indexOf("+")-1);
                                imgloc = ch.substring(ch.indexOf("+") + 1, ch.length());
                                Log.d("Strings", d + "   " + idloc);
                            }
                            else {
                                d=marker.getTitle();
                            }

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
                                    LocationWifi loca = new LocationWifi();
                                    loca.setId(Integer.parseInt(idloc));
                                    loca.setDesc(marker.getTitle());
                                    loca.setWifi_pass(marker.getSnippet());
                                    loca.setLat(String.valueOf(marker.getPosition().getLatitude()));
                                    loca.setLng(String.valueOf(marker.getPosition().getLongitude()));
                                    loca.setImg(imgloc);

                                    //addToFavourite(idloc);
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

                                ImageView imgWifi = (ImageView)popup.findViewById(R.id.clientPic);
                                Picasso.with(getActivity())
                                        .load(imgloc)
                                        .into(imgWifi);

                            } catch (Exception ev) {
                                System.out.print(ev.getMessage());
                            }


                           // popup.setClickable(true);
                           /* LinearLayout popuplayout = (LinearLayout)popup.findViewById(R.id.popupid);
                            popuplayout.setOnClickListener(v ->
                                    Toast.makeText(getActivity(), "Popup clicked", Toast.LENGTH_LONG).show());*/

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
