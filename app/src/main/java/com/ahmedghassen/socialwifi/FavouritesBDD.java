package com.ahmedghassen.socialwifi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ESPRIT on 09/12/2017.
 */

public class FavouritesBDD {
    private static final int VERSION_BDD = 2;
    private static final String NAME_BDD = "favourites.db";

    private SQLiteDatabase bdd;
    Context context;
    private FavDBHelper favDbHelper;

    public FavouritesBDD(Context context) {
        super();
        favDbHelper = new FavDBHelper(context, NAME_BDD, null, VERSION_BDD);
    }


    public void open(){
        bdd = favDbHelper.getWritableDatabase();
    }
    public void close(){
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertTop(LocationWifi wifi){
        Cursor cursor = bdd.rawQuery("select max("+ FavDBHelper.ID_LOC+") from "+ FavDBHelper.TABLE_LOCATIONS, null);
        if (cursor.moveToFirst()) wifi.setId((cursor.getInt(0)+1));
        else wifi.setId(1);

        ContentValues values = new ContentValues();
        values.put(FavDBHelper.ID_LOC, wifi.getId());
        values.put(FavDBHelper.SSID_LOC, wifi.getSsid());
        values.put(FavDBHelper.WIFI_PASS, wifi.getWifi_pass());
        values.put(FavDBHelper.LAT, wifi.getLat());
        values.put(FavDBHelper.LNG, wifi.getLng());
        values.put(FavDBHelper.IMG, wifi.getImg());
        values.put(FavDBHelper.MAC, wifi.getMac());
        return bdd.insert(FavDBHelper.TABLE_LOCATIONS, null, values);
    }

    public int removeAllLocations(){
        return bdd.delete(FavDBHelper.TABLE_LOCATIONS,null,null);
    }
    public int removeLocation(int index){
        return bdd.delete(FavDBHelper.TABLE_LOCATIONS, "`"+ FavDBHelper.ID_LOC+"`=?", new String[] {String.valueOf(index)});
    }

    public ArrayList<LocationWifi> selectAll() {
        ArrayList<LocationWifi> list = new ArrayList<LocationWifi>();
        Cursor cursor = this.bdd.query(FavDBHelper.TABLE_LOCATIONS,new String[] {"*"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                LocationWifi wifi = new LocationWifi();
                wifi.setId(cursor.getInt(0));
                wifi.setSsid(cursor.getString(1));
                wifi.setWifi_pass(cursor.getString(2));
                wifi.setLat(cursor.getString(3));
                wifi.setLng(cursor.getString(4));
                wifi.setImg(cursor.getString(5));
                wifi.setMac(cursor.getString(6));
                list.add(wifi);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }



}


