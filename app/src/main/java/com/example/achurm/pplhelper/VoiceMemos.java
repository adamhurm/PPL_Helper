package com.example.achurm.pplhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoiceMemos extends AppCompatActivity {
    private Button mRecordButton, mStopRecordButton, mPlayButton, mStopPlayButton;
    private Button mPrevButton, mNextButton;
    private TextView mRecordingName, mPlayingName;
    private String recordAudioFilePath;
    private String playAudioFilePath;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;

    private File[] mFiles = null;
    private int mFileIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_memos);

        mRecordButton = findViewById(R.id.recordButton);
        mStopRecordButton = findViewById(R.id.stopRecordButton);
        mPlayButton = findViewById(R.id.playButton);
        mStopPlayButton = findViewById(R.id.stopPlayButton);
        mRecordingName = findViewById(R.id.recordingName);
        mPrevButton = findViewById(R.id.prevButton);
        mNextButton = findViewById(R.id.nextButton);
        mPlayingName = findViewById(R.id.playingName);

        /* Add microphone and external storage write permissions */
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        || (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(VoiceMemos.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
            101);
            ActivityCompat.requestPermissions(VoiceMemos.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        }

        PackageManager manager = this.getPackageManager();

        if(manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
            mRecordButton.setEnabled(true);
        else
            mRecordButton.setEnabled(false);

        checkAudioFiles();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date = sdf.format(new Date());


        File f = new File(Environment.getExternalStorageDirectory(), "memos");
        if (!f.exists()) {
            f.mkdirs();
        }

        recordAudioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/memos/memo_"+date+".3gp";

        mRecordingName.setText("file:\t" + recordAudioFilePath);

        myRecorder = new MediaRecorder();
        myPlayer = new MediaPlayer();

    }

    public void onRecordButtonClick(View v) throws IOException {

        try {
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch(Exception e) {
            mRecordingName.setText("Microphone not found, is this an emulator?\n" + e.getMessage());
            return;
        }

        //update with new name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date = sdf.format(new Date());

        File f = new File(Environment.getExternalStorageDirectory(), "memos");
        if (!f.exists()) {
            f.mkdirs();
        }

        recordAudioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/memos/memo_"+date+".3gp";

        mRecordingName.setText("file:\t" + recordAudioFilePath);

        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myRecorder.setOutputFile(recordAudioFilePath);

        try {
            myRecorder.prepare();
        } catch(Exception e) {
            mRecordingName.setText("Recorder cannot be prepared.\n"+e);
        }

        myRecorder.start();

        mRecordButton.setEnabled(false);
        mStopRecordButton.setEnabled(true);

    }
    public void onStopRecordButtonClick(View v){
        myRecorder.stop();
        myRecorder.reset();
        myRecorder.release();

        mStopRecordButton.setEnabled(false);

        Toast.makeText(getApplicationContext(), "New recording created.", Toast.LENGTH_LONG).show();
        playAudioFilePath = recordAudioFilePath;
        mFileIndex++;
        checkAudioFiles();
        mPlayButton.setEnabled(true);
    }
    public void onPlayButtonClick(View v){
        myPlayer = new MediaPlayer();
        try {
            myPlayer.setDataSource(playAudioFilePath);
        } catch(Exception e) {
            mRecordingName.setText("Player cannot set data source.\n"+e);
        }

        try {
            myPlayer.prepare();
        } catch(Exception e) {
            mRecordingName.setText("Player cannot be prepared.\n"+e);
        }

        myPlayer.start();

        mPlayButton.setEnabled(false);
        mStopPlayButton.setEnabled(true);
    }
    public void onStopPlayButtonClick(View v){
        myPlayer.stop();
        myPlayer.reset();
        myPlayer.release();

        myRecorder.release();

        mStopPlayButton.setEnabled(false);
        mRecordButton.setEnabled(true);
        checkAudioFiles();
    }

    public void checkAudioFiles() {
        File folder = new File(Environment.getExternalStorageDirectory(), "memos");
        File[] files = folder.listFiles();
        if(files != null) {
            if (files.length == 1) {
                mPrevButton.setEnabled(false);
                mNextButton.setEnabled(false);
            }
            else {
                mPrevButton.setEnabled(true);
                mNextButton.setEnabled(true);
            }
            mFiles = files;
            playAudioFilePath = mFiles[mFileIndex].getAbsolutePath();
            mPlayingName.setText("file:\t"+playAudioFilePath);
            mPlayButton.setEnabled(true);
        }
    }

    public void onPrevButtonClick(View v) {
        checkAudioFiles();
        if(mFiles == null)
            Toast.makeText(getApplicationContext(), "No recordings.", Toast.LENGTH_SHORT).show();
        else if((mFileIndex-1) < 0) {
            mPrevButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No previous recordings.", Toast.LENGTH_SHORT).show();
        }
        else {
            mPrevButton.setEnabled(true);
            mPlayButton.setEnabled(true);
            playAudioFilePath = mFiles[mFileIndex-1].getAbsolutePath();
            mPlayingName.setText("file:\t"+playAudioFilePath);
        }
    }
    public void onNextButtonClick(View v) {
        checkAudioFiles();
        if(mFiles == null)
            Toast.makeText(getApplicationContext(), "No recordings.", Toast.LENGTH_SHORT).show();
        else if((mFileIndex+1) >= mFiles.length) {
            mNextButton.setEnabled(false);
            Toast.makeText(getApplicationContext(), "At newest recording.", Toast.LENGTH_SHORT).show();
        }
        else {
            mNextButton.setEnabled(true);
            mPlayButton.setEnabled(true);
            playAudioFilePath = mFiles[mFileIndex+1].getAbsolutePath();
            mPlayingName.setText("file:\t"+playAudioFilePath);

        }
    }

    public void listAudioFiles(View v) {
        String temp = "";
        if(mFiles == null) temp = "No files found.";
        for(File f: mFiles) {
            temp += f.getAbsolutePath()+"\n";
        }
        Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
    }
}
