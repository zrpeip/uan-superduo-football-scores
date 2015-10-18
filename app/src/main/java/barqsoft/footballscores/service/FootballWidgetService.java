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
    private static final int NUM_RESULTS =30;
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
        // Log.d(LOG_TAG, "in OnCreate ");

        Cursor cursor = mContext.getContentResolver().query(DatabaseContract.BASE_CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            // Log.d(LOG_TAG, "Cursor results: " + cursor.getCount());

            matchList = new ArrayList<>();
            int i = 0;
            cursor.moveToFirst();
            while (i < NUM_RESULTS) {
                String[] match = new String[4];
                match[0] = cursor.getString(COL_HOME);
                match[1] = cursor.getString(COL_AWAY);
                if (cursor.getString(COL_HOME_GOALS).equals("-1")) {
                    match[2] = " - : - ";
                } else {
                    match[2] = cursor.getString(COL_HOME_GOALS) +
                            " : " + cursor.getString(COL_AWAY_GOALS);
                }
                match[3] = Utilities.getFriendlyDate(cursor.getString(COL_DATE)) +
                        " at " + cursor.getString(COL_MATCHTIME);
                matchList.add(match);
                i++;
                cursor.moveToNext();
            }
            Log.d(LOG_TAG, "matchList.size(): " + matchList.size());
            for (String s : matchList.get(0)) {
              Log.d(LOG_TAG, "Match 0 in matchList: " + s);
            }
            cursor.close();
        } else {
            Log.d(LOG_TAG, "Widget cursor null!");
        }
    }

    /**
     * Populates each ListView item in the collection widget, using the matches in the ArrayList
     * "matchList." The first View acts as a simple header to identify the hosting app.
     */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv;
        if(position == 0) {
            rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_header);
        } else {
            // Construct a remote views item based on the app widget item XML file,
            // and set the text based on the position.
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
