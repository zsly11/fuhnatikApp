package com.fuhnatik.fuhnatik;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.ShapeImageView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class ProfileEditActivity extends AppCompatActivity {

    private ImageView userProfileImage;
    private Bitmap loadedBitmap;
    private TextView userProfileName, userProfileUsername, userObjectID;
    private EditText fullNameEdit, usernameEdit, emailEdit;
    private String userProfileNameString, userProfileUsernameString, emailString;
    private Button updateProfileButton, cancelProfileButton;
    private Dialog progressDialog;
    private ParseUser user;

    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;
    private Uri mMediaUri;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        userProfileImage = (ImageView)findViewById(R.id.userProfileImage);

        userProfileName = (TextView)findViewById(R.id.userProfileName);
        userObjectID = (TextView)findViewById(R.id.userObjectID);
        userProfileUsername = (TextView)findViewById(R.id.userProfileUsername);
        updateProfileButton = (Button)findViewById(R.id.updateProfileButton);
        cancelProfileButton = (Button)findViewById(R.id.cancelProfileButton);

        fullNameEdit = (EditText)findViewById(R.id.fullNameEdit);
        usernameEdit = (EditText)findViewById(R.id.usernameEdit);
        emailEdit = (EditText)findViewById(R.id.emailEdit);

        user = ParseUser.getCurrentUser();
        userProfileNameString = user.getString(AppConstants.PARSEUSER_FULLNAME);
        userProfileUsernameString = user.getUsername();
        emailString = user.getEmail();

        //Display the object ID to the user.
        userObjectID.setText("ID: " + user.getObjectId());

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if (object == null){
                    Log.d(TAG, "error");
                } else {
                    object.getObjectId();

                    String getUsername = object.getUsername();
                    String getFullname = object.getString(AppConstants.PARSEUSER_FULLNAME);
                    String getEmail = object.getEmail();

                    userProfileName.setText(getFullname);
                    userProfileUsername.setText(getUsername);
                    fullNameEdit.setText(getFullname);
                    usernameEdit.setText(getUsername);
                    emailEdit.setText(getEmail);


                    ParseFile file = object.getParseFile(AppConstants.PARSEUSER_PROFILE_IMAGE);

                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                userProfileImage.setImageBitmap(bitmap);
                            } else {
                                Log.d("test", "Error downloading information");
                            }
                        }
                    });
                }
            }
        });

        textWatchers();
        profileImage();
        updateProfile();
        cancelUpdateProfile();

    }

    public void textWatchers(){
        TextWatcher textWatcherUserProfileName = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userProfileName.setText(s.toString());
            }
        };
        fullNameEdit.addTextChangedListener(textWatcherUserProfileName);

        TextWatcher textWatcherUserProfileUsername = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                userProfileUsername.setText(s.toString());
            }
        };
        usernameEdit.addTextChangedListener(textWatcherUserProfileUsername);
    }
    /**
     * OnClickListener when we tap on the image.
     * We are looking to update our user profile image.
     */
    public void profileImage(){
        //TODO implement a one time change of picture
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
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
    }

    /**
     * OnClickListener when we tap to update the profile.
     * We are saving the changes to Parse
     */
    public void updateProfile(){
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showProgressBar("Updating profile...");

                final String setNameEditText = fullNameEdit.getText().toString().trim();
                final String setUsernameEditText = usernameEdit.getText().toString().trim();
                final String setEmailEditText = emailEdit.getText().toString().trim();

            final ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
            try {
                byte[] fileBytes = FileHelper.getByteArrayFromFile(ProfileEditActivity.this, mMediaUri);
                if (fileBytes == null) {
                    //there was an error
                    Toast.makeText(getApplicationContext(), "There was an error. Try again!", Toast.LENGTH_LONG).show();
                    dismissProgressBar();
                } else {
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                    String fileName = FileHelper.getFileName(ProfileEditActivity.this, mMediaUri, "image");
                    final ParseFile file = new ParseFile(fileName, fileBytes);


                    queryUser.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser object, ParseException e) {

                            object.put(AppConstants.PARSEUSER_FULLNAME, setNameEditText);
                            object.setUsername(setUsernameEditText);
                            object.setEmail(setEmailEditText);
                            object.put(AppConstants.PARSEUSER_PROFILE_IMAGE, file);

                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            if (e == null){
                                file.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            dismissProgressBar();
                                            Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dismissProgressBar();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                dismissProgressBar();
                            }
                            }
                        });
                        }
                    });
                }
            } catch (Exception e1){
                Toast.makeText(getApplicationContext(), "You must change your\nprofile image to proceed.", Toast.LENGTH_SHORT).show();
                dismissProgressBar();
            }
            }
        });
    }

    public void cancelUpdateProfile(){
        cancelProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelButton = new Intent(ProfileEditActivity.this, MainActivity.class);
                cancelButton.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cancelButton.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                cancelButton.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(cancelButton);
            }
        });
    }





    private String getRealPathFromURI(Uri contentURI, Activity activity){
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
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
                    /**
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                    Cursor cur = managedQuery(mMediaUri, orientationColumn, null, null, null);
                    int orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);

                    switch (orientation){
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            //matrix.setRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            //matrix.setRotate(180);
                            break;
                        default:
                    }
                    //set previews
                    try {
                        loadedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mMediaUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmapNew = Bitmap.createBitmap(loadedBitmap, 0, 0, loadedBitmap.getWidth(), loadedBitmap.getHeight(), matrix, true);
                    **/
                     userProfileImage.setImageURI(mMediaUri);
                }
            }else {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
                //set previews
                userProfileImage.setImageURI(mMediaUri);


            }

        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
        }
    }


    public void showProgressBar(String msg){
        progressDialog = ProgressDialog.show(this, "", msg, true);
    }

    public void dismissProgressBar(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
