package com.example.akshaypall.bitdate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChoosingFragment extends Fragment implements UserDataSource.UserDataCallbacks, CardStackContainer.SwipeCallbacks {


    private static final String TAG = "CHOOSING FRAGMENT";
    private CardStackContainer mCardStack;
    private CardAdapter mCardAdapter;
    private List<User> mUsers;

    private ProgressBar mProgressBar;

    public ChoosingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        //progress bar
        mProgressBar = (ProgressBar)v.findViewById(R.id.progressbar_choosing);

        mCardStack = (CardStackContainer)v.findViewById(R.id.card_stack);
        UserDataSource.getUnseenUsers(this);

        mUsers = new ArrayList<>();
        mCardAdapter = new CardAdapter(getActivity(), mUsers);
        mCardStack.setmAdapter(mCardAdapter);
        mCardStack.setmSwipeCallbacks(this);

        ImageButton nahButton= (ImageButton)v.findViewById(R.id.nah_selection_button);
        nahButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardStack.swipeLeft();
            }
        });
        ImageButton yaaButton= (ImageButton)v.findViewById(R.id.yaa_selection_button);
        yaaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardStack.swipeRight();
            }
        });
        return v;
    }

    @Override
    public void onFetchedUsers(List<User> users) {
        mUsers.addAll(users);
        mCardAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onSwipedRight(User user) {
        Log.d(TAG, "swiped right "+ user.getmFirstName());
        ActionDataSource.saveUserLiked(user.getmId());
    }

    @Override
    public void onSwipedLeft(User user) {
        Log.d(TAG, "swiped left "+user.getmFirstName());
        ActionDataSource.saveUserSkipped(user.getmId());
    }
}
