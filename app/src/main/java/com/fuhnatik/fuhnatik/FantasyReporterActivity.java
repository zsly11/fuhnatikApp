package com.fuhnatik.fuhnatik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FantasyReporterActivity extends AppCompatActivity {

    public static final String TAG = FantasyReporterActivity.class.getSimpleName();
    private ImageButton fabAddStory;
    private ParseUser currentUser;
    protected List<ParseObject> fantasyReporterObjects;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_reporter);
        showProgressBar("Loading Fantasy Reporter...");
        currentUser = ParseUser.getCurrentUser();
        fabAddStory = (ImageButton)findViewById(R.id.fabAddStory);
        fabAddStory.setVisibility(View.GONE);

        if(!currentUser.get(AppConstants.PARSEUSER_ADMIN_RIGHTS).equals(AppConstants.PARSEUSER_NOT_AN_ADMIN)){
            fabAddStory.setVisibility(View.VISIBLE);
        }

        fabAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addReport = new Intent(FantasyReporterActivity.this, FantasyReporterAddActivity.class);
                addReport.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                addReport.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(addReport);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //questionMark.setVisibility(View.GONE);
        //ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.FANTASY_REPORTER_TABLE_NAME);
        //query.whereNotEqualTo("Username", user);
        query.addDescendingOrder(AppConstants.PARSE_CREATED_DATE);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //Good to go
                    fantasyReporterObjects = objects;
                    dismissProgressBar();
                    //if there are less than 1 report.
                    if (fantasyReporterObjects.size() < 1) {
                        dismissProgressBar();
                    } else if (fantasyReporterObjects.size() >= 1) {
                        //more than 1 report
                        ListView lv = (ListView) findViewById(R.id.listView);
                        FantasyReporterListAdapter adapter = new FantasyReporterListAdapter(lv.getContext(), fantasyReporterObjects);
                        lv.setLongClickable(true);
                        lv.setAdapter(adapter);
                        dismissProgressBar();
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent readingStoryIntent = new Intent(FantasyReporterActivity.this, FantasyReporterReadActivity.class);
                                readingStoryIntent.putExtra(AppConstants.PARSE_FANTASYREPORTER_TITLE, fantasyReporterObjects.get(position).getString(AppConstants.PARSE_FANTASYREPORTER_TITLE));
                                readingStoryIntent.putExtra(AppConstants.PARSE_FANTASYREPORTER_STORY, fantasyReporterObjects.get(position).getString(AppConstants.PARSE_FANTASYREPORTER_STORY));
                                readingStoryIntent.putExtra(AppConstants.FANTASY_REPORTER_IMAGE_CONTENT, fantasyReporterObjects.get(position).getString(AppConstants.FANTASY_REPORTER_IMAGE_CONTENT));
                                readingStoryIntent.putExtra(AppConstants.PARSE_OBJECT_ID, fantasyReporterObjects.get(position).getObjectId());
                                readingStoryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(readingStoryIntent);
                            }
                        });
                    }
                } else {
                    // Something went wrong
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FantasyReporterActivity.this);
                    dialogBuilder.setMessage(e.getMessage())
                            .setTitle(R.string.dialog_whoops_title)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            }
        });
    }

    public void showProgressBar(String msg){
        progressDialog = ProgressDialog.show(this, "", msg, true);
    }

    public void dismissProgressBar(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }
}