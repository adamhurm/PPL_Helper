package com.example.achurm.pplhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class editScreen extends AppCompatActivity implements TabHost.OnTabChangeListener {

    private ArrayList<TextView> exerciseTVs;

    private Exercise mExercise;
    private int mNumber;

    private TabHost tabHost;
    private String whichPPL = "PULL";

    /* Intent flags */
    private static final boolean USE_FLAG = true;
    private static final int mSaveFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
    private static final int mEditFlag = Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;


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
        setContentView(R.layout.activity_edit_screen);

        tabSetup();
        tabHost.setOnTabChangedListener(this);
        onNewIntent(getIntent());
        /*pullExerciseTV = new TextView[6][2]; //hardcoded for now

        /*
        pullExerciseTV[0][0] = (TextView)findViewById(R.id.editEx1op1);
        pullExerciseTV[0][1] = (TextView)findViewById(R.id.editEx1op2);
        pullExerciseTV[1][0] = (TextView)findViewById(R.id.editEx2op1);
        pullExerciseTV[1][1] = (TextView)findViewById(R.id.editEx2op2);
        pullExerciseTV[2][0] = (TextView)findViewById(R.id.editEx3op1);
        pullExerciseTV[2][1] = (TextView)findViewById(R.id.editEx3op2);
        pullExerciseTV[3][0] = (TextView)findViewById(R.id.editEx4op1);
        pullExerciseTV[3][1] = (TextView)findViewById(R.id.editEx4op2);
        pullExerciseTV[4][0] = (TextView)findViewById(R.id.editEx5op1);
        pullExerciseTV[4][1] = (TextView)findViewById(R.id.editEx5op2);
        pullExerciseTV[5][0] = (TextView)findViewById(R.id.editEx6op1);
        pullExerciseTV[5][1] = (TextView)findViewById(R.id.editEx6op2);
        */
        exerciseTVs = new ArrayList<TextView>();
        exerciseTVs.add((TextView)findViewById(R.id.nameEx1));
        exerciseTVs.add((TextView)findViewById(R.id.nameEx2));
        exerciseTVs.add((TextView)findViewById(R.id.nameEx3));
        exerciseTVs.add((TextView)findViewById(R.id.nameEx4));
        exerciseTVs.add((TextView)findViewById(R.id.nameEx5));
        exerciseTVs.add((TextView)findViewById(R.id.nameEx6));

        fillTextViews();

    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle b = this.getIntent().getExtras();
        if(b != null) {
            /* will have to check for null results if startScreen passes extras with intent */
            mExercise = b.getParcelable("exercise");
            mNumber = b.getInt("number");


            /* Refresh updated textViews */
            exerciseTVs.get(mNumber-1).setText(String.format("%s\n%dx%d\t\t\t\t%d",
                    mExercise.getExercise(), mExercise.getSets(), mExercise.getReps(),
                    mExercise.getWeight()));
        }

        setIntent(intent);
    }

    /* Switch to startScreen */
    public void onSaveButtonClick(View v) {
        Intent mIntent = new Intent(this, startScreen.class);

        /*
        Bundle b = new Bundle();
        b.putParcelable("exercise", mExercise);
        mIntent.putExtras(b);
        */

        if(USE_FLAG)
            mIntent.addFlags(mSaveFlag);

        startActivity(mIntent);
    }

    /* Switch to customExerciseScreen */
    public void onEditButtonClick(View v) {
        Intent mIntent = new Intent(this, customExerciseScreen.class);

        Bundle b = new Bundle();

        switch(v.getId()) {
            case R.id.editEx1:
                mNumber = 1;
                break;
            case R.id.editEx2:
                mNumber = 2;
                break;
            case R.id.editEx3:
                mNumber = 3;
                break;
            case R.id.editEx4:
                mNumber = 4;
                break;
            case R.id.editEx5:
                mNumber = 5;
                break;
            case R.id.editEx6:
                mNumber = 6;
                break;
            default: mNumber = 1;
        }
        if(whichPPL == "PULL") mExercise = pullExercises[mNumber-1]; //hardcoded exercises
        else if(whichPPL == "PUSH") mExercise = pushExercises[mNumber-1]; //hardcoded exercises
        else if(whichPPL == "LEGS") mExercise = legsExercises[mNumber-1]; //hardcoded exercises
        b.putParcelable("exercise", mExercise);
        b.putInt("number", mNumber);

        mIntent.putExtras(b);


        if(USE_FLAG)
            mIntent.addFlags(mEditFlag);

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

    @Override
    public void onTabChanged(String tabId) {
        Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
        whichPPL = tabId;
        fillTextViews();
    }

    public void fillTextViews() {
        /* Fill TextViews */
        for(int i=0; i<6; i++){

            Exercise temp = null;
            if(whichPPL == "PULL") temp = pullExercises[i];
            else if(whichPPL == "PUSH") temp = pushExercises[i];
            else if(whichPPL == "LEGS") temp = legsExercises[i];

            if(temp != null)
                exerciseTVs.get(i).setText(String.format("%s\n%dx%d\t\t\t\t%d",
                    temp.getExercise(), temp.getSets(), temp.getReps(), temp.getWeight()));
        }
    }
}
