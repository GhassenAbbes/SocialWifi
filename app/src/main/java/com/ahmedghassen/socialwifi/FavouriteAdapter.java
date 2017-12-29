package com.ahmedghassen.socialwifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by LENOVO-Z510 on 28/11/2017.
 */

public class FavouriteAdapter extends ArrayAdapter<LocationWifi> {

    public FavouriteAdapter(Context context,   ArrayList<LocationWifi> favourites) {
        super(context, 0, favourites);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LocationWifi p = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
        }
        // Lookup view for data population
        TextView tvdesc = (TextView) convertView.findViewById(R.id.favdec);
        TextView tvpw = (TextView) convertView.findViewById(R.id.favpw);

        ImageView tvHome = (ImageView) convertView.findViewById(R.id.favimg);

        // Populate the data into the template view using the data object
        tvdesc.setText(p.getSsid());
        //tvHome.setImageResource(p.imageressource);
        tvpw.setText(p.getWifi_pass());
        // Return the completed view to render on screen

        return convertView;
    }
}