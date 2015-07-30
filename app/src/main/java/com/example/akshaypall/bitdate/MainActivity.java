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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    private static final int SIGNIN_REQUESTCODE = 10;
    //fragments kept here to handle lifecycle
//    ChoosingFragment mChoosingFragment;
//    MatchesFragment mMatchesFragment;
    private ImageView mChoosingImage;
    private ImageView mMatchesImage;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState == null)
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new ChoosingFragment())
//                    .commit();
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        mChoosingImage = (ImageView)findViewById(R.id.title_icon);
        mChoosingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mMatchesImage = (ImageView)findViewById(R.id.chat_icon);
        mMatchesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        mChoosingImage.setSelected(true);
        toggleColour(mChoosingImage);
        toggleColour(mMatchesImage);

//        creating and setting up the drawerlayout
        DrawerLayout drawerLayout= (DrawerLayout)findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (UserDataSource.getCurrentUser() == null){
            Intent i = new Intent(this, SignInActivity.class);
            startActivityForResult(i, SIGNIN_REQUESTCODE);
            return;
        }

        setupUserDataInDrawer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGNIN_REQUESTCODE && resultCode == RESULT_OK){
            setupUserDataInDrawer();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setupUserDataInDrawer() {
        //        setting the data in the drawerlayout
        ImageView drawerPhoto = (ImageView)findViewById(R.id.drawer_user_photo);
        TextView drawerUserName= (TextView)findViewById(R.id.drawer_user_name);
        Picasso.with(this).load(UserDataSource.getCurrentUser().getLargePictureURL()).into(drawerPhoto);
        drawerUserName.setText(UserDataSource.getCurrentUser().getmFirstName());
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
//                    if (mChoosingFragment == null) mChoosingFragment = new ChoosingFragment();
//                    return mChoosingFragment;
                    return new ChoosingFragment();
                case 1:
//                    if (mMatchesFragment == null) mMatchesFragment = new MatchesFragment();
//                    return mMatchesFragment;
                    return new MatchesFragment();
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
