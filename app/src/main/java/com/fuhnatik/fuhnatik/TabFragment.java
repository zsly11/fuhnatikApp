package com.fuhnatik.fuhnatik;

/**
 * Created by mattginsberg on 7/23/16.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment extends Fragment {

    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static int int_items = 4 ;

    private int[] tabIcons = {
            R.drawable.login_cards
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate tab_layout and setup Views.
        View tabView =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) tabView.findViewById(R.id.tabs);
        viewPager = (ViewPager) tabView.findViewById(R.id.viewpager);

        //Set an adaptor for the View Pager
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));


        // Now , this is a workaround ,
        // The setupWithViewPager dose't works without the runnable .
        // Maybe a Support Library Bug .
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                //Icons instead of text? tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            }
        });

        return tabView;

    }



    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        // Return fragment with respect to Position.
        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new FragmentOne();
                case 1 : return new FragmentTwo();
                case 2 : return new FragmentThree();
                case 3 : return new FragmentFour();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        // This method returns the title of the tab according to the position.
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Starters";
                case 1 :
                    return "Bench";
                case 2 :
                    return "Shop";
                case 3 :
                    return "Leagues";
            }
            return null;
        }
    }

}