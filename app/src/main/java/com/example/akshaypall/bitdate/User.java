package com.example.akshaypall.bitdate;

import com.parse.ParseUser;

/**
 * Created by Akshay Pall on 24/07/2015.
 */
public class User {

    private String mFirstName;
    private String mPictureUrl;
    private String mId;
    private String mFacebookId;

    public String getmFacebookId() {
        return mFacebookId;
    }

    public void setmFacebookId(String mFacebookId) {
        this.mFacebookId = mFacebookId;
    }

    public String getLargePictureURL(){
        return "https://graph.facebook.com/v2.3/"+mFacebookId+"/picture?type=large";
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmPictureUrl() {
        return mPictureUrl;
    }

    public void setmPictureUrl(String mPictureUrl) {
        this.mPictureUrl = mPictureUrl;
    }
}
