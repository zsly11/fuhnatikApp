package com.fuhnatik.fuhnatik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Dialog progressDialog;
    private TextView usernameMenuHeader, userObjectIDHeader, userCoins, userGems;
    private ImageView profileImage;
    private ParseUser currentUser;
    private Bitmap loadedBitmap;

    private Uri mediaUri;

    public static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = ParseUser.getCurrentUser();
        ParseAnalytics.trackAppOpened(getIntent());
        //Setup the DrawerLayout and NavigationView
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        // Change the name to the username!
        View headerMenuView = navigationView.getHeaderView(0);
        usernameMenuHeader = (TextView)headerMenuView.findViewById(R.id.usernameMenuHeader);
        userObjectIDHeader = (TextView) headerMenuView.findViewById(R.id.userObjectIDHeader);
        profileImage = (ImageView)headerMenuView.findViewById(R.id.profileImage);
        userCoins = (TextView)headerMenuView.findViewById(R.id.userCoins);
        userGems = (TextView)headerMenuView.findViewById(R.id.userGems);


        //Lets inflate the very first fragment
        //We are inflating the TabFragment as the first Fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        // If user is logged in, show this - else show NotLoggedInActivity.
        showProgressBar("We're loading things...");

        if (currentUser != null) {
            onResume();
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(final ParseUser object, ParseException e) {
                    if (object == null){
                        Log.d(TAG, "error");
                    } else {
                        object.getObjectId();

                        final ParseFile file = object.getParseFile(AppConstants.PARSEUSER_PROFILE_IMAGE);

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null){

                                    loadedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profileImage.setImageBitmap(loadedBitmap);

                                    userObjectIDHeader.setText("ID: " + object.getObjectId());

                                    String getCurrentUsername = object.getUsername();
                                    usernameMenuHeader.setText(getCurrentUsername);
                                    //Display Default Coins For User
                                    int currentUserCoins = object.getInt(AppConstants.PARSEUSER_NUMBER_OF_COINS);
                                    userCoins.setText(Integer.toString(currentUserCoins));
                                    //Display Default Gems For User
                                    int currentUserGems = object.getInt(AppConstants.PARSEUSER_NUMBER_OF_GEMS);
                                    userGems.setText(Integer.toString(currentUserGems));
                                    dismissProgressBar();
                                } else {
                                    Log.d("test", "Error downloading information");
                                }
                            }
                        });
                    }
                }
            });

            /**
            query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
                public void done(ParseUser getCoinsAndGems, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data. In this case, only cheatMode and score
                        // will get sent to the Parse Cloud. playerName hasn't changed.
                        ParseFile getImage = currentUser.getParseFile(AppConstants.PARSEUSER_PROFILE_IMAGE);

                        Picasso.with(profileImage.getContext())
                                .load(getImage.getUrl())
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(profileImage);


                        getCurrentUsername = currentUser.getUsername();
                        usernameMenuHeader.setText(getCurrentUsername);
                        //Display Default Coins For User
                        int currentUserCoins = getCoinsAndGems.getInt(AppConstants.PARSEUSER_NUMBER_OF_COINS);
                        userCoins.setText(Integer.toString(currentUserCoins));
                        //Display Default Gems For User
                        int currentUserGems = getCoinsAndGems.getInt(AppConstants.PARSEUSER_NUMBER_OF_GEMS);
                        userGems.setText(Integer.toString(currentUserGems));
                        dismissProgressBar();

                    } else {
                        e.getStackTrace();
                    }
                }
            });
            **/


        } else {
            navigateToLogin();
        }

    }

    private void rotateImage(Bitmap bitmap){

    }

    public void showProgressBar(String msg){
        progressDialog = ProgressDialog.show(this, "", msg, true);
    }

    public void dismissProgressBar(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, NotLoggedInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editUser = new Intent(MainActivity.this, ProfileEditActivity.class);
                editUser.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(editUser);
            }
        });

        //Setup click events on the Navigation View Items.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()){

                    case R.id.howToPlayMenu:
                        Intent howToPlay = new Intent(MainActivity.this, HowToPlayActivity.class);
                        howToPlay.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        howToPlay.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(howToPlay);
                        break;

                    case R.id.fantasyReporterMenu:
                        Intent fantasyReporter = new Intent(MainActivity.this, FantasyReporterActivity.class);
                        fantasyReporter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        fantasyReporter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(fantasyReporter);
                        break;

                    case R.id.trophyRoomMenu:
                        Toast.makeText(getApplicationContext(), "Trophy Room Menu", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.myFriendsMenu:
                        Intent myFriends = new Intent(MainActivity.this, FriendsActivity.class);
                        myFriends.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myFriends.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myFriends);
                        break;

                    case R.id.contactSupportMenu:
                        Toast.makeText(getApplicationContext(),"Contact Support Menu", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.signOutMenu:
                        showProgressBar("Signing out...");
                        ParseUser.logOut();
                        Intent logoutIntent = new Intent(MainActivity.this, NotLoggedInActivity.class);
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(logoutIntent);
                        dismissProgressBar();
                        break;
                }
                /**
                 //THIS IS WHEN WE PRESS THE ITEMS FROM THE MENU!!!!
                 if (menuItem.getItemId() == R.id.findFriendsMenu) {
                 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                 fragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();

                 }
                 */
                return false;
            }

        });

        //Setup Drawer Toggle of the Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar,R.string.app_name, R.string.app_name);

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

}