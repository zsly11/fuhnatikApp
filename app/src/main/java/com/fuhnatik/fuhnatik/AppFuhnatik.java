package com.fuhnatik.fuhnatik;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by mattginsberg on 7/16/16.
 */
public class AppFuhnatik extends Application{


    @Override
    public void onCreate() {
        super.onCreate();



        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fuhnatik121werYKJ73RHJweg768DtuWEg7frgh")
                .clientKey("fuhnatik121wsfYTdghJ%&*2355623*##&sdfgDFH23ds4547")
                .server("http://fuhnatik.herokuapp.com/parse/")

                .build()
        );


        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }


}
