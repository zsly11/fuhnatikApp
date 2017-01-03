package com.fuhnatik.fuhnatik;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseUser;

public class FriendsActivity extends AppCompatActivity {

    public static final String TAG = FantasyReporterActivity.class.getSimpleName();
    private ImageButton fabSearchFriends;
    private ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        currentUser = ParseUser.getCurrentUser();
        fabSearchFriends = (ImageButton)findViewById(R.id.fabAddStory);

        Toast.makeText(FriendsActivity.this, "Friends", Toast.LENGTH_LONG).show();
    }

}
