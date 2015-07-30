package com.example.akshaypall.bitdate;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Akshay Pall on 29/07/2015.
 */
public class MessagesDataSource {
    private final static Firebase sRef = new Firebase("https://akshay-bitdate.firebaseio.com/messages");
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddmmss");
    private static final String ERROR_TAG = "ERROR";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_TEXT = "text";

    public static void saveMessage (Message message, String convoID){
        Date date = message.getmDate();
        String key = sDateFormat.format(date);

        HashMap<String, String> msg = new HashMap<>();
        msg.put(COLUMN_TEXT, message.getmText());
        msg.put(COLUMN_SENDER, message.getmSender());

        sRef.child(convoID).child(key).setValue(msg);
    }

    public static MessagesListener addMessagesListener (String convoId, final MessagesCallback messagesCallback){
        MessagesListener listener = new MessagesListener(messagesCallback);
        sRef.child(convoId).addChildEventListener(listener);
        return listener;
    }

    public static void stopListener (MessagesListener listener) {
        sRef.removeEventListener(listener);
    }

    public static class MessagesListener implements ChildEventListener{
        private MessagesCallback callback;

        MessagesListener(MessagesCallback callback){
            this.callback = callback;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap<String, String> msg = (HashMap)dataSnapshot.getValue();
            Message message = new Message();
            message.setmText(msg.get(COLUMN_TEXT));
            message.setmSender(msg.get(COLUMN_SENDER));
            try {
                message.setmDate(sDateFormat.parse(dataSnapshot.getKey()));
            } catch (ParseException e) {
                Log.d(ERROR_TAG, "Couldn't parse date "+e);
            }
            if (callback != null){
                callback.onMessageAdded(message);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }

    public interface MessagesCallback {
        public void onMessageAdded(Message message);
    }
}
