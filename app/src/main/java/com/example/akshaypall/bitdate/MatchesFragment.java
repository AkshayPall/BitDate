package com.example.akshaypall.bitdate;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MatchesFragment extends Fragment implements ActionDataSource.ActionDataCallbacks, UserDataSource.UserDataCallbacks, AdapterView.OnItemClickListener{


    private static final String TAG = "TAG";
    private List<User> mUsers;
    private MatchesAdapter mAdapter;

    private ProgressBar mProgressBar;

    public MatchesFragment() {
        //constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matches, container, false);
        mProgressBar = (ProgressBar)v.findViewById(R.id.progressbar_matches);
        ActionDataSource.getMatches(this);
        ListView matchesListView = (ListView)v.findViewById(R.id.matches_list);
        mUsers = new ArrayList<>();
        mAdapter = new MatchesAdapter(mUsers);
        matchesListView.setAdapter(mAdapter);
        matchesListView.setOnItemClickListener(this);
        return v;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = mUsers.get(position);
        Intent i = new Intent(getActivity(), ChatActivity.class);
        i.putExtra(ChatActivity.USER_EXTRA, user);
        startActivity(i);
    }

    @Override
    public void onFetchedMatches(List<String> matchIds) {
        UserDataSource.getUsersIn(matchIds, this);
    }

    @Override
    public void onFetchedUsers(List<User> users) {
        for (User user : users){
            Log.d(TAG, "User is "+user.getmFirstName());
        }
        mUsers.clear();
        mUsers.addAll(users);
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(ProgressBar.GONE);
    }

    private class MatchesAdapter extends ArrayAdapter<User>{
        MatchesAdapter(List<User> users){
            super(MatchesFragment.this.getActivity(), android.R.layout.simple_list_item_1, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView)super.getView(position, convertView, parent);
            v.setText(getItem(position).getmFirstName());
            return v;
        }
    }
}
