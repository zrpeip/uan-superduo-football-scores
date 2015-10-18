package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.PagerFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Service that manages the collection widget's RemoteViewsFactory.
 */
public class FootballWidgetService extends RemoteViewsService {
    @Override
    public ScoreViewsFactory onGetViewFactory(Intent intent) {
        return (new ScoreViewsFactory(this.getApplicationContext(),
                intent));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

}

/**
 * ViewFactory that populates the collection widget's ListView with each view.
 * At position 0, there is a simple header describing the widget, and beyond that a similar
 * view to that found within the Football Scores application itself.
 */
class ScoreViewsFactory implements
        RemoteViewsService.RemoteViewsFactory {
    private static final int NUM_RESULTS = 30;
    private String LOG_TAG = "R V FACTORY";
    private int numPages;
    private int mAppWidgetId;
    private Context mContext;
    private ArrayList<String> dbDates;
    private ArrayList<String[]> matchList = new ArrayList<>();
    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;

    public ScoreViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    /**
     * Accesses database and populates a String array with each matches' details.
     * Each of those String arrays are then added to the ArrayList "matchList" which is used to
     * populate the RemoteViews in getViewAt().
     */
    @Override
    public void onCreate() {
        numPages = PagerFragment.NUM_PAGES;
        String todaysDate = Utilities.getTodaysDate(0);
        String selection = "date LIKE ?";
        String[] dates = Utilities.getRecentAndUpcomingMatchDates();
        matchList = new ArrayList<>();

        // Since I (Udacity student) couldn't find a way for Content Providers to support an SQL "WHERE .. IN"
        // statement to access a date range, the following structure is used:
        // 1. Utilities.getRecentAndUpcomingMatchDates() returns a String array with dates starting
        // a week before the current date and going to a week afterwards,
        // 2. A cursor is directed to the week-old date in the database and returns the matches for
        // that date,
        // 3. Those matches are written to matchList,
        // 4. The process is repeated for each date in the array (total of 14 dates, no maximum
        // number of matches).
        // There is probably a much better way to do this, but no more time to search.

        // Log.d(LOG_TAG, "in OnCreate ");
        for (int i = 0; i < dates.length; i++) {
            String[] selArg = new String[]{dates[i]};

            Cursor cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                    null, selection, selArg, null);

            if (cursor != null) {
                // Log.d(LOG_TAG, "Cursor results: " + cursor.getCount());

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    // Creates the string array that describes the match
                    String[] match = new String[4];

                    // Assign the nome team name
                    match[0] = cursor.getString(COL_HOME);

                    // Assign the away team name
                    match[1] = cursor.getString(COL_AWAY);

                    // Assign the score - if there is no score yet, return score filler string
                    if (cursor.getString(COL_HOME_GOALS).equals("-1")) {
                        match[2] = " - : - ";
                    } else {
                        match[2] = cursor.getString(COL_HOME_GOALS) +
                                " : " + cursor.getString(COL_AWAY_GOALS);
                    }

                    // Assign the match date and time information as one string.
                    // If the match is today, tomorrow, or yesterday, let the user know
                    // with an extra message.
                    String matchDate = cursor.getString(COL_DATE);
                    String matchTime = cursor.getString(COL_MATCHTIME);
                    if (todaysDate.equals(matchDate)) {
                        match[3] = "TODAY! - " + Utilities.getFriendlyDate(matchDate) +
                                " at " + matchTime;
                    } else if (Utilities.getTodaysDate(-1).equals(matchDate)) {
                        match[3] = "Yesterday " + Utilities.getFriendlyDate(matchDate) +
                                " at " + matchTime;
                    } else if (Utilities.getTodaysDate(1).equals(matchDate)) {
                        match[3] = "TOMORROW! - " + Utilities.getFriendlyDate(matchDate) +
                                " at " + matchTime;
                    } else {
                        match[3] =  Utilities.getFriendlyDate(matchDate) +
                                " at " + matchTime;
                    }

                    // Add the match string array to the ArrayList which will help in populating
                    // the RemoteViews object in getViewAt.
                    matchList.add(match);
                    cursor.moveToNext();
                }

                cursor.close();
//                Log.d(LOG_TAG, "matchList.size(): " + matchList.size());
//                for (String s : matchList.get(0)) {
//                    Log.d(LOG_TAG, "Match 0 in matchList: " + s);
//                }
            } else {
                Log.d(LOG_TAG, "Widget cursor null!");
            }
        }
    }

    /**
     * Populates each ListView item in the collection widget, using the matches in the ArrayList
     * "matchList." The first View acts as a simple header to identify the hosting app.
     */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv;

        if (position == 0) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_header);
        } else {
            // Construct a remote views item based on the app widget item XML file,
            // and set the text for each TextView..
            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
            String[] match = matchList.get(position - 1);

            rv.setTextViewText(R.id.widget_home_name, match[0]);
            rv.setTextViewText(R.id.widget_away_name, match[1]);
            rv.setTextViewText(R.id.widget_score_textview, match[2]);
            rv.setTextViewText(R.id.widget_data_textview, match[3]);

        }
        return rv;
    }

    @Override
    public void onDataSetChanged() {
        // Log.d(LOG_TAG, "in onDataSetChanged()");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return matchList.size();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * Returns the ViewTypeCount for the Factory.
     * 2 View types - widget_list_header (at position 0) and widget_list_item (> position 0)
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
