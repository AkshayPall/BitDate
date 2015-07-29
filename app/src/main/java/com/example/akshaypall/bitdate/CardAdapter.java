package com.example.akshaypall.bitdate;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akshay Pall on 24/07/2015.
 */
public class CardAdapter extends ArrayAdapter<User> {

    CardAdapter(Context ctx, List<User> users) {
        super(ctx, R.layout.card, R.id.name, users);
    }

    @Override
    public CardView getView(int position, View convertView, ViewGroup parent) {
        CardView v = (CardView)super.getView(position, convertView, parent);
        User user = getItem(position);

        TextView nameView = (TextView)v.findViewById(R.id.name);
        nameView.setText(user.getmFirstName());

        ImageView cardPhoto = (ImageView)v.findViewById(R.id.card_photo);
        Picasso.with(getContext()).load(user.getLargePictureURL()).into(cardPhoto);

        return v;
    }
}
