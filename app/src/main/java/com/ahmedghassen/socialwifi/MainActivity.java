package com.ahmedghassen.socialwifi;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        FragmentManager manager = getSupportFragmentManager();

        LocationsFragment pf = new LocationsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("container", R.id.content_frame);
        pf.setArguments(bundle);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.content_frame, pf, "init");
        transaction.addToBackStack("init");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);

        SharedPreferences pref = getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);;

        //Set Profile Name
        TextView nav_user = (TextView)hView.findViewById(R.id.profile_name);
        nav_user.setText(pref.getString("fb_first_name","Unknown")+ " " +pref.getString("fb_last_name","Unknown") );

        //Set Profile Email
        TextView nav_email = (TextView)hView.findViewById(R.id.profile_email);
        nav_email.setText(pref.getString("fb_email","Unknown") );

        //Set Profile Picture
        ImageView nav_pic = (ImageView) hView.findViewById(R.id.profile_pic);
        Picasso.with(this)
                .load(pref.getString("fb_profileURL",null))
                .into(nav_pic);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        android.support.v4.app.Fragment fav = null;
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            fragment = new ProfilFragment();
        } else if (id == R.id.nav_gallery) {
            fragment = new LocationsFragment();

        } else if (id == R.id.nav_slideshow) {
            fragment = new FavFragment();

        } else if (id == R.id.nav_manage) {
            fragment = new SettingsFragment();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();

            Bundle bundle2 = new Bundle();
            bundle2.putInt("container", R.id.content_frame);

            fragment.setArguments(bundle2);

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
}