package com.example.achurm.pplhelper;

import java.io.Serializable;

/**
 * Created by achurm on 4/17/18.
 */

public class DataBus implements Serializable {
    private Exercise mExercises[];
    private String mDates[];
    private int mFavorites[];

    public DataBus(String s){
        if(s.equals("history")) {
            mExercises = new Exercise[3];
            mDates = new String[3];
        }
        else if (s.equals("exercises")){
            mExercises = new Exercise[6];
            mDates = new String[6];
            mFavorites = new int[6];
        }
    }

    public Exercise[] getExercises() {
        return mExercises;
    }

    public void setExercises(Exercise[] exercises) {
        mExercises = exercises;
    }

    public String[] getDates() {
        return mDates;
    }

    public void setDates(String[] dates) {
        mDates = dates;
    }

    public int[] getFavorites() {
        return mFavorites;
    }

    public void setFavorites(int[] favorites) {
        mFavorites = favorites;
    }
}
