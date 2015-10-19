package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import barqsoft.footballscores.service.FootballWidgetService;

/**
 * Manages the Football Scores app widget, a collection widget that displays the scores
 * of matches 1 week before and 1 week after the current date.
 * Calls to the FootballWidgetService and its ScoreViewsFactory to populate the ListView of the
 * widget_layout XML file.
 */
public class FootballWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_CLICK = "ACTION_CLICK";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Set up the intent that starts the StackViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, FootballWidgetService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects
            // to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, intent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews
            // object above.
            rv.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            Intent launchIntent = new Intent(context, FootballWidgetProvider.class);
            // Set the action for the intent.
            // When the user touches a particular view, it will have the effect of
            // broadcasting ACTION_CLICK.
            launchIntent.setAction(FootballWidgetProvider.ACTION_CLICK);
            launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            launchIntent.setData(Uri.parse(launchIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_list_view, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Receives broadcast click events from the RemoteViews within the widget's ListView.
     * For the moment, all click events open the app to the standard view (if time, would be
     * modified to open to the specific match day that was clicked).
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(ACTION_CLICK)) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
        super.onReceive(context, intent);
    }
}
