package com.example.akshaypall.bitdate;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;


/**
 * Created by Akshay Pall on 24/07/2015.
 */
public class CardStackContainer extends RelativeLayout implements View.OnTouchListener {
    private static final String TOUCH_TAG = "CARDVIEW TOUCHED";
    private CardAdapter mAdapter;

    //    for the movement of cardviews from the cardstack
    private float mLastTouchX;
    private float mLastTouchY;
    private float mCardPositionX;
    private float mCardPositionY;
    private float mOriginX;
    private float mOriginY;

    //    for the fling gestures
    private GestureDetector mGestureDetector;
    private CardView mTopCard;
    private CardView mBackCard;

//    to track how many cards from the adapter have been swiped away
    private int mNextCardPosition = 0;

    private SwipeCallbacks mSwipeCallbacks;

    public CardStackContainer(Context context) {
        this(context, null, 0); //calls the following method
    }

    public CardStackContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0); //calls the following method
    }

    public CardStackContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); //FINALLY where everything will be dealt with
        mGestureDetector = new GestureDetector(context, new FlingListener());
    }

    public void setmSwipeCallbacks(SwipeCallbacks mSwipeCallbacks) {
        this.mSwipeCallbacks = mSwipeCallbacks;
    }

    public void setmAdapter(CardAdapter adapter) {
        mAdapter = adapter;
        DataSetObserver dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                addFrontCard();
                addBackCard();
            }
        };
        mAdapter.registerDataSetObserver(dataSetObserver);
        addFrontCard();
        addBackCard();
    }

    private void addFrontCard() {
        if (mAdapter.getCount() > 0 && mTopCard == null){
            CardView cardView = mAdapter.getView(0,null,this);
            cardView.setCardElevation(8);
            cardView.setOnTouchListener(this);
            mTopCard = cardView;
            addView(cardView);
            mNextCardPosition++;
        }
    }

    private void addBackCard() {
        if (mAdapter.getCount() > mNextCardPosition && mBackCard == null){
            CardView cardView = mAdapter.getView(mNextCardPosition,null,this);
            cardView.setTranslationY(30);
            mBackCard = cardView;
            addView(cardView);
            mNextCardPosition++;
        }
        bringChildToFront(mTopCard);
    }

    private void swipeCard (boolean swipeRight){
        int position = getPositionOfCardSwiped();
        if (swipeRight){
            mTopCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_right));
            if (mSwipeCallbacks != null) {
                mSwipeCallbacks.onSwipedRight(mAdapter.getItem(position));
            }

        }else{
            mTopCard.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_left));
            if (mSwipeCallbacks != null) {
                mSwipeCallbacks.onSwipedLeft(mAdapter.getItem(position));
            }
        }
        removeView(mTopCard);
        mTopCard = null;
        if (mBackCard != null) {
            mBackCard.animate()
                    .translationY(0)
                    .setDuration(200);
            mBackCard.setOnTouchListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) mBackCard.setElevation(8);
            mTopCard = mBackCard;
            mBackCard = null;
            addBackCard();
        }
    }

    public void swipeRight() { //to make the animation in the fragment when the yaa button is pressed
        swipeCard(true);

    }

    public void swipeLeft() { //to make the animation in the fragment when the nah button is pressed
        swipeCard(false);
    }

    private int getPositionOfCardSwiped() {
        int position;
        if (mBackCard != null){
//                the mnextposition counter is the card AFTER THE BACK CARD. so the top card in 2 indices behind that
            position = mNextCardPosition - 2;
        } else {
//                only the top card left
            position = mNextCardPosition - 1;
        }
        return position;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            swipeCard(mCardPositionX > mOriginX);
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TOUCH_TAG, "pressed down");
                mCardPositionX = v.getX();
                mCardPositionY = v.getY();
                mOriginX = v.getX();
                mOriginY = v.getY();
                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                resetCard(v);
                Log.d(TOUCH_TAG, "let go");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TOUCH_TAG, "moved");
                float changedX = event.getX() - mLastTouchX;
                float changedY = event.getY() - mLastTouchY;
                mCardPositionX += changedX;
                mCardPositionY += changedY;
                v.setX(mCardPositionX);
                v.setY(mCardPositionY);
                break;
        }
        return true;

    }

    private void resetCard(View v) {
        mCardPositionX = mOriginX;
        mCardPositionY = mOriginY;
        v.animate()
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .x(mCardPositionX)
                .y(mCardPositionY);
    }

    private class FlingListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            else {
                Log.d(TOUCH_TAG, "flung card");
                return true;
            }
        }
    }

    public interface SwipeCallbacks{
        public void onSwipedRight(User user);
        public void onSwipedLeft(User user);
    }
}

