package com.example.akshaypall.bitdate;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SignInActivity extends ActionBarActivity implements View.OnTouchListener, SpringListener {
    private static final String TAG = "SignInActivity";

    private ProgressBar mLoadingSpinner;
    private ImageView mLogoView;

    //setting up the springsystem for animations and its attributes
    private static double TENSION = 800;
    private static double DAMPER = 20; //friction
    private SpringSystem mSpringSystem;
    private Spring mLogoSpring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);

        Button fbLogin = (Button) findViewById(R.id.facebook_login_button);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingSpinner.setVisibility(View.VISIBLE);
                List<String> permissions = new ArrayList<String>();
                permissions.add("user_birthday");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(SignInActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        mLoadingSpinner.setVisibility(View.GONE);
                        if (e != null) {
                            //login with facebook failed
                            Log.d(TAG, "Failed to login user");
                        } else if (parseUser.isNew()) {
                            //new user successfully created and logged in
                            getFacebookInfo();
                        } else {
                            //existing user successfully logged in
                            Log.d(TAG, "Login Successful: Existing user logged in");
                            finish();
                        }
                    }
                });
            }
        });

        //setting up springs to animate movement of the MindBend logo
        mLogoView = (ImageView)findViewById(R.id.logo);
        mLogoView.setOnTouchListener(this);

        mSpringSystem = SpringSystem.create();

        mLogoSpring = mSpringSystem.createSpring();
        mLogoSpring.addListener(this);

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mLogoSpring.setSpringConfig(config);

        mLogoSpring.setEndValue(0f);
    }

    private void getFacebookInfo() {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "picture,first_name,id");
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        //when the facebook user object is loaded
                        JSONObject user = graphResponse.getJSONObject();
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        currentUser.put("firstName", user.optString("first_name"));
                        currentUser.put("facebookId", user.optString("id"));
                        currentUser.put("pictureURL", user.optJSONObject("picture")
                                .optJSONObject("data")
                                .optString("url"));
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "New User created and logged in");
                                    finish();
                                }
                            }
                        });
                    }
                }).executeAsync();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLogoSpring.setEndValue(1f);
                break;
            case MotionEvent.ACTION_UP:
                mLogoSpring.setEndValue(0f);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    @Override
    public void onSpringUpdate(Spring spring) {
        float value = (float) spring.getCurrentValue();
        float scale = 1f - (value * 0.5f);
        mLogoView.setScaleX(scale);
        mLogoView.setScaleY(scale);
    }

    @Override
    public void onSpringAtRest(Spring spring) {

    }

    @Override
    public void onSpringActivate(Spring spring) {

    }

    @Override
    public void onSpringEndStateChange(Spring spring) {

    }

    @Override
    public void onBackPressed() {
//        don't allow users access back to cards
//        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
