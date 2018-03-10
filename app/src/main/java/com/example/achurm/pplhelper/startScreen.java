package com.example.achurm.pplhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Set;
import java.util.Stack;

import static java.util.Locale.US;

public class startScreen extends AppCompatActivity implements TabHost.OnTabChangeListener {

    private TabHost tabHost;
    private TextView setData;
    private TextView repData;
    private TextView weightData;
    private TextView[][] recentExerciseTV;
    private Exercise[] recentExercise;
    private Exercise currentExercise;

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

        //Tab Setup
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        //Push
        TabHost.TabSpec spec1 = tabHost.newTabSpec("PUSH");
        spec1.setContent(R.id.pushTab);
        spec1.setIndicator("PUSH");
        tabHost.addTab(spec1);

        //Pull
        TabHost.TabSpec spec2 = tabHost.newTabSpec("PULL");
        spec2.setContent(R.id.pullTab);
        spec2.setIndicator("PULL");
        tabHost.addTab(spec2);

        //Legs
        TabHost.TabSpec spec3 = tabHost.newTabSpec("LEGS");
        spec3.setContent(R.id.legsTab);
        spec3.setIndicator("LEGS");
        tabHost.addTab(spec3);

        tabHost.setOnTabChangedListener(this);
        //tabSetup();


        /* Weight, Set, Rep data */
        weightData = (TextView)findViewById(R.id.weightData);
        setData = (TextView)findViewById(R.id.setData);
        repData = (TextView)findViewById(R.id.repData);

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
        Intent mIntent = new Intent(this, editScreen.class);

        if(USE_FLAG)
            mIntent.addFlags(mFlag);

        startActivity(mIntent);
    }

    /*
    public void tabSetup() {
        //Tab Setup
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        //Push
        TabHost.TabSpec spec1 = tabHost.newTabSpec("PUSH");
        spec1.setContent(R.id.pushTab);
        spec1.setIndicator("PUSH");
        tabHost.addTab(spec1);

        //Pull
        TabHost.TabSpec spec2 = tabHost.newTabSpec("PULL");
        spec2.setContent(R.id.pullTab);
        spec2.setIndicator("PULL");
        tabHost.addTab(spec2);

        //Legs
        TabHost.TabSpec spec3 = tabHost.newTabSpec("LEGS");
        spec3.setContent(R.id.legsTab);
        spec3.setIndicator("LEGS");
        tabHost.addTab(spec3);

        tabHost.setOnTabChangedListener(this);
    }
    */

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
    @Override
    public void onTabChanged(String tabId) {
        Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
        whichPPL = tabId;
        setCurrent = 1;
        currentExerciseNumber = 0;
        updateSetRepData();
    }
}
