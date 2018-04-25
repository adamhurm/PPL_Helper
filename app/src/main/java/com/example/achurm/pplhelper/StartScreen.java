package com.example.achurm.pplhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static java.util.Locale.US;

/** Lab 6 **/
//DONE? - Db values being overridden (30)
/* TODO: Ask AI if I can reset Set/Exercise Counters when changing between PPL
   Save counters when switching between PUSH/PULL/LEGS ?
   Two Options:
   1. We have partially completed workouts; need to add reset button to easily restart workout
   2. Don't save counters, but user loses progress if they misclick on PUSH/PULL/LEGS
 */
//TODO - Lab 6 document (20)
//TODO - Pixel 2 test (20)
//DONE - Fav button fix onResume (30)

public class StartScreen extends AppCompatActivity {
    /* Current Exercise Data */
    private Exercise currentExercise;
    private String whichPPL = "PUSH";
    private int currentExerciseNumber = 0;
    private int currentSetNumber = 1;

    /* Exercise, Dates, and Favorites Data */
    private Exercise mExercises[] = new Exercise[6];
    private String mDates[] = new String[6];
    private int mFavorites[] = new int[6];

    /* Historical Exercises and Dates data */
    private Exercise mHistoryExercises[] = new Exercise[3];
    private String mHistoryDates[] = new String[3];

    /* Exercise TVs */
    private TextView exercise;
    private TextView weightData, setData, repData;
    private TextView nextExercise;
    private TextView[][] recentExerciseTV;

    /* Timer */
    private Chronometer mChronometer;
    private long trackingTime = 0;

    /* Button bar */
    private Button mPullButton, mPushButton, mLegsButton;

    /* Intent flags */
    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


    /** Override default activity management functions **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        /* Weight, Set, Rep data */
        weightData = findViewById(R.id.weightData);
        setData = findViewById(R.id.setData);
        repData = findViewById(R.id.repData);

