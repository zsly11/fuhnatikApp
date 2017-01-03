package com.fuhnatik.fuhnatik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText fullNameEdit, emailEdit, passwordEdit, nickNameEdit;
    private TextView aboutFuhnatikText, passwordShowText, passwordHideText, howToPlayText;
    private Button registerButton;
    private Dialog progressDialog;
    private Uri mMediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


        //Initiate the above
        fullNameEdit = (EditText)findViewById(R.id.fullNameEdit);
        nickNameEdit = (EditText)findViewById(R.id.nickNameEdit);
        emailEdit = (EditText)findViewById(R.id.emailEdit);
        passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        aboutFuhnatikText = (TextView)findViewById(R.id.aboutFuhnatikText);
        passwordShowText = (TextView)findViewById(R.id.passwordShowText);
        passwordHideText = (TextView)findViewById(R.id.passwordHideText);
        howToPlayText = (TextView)findViewById(R.id.howToPlayText);
        registerButton = (Button)findViewById(R.id.registerButton);

        //Setting up the aboutFuhnatikText Information
        String link = " At fuhnatik we strive to make sure the fantasy sports fans are having fun\n" +
                "         with this new experience. By tapping on the button below, you agree to\n" +
                "         our <a href=http://www.fuhnatik.com/ppt>Privacy Policy and our Terms</a>.\n" +
                "        Please <a href=http://www.fuhnatik.com/contact>contact us</a> if you have any questions about our service.";
        aboutFuhnatikText.append(Html.fromHtml(link));
        aboutFuhnatikText.setMovementMethod(LinkMovementMethod.getInstance());

        registerButton.setOnClickListener(this);
        passwordShowText.setOnClickListener(this);
        passwordHideText.setOnClickListener(this);
        howToPlayText.setOnClickListener(this);
    }

    public void showProgressBar(String msg){
        progressDialog = ProgressDialog.show(this, "", msg, true);
    }

    public void dismissProgressBar(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                showProgressBar("Working...");
                final String fullName = fullNameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                final String username = nickNameEdit.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";

                //ParseQuery<ParseUser> query = ParseUser.getQuery();


                // If anything is empty, display error
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    dismissProgressBar();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinActivity.this);
                    dialogBuilder.setMessage(R.string.dialog_whoops_message)
                            .setTitle(R.string.dialog_whoops_title)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

                else if (username.length() > 25) {
                    dismissProgressBar();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinActivity.this);
                    dialogBuilder.setMessage(R.string.dialog_email_title)
                            .setTitle(R.string.dialog_username_length_title)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

                // If email is invalid, display toast.
                else if (!email.matches(emailPattern)) {
                    dismissProgressBar();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinActivity.this);
                    dialogBuilder.setMessage(R.string.dialog_email_title)
                            .setTitle(R.string.dialog_join_email_message)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

                // Password must be a min. of 8 characters
                else if (password.length() < 8) {
                    dismissProgressBar();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinActivity.this);
                    dialogBuilder.setMessage(R.string.dialog_password_message)
                            .setTitle(R.string.dialog_password_title)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                } else {
                    // Check if nickname exists too.
                    //Add User
                    // Create the new user.
                    final ParseUser newUser = new ParseUser();
                    //newUser.setUsername(email);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_picture);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    ParseFile file = new ParseFile(AppConstants.PARSEUSER_IMAGE_FILE_NAME, image);
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        newUser.setEmail(email);
                        newUser.put(AppConstants.PARSEUSER_FULLNAME, fullName);
                        newUser.put(AppConstants.PARSEUSER_ADMIN_RIGHTS, AppConstants.PARSEUSER_NOT_AN_ADMIN);
                        newUser.put(AppConstants.PARSEUSER_NUMBER_OF_COINS, 1500);
                        newUser.put(AppConstants.PARSEUSER_NUMBER_OF_GEMS, 100);
                        newUser.put(AppConstants.PARSEUSER_PROFILE_IMAGE, file);

                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                newUser.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            dismissProgressBar();
                                            // USER CREATED!
                                            // TODO SEND AN EMAIL TO THE USER WITH USERNAME AND PASSWORD
                                            Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        } else {
                                            dismissProgressBar();
                                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinActivity.this);
                                            dialogBuilder.setMessage(e.getMessage())
                                                    .setTitle(R.string.dialog_whoops_title)
                                                    .setPositiveButton(R.string.dialog_tryagain, null);
                                            AlertDialog dialog = dialogBuilder.create();
                                            dialog.show();
                                        }
                                    }
                                });
                            } else {
                                dismissProgressBar();
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(JoinActivity.this);
                                dialogBuilder.setMessage(e.getMessage())
                                        .setTitle(R.string.dialog_whoops_title)
                                        .setPositiveButton(R.string.dialog_tryagain, null);
                                AlertDialog dialog = dialogBuilder.create();
                                dialog.show();
                            }
                        }
                    });


                }


                break;

            case R.id.passwordShowText:
                passwordEdit.setTransformationMethod(null);
                passwordShowText.setVisibility(View.GONE);
                passwordHideText.setVisibility(View.VISIBLE);
                break;

            case R.id.passwordHideText:
                passwordEdit.setTransformationMethod(new PasswordTransformationMethod());
                passwordHideText.setVisibility(View.GONE);
                passwordShowText.setVisibility(View.VISIBLE);
                break;

            case R.id.howToPlayText:
                Intent howToPlayIntent = new Intent(JoinActivity.this, HowToPlayActivity.class);
                howToPlayIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(howToPlayIntent);
                break;
        }
    }
}


