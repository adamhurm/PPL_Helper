package com.example.achurm.pplhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.example.achurm.pplhelper.Exercise;

/**
 * Created by achurm on 2/20/18.
 */

public class ExerciseDBHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "exerciseDB.db";

    private static final String TABLE_EXERCISE = "Exercises";
    private static final String COLUMN_ID = "ExerciseID";
    private static final String COLUMN_NAME = "ExerciseName";
    private static final String COLUMN_REPS = "Reps";
    private static final String COLUMN_SETS = "Sets";
    private static final String COLUMN_WEIGHT = "Weight";
    private static final String COLUMN_TIME = "Timestamp";
    private static final String COLUMN_FAVORITE = "Favorite";
    private static final String COLUMN_TYPE = "Type";

    public ExerciseDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EXERCISE_TABLE =
                String.format(("CREATE TABLE %s (%s INT PRIMARY KEY,"
                        + " %s TEXT NOT NULL, %s INT NOT NULL, %s INT NOT NULL, %s INT NOT NULL,"
                        + " %s TEXT NOT NULL, %s BIT NOT NULL, %s TEXT NOT NULL);"),
                        TABLE_EXERCISE, COLUMN_ID, COLUMN_NAME, COLUMN_WEIGHT, COLUMN_SETS,
                        COLUMN_REPS, COLUMN_TIME, COLUMN_FAVORITE, COLUMN_TYPE);

        db.execSQL(CREATE_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        onCreate(db);
    }

    public void addExercise(Exercise exercise, boolean isFavorite, String exerciseType) {
        ContentValues values = new ContentValues();

        String query = "SELECT * FROM " + TABLE_EXERCISE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        //get time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());

        values.put(COLUMN_ID, count);
        values.put(COLUMN_NAME, exercise.getExercise());
        values.put(COLUMN_WEIGHT, exercise.getWeight());
        values.put(COLUMN_SETS, exercise.getSets());
        values.put(COLUMN_REPS, exercise.getReps());
        values.put(COLUMN_TIME, date);
        values.put(COLUMN_FAVORITE, isFavorite);
        values.put(COLUMN_TYPE, exerciseType);

        db.insert(TABLE_EXERCISE, null, values);

        db.close();
    }


    public Exercise findExercise(String exerciseName) {
        String sqlQuery =
                String.format("SELECT * FROM %s WHERE %s = \'%s\'",
                        TABLE_EXERCISE, COLUMN_NAME, exerciseName);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor myCursor = db.rawQuery(sqlQuery, null);

        Exercise myExercise = null;

        if(myCursor.moveToFirst()) {

            String tmpExercise = myCursor.getString(1);
            int tmpWeights = myCursor.getInt(2);
            int tmpSets = myCursor.getInt(3);
            int tmpReps = myCursor.getInt(4);
            myCursor.close();
            myExercise = new Exercise(tmpExercise, tmpWeights, tmpSets, tmpReps);
        }

        db.close();

        return myExercise;

    }

    public Exercise findExercise(String exerciseName, int weight, int sets, int reps) {
        String sqlQuery =
                String.format("SELECT * FROM %s WHERE %s=\'%s\' AND %s=%d AND %s=%d AND %s=%d",
                        TABLE_EXERCISE, COLUMN_NAME, exerciseName, COLUMN_WEIGHT, weight,
                        COLUMN_SETS, sets, COLUMN_REPS, reps);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor myCursor = db.rawQuery(sqlQuery, null);

        Exercise myExercise = null;

        if(myCursor.moveToFirst()) {

            String tmpExercise = myCursor.getString(1);
            int tmpWeights = myCursor.getInt(2);
            int tmpSets = myCursor.getInt(3);
            int tmpReps = myCursor.getInt(4);
            myCursor.close();
            myExercise = new Exercise(tmpExercise, tmpWeights, tmpSets, tmpReps);
        }

        db.close();

        return myExercise;

    }

    public boolean deleteExercise(String exerciseName, int sets, int reps, int weight) {
        boolean result = false;

        String sqlQuery =
                String.format("SELECT * FROM %s WHERE %s=\'%s\' AND %s=%d AND %s=%d AND %s=%d",
                        TABLE_EXERCISE, COLUMN_NAME, exerciseName, COLUMN_WEIGHT, weight,
                        COLUMN_SETS, sets, COLUMN_REPS, reps);
        //String.format("SELECT * FROM %s WHERE %s = \"%s\"", TABLE_EXERCISES, COLUMN_NAMES, exerciseName);


        SQLiteDatabase db = this.getWritableDatabase();

        Cursor myCursor = db.rawQuery(sqlQuery, null);

        if(myCursor.moveToFirst()) {

            int tmpExerciseID = myCursor.getInt(0);

            db.delete(TABLE_EXERCISE, COLUMN_ID + "=" + tmpExerciseID, null);

            myCursor.close();

            result = true;
        }

        db.close();

        return result;

    }

    public void updateExercise(String exerciseName, int sets, int reps, int weight) {
        String sqlQuery =
                String.format("UPDATE %s SET %s=%d WHERE %s=\'%s\' AND %s=%d AND %s=%d AND %s=%d",
                        TABLE_EXERCISE, COLUMN_FAVORITE, 1, COLUMN_NAME, exerciseName,
                        COLUMN_WEIGHT, weight, COLUMN_SETS, sets, COLUMN_REPS, reps);

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlQuery);

        db.close();

    }
}