package com.fuhnatik.fuhnatik;

/**
 * Created by mattginsberg on 7/23/16.
 */
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FragmentTwo extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    TextView benchTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //return inflater.inflate(R.layout.primary_layout2,null);
        final View layoutView = inflater.inflate(R.layout.primary_layout2, null);
        benchTextView = (TextView)layoutView.findViewById(R.id.benchTextView);
        expandableListView = (ExpandableListView)layoutView.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        benchTextView.setText("Select Position");

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        expandableListView.setIndicatorBounds(width-GetDipsFromPixel(130), width-GetDipsFromPixel(5));


        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if(childPosition == 0){
                    benchTextView.setText("All Positions");
                    expandableListView.collapseGroup(0);
                } else if (childPosition == 1) {
                    benchTextView.setText("Quaterback");
                    expandableListView.collapseGroup(0);
                } else if (childPosition == 2) {
                    benchTextView.setText("Running Backs");
                    expandableListView.collapseGroup(0);
                } else if (childPosition == 3) {
                    benchTextView.setText("Wide Receivers");
                    expandableListView.collapseGroup(0);
                } else if (childPosition == 4) {
                    benchTextView.setText("Tight Ends");
                    expandableListView.collapseGroup(0);
                } else if (childPosition == 5) {
                    benchTextView.setText("Kickers");
                    expandableListView.collapseGroup(0);
                } else if (childPosition == 6) {
                    benchTextView.setText("Defence");
                    expandableListView.collapseGroup(0);
                }

                return false;
            }
        });
        //spinner = (Spinner)layoutView.findViewById(R.id.spinner);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, stringPlayers);
        //spinner.setAdapter(adapter);


        return layoutView;
    }

    //Convert pixel to dip
    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

}