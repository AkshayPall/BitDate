package com.example.akshaypall.bitdate;

import android.graphics.Picture;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshay Pall on 24/07/2015.
 */
public class UserDataSource {
    private static User sCurrentUser;

//    Parse columns for User class
    private static final String COLUMN_FIRST_NAME = "firstName";
    private static final String COLUMN_PICTURE_URL = "pictureURL";
    private static final String COLUMN_USER_OBJECTID = "objectId";
    private static final String COLUMN_FACEBOOKID = "facebookId";

//    Parse classes
    private final static String USER_CLASS = "User";

    public static User getCurrentUser(){
        if (sCurrentUser == null && ParseUser.getCurrentUser() != null) {
            sCurrentUser = parseUserToUser(ParseUser.getCurrentUser());
        }
        return sCurrentUser;
    }

    public static void getUsersIn (List<String> ids, final UserDataCallbacks userDataCallbacks){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn(COLUMN_USER_OBJECTID, ids);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e==null){
                    formatCallback(list, userDataCallbacks);
                }
            }
        });
    }

    public static void getUnseenUsers(final UserDataCallbacks callbacks){
        //query the list of already "marked" users to AVOID in the unseen users query
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ActionDataSource.TABLE_NAME);
        query.whereEqualTo(ActionDataSource.COLUMN_BY_USER, UserDataSource.getCurrentUser().getmId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e==null){
                    List<String> toUsersIds = new ArrayList<String>();
                    for (ParseObject actionObject : list){
                        toUsersIds.add(actionObject.getString(ActionDataSource.COLUMN_TO_USER));
                    }

                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereNotEqualTo(COLUMN_USER_OBJECTID, getCurrentUser().getmId());
                    query.whereNotContainedIn(COLUMN_USER_OBJECTID, toUsersIds);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> list, ParseException e) {
                            if (e==null){
                                formatCallback(list, callbacks);
                            }
                        }
                    });
                }
            }
        });
    }

    private static void formatCallback(List<ParseUser> list, UserDataCallbacks callbacks) {
        List<User> toReturnUsers = new ArrayList<User>();
        for (ParseUser user : list) {
            toReturnUsers.add(parseUserToUser(user));
        }
        callbacks.onFetchedUsers(toReturnUsers);
    }

    private static User parseUserToUser (ParseUser parseUser) {
        User user = new User();
        user.setmFirstName(parseUser.getString(COLUMN_FIRST_NAME));
        user.setmPictureUrl(parseUser.getString(COLUMN_PICTURE_URL));
        user.setmFacebookId(parseUser.getString(COLUMN_FACEBOOKID));
        user.setmId(parseUser.getObjectId());
        return user;
    }

    public interface UserDataCallbacks {
        public void onFetchedUsers (List<User> users);
    }
}
