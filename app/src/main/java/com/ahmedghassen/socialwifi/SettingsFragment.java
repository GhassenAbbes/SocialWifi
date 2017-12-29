package com.ahmedghassen.socialwifi;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmedghassen.socialwifi.Adapters.ListViewAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingsFragment extends Fragment {
    ConnectionManager con;
    RequestQueue queue ;

     ListView list;
    List<LocationWifi> listlocations;
    LocationsBDD locBDD;
    private ListViewAdapter mAdapter;
    private Context mContext = getContext();
    GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson = gsonBuilder.create();

    public SettingsFragment() {
        // Required empty public constructor
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ;

        list = view.findViewById(R.id.flistfav);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Favourites");
            }
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

            /*list.setAdapter(adapter);
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
                transaction.replace(R.id.content_frame, fragment, "SC");
                transaction.addToBackStack("fav");
                transaction.commit();
                Log.d("favourite",ch.toString());

            });*/

            mAdapter = new ListViewAdapter(getContext(),listlocations,getActivity().getSupportFragmentManager());
            list.setAdapter(mAdapter);
            mAdapter.setMode(Attributes.Mode.Single);
            list.setOnItemClickListener((parent, view1, position, id) -> ((SwipeLayout)(list.getChildAt(position - list.getFirstVisiblePosition()))).open(true));
            list.setOnTouchListener((v, event) -> {
                Log.e("ListView", "OnTouch");
                return false;
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            list.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    Log.e("ListView", "onScrollStateChanged");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("ListView", "onItemSelected:" + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.e("ListView", "onNothingSelected:");
                }
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
             String ch=response;
            // mMapView.onResume();
            listlocations = Arrays.asList(gson.fromJson(ch, LocationWifi[].class));

            locBDD = new LocationsBDD(getActivity().getApplicationContext());
            locBDD.open();
            locBDD.removeAllLocations();
            for ( LocationWifi l : listlocations) {
                locBDD.insertTop(l);
            }
            locBDD.close();


            Log.d("string fev ",ch);

            Log.d("list fev ",listlocations.toString());

            /*list.setAdapter(adapter);
            list.setClickable(true);
            list.setOnItemClickListener((parent, view, position, id) -> {
                Object o = list.getItemAtPosition(position);
                view.setBackgroundColor(Color.DKGRAY);

                LocationWifi ch = (LocationWifi)o;
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Fragment fragment = new FavouriteMapFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("myObject", new Gson().toJson(ch));

                fragment.setArguments(bundle2);

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment, "SC");
                transaction.addToBackStack("fav");
                transaction.commit();
                Log.d("favourite",ch.toString());

            });*/

            mAdapter = new ListViewAdapter(getContext(),listlocations,getActivity().getSupportFragmentManager());
            list.setAdapter(mAdapter);
            mAdapter.setMode(Attributes.Mode.Single);
            list.setOnItemClickListener((parent, view1, position, id) -> ((SwipeLayout)(list.getChildAt(position - list.getFirstVisiblePosition()))).open(true));
            list.setOnTouchListener((v, event) -> {
                Log.e("ListView", "OnTouch");
                return false;
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            list.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    Log.e("ListView", "onScrollStateChanged");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("ListView", "onItemSelected:" + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.e("ListView", "onNothingSelected:");
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
        boolean test = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        Log.d("CONNECTION TEST ",Boolean.toString(test));
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
