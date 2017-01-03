package com.fuhnatik.fuhnatik;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FantasyReporterListAdapter extends ArrayAdapter<ParseObject> {

    protected Context activityContext;
    protected List<ParseObject> fantasyReporterList;
    private Date date;
    private Dialog progressDialog;
    private ParseUser currentUser = ParseUser.getCurrentUser();

    public static final String TAG = FantasyReporterListAdapter.class.getSimpleName();

    /**
     * Constructor for the adaptor
     * @param context This is the context of the activity
     * @param reporterList The list the information is stored in
     */

    public FantasyReporterListAdapter(Context context, List<ParseObject> reporterList){
        super(context, R.layout.activity_fantasy_reporter_list_adapter, reporterList);
        activityContext = context;
        fantasyReporterList = reporterList;
    }

    /**
     * Get the entre view and display this information in the ListView
     * @param position Where is the list we are.
     * @param convertView The view
     * @param parent The parent of this view
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ParseObject report = fantasyReporterList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(activityContext).inflate(
                    R.layout.activity_fantasy_reporter_list_adapter, null);
            holder = new ViewHolder();
            holder.fantasyTitle = (TextView) convertView.findViewById(R.id.fantasyTitle);
            holder.dateText = (TextView)convertView.findViewById(R.id.dateText);
            holder.adminMenuIcon = (ImageView)convertView.findViewById(R.id.adminMenuIcon);
            holder.fantasyReporterImage = (ImageView)convertView.findViewById(R.id.fantasyReporterImage);
            holder.readStoryArrow = (ImageView)convertView.findViewById(R.id.readStoryArrow);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();

        }
        showProgressBar("Loading Fantasy Reporter...");


        //SET UP THE IMAGES
        final ParseObject objectPosition = fantasyReporterList.get(position);


        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConstants.FANTASY_REPORTER_TABLE_NAME);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, com.parse.ParseException e) {
                if (object == null){
                    Log.d(TAG, "error");
                } else {
                    object.getObjectId();

                    ParseFile file = objectPosition.getParseFile(AppConstants.FANTASY_REPORTER_IMAGE_CONTENT);

                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, com.parse.ParseException e) {
                            if (e == null){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                holder.fantasyReporterImage.setImageBitmap(bitmap);

                                dismissProgressBar();
                            } else {
                                Log.d("test", "Error downloading information");
                            }
                        }
                    });
                }
            }
        });








        //Picasso.with(getContext().getApplicationContext()).load(object.getParseFile(AppConstants.FANTASY_REPORTER_IMAGE_CONTENT).getUrl()).noFade().into(holder.fantasyReporterImage);

        /**
         * We are getting the time that the post was submitted
         * and using that with PrettyTime to get how long
         * ago we submitted the post.
         */
        //PrettyTime Past Tense
        PrettyTime pt = new PrettyTime();
        //Get the time the post was created
        String dateFromParse = report.getCreatedAt().toString();
        //Split the time based on spaces
        String delims = "[ ]";
        //Create an array of entries
        String[] token = dateFromParse.split(delims);
        //Build a String
        String dateInString = token[0] + " " + token[1] + " " + token[2] + " " + token[3] + " " + token[5];
        //Format the String
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy");
        try {
            date = dateFormat.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * Get the total number of letters in the title
         * and if that is more than 30, add dots at the end!
         */
        String stringTitle = report.getString(AppConstants.PARSE_FANTASYREPORTER_TITLE);
        int numberOfLettersInTitle = stringTitle.length();
        if(numberOfLettersInTitle > 30){
            holder.fantasyTitle.setText(stringTitle.substring(0, 30) + "...");
        } else {
            holder.fantasyTitle.setText(report.getString(AppConstants.PARSE_FANTASYREPORTER_TITLE));
        }
        /**
         * Display the rest of the information.
         */
        holder.readStoryArrow.setVisibility(View.VISIBLE);
        holder.dateText.setText(pt.format(new Date(date.getTime())));
        holder.adminMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activityContext, holder.adminMenuIcon);
                popupMenu.getMenuInflater().inflate(R.menu.admin_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.deletePostFromFantasyReporter:
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activityContext);
                                dialogBuilder.setMessage("Are you sure you want to delete " + report.getString(AppConstants.PARSE_FANTASYREPORTER_TITLE))
                                        .setTitle(R.string.dialog_delete_story)
                                        .setPositiveButton(R.string.dialog_delete_post, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                report.deleteInBackground();
                                                Intent reloadFantasyReporter = new Intent(activityContext, FantasyReporterActivity.class);
                                                reloadFantasyReporter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                activityContext.startActivity(reloadFantasyReporter);
                                            }
                                        })
                                        .setNegativeButton(R.string.dialog_cancel, null);
                                AlertDialog dialog = dialogBuilder.create();
                                dialog.show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        if(!currentUser.get(AppConstants.PARSEUSER_ADMIN_RIGHTS).equals("NOTADMIN")) {
            holder.adminMenuIcon.setVisibility(View.VISIBLE);
        }
        dismissProgressBar();
        return convertView;

    }

    private static class ViewHolder{
        TextView fantasyTitle, dateText;
        ImageView adminMenuIcon, fantasyReporterImage, readStoryArrow;
    }

    /**
     * Showing the progress information
     * @param msg This is the message we display.
     */
    public void showProgressBar(String msg){
        progressDialog = ProgressDialog.show(activityContext, "", msg, true);
    }

    /**
     * Dismiss the progress information
     */
    public void dismissProgressBar(){
        if(progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
