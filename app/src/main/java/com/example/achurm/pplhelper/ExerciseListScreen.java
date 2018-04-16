package com.example.achurm.pplhelper;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ExerciseListScreen extends AppCompatActivity {

    private ArrayList<TextView> exerciseTVs;

    private Exercise mExercise;
    private int mNumber;

    private Button mPushButton, mPullButton, mLegsButton;
    private String whichPPL = "PULL";

    /* Intent flags */
    private static final boolean USE_FLAG = true;
    private static final int mSaveFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
    private static final int mEditFlag = Intent.FLAG_ACTIVITY_NO_HISTORY;


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
        setContentView(R.layout.activity_exercise_list_screen);

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


        /* Button bar pointers */
        mPushButton = (Button) findViewById(R.id.pushButton);
        mPullButton = (Button) findViewById(R.id.pullButton);
        mLegsButton = (Button) findViewById(R.id.legsButton);
        
        fillTextViews();

    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle b = this.getIntent().getExtras();

        if(b != null) {

            if(b.getString("ppl") != null) {
                String temp = b.getString("ppl");
                if(temp == "NEW") whichPPL = "PUSH";
                else whichPPL = temp;
            }
            else {

                /* will have to check for null results if startScreen passes extras with intent */
                mExercise = b.getParcelable("exercise");
                mNumber = b.getInt("number");


                /* Refresh updated textViews */
                exerciseTVs.get(mNumber - 1).setText(String.format("%s\n%dx%d\t\t\t\t%d",
                        mExercise.getExercise(), mExercise.getSets(), mExercise.getReps(),
                        mExercise.getWeight()));
            }
        }

        setIntent(intent);
    }

    /* Switch to startScreen */
    public void onSaveButtonClick(View v) {
        Intent mIntent = new Intent(this, StartScreen.class);

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
        Intent mIntent = new Intent(this, ExerciseCustomizeScreen.class);

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

        //add exercise and TextView number to bundle
        b.putParcelable("exercise", mExercise);
        b.putInt("number", mNumber);
        //determine exerciseType and add to bundle
        b.putString("type", whichPPL);

        mIntent.putExtras(b);

        if(USE_FLAG)
            mIntent.addFlags(mEditFlag);

        startActivity(mIntent);
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

    /* Code for 6 favorite buttons, this is being moved to customExerciseScreen for now
    public void onFavoriteButtonClick(View v) {
        int favIndex = 0;
        switch(v.getId()) {
            case R.id.favEx1:
                favIndex = 0;
                break;
            case R.id.favEx2:
                favIndex = 1;
                break;
            case R.id.favEx3:
                favIndex = 2;
                break;
            case R.id.favEx4:
                favIndex = 3;
                break;
            case R.id.favEx5:
                favIndex = 4;
                break;
            case R.id.favEx6:
                favIndex = 5;
                break;
        }

        ExerciseDBHandler dbhandler = new ExerciseDBHandler(this);

        String temp = exerciseTVs.get(favIndex).getText().toString();
        String[] tokens = temp.split("\\s+");
        String name = tokens[0];
        int weight = Integer.parseInt(exerciseTVs.get(favIndex).getText().toString();
        int sets = Integer.parseInt(mSetsView.getText().toString());
        int reps = Integer.parseInt(mRepsView.getText().toString());

        dbhandler.findExercise(name, weight, sets, reps);


        String query = "UPDATE Exercises SET Favorite = 1";
         + (FavoriteButton.isEnabled() ? 1 : 0)
    }
    */

    /* Button bar functions */
    /* Button bar functions */
    public void pplButtonClick(View v) {
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
        fillTextViews();
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
}
