package com.example.achurm.pplhelper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by achurm on 3/8/18.
 */

public class customExerciseScreen extends AppCompatActivity {

    /* Intent flags */
    private static final boolean USE_FLAG = true;
    private static final int mSaveFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

    private Exercise mExercise;
    private int mNumber;

    private TextView mExerciseNameView, mSetsView, mRepsView, mWeightView;
    private CheckBox mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_exercise_screen);

        /* TextViews for Exercise entry */
        mExerciseNameView = (TextView) findViewById(R.id.editExercise);
        mSetsView = (TextView) findViewById(R.id.editSet);
        mRepsView = (TextView) findViewById(R.id.editRep);
        mWeightView = (TextView) findViewById(R.id.editWeight);
        mFavorite = (CheckBox) findViewById(R.id.toggleFavorite);

        onNewIntent(getIntent());

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

            Exercise exercise = handler.findExercise(name, weight, sets, reps);

            if (exercise == null) {
                mExercise = new Exercise(name, weight, sets, reps);
                handler.addExercise(mExercise, isFavorite);
                Toast.makeText(this,
                    String.format("%s exercise was added to the database.", name),
                    Toast.LENGTH_SHORT).show();
            }

            else Toast.makeText(this,
                    String.format("%s exercise already exists in the database with the given info.", name),
                    Toast.LENGTH_SHORT).show();

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

    public void favoriteClick() {
        /* Check for empty fields */
        if(mExerciseNameView.getText().toString().isEmpty()
                || mSetsView.getText().toString().isEmpty()
                || mRepsView.getText().toString().isEmpty()
                || mWeightView.getText().toString().isEmpty()) {
            Toast.makeText(this,
                    "Unable to add exercise.\nPlease check that all fields are filled in.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            /* collect variables */
            String name = mExerciseNameView.getText().toString();
            int sets = Integer.parseInt(mSetsView.getText().toString());
            int reps = Integer.parseInt(mRepsView.getText().toString());
            int weight = Integer.parseInt(mWeightView.getText().toString());

            ExerciseDBHandler handler = new ExerciseDBHandler(this);

            Exercise exercise = handler.findExercise(name, weight, sets, reps);
        }
    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle b = this.getIntent().getExtras();
        if(b != null) {
            mExercise = b.getParcelable("exercise");
            mNumber = b.getInt("number");

            if(mExercise == null)
                Toast.makeText(this, "Exercise is null", Toast.LENGTH_SHORT).show();
            else {
                mExerciseNameView.setText(mExercise.getExercise());
                mSetsView.setText(Integer.toString(mExercise.getSets()));
                mRepsView.setText(Integer.toString(mExercise.getReps()));
                mWeightView.setText(Integer.toString(mExercise.getWeight()));
            }
        }
        setIntent(intent);
    }

    /* Switch back to Exercise Edit Screen */
    public void onSaveButtonClick(View v) {

        /* Exit back to editScreen is add is successful */
        if(addClick(v)) {

            Intent mIntent = new Intent(this, editScreen.class);
            Bundle b = new Bundle();
            b.putParcelable("exercise", mExercise);
            b.putInt("number", mNumber);
            mIntent.putExtras(b);

            if(USE_FLAG)
                mIntent.addFlags(mSaveFlag);

            startActivity(mIntent);
        }
    }
}
