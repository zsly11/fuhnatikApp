package com.fuhnatik.fuhnatik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.siyamed.shapeimageview.RoundedImageView;
import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FantasyReporterAddActivity extends AppCompatActivity {

    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;
    private Uri mMediaUri;

    private Button uploadStory;
    private Dialog progressDialog;
    private EditText titleEditText, storyEditText;
    private Button uploadImage;
    private ImageView previewFantasyImage;
    private TextView previewFantasyTitle;
    //ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_reporter_add);

        //currentUser = ParseUser.getCurrentUser();
        uploadStory = (Button)findViewById(R.id.uploadStory);
        uploadImage = (Button)findViewById(R.id.uploadImage);
        titleEditText = (EditText)findViewById(R.id.titleEditText);
        storyEditText = (EditText)findViewById(R.id.storyEditText);
        previewFantasyImage = (ImageView)findViewById(R.id.previewFantasyImage);
        previewFantasyTitle = (TextView)findViewById(R.id.previewFantasyTitle);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                previewFantasyTitle.setText(s.toString());
            }
        };

        titleEditText.addTextChangedListener(textWatcher);

        //when we click on the upload image
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FantasyReporterAddActivity.this);
                builder.setTitle("Upload A Photo");
                builder.setPositiveButton("Select Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //upload image
                        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePictureIntent.setType("image/*");
                        startActivityForResult(choosePictureIntent, CHOOSE_PIC_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        //listen to upload button click
        uploadStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder proofreadOrSend = new AlertDialog.Builder(FantasyReporterAddActivity.this);
                proofreadOrSend.setMessage(R.string.dialog_did_you_proof_read);
                proofreadOrSend.setPositiveButton(R.string.dialog_yes_send_story, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressBar("Saving Information...");
                        //create parse object for image to upload
                        ParseACL acl = new ParseACL();
                        //Anyone can read the stories.
                        acl.setPublicReadAccess(true);
                        //Only the user posting can delete (ADMIN)
                        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                        final ParseObject addFantasyReporter = new ParseObject(AppConstants.FANTASY_REPORTER_TABLE_NAME);
                        addFantasyReporter.setACL(acl);
                        final String titleTextForStory = titleEditText.getText().toString().trim();
                        final String storyTextForStory = storyEditText.getText().toString().trim();

                        if (titleTextForStory.isEmpty() || storyTextForStory.isEmpty()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(FantasyReporterAddActivity.this);
                            builder.setTitle(R.string.dialog_title_story_empty);
                            builder.setMessage(R.string.dialog_title_story_empty_message);
                            builder.setPositiveButton(R.string.dialog_tryagain, null);
                            AlertDialog dialog2 = builder.create();
                            dialog2.show();
                            dismissProgressBar();
                        } else {
                            try {
                                byte[] fileBytes = FileHelper.getByteArrayFromFile(FantasyReporterAddActivity.this, mMediaUri);
                                if (fileBytes == null) {
                                    //there was an error
                                    Toast.makeText(getApplicationContext(), "There was an error. Try again!", Toast.LENGTH_LONG).show();
                                    dismissProgressBar();
                                } else {
                                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                                    String fileName = FileHelper.getFileName(FantasyReporterAddActivity.this, mMediaUri, "image");
                                    final ParseFile file = new ParseFile(fileName, fileBytes);
                                    addFantasyReporter.saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(com.parse.ParseException e) {

                                            if (e == null) {
                                                addFantasyReporter.put(AppConstants.FANTASY_REPORTER_IMAGE_CONTENT, file);
                                                addFantasyReporter.put(AppConstants.PARSE_FANTASYREPORTER_TITLE, titleTextForStory);
                                                addFantasyReporter.put(AppConstants.PARSE_FANTASYREPORTER_STORY, storyTextForStory);
                                                addFantasyReporter.put(AppConstants.FANTASY_REPORTER_NUMBER_OF_LIKES, 0);
                                                addFantasyReporter.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(com.parse.ParseException e) {
                                                        dismissProgressBar();
                                                        Intent intent = new Intent(FantasyReporterAddActivity.this, FantasyReporterActivity.class);
                                                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                dismissProgressBar();
                                            }
                                        }
                                    });
                                }
                            } catch (Exception e1){
                                Toast.makeText(getApplicationContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
                                dismissProgressBar();
                            }
                        }
                    }
                });
                proofreadOrSend.setNegativeButton(R.string.dialog_no_go_back, null);
                AlertDialog alertDialog = proofreadOrSend.create();
                alertDialog.show();

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

    //inner helper method
    private Uri getOutputMediaFileUri(int mediaTypeImage) {

        if(isExternalStorageAvailable()){
            //get the URI
            //get external storage dir
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UPLOADIMAGES");
            //create subdirectore if it does not exist
            if(!mediaStorageDir.exists()){
                //create dir
                if(! mediaStorageDir.mkdirs()){

                    return null;
                }
            }
            //create a file name
            //create file
            File mediaFile = null;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if(mediaTypeImage == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }
            //return file uri
            Log.d("UPLOADIMAGE", "FILE: "+Uri.fromFile(mediaFile));

            return Uri.fromFile(mediaFile);
        }else {

            return null;
        }

    }
    //check if external storage is mounted. helper method
    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CHOOSE_PIC_REQUEST_CODE){
                if(data == null){
                    Toast.makeText(getApplicationContext(), "Image cannot be null!", Toast.LENGTH_LONG).show();
                }else{
                    mMediaUri = data.getData();
                    //set previews
                    previewFantasyImage.setImageURI(mMediaUri);
                }
            }else {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
                //set previews
                previewFantasyImage.setImageURI(mMediaUri);

            }

        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
        }
    }

}
