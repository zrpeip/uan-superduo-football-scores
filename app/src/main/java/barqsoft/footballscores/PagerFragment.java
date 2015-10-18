package barqsoft.footballscores;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Fragment that holds and manages the ViewPager fragments and the PagerTabStrip.
 */
public class PagerFragment extends Fragment {
    public static final int NUM_PAGES = 9;
    public int currentItem;
    public ViewPager mPagerHandler;
    private MyPageAdapter mPagerAdapter;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[NUM_PAGES];
    public boolean isRtl;

    // Fragment inflated into the MainActivity container view
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new MyPageAdapter(getChildFragmentManager());
        isRtl = checkIfRtl();
        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            viewFragments[i] = new MainScreenFragment();
            viewFragments[i].setFragmentDate(mformat.format(fragmentdate));
        }
        // If we are in RTL locale or forced mode in developer options, reverse the order of the
        // fragments to have RTL swiping in ViewPager.
        mPagerHandler.setAdapter(mPagerAdapter);
        if (isRtl) {
            Collections.reverse(Arrays.asList(viewFragments));
            currentItem = (viewFragments.length - 1) - MainActivity.current_fragment;
            mPagerHandler.setCurrentItem(currentItem);
        } else {
            currentItem = 2;
            mPagerHandler.setCurrentItem(currentItem);
        }

        return rootView;
    }

    /**
     *     Adapter for the ViewPager above.
      */

    private class MyPageAdapter extends FragmentStatePagerAdapter {

        // Constructor
        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns fragment item at position i
        @Override
        public Fragment getItem(int i) {
            return viewFragments[i];
        }

        // Returns the number of pages in the viewFragment array
        @Override
        public int getCount() {
            return viewFragments.length;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if (isRtl) {
                //thanks to Udacity student josen (Jose) and grayraven42 for this code
                // This allows the titles in the PagerTabView to be switched for RTL locales.
                position = Utilities.inversePositionForRTL(position, getCount());
            }
            return getDayName(getActivity(), System.currentTimeMillis() + ((position - 2) * 86400000));

        }

        // Returns the day name of the given date
        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                return context.getString(R.string.tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                return context.getString(R.string.yesterday);
            } else {
                Time time = new Time();
                time.setToNow();

                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }

    // Checks if the phone is in a RTL-reading locale after checking build version
    public boolean checkIfRtl() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Configuration config = getResources().getConfiguration();

        int thisLayout = config.getLayoutDirection();
        int layoutRtl = View.LAYOUT_DIRECTION_RTL;
        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            if (thisLayout == layoutRtl) {
                return true;
            }
        }
        // If either the api version is lower than 17 or the layout direciton is not RTL,
        // return false.
        return false;
    }
}
