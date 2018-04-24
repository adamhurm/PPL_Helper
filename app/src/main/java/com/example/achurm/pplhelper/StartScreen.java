package com.example.achurm.pplhelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static java.util.Locale.US;

//TODO: Save currentExerciseNumber and currentSetNumber for keeping track of exercise onResume

public class StartScreen extends AppCompatActivity {
    /* Exercise Data */
    private Exercise currentExercise;
    private String whichPPL = "PUSH";
    private int currentExerciseNumber = 0;
    private int currentSetNumber = 1;

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

    private Exercise mExercises[] = new Exercise[6];
    private String mDates[] = new String[6];
    private int mFavorites[] = new int[6];

    private Exercise mHistoryExercises[] = new Exercise[3];
    private String mHistoryDates[] = new String[3];


    //TODO: Keep track of current exercise, save for next time you come back from splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        /* Weight, Set, Rep data */
        weightData = (TextView)findViewById(R.id.weightData);
        setData = (TextView)findViewById(R.id.setData);
        repData = (TextView)findViewById(R.id.repData);

        /* Timer pointers */
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());


        /* Button bar pointers */
        mPushButton = (Button) findViewById(R.id.pushButton);
        mPullButton = (Button) findViewById(R.id.pullButton);
        mLegsButton = (Button) findViewById(R.id.legsButton);

        exercise = (TextView)findViewById(R.id.exercise);
        nextExercise = (TextView)findViewById(R.id.nextExercise);

        fetchInfo();

        recentExerciseTV = new TextView[3][4];

        /* TextViews: 0 "Exercise", 1 "Weight", 2 "Set"x"Rep", 3 "Date" */
        recentExerciseTV[0][0] = (TextView)findViewById(R.id.entry_exercise1);
        recentExerciseTV[1][0] = (TextView)findViewById(R.id.entry_exercise2);
        recentExerciseTV[2][0] = (TextView)findViewById(R.id.entry_exercise3);
        recentExerciseTV[0][1] = (TextView)findViewById(R.id.entry_weight1);
        recentExerciseTV[1][1] = (TextView)findViewById(R.id.entry_weight2);
        recentExerciseTV[2][1] = (TextView)findViewById(R.id.entry_weight3);
        recentExerciseTV[0][2] = (TextView)findViewById(R.id.entry_setrep1);
        recentExerciseTV[1][2] = (TextView)findViewById(R.id.entry_setrep2);
        recentExerciseTV[2][2] = (TextView)findViewById(R.id.entry_setrep3);
        recentExerciseTV[0][3] = (TextView)findViewById(R.id.entry_time1);
        recentExerciseTV[1][3] = (TextView)findViewById(R.id.entry_time2);
        recentExerciseTV[2][3] = (TextView)findViewById(R.id.entry_time3);

        onResume();
        fetchHistory();
    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        updateSetRepData();
    }

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

    public void updateSetRepData() {
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
                recentExerciseTV[i][0].setText(mHistoryExercises[i].getExercise());
                recentExerciseTV[i][1].setText(String.format(US, "%d lbs",
                        mHistoryExercises[i].getWeight()));
                recentExerciseTV[i][2].setText(String.format(US, "%dx%d",
                        mHistoryExercises[i].getSets(), mHistoryExercises[i].getReps()));
                recentExerciseTV[i][3].setText(mHistoryDates[i]);
            }
            else {
                recentExerciseTV[i][0].setText("");
                recentExerciseTV[i][1].setText("");
                recentExerciseTV[i][2].setText("");
                recentExerciseTV[i][3].setText("");
            }
        }
    }

    /* Press Done Button */
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
        updateSetRepData();
    }


    /* Stopwatch (Chronometer) functions */
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

    /* Voice memo functions */
    public void onVoiceMemoClick(View v) {
        Intent mIntent = new Intent(this, VoiceMemos.class);

        if(USE_FLAG)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(mIntent);
    }


    /* Button bar functions */
    public void pplButton(View v) {
        switch(v.getId()) {
            case R.id.pushButton:
                whichPPL = "PUSH";
                break;
            case R.id.pullButton:
                whichPPL = "PULL";
                break;
            case R.id.legsButton:
                whichPPL = "LEGS";
                break;
        }
        updateButtonBar();
        fetchInfo();
        fetchHistory();
        updateSetRepData();
    }
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
