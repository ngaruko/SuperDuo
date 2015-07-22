package barqsoft.footballscores;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by itl on 20/07/2015.
 */
public class ScoreWidgetIntentService extends IntentService {
    // A "projection" defines the columns that will be returned for each row
    private static final String[] SCORE_COLUMNS = {

            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_DAY,


    };


//    // Defines a string to contain the selection clause
//    String mSelectionClause = null;
//
//    // Initializes an array to contain selection arguments
//    String[] mSelectionArgs = {""};

    public double MATCH_ID = 0;
    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_MATCHDAY = 8;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

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

       SQLiteDatabase db = new ScoresDBHelper(getBaseContext()).getReadableDatabase();
       Cursor data = db.query(DatabaseContract.SCORES_TABLE, SCORE_COLUMNS, null, null, null, null, DatabaseContract.scores_table.DATE_COL + " ASC");


        if (data == null) {

            return;
        } else if (data.getCount() < 1) {
            data.close();
            Log.e("Data Empty", "No data");

        }
        else if (data.moveToFirst()) {


            String away = data.getString(COL_AWAY);
            String home = data.getString(COL_HOME);
            int away_goals = data.getInt(COL_AWAY_GOALS);
            int home_goals = data.getInt(COL_HOME_GOALS);
            int league = data.getInt(COL_LEAGUE);
            String mTime = data.getString(COL_MATCHTIME);
            int match_day = data.getInt(COL_MATCHDAY);
            String mDate = data.getString(COL_DATE);


            data.close();

            // Perform this loop procedure for each Today widget
            for (
                    int appWidgetId
                    : appWidgetIds)

            {
                int layoutId = R.layout.score_appwidget;
                //RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
                RemoteViews views = new RemoteViews(getPackageName(), layoutId);

                //TODO change the widget icon to generic icon

                views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(home));
                views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(away));
                views.setTextViewText(R.id.home_name, home);
                views.setTextViewText(R.id.away_name, away);
                views.setTextViewText(R.id.data_textview, mTime);
                views.setTextViewText(R.id.score_textview, Utilies.getScores(home_goals, away_goals));
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


}