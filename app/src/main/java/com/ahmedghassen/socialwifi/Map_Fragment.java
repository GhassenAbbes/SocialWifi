package com.ahmedghassen.socialwifi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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


        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        fetchLocations();
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();


        Mapbox.getInstance(getActivity().getApplicationContext(), getString(R.string.access_token));


        mapFragment = (MapView) root.findViewById(R.id.map);
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
                            con = new ConnectionManager("addloc");

                            queue = Volley.newRequestQueue(getActivity().getApplicationContext());




                            String s = con.getPath();
                            String uri = s+String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s",
                                    desc.getText().toString(),
                                    pw.getText().toString(),
                                    Double.toString(point.getLatitude()),
                                    Double.toString(point.getLongitude()));

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

                            dialog.dismiss();
                        });

                        dialog.show();
                    }
                });

                mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {

                        View v = null;
                        try {

                            // Getting view from the layout file info_window_layout
                            v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                            // Getting reference to the TextView to set latitude
                            TextView wifiTxt = (TextView) v.findViewById(R.id.titleWifi);
                            wifiTxt.setText(marker.getTitle());

                            TextView passTxt = (TextView) v.findViewById(R.id.passworWifi);
                            passTxt.setText(marker.getSnippet());

                        } catch (Exception ev) {
                            System.out.print(ev.getMessage());
                        }

                        return v;
                    }
                });


            }
        });

        return root;
    }



    @Override
    public boolean onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity().getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
        return false;
    }

    private void addMarker(MapboxMap map, double lat, double lon,
                           String title, String snippet) {
        map.addMarker(new MarkerViewOptions()
                .position(new LatLng(lat,lon))
                .title(title)
                .snippet(snippet)

        );
    }

    private void fetchLocations() {
        StringRequest request = new StringRequest(Request.Method.GET, con.getPath(), onPostsLoaded, onPostsError);
        queue.add(request);

    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            String ch=response;
            List<LocationWifi> listlocations = Arrays.asList(gson.fromJson(response, LocationWifi[].class));
            Log.i("PostActivity", listlocations.size() + " posts loaded.");
            Log.d("string ",ch);

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

                    for (LocationWifi loc : listlocations) {
                        LatLng sydney = new LatLng(Double.parseDouble(loc.getLat()), Double.parseDouble(loc.getLng()));
                        mapboxMap.addMarker(new MarkerViewOptions().position(sydney)
                                .title(loc.getDesc())
                                .snippet(loc.getWifi_pass()));
                    }

                    mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(@NonNull LatLng point) {
                            MarkerViewOptions mark = new MarkerViewOptions().position(point);
                            mapboxMap.addMarker(mark);
                            marky = new MarkerView(new MarkerViewOptions().position(point));

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
                                con = new ConnectionManager("addloc");

                                queue = Volley.newRequestQueue(getActivity().getApplicationContext());




                                String s = con.getPath();
                                String uri = s+String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s",
                                        desc.getText().toString(),
                                        pw.getText().toString(),
                                        Double.toString(point.getLatitude()),
                                        Double.toString(point.getLongitude()));

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

                                dialog.dismiss();
                            });

                            dialog.show();
                        }
                    });

                    mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(@NonNull Marker marker) {

                            View v = null;
                            try {

                                // Getting view from the layout file info_window_layout
                                v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                                // Getting reference to the TextView to set latitude
                                TextView wifiTxt = (TextView) v.findViewById(R.id.titleWifi);
                                wifiTxt.setText(marker.getTitle());

                                TextView passTxt = (TextView) v.findViewById(R.id.passworWifi);
                                passTxt.setText(marker.getSnippet());

                            } catch (Exception ev) {
                                System.out.print(ev.getMessage());
                            }

                            return v;
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
        Bitmap bm=null;
        if (data != null) {
            try {
                Uri uri = data.getData();
                Log.d(TAG, "File Uri: " + uri.toString());
                String path = uri.getPath();
                Log.d(TAG, "File Path: " + path);
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageLoc.setImageBitmap(bm);
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
    }




}
