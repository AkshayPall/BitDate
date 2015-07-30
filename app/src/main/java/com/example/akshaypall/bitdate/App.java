package com.example.akshaypall.bitdate;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.firebase.client.Firebase;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Created by Akshay Pall on 19/07/2015.
 */
public class App extends Application {
    private static final String APP = "App Started";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "VgiTaHXxhXzWtBwsFeDHuFXmcGc9hVSAn1QFEdU7", "myY8xl5dtMZtJPxVj6mGjHJUdUlfAxmeDoeR1zj9");
        Log.d(APP, "Parse initialized");

        ParseFacebookUtils.initialize(this);
        Log.d(APP, "Parse Facebook Utils initialized");
    }
}
