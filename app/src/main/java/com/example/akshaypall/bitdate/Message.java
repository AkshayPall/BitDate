package com.example.akshaypall.bitdate;

import java.util.Date;

/**
 * Created by Akshay Pall on 29/07/2015.
 */
public class Message {
    private String mText;
    private String mSender;
    private Date mDate;

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getmSender() {
        return mSender;
    }

    public void setmSender(String mSender) {
        this.mSender = mSender;
    }
}
