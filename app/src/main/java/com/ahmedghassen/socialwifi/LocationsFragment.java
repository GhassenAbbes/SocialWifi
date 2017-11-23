package com.ahmedghassen.socialwifi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;


public class LocationsFragment extends Fragment {
    ConnectionManager con;
    private Gson gson;

    RequestQueue queue ;
    public LocationsFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_locations, container, false);
        //FloatingActionButton fab = (FloatingActionButton) (view).findViewById(R.id.fab);


        con = new ConnectionManager("selectloc");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        fetchLocations();
        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();
        return view;
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
            FragmentManager manager = getActivity().getSupportFragmentManager();

            Map_Fragment m = new Map_Fragment();


            Bundle bundle2 = new Bundle();
            bundle2.putString("locations", ch);
            m.setArguments(bundle2);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.maploc, m, "SC");
            transaction.commit();


           /* for (Location loc : listlocations) {
                Log.i("PostActivity", loc.getLat() + ": " + loc.getLng());
            }*/
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };





    public void onDestroy(){
        super.onDestroy();
    }
}
