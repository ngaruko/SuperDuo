package barqsoft.footballscores;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by itl on 20/07/2015.
 */
public class ScoreWidgetIntentService extends IntentService {



    private static final String[] SCORE_COLUMNS = {

            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.MATCH_DAY,
            //  DatabaseContract.scores_table.L




    };


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


    public ScoreWidgetIntentService() {
        super("ScoreWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("handling ", "widget");
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoreWidgetProvider.class));

        Uri leagueUri=DatabaseContract.scores_table.buildScoreWithLeague();
        Cursor data = getContentResolver().query(leagueUri, SCORE_COLUMNS, null,
                        null, DatabaseContract.scores_table.DATE_COL + " ASC");
        if (data == null) {

           // new MainScreenFragment().update_scores();
        }
        if (!data.moveToFirst()) {
            data.close();

        }

        // Extract the weather data from the Cursor
        //Match data
        int league = 0;
        String mDate = null;
        String mTime = "4:00";
        String home = "TEST1";
        String away = "TEST2";
        int home_goals=0 ;
        int away_goals=0;
        String match_id = null;
        int match_day = 0;
        if (data.moveToFirst()) {
            int matchId = data.getInt(COL_ID);
            //  int weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId);
            away = data.getString(COL_AWAY);
            home = data.getString(COL_HOME);
           away_goals = data.getInt(COL_AWAY_GOALS);
             home_goals = data.getInt(COL_HOME_GOALS);
        league = data.getInt(COL_LEAGUE);
            mTime = data.getString(COL_MATCHTIME);
             match_day=data.getInt(COL_MATCHDAY);
            mDate=data.getString(COL_DATE);
        }



       data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
          int layoutId = R.layout.score_appwidget;
            //RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            //TODO change the widget icon to generic icon

            views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(home));
            views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(away));
            views.setTextViewText(R.id.home_name, home);
            views.setTextViewText(R.id.away_name,  away);
            views.setTextViewText(R.id.data_textview, mTime);
            views.setTextViewText(R.id.score_textview, Utilies.getScores(home_goals,away_goals));
//            views.setTextViewText(R.id.league_textview,Utilies.getLeague(league));
//            views.setTextViewText(R.id.matchday_textview,Utilies.getMatchDay(match_day,league));



            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }


}