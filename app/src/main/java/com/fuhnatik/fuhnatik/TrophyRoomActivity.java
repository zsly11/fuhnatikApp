package com.fuhnatik.fuhnatik;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TrophyRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_room);

        Toast.makeText(TrophyRoomActivity.this, "Entered Trophy Room", Toast.LENGTH_LONG).show();
    }
}
