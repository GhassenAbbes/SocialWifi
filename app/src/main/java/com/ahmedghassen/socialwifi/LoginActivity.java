package com.ahmedghassen.socialwifi;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Created by Ghassen on 14/11/2017.
 */

public class LoginActivity extends AppCompatActivity {


    private LoginButton loginButton;
    private CallbackManager callbackManager;


    private static final String TAG = "facebook_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else {
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {


                            String accessToken = loginResult.getAccessToken().getToken();
                            // save accessToken to SharedPreference
                            SharedPreferences prefs = getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("fb_access_token", accessToken);
                            editor.apply();
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject jsonObject,
                                                                GraphResponse response) {

                                            // Getting FB User Data
                                            Bundle facebookData = getFacebookData(jsonObject);

                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,email,gender");
                            request.setParameters(parameters);
                            request.executeAsync();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }


                        @Override
                        public void onCancel() {
                            Log.d(TAG, "Login attempt cancelled.");
                        }

                        @Override
                        public void onError(FacebookException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Login attempt failed.");
                            deleteAccessToken();
                        }
                    }
            );
        }

    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            SharedPreferences prefs = getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String id = object.getString("id");
            editor.putString("fb_id", id);
            Log.d("dssgsdg : ",id);
            URL profile_pic;
            try {

                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));

            editor.putString("fb_first_name", object.getString("first_name"));
            editor.putString("fb_last_name", object.getString("last_name"));
            editor.putString("fb_email", object.getString("email"));
            editor.putString("fb_gender", object.getString("gender"));
            editor.putString("fb_profileURL", profile_pic.toString());
            editor.apply(); // This line is IMPORTANT !!!
            Log.d("MyApp", "Acces Tokken : "+prefs.getString("fb_access_token",null)+"\n Shared Name : "+object.getString("first_name")+"\nLast Name : "+object.getString("last_name")+"\nEmail : "+object.getString("email")+"\nGender : "+object.getString("gender")+"\nProfile Pic : "+profile_pic.toString());


        } catch (Exception e) {
            Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }

        return bundle;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out
                    SharedPreferences prefs = getSharedPreferences("FacebookProfile", ContextWrapper.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

}