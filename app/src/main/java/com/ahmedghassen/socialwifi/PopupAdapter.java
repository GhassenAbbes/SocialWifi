package com.ahmedghassen.socialwifi;

import android.annotation.SuppressLint;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

/**
 * Created by LENOVO-Z510 on 13/11/2017.
 */

public class PopupAdapter extends Application implements InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;

    PopupAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {

            if (popup == null) {
                popup = inflater.inflate(R.layout.popup, null);
            }



            TextView tv = (TextView) popup.findViewById(R.id.desc_loc);

            tv.setText("3asba");
            tv = (TextView) popup.findViewById(R.id.pw_loc);
            tv.setText("3asba");

        return(popup);
    }
}
