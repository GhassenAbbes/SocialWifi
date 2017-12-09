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

public class LocationsBDD  {
    private static final int VERSION_BDD = 1;
    private static final String NAME_BDD = "locations.db";

    private SQLiteDatabase bdd;

    private DBHelper dbHelper;

    public LocationsBDD(Context context) {
        super();
        dbHelper = new DBHelper(context, NAME_BDD, null, VERSION_BDD);
    }

    public void open(){
        bdd = dbHelper.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertTop(LocationWifi wifi){
        Cursor cursor = bdd.rawQuery("select max("+DBHelper.ID_LOC+") from "+DBHelper.TABLE_LOCATIONS, null);
        if (cursor.moveToFirst()) wifi.setId(cursor.getInt(0)+1);
        else wifi.setId(1);

        ContentValues values = new ContentValues();
        values.put(DBHelper.ID_LOC, wifi.getId());
        values.put(DBHelper.DESC_LOC, wifi.getDesc());
        values.put(DBHelper.WIFI_PASS, wifi.getWifi_pass());
        values.put(DBHelper.LAT, wifi.getLat());
        values.put(DBHelper.LNG, wifi.getLng());
        values.put(DBHelper.IMG, wifi.getImg());
        values.put(DBHelper.MAC, wifi.getMac());
        return bdd.insert(DBHelper.TABLE_LOCATIONS, null, values);
    }

    public int removeAllLocations(){
        return bdd.delete(DBHelper.TABLE_LOCATIONS,null,null);
    }
    public int removeLocation(int index){
        return bdd.delete(DBHelper.TABLE_LOCATIONS, "`"+DBHelper.ID_LOC+"`=?", new String[] {String.valueOf(index)});
    }

    public List<LocationWifi> selectAll() {
        List<LocationWifi> list = new ArrayList<LocationWifi>();
        Cursor cursor = this.bdd.query(DBHelper.TABLE_LOCATIONS,new String[] {"*"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                LocationWifi wifi = new LocationWifi();
                wifi.setId(cursor.getInt(0));
                wifi.setDesc(cursor.getString(1));
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


