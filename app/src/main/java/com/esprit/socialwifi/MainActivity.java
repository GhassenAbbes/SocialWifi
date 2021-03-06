package com.esprit.socialwifi;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static FirebaseStorage storage;
    public static StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FragmentManager manager = getSupportFragmentManager();

        Map_Fragment pf = new Map_Fragment();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.content_frame, pf, "init");
        transaction.addToBackStack("init");
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);

        SharedPreferences prefs = getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);;
        /*SuperActivityToast.create(this, "Hello world!", SuperToast.Duration.LONG,
                Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();*/
        Log.d("MainActivity", "Acces Tokken : "+prefs.getString("fb_access_token",null)+
                "\n Shared Name : "+prefs.getString("fb_first_name",null)+
                "\nLast Name : "+prefs.getString("fb_last_name",null)+
                "\nEmail : "+prefs.getString("fb_email",null)+
                "\nGender : "+prefs.getString("fb_gender",null)+
                "\nProfile Pic : "+prefs.getString("fb_profileURL",null));

        //Set Profile Name
        TextView nav_user = hView.findViewById(R.id.profile_name);
        nav_user.setText(prefs.getString("fb_first_name","Unknown")+ " " +prefs.getString("fb_last_name","Unknown") );

        //Set Profile Email
        TextView nav_email = hView.findViewById(R.id.profile_email);
        nav_email.setText(prefs.getString("fb_email","Unknown") );

        //Set Profile Picture
        ImageView nav_pic =  hView.findViewById(R.id.profile_pic);
        Picasso.with(this)
                .load(prefs.getString("fb_profileURL",null))
                .into(nav_pic);


    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

         if (id == R.id.nav_gallery) {
            fragment = new Map_Fragment();

        } else if (id == R.id.nav_slideshow) {
             fragment = new FavouritesFragment();

         }
         else if (id == R.id.local) {
             fragment = new NearByFragment();
         }else if (id == R.id.nav_send) {
            LoginManager.getInstance().logOut();
            SharedPreferences pref = getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();



            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content_frame, fragment, "SC");
            transaction.commit();

        }
        else{
            Log.e("MainActivity", "Error in creating fragment");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*@Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
*/


}
