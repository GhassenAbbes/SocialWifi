package com.ahmedghassen.socialwifi.Adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.ahmedghassen.socialwifi.ConnectionManager;
import com.ahmedghassen.socialwifi.FavouriteMapFragment;
import com.ahmedghassen.socialwifi.LocationWifi;
import com.ahmedghassen.socialwifi.R;
import com.ahmedghassen.socialwifi.FavouritesFragment;
import com.ahmedghassen.socialwifi.Wifi;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ListViewAdapter extends BaseSwipeAdapter {
    private List<Wifi> favourites ;
    private Context mContext;
    private FragmentManager manager;
    private ConnectionManager con;
    private RequestQueue queue ;
    LocationWifi p ;

    public ListViewAdapter(Context mContext, List<Wifi> favourites, FragmentManager manager) {
        this.mContext = mContext;
        this.favourites = favourites;
        this.manager = manager;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.eye));
            }
        });


        Log.d("position",favourites.toString());



        swipeLayout.setOnDoubleClickListener((layout, surface) -> Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show());
        v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
                DelFavourite(favourites.get(position).getId_loc());
                v.refreshDrawableState();
            }
        });

        v.findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "click open", Toast.LENGTH_SHORT).show();



                Wifi test = favourites.get(position);
                p = new LocationWifi(Integer.parseInt(test.getId_loc()),test.getSsid(),test.getWifi_pass(),test.getLat(),test.getLng(),test.getImg(),test.getMac());

                Fragment fragment = new FavouriteMapFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString("myObject", new Gson().toJson(p));

                fragment.setArguments(bundle2);

                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment, "SC");
                transaction.addToBackStack("fav");
                transaction.commit();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView t = (TextView)convertView.findViewById(R.id.position);
        t.setText((position + 1) + ".");
        Wifi p =favourites.get(position);

        TextView tvdesc = (TextView) convertView.findViewById(R.id.favdec_s);
        TextView tvpw = (TextView)convertView.findViewById(R.id.favpw_s);

        ImageView tvHome = (ImageView) convertView.findViewById(R.id.favimg_s);

        // Populate the data into the template view using the data object
        tvdesc.setText(p.getSsid());
        //tvHome.setImageResource(p.imageressource);
        tvpw.setText(p.getWifi_pass());
        Picasso.with(mContext)
                .load(p.getImg())
                .into(tvHome);
    }

    @Override
    public int getCount() {
        return favourites.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private void DelFavourite(String id){
        SharedPreferences prefs = mContext.getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String id_user = prefs.getString("fb_id",null);
        con = new ConnectionManager("delfavourite");

        queue = Volley.newRequestQueue(mContext);


        String s = con.getPath();
        String uri = s+String.format("&id_user=%1$s&id_loc=%2$s",
                id_user,
                id);

        StringRequest myReq = new StringRequest(Request.Method.GET,
                uri,
                response -> {
                    Toast.makeText(mContext,"Successful",Toast.LENGTH_LONG).show();
                },
                error -> {
                    Toast.makeText(mContext,"Failed",Toast.LENGTH_LONG).show();

                });
        Log.d("requet",myReq.toString());

        queue.add(myReq);
        Log.d("requet",myReq.toString());


        Fragment frag = new FavouritesFragment();


        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_frame, frag, "dtv");
        transaction.addToBackStack("fav");
        transaction.commit();
    }
}
