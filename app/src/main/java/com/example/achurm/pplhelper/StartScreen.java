package com.example.achurm.pplhelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import static java.util.Locale.US;

public class StartScreen extends AppCompatActivity {

    private TextView weightData, setData, repData;
    private TextView[][] recentExerciseTV;
    private Exercise[] recentExercise;
    private Exercise currentExercise;

    /* Timer */
    private Chronometer mChronometer;
    private Button startWatchButton, stopWatchButton, resetWatchButton;

    /* Button bar */
    private Button mPullButton, mPushButton, mLegsButton;

    private TextView exercise;
    private TextView nextExercise;

    private String whichPPL = "PULL";

    private int currentExerciseNumber = 0;
    private int setCurrent = 1;

    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


    /* This will be replaced with non-dummy data once a user accumulates a history of workouts */
    private static final Exercise[] dummyExercises = {
            new Exercise("Deadlift",205, 3,5),
            new Exercise("Deadlift", 200, 3,5),
            new Exercise("Deadlift", 195, 3,5),
            new Exercise("Deadlift", 190, 3,5)
    };

    /* This will fill the app with initial exercises */
    private static Exercise[] pullExercises = {
            new Exercise("Deadlift", 200, 3, 5),
            new Exercise("Barbell Rows", 90, 3, 5),
            new Exercise("Pullups", 0, 3, 8),
            new Exercise("Seated Cable Rows", 160, 3, 12),
            new Exercise("Dumbbell Curls", 40, 4, 12),
            new Exercise("Face Pulls", 40, 5, 15)
    };

    private static Exercise[] pushExercises = {
            new Exercise("Bench Press", 100, 5, 5),
            new Exercise("Overhead Press", 90, 3, 5),
            new Exercise("Incline Bench Press", 70, 3, 5),
            new Exercise("DB Side Lateral Raise", 10, 3, 12),
            new Exercise("Triceps Pushdown", 50, 3, 12),
            new Exercise("Overhead Triceps Extension", 50, 3, 12)
    };

    private static Exercise[] legsExercises = {
            new Exercise("Squat", 180, 3, 5),
            new Exercise("Romanian Deadlift", 90, 3, 12),
            new Exercise("Leg Press", 240, 3, 12),
            new Exercise("Leg Curls", 70, 3, 12),
            new Exercise("Calf Raises", 50, 3, 12),
            new Exercise("Weighted Crunches", 100, 3, 12)
    };

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
        startWatchButton = (Button)findViewById(R.id.startWatchButton);
        stopWatchButton = (Button)findViewById(R.id.stopWatchButton);
        resetWatchButton = (Button)findViewById(R.id.resetWatchButton);

        /* Button bar pointers */
        mPushButton = (Button) findViewById(R.id.pushButton);
        mPullButton = (Button) findViewById(R.id.pullButton);
        mLegsButton = (Button) findViewById(R.id.legsButton);

        /* Exercise data */
        //fill current exercise
        if(whichPPL == "PULL") currentExercise = pullExercises[currentExerciseNumber];
        else if(whichPPL == "PUSH") currentExercise = pushExercises[currentExerciseNumber];
        else if(whichPPL == "PUSH") currentExercise = legsExercises[currentExerciseNumber];
        exercise = (TextView)findViewById(R.id.exercise);
        exercise.setText(currentExercise.getExercise());


        //fill next exercise
        Exercise nextExerciseTemp = null;
        if(whichPPL == "PULL") nextExerciseTemp = pullExercises[(currentExerciseNumber+1) % pullExercises.length];
        else if(whichPPL == "PUSH") nextExerciseTemp = pushExercises[(currentExerciseNumber+1) % pushExercises.length];
        else if(whichPPL == "LEGS") nextExerciseTemp = legsExercises[(currentExerciseNumber+1) % legsExercises.length];
        nextExercise = (TextView)findViewById(R.id.nextExercise);

        if (nextExerciseTemp != null) nextExercise.setText(nextExerciseTemp.getExercise());

        /* Recent Exercise data */
        recentExercise = new Exercise[3];
        recentExerciseTV = new TextView[3][3];

        /* Fill with Dummy Data */
        System.arraycopy(dummyExercises, 1, recentExercise, 0, recentExercise.length);

