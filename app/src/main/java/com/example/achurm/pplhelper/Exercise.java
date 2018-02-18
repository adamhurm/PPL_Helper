package com.example.achurm.pplhelper;

public class Exercise {
    private String mExercise;
    private int mSets, mReps, mWeight;

    Exercise(String exercise, int weight, int sets, int reps) {
        mExercise = exercise;
        mWeight = weight;
        mSets = sets;
        mReps = reps;
    }

    String getExercise() {
        return mExercise;
    }

    void setExercise(String exercise) {
        mExercise = exercise;
    }

    int getSets() {
        return mSets;
    }

    public void setSets(int sets) {
        mSets = sets;
    }

    int getReps() {
        return mReps;
    }

    void setReps(int reps) {
        mReps = reps;
    }

    int getWeight() {
        return mWeight;
    }

    void setWeight(int weight) {
        mWeight = weight;
    }
}
