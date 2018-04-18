package com.example.achurm.pplhelper;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ExerciseListScreen extends AppCompatActivity {

    private ArrayList<TextView> exerciseTVs;

    private Exercise mExercise;
    private Exercise mExercises[] = new Exercise[6];
    private CheckBox mFavs[] = new CheckBox[6];
    private int mFavorites[] = new int[6];

    private int mNumber;

    private Button mPushButton, mPullButton, mLegsButton;
    private String whichPPL = "PUSH";

    /* Intent flags */
    private static final boolean USE_FLAG = true;
    private static final int mSaveFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
    private static final int mEditFlag = Intent.FLAG_ACTIVITY_NO_HISTORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list_screen);

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

        mFavs[0] = findViewById(R.id.favEx1);
        mFavs[1] = findViewById(R.id.favEx2);
        mFavs[2] = findViewById(R.id.favEx3);
        mFavs[3] = findViewById(R.id.favEx4);
        mFavs[4] = findViewById(R.id.favEx5);
        mFavs[5] = findViewById(R.id.favEx6);


        unBundle(getIntent());
        updateButtonBar();
        fillTextViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        unBundle(this.getIntent());
        updateButtonBar();
        fillTextViews();
    }

    public void unBundle(Intent intent) {

        Bundle b = intent.getExtras();

        if(b != null) {

            if (b.getString("ppl") != null) {
                whichPPL = b.getString("ppl");
            }
            if (b.getIntArray("favorites") != null) { //we passed data from StartScreen
                //DataBus temp = (DataBus) b.getSerializable("databus");
                mExercises[0] = b.getParcelable("exercise0");
                mExercises[1] = b.getParcelable("exercise1");
                mExercises[2] = b.getParcelable("exercise2");
                mExercises[3] = b.getParcelable("exercise3");
                mExercises[4] = b.getParcelable("exercise4");
                mExercises[5] = b.getParcelable("exercise5");

                mFavorites = b.getIntArray("favorites"); //.getFavorites();
            }
            /* will have to check for null results if startScreen passes extras with intent */
            else {
                whichPPL = b.getString("type");
                mExercise = b.getParcelable("exercise");
                mNumber = b.getInt("number");
                /* Refresh updated textViews */
                exerciseTVs.get(mNumber - 1).setText(String.format("%s\n%dx%d\t\t\t\t%d",
                        mExercise.getExercise(), mExercise.getSets(), mExercise.getReps(),
                        mExercise.getWeight()));
            }
        }
    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    /* Switch to startScreen */
    public void onSaveButtonClick(View v) {
        Intent mIntent = new Intent(this, StartScreen.class);

        Bundle b = new Bundle();
        b.putString("ppl", whichPPL);
        b.putParcelable("exercise0", mExercises[0]);
        b.putParcelable("exercise1", mExercises[1]);
        b.putParcelable("exercise2", mExercises[2]);
        b.putParcelable("exercise3", mExercises[3]);
        b.putParcelable("exercise4", mExercises[4]);
        b.putParcelable("exercise5", mExercises[5]);
        mIntent.putExtras(b);

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
        mExercise = mExercises[mNumber-1];

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

            Exercise temp = mExercises[i];

            if(temp != null)
                exerciseTVs.get(i).setText(String.format("%s\n%dx%d\t\t\t\t%d",
                    temp.getExercise(), temp.getSets(), temp.getReps(), temp.getWeight()));
        }
        for(int i=0; i<6; i++) {
            boolean isFav = (mFavorites[i]==1);
            mFavs[i].setChecked(isFav);
        }
    }

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

        boolean isFavorite = !(mFavorites[favIndex] == 1); //reverse current favorite
        ExerciseDBHandler handler = new ExerciseDBHandler(this);
        try {
            handler.createDatabase();
        } catch (IOException io) {
            throw new Error("Unable to create database");
        }
        Exercise temp = mExercises[favIndex];
        handler.updateExercise(temp.getExercise(), temp.getSets(), temp.getReps(), temp.getWeight(), isFavorite);
        fetchHistory();
        fillTextViews();
    }

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
        fetchHistory();
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
    public void fetchHistory() {
        ExerciseDBHandler handler = new ExerciseDBHandler(this);
        try {
            handler.createDatabase();
        } catch (IOException io) {
            throw new Error("Unable to create database");
        }

        DataBus temp = handler.getExercises(whichPPL);
        mExercises = temp.getExercises();
        mFavorites = temp.getFavorites();
    }
}
