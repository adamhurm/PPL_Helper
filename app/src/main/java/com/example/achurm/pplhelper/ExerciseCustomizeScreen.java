package com.example.achurm.pplhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by achurm on 3/8/18.
 */

public class ExerciseCustomizeScreen extends AppCompatActivity {

    /* Intent flags */
    private static final boolean USE_FLAG = true;
    private static final int mSaveFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

    private Exercise mExercise;
    private int mNumber; //textView number
    private String mExerciseType;

    private TextView mExerciseNameView, mSetsView, mRepsView, mWeightView;
    private CheckBox mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_customize_screen);

        /* TextViews for Exercise entry */
        mExerciseNameView = (TextView) findViewById(R.id.editExercise);
        mSetsView = (TextView) findViewById(R.id.editSet);
        mRepsView = (TextView) findViewById(R.id.editRep);
        mWeightView = (TextView) findViewById(R.id.editWeight);
        mFavorite = (CheckBox) findViewById(R.id.toggleFavorite);

        unBundle(getIntent());
        updateFavorite();

    }
    /* Switching with flags */
    @Override
    protected void onResume() {
        /* Change this to onResume - separate filling code to function, call from onCreate and onNewIntent */
        super.onResume();

        unBundle(getIntent());
        updateFavorite();
    }

    public void unBundle(Intent intent) {
        Bundle b = intent.getExtras();
        /* Check if bundle has content, if it does, assign exercise information to TVs */
        if(b != null) {
            mExercise = b.getParcelable("exercise");
            mNumber = b.getInt("number");
            mExerciseType = b.getString("type");

            if(mExercise == null)
                Toast.makeText(this, "Exercise is null", Toast.LENGTH_SHORT).show();
            else {
                mExerciseNameView.setText(mExercise.getExercise());
                mSetsView.setText(Integer.toString(mExercise.getSets()));
                mRepsView.setText(Integer.toString(mExercise.getReps()));
                mWeightView.setText(Integer.toString(mExercise.getWeight()));
            }
        }
    }

    public boolean addClick(View v) {
        /* Check for empty fields */
        if(mExerciseNameView.getText().toString().isEmpty()
                || mSetsView.getText().toString().isEmpty()
                || mRepsView.getText().toString().isEmpty()
                || mWeightView.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Unable to add exercise.\nPlease check that all fields are filled in.",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            /* collect variables */
            String name = mExerciseNameView.getText().toString();
            int sets = Integer.parseInt(mSetsView.getText().toString());
            int reps = Integer.parseInt(mRepsView.getText().toString());
            int weight = Integer.parseInt(mWeightView.getText().toString());
            boolean isFavorite = mFavorite.isChecked();

            ExerciseDBHandler handler = new ExerciseDBHandler(this);
            try {
                handler.createDatabase();
            } catch (IOException io) {
                throw new Error("Unable to create database");
            }

            Exercise exercise = handler.findExercise(name, weight, sets, reps);

            /* add new exercise to database */
            if (exercise == null) {
                mExercise = new Exercise(name, weight, sets, reps);
                handler.addExercise(mExercise, isFavorite, mExerciseType);
                Toast.makeText(this,
                    String.format("%s exercise was added to the database.", name),
                    Toast.LENGTH_SHORT).show();
            }
            /* update exercise favorite & timestamp */
            else if(handler.testFavorite(name, sets, reps, weight) != isFavorite) {
                handler.updateExercise(name, sets, reps, weight, isFavorite);
                Toast.makeText(this,
                        String.format("%s exercise was marked as favorite.", name),
                        Toast.LENGTH_SHORT).show();
            }
            /* do nothing, database is up to date */
            else {
                Toast.makeText(this,
                        String.format("%s exercise already exists in the database with the given info.", name),
                        Toast.LENGTH_SHORT).show();
            }

            /* Reset TextViews */
            mExerciseNameView.setText("");
            mSetsView.setText("");
            mRepsView.setText("");
            mWeightView.setText("");

            return true;
        }
    }

    public void findClick(View v) {
        /* collect search variables */
        String s_name = mExerciseNameView.getText().toString();
        int s_sets = -1;
        int s_reps = -1;
        int s_weight = -1;
        /* check for partial fields */
        if(s_name.isEmpty()) {
            Toast.makeText(this,
                    "Please enter an exercise name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mSetsView.getText().toString().isEmpty())
            s_sets = Integer.parseInt(mSetsView.getText().toString());
        if(!mRepsView.getText().toString().isEmpty())
            s_reps = Integer.parseInt(mRepsView.getText().toString());
        if(!mWeightView.getText().toString().isEmpty())
            s_weight = Integer.parseInt(mWeightView.getText().toString());

        ExerciseDBHandler handler = new ExerciseDBHandler(this);
        try {
            handler.createDatabase();
        } catch (IOException io) {
        throw new Error("Unable to create database");
        }


        /* check if exercise exists, using exerciseName or all attrs */

        Exercise exercise;

        if(s_sets == -1 || s_reps == -1 || s_weight == -1)
            exercise = handler.findExercise(s_name);
        else
            exercise = handler.findExercise(s_name, s_weight, s_sets, s_reps);




        if(exercise != null) {
            String exerciseName = exercise.getExercise();
            int sets = exercise.getSets();
            int reps = exercise.getReps();
            int weight = exercise.getWeight();

            mExerciseNameView.setText(exerciseName);
            mSetsView.setText(Integer.toString(sets));
            mRepsView.setText(Integer.toString(reps));
            mWeightView.setText(Integer.toString(weight));
        } else {
            Toast.makeText(this, "Exercise not found", Toast.LENGTH_SHORT).show();
            //mExerciseNameView.setText("Exercise not found");
        }
    }

    public void deleteClick(View v) {
        /* Check that all fields are filled in */
        if(mExerciseNameView.getText().toString().isEmpty()
                || mSetsView.getText().toString().isEmpty()
                || mRepsView.getText().toString().isEmpty()
                || mWeightView.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    ("Unable to delete exercise.\n" +
                            "Please check that all fields are filled in."),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            String s_name = mExerciseNameView.getText().toString();
            int s_sets = Integer.parseInt(mSetsView.getText().toString());
            int s_reps = Integer.parseInt(mRepsView.getText().toString());
            int s_weight = Integer.parseInt(mWeightView.getText().toString());

            ExerciseDBHandler handler = new ExerciseDBHandler(this);
            try {
                handler.createDatabase();
            } catch (IOException io) {
                throw new Error("Unable to create database");
            }

            boolean result = handler.deleteExercise(s_name, s_sets, s_reps, s_weight);

            if (result) {
                Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show();
                mExerciseNameView.setText("");
                mSetsView.setText("");
                mRepsView.setText("");
                mWeightView.setText("");
            } else {
                Toast.makeText(this, "No match found", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void updateFavorite() {
        /* collect search variables */
        String s_name = mExerciseNameView.getText().toString();
        int s_sets = -1;
        int s_reps = -1;
        int s_weight = -1;
        /* check for partial fields */
        if(!mSetsView.getText().toString().isEmpty())
            s_sets = Integer.parseInt(mSetsView.getText().toString());
        if(!mRepsView.getText().toString().isEmpty())
            s_reps = Integer.parseInt(mRepsView.getText().toString());
        if(!mWeightView.getText().toString().isEmpty())
            s_weight = Integer.parseInt(mWeightView.getText().toString());

        ExerciseDBHandler handler = new ExerciseDBHandler(this);
        try {
            handler.createDatabase();
        } catch (IOException io) {
            throw new Error("Unable to create database");
        }

        /* check if exercise exists, using exerciseName or all attrs */

        boolean isFavorite = false;

        if(!s_name.isEmpty() || s_sets != -1 || s_reps != -1 || s_weight != -1) {
            isFavorite = handler.testFavorite(s_name, s_sets, s_reps, s_weight);
        }
        mFavorite.setChecked(isFavorite);
    }

    /* Switch back to Exercise Edit Screen */
    public void onSaveButtonClick(View v) {

        /* Exit back to editScreen is add is successful */
        if(addClick(v)) {

            Intent mIntent = new Intent(this, ExerciseListScreen.class);
            Bundle b = new Bundle();
            b.putParcelable("exercise", mExercise);
            b.putInt("number", mNumber);
            b.putString("type", mExerciseType);
            mIntent.putExtras(b);

            if(USE_FLAG)
                mIntent.addFlags(mSaveFlag);

            startActivity(mIntent);
        }
    }
}
