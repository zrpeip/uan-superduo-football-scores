package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;

/**
 * Created by meg on 14.10.15.
 */
public class FootballWidgetService extends RemoteViewsService {

    @Override
    public ScoreViewsFactory onGetViewFactory(Intent intent) {
        return(new ScoreViewsFactory(this.getApplicationContext(),
                intent));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}

class ScoreViewsFactory implements
        RemoteViewsService.RemoteViewsFactory {
    private String LOG_TAG = "R V FACTORY";
    private int mAppWidgetId;
    private Context mContext;

    public ScoreViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    @Override
    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        String[] days;
        Cursor cursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, null, null);
        if(cursor != null) {
            Log.d(LOG_TAG, "Cursor: " + cursor.getCount());
        } else {
            Log.d(LOG_TAG, "Cursor null!!!! ");

        }
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
