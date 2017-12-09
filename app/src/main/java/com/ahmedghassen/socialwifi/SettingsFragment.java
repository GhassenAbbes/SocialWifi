package com.ahmedghassen.socialwifi;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingsFragment extends Fragment {
    ConnectionManager con;
    RequestQueue queue ;
    private Gson gson;
     ListView list;
     int  frame;
    List<LocationWifi> listlocations;
    LocationsBDD locBDD;


    public SettingsFragment() {
        // Required empty public constructor
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ;

        list = (ListView) view.findViewById(R.id.flistfav);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            frame = bundle.getInt("container");
        }


        con = new ConnectionManager("selectfav");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());


        SharedPreferences prefs = getActivity().getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        String id_user = prefs.getString("fb_id", null);
        String s = con.getPath();
        String uri = s + String.format("&id_user=%1$s",
                id_user);
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        Log.d("uri_fav", uri);

        if (isNetworkAvailable()) {
            fetchLocations(uri);
        } else {
            locBDD = new LocationsBDD(getActivity().getApplicationContext());
            locBDD.open();
            listlocations = locBDD.selectAll();
            ArrayList<LocationWifi> plist = new ArrayList<>(listlocations);
            FavouriteAdapter adapter = new FavouriteAdapter(getContext(), plist);
            list.setAdapter(adapter);
            list.setClickable(true);
            list.setOnItemClickListener((parent, view1, position, id) -> {
                Object o = list.getItemAtPosition(position);
                view1.setBackgroundColor(Color.DKGRAY);

                LocationWifi ch = (LocationWifi)o;
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Fragment fragment = new FavouriteMapFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("myObject", new Gson().toJson(ch));

                fragment.setArguments(bundle2);

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(frame, fragment, "SC");
                transaction.addToBackStack("fav");
                transaction.commit();
                Log.d("favourite",ch.toString());

            });
        }

        return view;
    }

    private void fetchLocations(String ss) {
        StringRequest request = new StringRequest(Request.Method.GET, ss, onPostsLoaded, onPostsError);
        queue.add(request);

    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //String ch=response;
            listlocations = Arrays.asList(gson.fromJson(response, LocationWifi[].class));
            /*Log.i("PostActivity", listlocations.size() + " posts loaded.");
            Log.d("string ",ch);*/
            locBDD = new LocationsBDD(getActivity().getApplicationContext());
            locBDD.open();
            locBDD.removeAllLocations();
            for ( LocationWifi l : listlocations) {
                locBDD.insertTop(l);
            }
            locBDD.close();

            ArrayList<LocationWifi> plist = new ArrayList<>(listlocations);
            FavouriteAdapter adapter = new FavouriteAdapter(getContext(), plist);
            list.setAdapter(adapter);
            list.setClickable(true);
            list.setOnItemClickListener(new  AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object o = list.getItemAtPosition(position);
                    view.setBackgroundColor(Color.DKGRAY);

                    LocationWifi ch = (LocationWifi)o;
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new FavouriteMapFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("myObject", new Gson().toJson(ch));

                    fragment.setArguments(bundle2);

                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(frame, fragment, "SC");
                    transaction.addToBackStack("fav");
                    transaction.commit();
                    Log.d("favourite",ch.toString());

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
