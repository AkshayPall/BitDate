package com.example.akshaypall.bitdate;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseUser;


public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    //fragments kept here to handle lifecycle
    ChoosingFragment mChoosingFragment;
    MatchesFragment mMatchesFragment;
    private ImageView mChoosingImage;
    private ImageView mMatchesImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (UserDataSource.getCurrentUser() == null){
            Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
        }

//        if (savedInstanceState == null)
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new ChoosingFragment())
//                    .commit();
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(this);

        mChoosingImage = (ImageView)findViewById(R.id.title_icon);
        mMatchesImage = (ImageView)findViewById(R.id.chat_icon);
        mChoosingImage.setSelected(true);
        toggleColour(mChoosingImage);
        toggleColour(mMatchesImage);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mChoosingImage.setSelected(!mChoosingImage.isSelected()); //this causes the image to switch from F to T and vice versa
        mMatchesImage.setSelected(!mMatchesImage.isSelected());
        toggleColour(mChoosingImage);
        toggleColour(mMatchesImage);
    }

    private void toggleColour(ImageView v) {
        if (v.isSelected()){
            v.setColorFilter(Color.WHITE);
        } else {
            v.setColorFilter(getResources().getColor(R.color.primary_pink_dark));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (mChoosingFragment == null) mChoosingFragment = new ChoosingFragment();
                    return mChoosingFragment;
                case 1:
                    if (mMatchesFragment == null) mMatchesFragment = new MatchesFragment();
                    return mMatchesFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
//            to return the number of total pages swipable, ****CURRENTLY 2
            return 2;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
