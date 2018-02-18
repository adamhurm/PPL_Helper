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

import java.util.Locale;
import java.util.Set;
import java.util.Stack;

import static java.util.Locale.US;

public class startScreen extends AppCompatActivity {

    private TabHost tabHost;
    private TextView setData;
    private TextView repData;
    private TextView weightData;
    private TextView[][] recentExerciseTV;
    private Exercise[] recentExercise;
    private Exercise currentExercise;

    private int setCurrent = 1;

    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

    private static final Exercise[] dummyExercises = {
            new Exercise("Deadlift",205, 3,5),
            new Exercise("Deadlift", 200, 3,5),
            new Exercise("Deadlift", 195, 3,5),
            new Exercise("Deadlift", 190, 3,5)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        tabSetup();
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
            }
        });

        /* Weight, Set, Rep data */
        weightData = (TextView)findViewById(R.id.weightData);
        setData = (TextView)findViewById(R.id.setData);
        repData = (TextView)findViewById(R.id.repData);

        /* Exercise data */
        currentExercise = dummyExercises[0];

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


    public void tabSetup() {
        /* Tab Setup */
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
    }

    public void updateSetRepData() {
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

    /* Switch to editScreen */
    public void doneSetButtonPressed(View v) {
        setCurrent++;
        if(setCurrent > currentExercise.getSets()) {
            Toast.makeText(this, "Finished this exercise.", Toast.LENGTH_SHORT).show();
            setCurrent = 1;
        }
        updateSetRepData();
    }
}
