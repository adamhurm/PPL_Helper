package com.example.achurm.pplhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private Context mContext;
    private String DB_PATH;

    public ExerciseDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //store context and DB_PATH for creating new database
        mContext = context;
        DB_PATH = context.getApplicationInfo().dataDir+"/databases/";

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* The logic here has been changed in order to handle IO Errors, see CreateDatabase below.

        String CREATE_EXERCISE_TABLE =
                String.format(("CREATE TABLE %s (%s INT PRIMARY KEY,"
                        + " %s TEXT NOT NULL, %s INT NOT NULL, %s INT NOT NULL, %s INT NOT NULL,"
                        + " %s TEXT NOT NULL, %s BIT NOT NULL, %s TEXT NOT NULL);"),
                        TABLE_EXERCISE, COLUMN_ID, COLUMN_NAME, COLUMN_WEIGHT, COLUMN_SETS,
                        COLUMN_REPS, COLUMN_TIME, COLUMN_FAVORITE, COLUMN_TYPE);

        db.execSQL(CREATE_EXERCISE_TABLE);
        */
    }

    /* call copyDatabase if db file does not exist */
    public void createDatabase() throws IOException {
        /* check if database exists */
        File dbFile = new File(DB_PATH + DB_NAME);
        boolean fileExists = dbFile.exists();

        /* copy pre-populated database from assets */
        if(!fileExists) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDatabase();
            } catch(IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    /* pull a database from assets instead of creating one from scratch. */
    private void copyDatabase() throws IOException {
        //create input and output streams
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH+DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);

        //use buffer to copy between streams
        byte[] mBuffer = new byte[1024];
        int mLength;
        while((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);

        //close everything out to avoid memory leaks
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        //onCreate(db);
        try {
            createDatabase();
        } catch (IOException io) {
            throw new Error("Unable to create database");
        }
    }

    /* add Exercise object and specify favorite & type */
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

    /* find exercise using only name */
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

    /* find exercise using name, weight, sets, reps */
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

    /* delete exercise using set, reps, weight */
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
    public boolean testFavorite(String exerciseName, int sets, int reps, int weight) {
        String sqlQuery =
                String.format("SELECT * FROM %s WHERE %s=\'%s\' AND %s=%d AND %s=%d AND %s=%d",
                        TABLE_EXERCISE, COLUMN_NAME, exerciseName, COLUMN_WEIGHT, weight,
                        COLUMN_SETS, sets, COLUMN_REPS, reps);

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor myCursor = db.rawQuery(sqlQuery, null);

        int isFavorite = 0;

        if(myCursor.moveToFirst()) {
            isFavorite = myCursor.getInt(6);
            myCursor.close();
        }

        db.close();

        return ((isFavorite == 1)? true : false);
    }
    /* Update favorite and timestamp */
    public void updateExercise(String exerciseName, int sets, int reps, int weight, boolean favorite) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());

        String sqlQuery =
                String.format("UPDATE %s SET %s=%d, %s=\'%s\' WHERE %s=\'%s\' AND %s=%d AND %s=%d AND %s=%d",
                        TABLE_EXERCISE, COLUMN_FAVORITE, (favorite? 1 : 0), COLUMN_TIME, date,
                        COLUMN_NAME, exerciseName, COLUMN_WEIGHT, weight, COLUMN_SETS, sets, COLUMN_REPS, reps);

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlQuery);

        db.close();

    }
}