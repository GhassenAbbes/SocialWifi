package com.ahmedghassen.socialwifi;

import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.android.gms.*;

/**
 * Created by Ghassen on 14/11/2017.
 */

public class SharePermActivity extends Fragment {


    private LoginButton loginButton;
    private static CallbackManager callbackManager;
    public static LoginManager loginManager;

    private static final String TAG = "facebook_login";

    public SharePermActivity (){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.activity_share_perm, null, false);

        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        loginButton = root.findViewById(R.id.facebook_login2);
            loginButton.setPublishPermissions("publish_actions");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {



                        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                        String accessToken = loginResult.getAccessToken().getToken();
                        // save accessToken to SharedPreference
                        SharedPreferences prefs = getActivity().getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("share", "share");
                        editor.apply();
                        //LoginActivity.presmissions.add("publish_actions");
                        getActivity().getSupportFragmentManager().popBackStack( FragmentManager.POP_BACK_STACK_INCLUSIVE,0);

                    }


                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Login attempt cancelled.");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Login attempt failed.");
                        Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                }
        );
        return root;
    }






    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }



}