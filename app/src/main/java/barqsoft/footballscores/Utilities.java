package barqsoft.footballscores;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Various helper methods and constants.
 */
public class Utilities {
    public static final int BUNDESLIGA        = 394;
    public static final int LIGUE             = 396;
    public static final int PREMIER_LEAGUE    = 398;
    public static final int PRIMERA_DIVISION  = 399;
    public static final int SEGUNDA_DIVISION  = 400;
    public static final int SERIE_A           = 401;
    public static final int PRIMERA_LIGA      = 402;
    public static final int EREDIVISIE        = 404;
    public static final int CHAMPIONS_LEAGUE  = 405;

    public static String getLeague(int league_num) {
        Resources res = Resources.getSystem();
        switch (league_num) {
            case SERIE_A:
                return res.getString(R.string.seria_a);
            case PREMIER_LEAGUE:
                return res.getString(R.string.premier_league);
            case CHAMPIONS_LEAGUE:
                return res.getString(R.string.champions_league);
            case PRIMERA_DIVISION:
                return res.getString(R.string.primera_divison);
            case BUNDESLIGA:
                return res.getString(R.string.bundesliga);
            default:
                return res.getString(R.string.no_league_found);
        }
    }

    public static String getMatchDay(int match_day, int league_num) {
        Resources res = Resources.getSystem();
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return res.getString(R.string.group_stage_text);
            } else if (match_day == 7 || match_day == 8) {
                return res.getString(R.string.first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return res.getString(R.string.quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return res.getString(R.string.semi_final);
            } else {
                return res.getString(R.string.final_text);
            }
        } else {
            return res.getString(R.string.matchday_text) + ": " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        };
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }

    public static int inversePositionForRTL(int position, int total) {
        return total - position - 1;
    }

    public static String getFriendlyDate(String date){
        String[] months = Resources.getSystem().getStringArray(R.array.months);

        // Format of date is "yyyy-MM-dd"
        int monthNumber = Integer.parseInt(date.substring(5, 7));

        // Array starts at zero index, but months start at 1, so we shift one backwards
        // to read the array properly.
        return months[monthNumber - 1] + " " + date.substring(8);

    }

    public static String[] getRecentAndUpcomingMatchDates(){
        // This method looks one week before and one week ahead to get matches
        int twoWeeks = 14;
        String[] dates = new String[twoWeeks];
        Time t = new Time();
        t.setToNow();
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        for(int i = 0; i < twoWeeks; i++) {
            Date d = new Date(System.currentTimeMillis() + ((i - 7) * 86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            String date = mformat.format(d);
            dates[i] = date;
        }

        return dates;
    }

    public static String getTodaysDate(int offset){
        Date d = new Date(System.currentTimeMillis() + (offset * 86400000));
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String date = mformat.format(d);
        return date;
    }
}
