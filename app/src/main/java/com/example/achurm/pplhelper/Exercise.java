package com.example.achurm.pplhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mExercise);
        out.writeInt(mWeight);
        out.writeInt(mSets);
        out.writeInt(mReps);
    }

    public static final Parcelable.Creator<Exercise> CREATOR
            = new Parcelable.Creator<Exercise>() {
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    private Exercise(Parcel in) {
        mExercise = in.readString();
        mWeight = in.readInt();
        mSets = in.readInt();
        mReps = in.readInt();
    }
}
