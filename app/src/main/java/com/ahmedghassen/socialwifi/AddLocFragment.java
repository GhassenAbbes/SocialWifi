package com.ahmedghassen.socialwifi;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddLocFragment extends Fragment {
    private ImageView imageLoc;
    private int GALLERY = 1, CAMERA = 2;
    EditText ssid;

    String myurl = "http://192.168.141.1/AndroidUploadImage/uploadImage.php";
    String imagePath="null";
    private static final String TAG = "LocationPickerActivity";

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
        ssid = (EditText) root.findViewById(R.id.ssidadd);
        EditText pw = (EditText)root.findViewById(R.id.pwadd);
        Button ajouter = (Button)root.findViewById(R.id.ajouter);
        imageLoc = (ImageView) root.findViewById(R.id.addlocimage);
        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();



        imageLoc.setOnClickListener(v -> showPictureDialog());
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

                Log.d("Image Path ", imagePath);
                String s = con.getPath();
                String uri = s + String.format("&desc=%1$s&pw=%2$s&lat=%3$s&lng=%4$s&img=%5$s",
                        ssid.getText().toString(),
                        pw.getText().toString(),
                        Double.toString(marky.getPosition().getLatitude()),
                        Double.toString(marky.getPosition().getLongitude()),
                        imagePath
                );



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
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                String images = getStringImage(bitmap);
                Log.i("Mynewsam",""+images);
                param.put("image",images);
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

}
