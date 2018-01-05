package com.ahmedghassen.socialwifi;

import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SettingsFragment extends Fragment {
    ConnectionManager con;
    RequestQueue queue ;

     ListView list;
    FavouritesBDD locBDD;
    private ListViewAdapter mAdapter;
    private Context mContext = getContext();
    GsonBuilder gsonBuilder = new GsonBuilder();
    private Gson gson = gsonBuilder.create();
    ArrayList<Wifi> listlocations2 ;
    List<LocationWifi> listlocations;

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
            android.support.v7.app.ActionBar actionBar =((AppCompatActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle("Favourites Wifi");
        }

        SharedPreferences prefs = getActivity().getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String id_user = prefs.getString("fb_id",null);
        Log.d("id_user",id_user);

        con = new ConnectionManager("selectfav");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());




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
            Toast.makeText(getContext(), "No Network Available", Toast.LENGTH_LONG).show();

            locBDD = new FavouritesBDD(getActivity().getApplicationContext());
            locBDD.open();
            listlocations = locBDD.selectAll();
            locBDD.close();

            listlocations2 = new ArrayList<Wifi>();
            for (int i =0 ; i<listlocations.size();i++){
                LocationWifi test = listlocations.get(i);

                Log.d("test",test.toString());
                Wifi Stringtest = new Wifi(Integer.toString(test.getId()),test.getSsid(),test.getWifi_pass(),test.getLat(),test.getLng(),test.getImg(),test.getMac());
                listlocations2.add(Stringtest);
            }
/*
            ArrayList<LocationWifi> plist = new ArrayList<>(listlocations);
*/
            Log.d("offline list favs",listlocations.toString());


            /*SuperActivityToast.create(getActivity(), new Style(), Style.TYPE_BUTTON)
                    .setButtonText("UNDO")

                    .setProgressBarColor(Color.WHITE)
                    .setText("Email deleted")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                    .setAnimations(Style.ANIMATIONS_POP).show();
*/
            mAdapter = new ListViewAdapter(getContext(),listlocations2,getActivity().getSupportFragmentManager());
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
                    SuperToast.create(getActivity().getApplication().getApplicationContext(), "Hello world!", SuperToast.Duration.LONG,
                            Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();
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
            Type listType = new TypeToken<List<LocationWifi>>(){}.getType();
            List<LocationWifi> locations = new Gson().fromJson(ch, listType);
            listlocations2 = new ArrayList<Wifi>();

            int [] tab = new int[locations.size()];
            JSONArray jsonArray = new JSONArray();
            JSONObject objJson = new JSONObject();
            try {
                jsonArray = new JSONArray(ch);

                for (int i = 0; i < jsonArray.length(); i++) {

                    objJson = jsonArray.getJSONObject(i);

                    // here you can get id,name,city...
                    int id = objJson.getInt("id_loc");
                    tab[i]=id;

                    LocationWifi test = locations.get(i);

                    Log.d("test",test.toString());
                    Wifi Stringtest = new Wifi(Integer.toString(test.getId()),test.getSsid(),test.getWifi_pass(),test.getLat(),test.getLng(),test.getImg(),test.getMac());
                    listlocations2.add(Stringtest);
                }

                listlocations = locations;
                int i =0;

                for (LocationWifi l : listlocations){
                    l.setId(tab[i]);
                    i++;
                }
                i=0;
                for (Wifi l : listlocations2){
                    l.setId_loc(Integer.toString(tab[i]));
                    i++;
                }
                Log.d("tab",tab.toString());
                Log.d("test",listlocations.toString());






            //listlocations = locations;






            mAdapter = new ListViewAdapter(getContext(),listlocations2,getActivity().getSupportFragmentManager());
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    //Toast.makeText(getContext(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                    /*SuperToast(getActivity())
                            .setText("SuperToast")
                            .setDuration(AttributeUtils.getDuration(getActivity()))
                            .setFrame(AttributeUtils.getFrame(getActivity()))
                            .setColor(AttributeUtils.getColor(getActivity()))
                            .setAnimations(AttributeUtils.getAnimations(getActivity()))
                            .setColor(AttributeUtils.getColor(getActivity())).show();
*/

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

            locBDD = new FavouritesBDD(getActivity().getApplicationContext());

            locBDD.open();
            locBDD.removeAllLocations();
            for ( LocationWifi l : listlocations) {
                locBDD.insertTop(l);
            }
            locBDD.close();
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
