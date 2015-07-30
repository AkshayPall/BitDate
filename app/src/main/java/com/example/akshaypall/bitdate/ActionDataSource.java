package com.example.akshaypall.bitdate;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshay Pall on 28/07/2015.
 */
public class ActionDataSource {
//    constants from the Action class
    public final static String TABLE_NAME = "Action";
    public final static String COLUMN_BY_USER = "byUser";
    public final static String COLUMN_TO_USER = "toUser";
    private final static String COLUMN_TYPE = "type";
    private final static String COLUMN_UPDATED_AT = "updatedAt";

    private final static String TYPE_LIKED = "liked";
    private final static String TYPE_SKIPPED = "skipped";
    private final static String TYPE_MATCHED = "matched";

    public static void saveUserSkipped (String userId){
        ParseObject action = createAction(userId, TYPE_SKIPPED);
        action.saveInBackground();
    }

    private static ParseObject createAction(String userId, String type) {
        ParseObject action = new ParseObject(TABLE_NAME);
        action.put(COLUMN_BY_USER, UserDataSource.getCurrentUser().getmId());
        action.put(COLUMN_TO_USER, userId);
        action.put(COLUMN_TYPE, type);
        return action;
    }

    public static void saveUserLiked (final String userId){
        //query set of actions where userLiked also liked the current user back -> used to determine match
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(TABLE_NAME);
        query.whereEqualTo(COLUMN_TO_USER, UserDataSource.getCurrentUser().getmId());
        query.whereEqualTo(COLUMN_BY_USER, userId);
        query.whereEqualTo(COLUMN_TYPE, TYPE_LIKED);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject action = null;
                if (e == null && list.size() > 0) { //the same user already liked the current user
                    //only need the most recent match
                    ParseObject priorAction = list.get(0);
                    priorAction.put(COLUMN_TYPE, TYPE_MATCHED);
                    priorAction.saveInBackground();
                    action = createAction(userId, TYPE_MATCHED);
                } else { //the current user is currently the ONLY one of the two users to create an action LIKED
                    action = createAction(userId, TYPE_LIKED);
                }
                action.saveInBackground();
            }
        });
    }

    public static void getMatches(final ActionDataCallbacks actionDataCallbacks) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(TABLE_NAME);
        query.whereEqualTo(COLUMN_BY_USER, UserDataSource.getCurrentUser().getmId());
        query.whereEqualTo(COLUMN_TYPE, TYPE_MATCHED);
        query.orderByDescending(COLUMN_UPDATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e==null){
                    List<String> matches = new ArrayList<String>();
                    for (ParseObject object : list){
                        matches.add(object.getString(COLUMN_TO_USER));
                    }
                    if (actionDataCallbacks != null){
                        actionDataCallbacks.onFetchedMatches(matches);
                    }
                }
            }
        });
    }

    public interface ActionDataCallbacks {
        public void onFetchedMatches (List<String> matchIds);
    }
}
