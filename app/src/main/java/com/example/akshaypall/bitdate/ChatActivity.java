package com.example.akshaypall.bitdate;

import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class ChatActivity extends ActionBarActivity implements View.OnClickListener, MessagesDataSource.MessagesCallback {

    public static final String USER_EXTRA = "USER";
    public static final String TAG = "TAG";

    private ArrayList<Message> mMessageArrayList;
    private MessagesAdapter mAdapter;
    private User mCurrentRecipientNumber;
    private ListView mMessagesListView;
    private Date mLastMessageDate = new Date();
    private String mConvoID;
    private MessagesDataSource.MessagesListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mCurrentRecipientNumber = (User)getIntent().getSerializableExtra(USER_EXTRA);
        mMessagesListView = (ListView)findViewById(R.id.messages_list);
        mMessageArrayList = new ArrayList<>();
        mAdapter = new MessagesAdapter(mMessageArrayList);
        mMessagesListView.setAdapter(mAdapter);

        setTitle(mCurrentRecipientNumber.getmFirstName());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String ids[] = {mCurrentRecipientNumber.getmId(), UserDataSource.getCurrentUser().getmId()};
        Arrays.sort(ids);
        mConvoID = ids[0]+ids[1];

        Button sendMessageButton = (Button)findViewById(R.id.send_message_button);
        sendMessageButton.setOnClickListener(this);

        mListener = MessagesDataSource.addMessagesListener(mConvoID, this);
    }

    @Override
    public void onClick(View v) {
        EditText messageField = (EditText)findViewById(R.id.new_message_field);
        String text = messageField.getText().toString();
        if (!text.equals("")) {
            messageField.setText("");
            Message msg = new Message();
            msg.setmDate(new Date());
            msg.setmSender(UserDataSource.getCurrentUser().getmId());
            msg.setmText(text);
            MessagesDataSource.saveMessage(msg, mConvoID);
        }
    }

    @Override
    public void onMessageAdded(Message message) {
        mMessageArrayList.add(message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessagesDataSource.stopListener(mListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages){
            super(ChatActivity.this, R.layout.message, R.id.message, messages);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            Message message = getItem(position);
            TextView messageView = (TextView)v.findViewById(R.id.message);
            messageView.setText(message.getmText());

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)messageView.getLayoutParams();
            int sdk = Build.VERSION.SDK_INT;

            if (message.getmSender().equals(UserDataSource.getCurrentUser().getmId())){
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) messageView.setBackground(getDrawable(R.drawable.bubble_right_green));
                else messageView.setBackgroundDrawable(getDrawable(R.drawable.bubble_right_green));
                layoutParams.gravity = Gravity.RIGHT;
            } else {
                if (sdk >= Build.VERSION_CODES.JELLY_BEAN) messageView.setBackground(getDrawable(R.drawable.bubble_left_grey));
                else messageView.setBackgroundDrawable(getDrawable(R.drawable.bubble_left_grey));
                layoutParams.gravity = Gravity.LEFT;
            }
            messageView.setLayoutParams(layoutParams);
            return v;
        }
    }
}