        /* Timer pointers */
        mChronometer = findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());


        /* Button bar pointers */
        mPushButton = findViewById(R.id.pushButton);
        mPullButton = findViewById(R.id.pullButton);
        mLegsButton = findViewById(R.id.legsButton);

        exercise = findViewById(R.id.exercise);
        nextExercise = findViewById(R.id.nextExercise);

        fetchInfo();

        recentExerciseTV = new TextView[3][4];

        /* TextViews: 0 "Exercise", 1 "Weight", 2 "Set"x"Rep", 3 "Date" */
        recentExerciseTV[0][0] = findViewById(R.id.entry_exercise1);
        recentExerciseTV[1][0] = findViewById(R.id.entry_exercise2);
        recentExerciseTV[2][0] = findViewById(R.id.entry_exercise3);
        recentExerciseTV[0][1] = findViewById(R.id.entry_weight1);
        recentExerciseTV[1][1] = findViewById(R.id.entry_weight2);
        recentExerciseTV[2][1] = findViewById(R.id.entry_weight3);
        recentExerciseTV[0][2] = findViewById(R.id.entry_setrep1);
        recentExerciseTV[1][2] = findViewById(R.id.entry_setrep2);
        recentExerciseTV[2][2] = findViewById(R.id.entry_setrep3);
        recentExerciseTV[0][3] = findViewById(R.id.entry_time1);
        recentExerciseTV[1][3] = findViewById(R.id.entry_time2);
        recentExerciseTV[2][3] = findViewById(R.id.entry_time3);

        onResume();
        fetchHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* Retrieve current exercise and set through SharedPreferences */
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        currentExerciseNumber = sharedPref.getInt("currentExerciseNumber", 0);
        currentSetNumber = sharedPref.getInt("currentSetNumber", 1);

        /* Unbundle whichPPL, exercises, favorites, and dates */
        Bundle b = getIntent().getExtras();
        if(b.getString("ppl") != null) {
            whichPPL = b.getString("ppl");
        }
        if(b.getParcelable("exercise0") != null) {

            mExercises[0] = b.getParcelable("exercise0");
            mExercises[1] = b.getParcelable("exercise1");
            mExercises[2] = b.getParcelable("exercise2");
            mExercises[3] = b.getParcelable("exercise3");
            mExercises[4] = b.getParcelable("exercise4");
            mExercises[5] = b.getParcelable("exercise5");

            mFavorites = b.getIntArray("favorites");
            mDates = b.getStringArray("dates");
            /*DataBus temp = (DataBus) b.getSerializable("databus");
            mExercises = temp.getExercises();
            mDates = temp.getDates();
            mFavorites = temp.getFavorites();*/
        }
        updateButtonBar();
        updateScreenData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /* Save current exercise and set to SharedPreferences */
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("currentExerciseNumber", currentExerciseNumber);
        editor.putInt("currentSetNumber", currentSetNumber);
        editor.apply(); //asynchronous to avoid UI stuttering
    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /** Update the GUI **/
    public void updateScreenData() {
         /* set current exercise */
        currentExercise = mExercises[currentExerciseNumber];
        exercise.setText(currentExercise.getExercise());

        /* set next exercise */
        String nextExerciseTemp;
        if(currentExerciseNumber+1 >= 6) {
            nextExerciseTemp = "Finished!";
        } else {
            nextExerciseTemp = mExercises[currentExerciseNumber+1].getExercise();
        }
        nextExercise.setText(nextExerciseTemp);


        weightData.setText( String.format(US, "%d lbs", currentExercise.getWeight()) );
        setData.setText( String.format(US, "%d / %d", currentSetNumber, currentExercise.getSets()) );
        repData.setText( String.format(US, "%d", currentExercise.getReps()) );

        /* Populate History Exercise TextViews */
        for(int i=0; i<mHistoryExercises.length; i++) {

            if(mHistoryExercises[i] != null) {
                //fill if an exercise exists
                recentExerciseTV[i][0].setText(mHistoryExercises[i].getExercise());
                recentExerciseTV[i][1].setText(String.format(US, "%d lbs",
                        mHistoryExercises[i].getWeight()));
                recentExerciseTV[i][2].setText(String.format(US, "%dx%d",
                        mHistoryExercises[i].getSets(), mHistoryExercises[i].getReps()));
                recentExerciseTV[i][3].setText(mHistoryDates[i]);
            }
            else {
                //handle empty exercises
                recentExerciseTV[i][0].setText("");
                recentExerciseTV[i][1].setText("");
                recentExerciseTV[i][2].setText("");
                recentExerciseTV[i][3].setText("");
            }
        }
    }

    /** New Activity Button Functions **/
    /* Switch to ExerciseListScreen */
    public void onEditButtonClick(View v) {
        Intent mIntent = new Intent(this, ExerciseListScreen.class);

        if(USE_FLAG)
            mIntent.addFlags(mFlag);

        Bundle b = new Bundle();
        b.putString("ppl", whichPPL);

        b.putParcelable("exercise0", mExercises[0]);
        b.putParcelable("exercise1", mExercises[1]);
        b.putParcelable("exercise2", mExercises[2]);
        b.putParcelable("exercise3", mExercises[3]);
        b.putParcelable("exercise4", mExercises[4]);
        b.putParcelable("exercise5", mExercises[5]);

        b.putStringArray("dates", mDates);
        b.putIntArray("favorites", mFavorites);
        /*
        DataBus temp = new DataBus("exercises");
        temp.setExercises(mExercises);
        temp.setDates(mDates);
        temp.setFavorites(mFavorites);
        b.putSerializable("databus", temp);
        */

        mIntent.putExtras(b);

        startActivity(mIntent);
    }

    /** Handle when sets are completed **/
    public void doneSetButtonPressed(View v) {
        /* Jump to next set */
        currentSetNumber++;
        if(currentSetNumber > currentExercise.getSets()) {
            Toast.makeText(this, "Finished "+currentExercise.getExercise(), Toast.LENGTH_SHORT).show();
            currentSetNumber = 1;


            /* Jump to next exercise */
            currentExerciseNumber++;
            if(currentExerciseNumber >= 6) {
                Toast.makeText(this, "Finished workout!", Toast.LENGTH_LONG).show();
                currentExerciseNumber = 0;
            }

        }
        updateScreenData();
    }

    /** Stopwatch (Chronometer) button functions **/
    public void onStartWatchClicked(View v) {
        if(trackingTime == 0) {
            //starting for first time
            mChronometer.setBase(SystemClock.elapsedRealtime());
        } else {
            //picking up where we left off
            mChronometer.setBase(SystemClock.elapsedRealtime() - trackingTime);
        }
        mChronometer.start();
    }
    public void onStopWatchClicked(View v) {
        mChronometer.stop();
        trackingTime = SystemClock.elapsedRealtime() - mChronometer.getBase();

    }
    public void onResetWatchClicked(View v) {
        trackingTime = 0;
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    /** Voice memo button functions **/
    public void onVoiceMemoClick(View v) {
        Intent mIntent = new Intent(this, VoiceMemos.class);

        if(USE_FLAG)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(mIntent);
    }


    /** Button bar functions **/
    /* Change whichPPL and reset the Exercise/Set counters */
    public void pplButton(View v) {
        switch(v.getId()) {
            case R.id.pushButton:
                whichPPL = "PUSH";
                currentExerciseNumber = 0;
                currentSetNumber = 1;
                break;
            case R.id.pullButton:
                whichPPL = "PULL";
                currentExerciseNumber = 0;
                currentSetNumber = 1;
                break;
            case R.id.legsButton:
                whichPPL = "LEGS";
                currentExerciseNumber = 0;
                currentSetNumber = 1;
                break;
        }

        updateButtonBar();  //color change
        fetchInfo();        //grab exercises, dates, favorites
        fetchHistory();     //grab history
        updateScreenData(); //update the gui
    }

    /* Handle the bar color change based on whichPPL */
    public void updateButtonBar() {
        switch(whichPPL) {
            case "PUSH":
                mPushButton.setTextColor(Color.BLACK);
                mPullButton.setTextColor(Color.parseColor("#165597"));
                mLegsButton.setTextColor(Color.parseColor("#165597"));
                break;
            case "PULL":
                mPushButton.setTextColor(Color.parseColor("#165597"));
                mPullButton.setTextColor(Color.BLACK);
                mLegsButton.setTextColor(Color.parseColor("#165597"));
                break;
            case "LEGS":
                mPushButton.setTextColor(Color.parseColor("#165597"));
                mPullButton.setTextColor(Color.parseColor("#165597"));
                mLegsButton.setTextColor(Color.BLACK);
                break;
            default:
                mPushButton.setTextColor(Color.BLACK);
                mPullButton.setTextColor(Color.parseColor("#165597"));
                mLegsButton.setTextColor(Color.parseColor("#165597"));
                break;
        }
    }

    /** Fetch information from database **/
    /* Fetch exercises, dates, favorites */
    public void fetchInfo() {
        ExerciseDBHandler handler = new ExerciseDBHandler(this);
        try {
            handler.createDatabase();
        } catch (IOException io) {
            throw new Error("Unable to create database");
        }

        DataBus temp = handler.getExercises(whichPPL);
        mExercises = temp.getExercises();
        mDates = temp.getDates();
        mFavorites = temp.getFavorites();
    }

    /* Fetch history exercises */
    public void fetchHistory() {
        ExerciseDBHandler handler = new ExerciseDBHandler(this);
        try {
            handler.createDatabase();
        } catch (IOException io) {
            throw new Error("Unable to create database");
        }

        DataBus temp = handler.getHistory(currentExercise.getExercise());
        mHistoryExercises = temp.getExercises();
        mHistoryDates = temp.getDates();
    }
}
