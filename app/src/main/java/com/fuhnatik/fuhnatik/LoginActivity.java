package com.fuhnatik.fuhnatik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView passwordShowText, passwordHideText;
    private EditText usernameEdit, passwordEdit;
    private Button loginButton;
    private Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordShowText = (TextView)findViewById(R.id.passwordShowText);
        passwordHideText = (TextView)findViewById(R.id.passwordHideText);
        usernameEdit = (EditText)findViewById(R.id.usernameEdit);
        passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        loginButton = (Button)findViewById(R.id.loginButton);

        passwordShowText.setOnClickListener(this);
        passwordHideText.setOnClickListener(this);
        loginButton.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.loginButton:
                showProgressBar("Signing in...");
                final String username = usernameEdit.getText().toString().trim();
                final String password = passwordEdit.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";

                // If anything is empty, display error
                if (username.isEmpty() || password.isEmpty()) {
                    dismissProgressBar();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    dialogBuilder.setMessage(R.string.dialog_whoops_message)
                            .setTitle(R.string.dialog_whoops_title)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

                //If email is invalid, display toast.
                else if (!username.matches(emailPattern)) {
                    dismissProgressBar();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                    dialogBuilder.setMessage(R.string.dialog_email_message)
                            .setTitle(R.string.dialog_username_title)
                            .setPositiveButton(R.string.dialog_tryagain, null);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }

                // Password must be a min. of 8 characters
                else {
                    if (password.length() < 8) {
                        dismissProgressBar();
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                        dialogBuilder.setMessage(R.string.dialog_password_message_login)
                                .setTitle(R.string.dialog_password_title)
                                .setPositiveButton(R.string.dialog_tryagain, null);
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    } else {

                        if (username.contains("@")) {

                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("email", username);
                            query.getFirstInBackground(new GetCallback<ParseUser>() {
                                @Override
                                public void done(ParseUser object, ParseException e) {
                                    if (object != null) {

                                        ParseUser.logInInBackground(object.getString("username"), password, new LogInCallback() {
                                            public void done(ParseUser user, ParseException e) {
                                                if (user != null) {
                                                    // Good to log in the user.
                                                    dismissProgressBar();
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                } else {
                                                    // Whoops, we have an error.
                                                    dismissProgressBar();
                                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                                    dialogBuilder.setMessage(e.getMessage())
                                                            .setTitle(R.string.dialog_whoops_title)
                                                            .setPositiveButton(R.string.dialog_tryagain, null);
                                                    AlertDialog dialog = dialogBuilder.create();
                                                    dialog.show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            dismissProgressBar();
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            dialogBuilder.setMessage(R.string.dialog_email_message)
                                    .setTitle(R.string.dialog_username_title)
                                    .setPositiveButton(R.string.dialog_tryagain, null);
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                        }

                    }
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
        }
    }
}
