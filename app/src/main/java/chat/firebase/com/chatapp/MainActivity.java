package chat.firebase.com.chatapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public FirebaseListAdapter<ChatMessage> adapter;
    FloatingActionButton fab;
    ListView listOfMessages;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab =(FloatingActionButton)findViewById(R.id.fab);
        listOfMessages = (ListView)findViewById(R.id.list_of_messages);

      if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }else {
            // User is already signed in. Therefore, display
            // a welcome Toast

          FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
          if (user != null) {
              for (UserInfo profile : user.getProviderData()) {
                  // Id of the provider (ex: google.com)
                  String providerId = profile.getProviderId();

                  // UID specific to the provider
                  uid = profile.getUid();

                  // Name, email address, and profile photo Url
                  String name = profile.getDisplayName();
                  String email = profile.getEmail();

                  Log.e("popopo","wewe ni"+name);
                  Log.e("popopo","wewe ni"+email);
                  Log.e("popopo","sisi ni"+uid);
                  Log.e("popopo","nyinyi ni"+providerId);

              };
          }







            Toast.makeText(this,

                    "Welcome " + uid,
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            displayChatMessages();
        }
        displayChatMessages();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),uid));

                input.setText("");
            }
        });


        FirebaseMessaging.getInstance().subscribeToTopic("chat");

    }

    private void displayChatMessages() {
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
            R.layout.messages, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                model.getMessageTime()));

            }


        };

        listOfMessages.setAdapter(adapter);


    }
}
