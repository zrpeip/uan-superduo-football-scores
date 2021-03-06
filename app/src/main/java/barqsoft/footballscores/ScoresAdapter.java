package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * CursorAdapter used to populate the ListView score items from data in the database.
 * Also manages the click behavior of each ListView item - on clicking an item, a Share button
 * is inflated into the FrameLayout at the bottom of the scores_list_item XML layout.
 */
public class ScoresAdapter extends CursorAdapter {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    private String viewPagerTitle;

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    // Binds data from the API to each View populated by the ScoresAdapter.
    // Also creates content descriptions dynamically.
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        // Getting selected data from cursor for creating the view and adding content descriptions
        String homeName = cursor.getString(COL_HOME);
        String awayName = cursor.getString(COL_AWAY);
        String matchTime = cursor.getString(COL_MATCHTIME);
        String matchDay = cursor.getString(COL_MATCHDAY);
        int homeGoals = cursor.getInt(COL_HOME_GOALS);
        int awayGoals = cursor.getInt(COL_AWAY_GOALS);

        // Bind the data from the cursor to the view
        mHolder.home_name.setText(homeName);
        mHolder.away_name.setText(awayName);
        mHolder.date.setText(matchTime);
        mHolder.score.setText(Utilities.getScores(homeGoals, awayGoals));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        mHolder.away_crest.setImageResource(Utilities.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)
        ));

        // Set content description, depending on whether the match has already taken place or not.
        // Use a base description for both cases.
        String baseDescription = "Match at " + matchTime + " between " +
                homeName + " and " + awayName + ". ";

        if (homeGoals > -1 || awayGoals > -1) {
            view.setContentDescription(baseDescription + "Score: " +
                    String.valueOf(homeGoals) + " to " + String.valueOf(awayGoals) + ".");
        } else {
            view.setContentDescription(baseDescription);
        }

        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if (mHolder.match_id == detail_match_id) {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);

            // Add the content description to the Share button for this match.
            String shareText = context.getString(R.string.accessibility_share_button);
            share_button.setContentDescription(shareText);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
