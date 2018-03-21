package com.esprit.socialwifi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	public static final String TABLE_LOCATIONS = "locations";

	public static final String ID_LOC= "id_loc";
	public static final String SSID_LOC = "desc_loc";
	public static final String WIFI_PASS = "wifi_pass";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String IMG = "img";
	public static final String MAC = "mac";


	private static final String CREATE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + " ("+
								ID_LOC + " INTEGER, "+
								SSID_LOC + " TEXT, "+
								WIFI_PASS + " TEXT, "+
								LAT + " TEXT, "+
								LNG + " TEXT, "+
								IMG + " TEXT, "+
								MAC + " TEXT);";

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_LOCATIONS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS + ";");
		onCreate(db);
	}



}
