package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.service.FootballFetchService;

/**
 * Fragments that are inflated into the ViewPager in PagerFragment for each day of scores.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public ScoresAdapter mScoresAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentDate = new String[1];
    private int last_selected_item = -1;

    public MainScreenFragment() {
    }

    private void updateScores() {
        Intent service_start = new Intent(getActivity(), FootballFetchService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(String date) {
        fragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Activate FootballFetchService to get JSON data
        updateScores();

        // Set ScoresAdapter onto the ListView in fragment_main
        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mScoresAdapter = new ScoresAdapter(getActivity(), null, 0);
        scoreList.setAdapter(mScoresAdapter);
        // Initialize the cursor loader to work with the ScoresAdapter (CursorAdapter)
        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        // TODO ?? I don't undrestand this yet.
        // When is MainActivity.selected_match_id initialized? In the Loader?
        mScoresAdapter.detail_match_id = MainActivity.selected_match_id;

        // Set up click behavior for each item in the ListView using the ViewHolder pattern
        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mScoresAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mScoresAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Get the matches that occur on each relevant date found in the DB
        return new CursorLoader(getActivity(), DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, fragmentDate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */
            int i = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                i++;
                cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mScoresAdapter.swapCursor(cursor);
        //mScoresAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mScoresAdapter.swapCursor(null);
    }


}
