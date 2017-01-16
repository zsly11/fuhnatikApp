package com.fuhnatik.fuhnatik;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FantasyReporterReadActivity extends AppCompatActivity {

    private TextView readingStoryTitle, readingStory, readingStoryDate;
    private ImageView readingStoryImage;
    private String objectID;
    private Date date;
    private ProgressDialog progressDialog;
    private String theTitle, theStory;
    private Bitmap loadedBitmapImage;
    private TextView TextView;
    public static final String TAG = FantasyReporterReadActivity.class.getSimpleName();
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_reporter_read);
        showProgressBar("Loading Story...");
        readingStoryTitle = (TextView) findViewById(R.id.readingStoryTitle);
        readingStory = (TextView) findViewById(R.id.readingStory);
        readingStoryDate = (TextView) findViewById(R.id.readingStoryDate);
        readingStoryImage = (ImageView) findViewById(R.id.readingStoryImage);

        Intent intent = getIntent();
        theTitle = intent.getStringExtra(AppConstants.PARSE_FANTASYREPORTER_TITLE);
        theStory = intent.getStringExtra(AppConstants.PARSE_FANTASYREPORTER_STORY);
        objectID = intent.getStringExtra(AppConstants.PARSE_OBJECT_ID);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.FANTASY_REPORTER_TABLE_NAME);
        query.getInBackground(objectID, new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, com.parse.ParseException e) {
                if (object == null){
                    Log.d(TAG, "error");
                } else {
                    object.getObjectId();

                    final ParseFile file = object.getParseFile(AppConstants.FANTASY_REPORTER_IMAGE_CONTENT);

                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, com.parse.ParseException e) {
                            if (e == null){
                                PrettyTime pt = new PrettyTime();
                                String dateFromParse = object.getCreatedAt().toString();
                                String delims = "[ ]";
                                String[] token = dateFromParse.split(delims);
                                String dateInString = token[0] + " " + token[1] + " " + token[2] + " " + token[3] + " " + token[5];
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy");
                                try {
                                    date = dateFormat.parse(dateInString);
                                } catch (java.text.ParseException error) {
                                    error.printStackTrace();
                                }

                                // Display time, title, story and image
                                readingStoryTitle.setText(theTitle);
                                readingStory.setText(theStory);
                                readingStoryDate.setText(pt.format(new Date(date.getTime())));
                                //Set Image
                                loadedBitmapImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                                readingStoryImage.setImageBitmap(loadedBitmapImage);
                                dismissProgressBar();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FantasyReporterReadActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("ok", null);
                                builder.show();
                            }
                        }
                    });
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
