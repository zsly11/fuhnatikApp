package com.fuhnatik.fuhnatik;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mattginsberg on 7/16/16.
 */
public class NotLoggedInActivity extends Activity implements View.OnClickListener {


    private TextView loginAccount;
    private Button joinButton;
    private ImageView mainImageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_logged_in);

        //Implement Items From XML
        loginAccount = (TextView)findViewById(R.id.loginAccount);
        joinButton = (Button)findViewById(R.id.joinButton);
        mainImageInfo = (ImageView)findViewById(R.id.mainImageInfo);

        //Set the onclick listener
        loginAccount.setOnClickListener(this);
        joinButton.setOnClickListener(this);
        mainImageInfo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //This happens when we click on joinButton
            case R.id.joinButton:
                Intent joinIntent = new Intent(NotLoggedInActivity.this, JoinActivity.class);
                joinIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(joinIntent);
                break;

            //This happens when we click on the loginAccount text
            case R.id.loginAccount:
                Intent loginIntent = new Intent(NotLoggedInActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(loginIntent);
                break;

            //This happens when we click on the mainImageInfo
            case R.id.mainImageInfo:
                Intent howToPlayIntent = new Intent(NotLoggedInActivity.this, HowToPlayActivity.class);
                howToPlayIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(howToPlayIntent);
                break;
        }
    }
}