        /* TextViews: 0 "Exercise", 1 "Weight", 2 "Set"x"Rep" */
        recentExerciseTV[0][0] = (TextView)findViewById(R.id.entry_exercise1);
        recentExerciseTV[1][0] = (TextView)findViewById(R.id.entry_exercise2);
        recentExerciseTV[2][0] = (TextView)findViewById(R.id.entry_exercise3);
        recentExerciseTV[0][1] = (TextView)findViewById(R.id.entry_weight1);
        recentExerciseTV[1][1] = (TextView)findViewById(R.id.entry_weight2);
        recentExerciseTV[2][1] = (TextView)findViewById(R.id.entry_weight3);
        recentExerciseTV[0][2] = (TextView)findViewById(R.id.entry_setrep1);
        recentExerciseTV[1][2] = (TextView)findViewById(R.id.entry_setrep2);
        recentExerciseTV[2][2] = (TextView)findViewById(R.id.entry_setrep3);

        updateSetRepData();
    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onEditButtonClick(View v) {
        Intent mIntent = new Intent(this, ExerciseListScreen.class);

        if(USE_FLAG)
            mIntent.addFlags(mFlag);

        startActivity(mIntent);
    }

    public void updateSetRepData() {
        /* Get next exercise */
        Exercise nextExerciseTemp = null;
        if (whichPPL == "PULL") {
            currentExercise = pullExercises[currentExerciseNumber];
             nextExerciseTemp = pullExercises[(currentExerciseNumber+1) % pullExercises.length];
        }
        else if (whichPPL == "PUSH") {
            currentExercise = pushExercises[currentExerciseNumber];
            nextExerciseTemp = pushExercises[(currentExerciseNumber+1) % pullExercises.length];
        }
        else if (whichPPL == "LEGS") {
            currentExercise = legsExercises[currentExerciseNumber];
            nextExerciseTemp = legsExercises[(currentExerciseNumber+1) % pullExercises.length];
        }
        /* Set names for current and next exercise */
        exercise.setText(currentExercise.getExercise());

        if (nextExerciseTemp != null) nextExercise.setText(nextExerciseTemp.getExercise());

        weightData.setText( String.format(US, "%d lbs", currentExercise.getWeight()) );
        setData.setText( String.format(US, "%d / %d", setCurrent, currentExercise.getSets()) );
        repData.setText( String.format(US, "%d", currentExercise.getReps()) );

        /* Populate Exercise TextViews */
        for(int i=0; i<recentExerciseTV.length; i++) {
            recentExerciseTV[i][0].setText(recentExercise[i].getExercise());
            recentExerciseTV[i][1].setText( String.format(US, "%d lbs",
                    recentExercise[i].getWeight()) );
            recentExerciseTV[i][2].setText( String.format(US, "%dx%d",
                    recentExercise[i].getSets(), recentExercise[i].getReps()) );
        }
    }

    /* Press Done Button */
    public void doneSetButtonPressed(View v) {
        /* Jump to next set */
        setCurrent++;
        if(setCurrent > currentExercise.getSets()) {
            Toast.makeText(this, "Finished this exercise.", Toast.LENGTH_SHORT).show();
            setCurrent = 1;


            /* Jump to next exercise */
            currentExerciseNumber++;
            if(currentExerciseNumber >= pullExercises.length) {
                Toast.makeText(this, "Finished workout!", Toast.LENGTH_LONG).show();
                currentExerciseNumber = 0;
            }

        }
        updateSetRepData();
    }

    /* Stopwatch (Chronometer) functions */
    public void onStartWatchClicked(View v) {
        mChronometer.start();
    }
    public void onStopWatchClicked(View v) {
        mChronometer.stop();
    }
    public void onResetWatchClicked(View v) {
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    /* Button bar functions */
    public void pplButtonClick(View v) {

        switch(v.getId()) {
            case R.id.pushButton:
                whichPPL = "PUSH";
                mPushButton.setTextColor(Color.BLACK);
                mPullButton.setTextColor(Color.parseColor("#165597"));
                mLegsButton.setTextColor(Color.parseColor("#165597"));
                break;
            case R.id.pullButton:
                whichPPL = "PULL";
                mPushButton.setTextColor(Color.parseColor("#165597"));
                mPullButton.setTextColor(Color.BLACK);
                mLegsButton.setTextColor(Color.parseColor("#165597"));
                break;
            case R.id.legsButton:
                whichPPL = "LEGS";
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
        updateSetRepData();
    }


}
