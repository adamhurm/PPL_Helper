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
import java.util.Locale;

public class VoiceMemos extends AppCompatActivity {
    /* Pointers to buttons and TVs */
    private Button mRecordButton, mStopRecordButton, mPlayButton, mStopPlayButton;
    private Button mPrevButton, mNextButton;
    private TextView mRecordingName, mPlayingName;

    /* Storage information */
    private String recordAudioFilePath;
    private String playAudioFilePath;
    private File[] mFiles = null;
    private int mFileIndex = 0;

    /* Pointer to MediaPlayer and MediaRecorder */
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;

    private static int PERMISSION_REQUEST = 101;


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
        && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(VoiceMemos.this,
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
        }

        /* Check that microphone is usable */
        PackageManager manager = this.getPackageManager();
        if(manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
            mRecordButton.setEnabled(true);
        else
            mRecordButton.setEnabled(false);

        checkAudioFiles();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        String date = sdf.format(new Date());


        File f = new File(Environment.getExternalStorageDirectory(), "memos");
        if (!f.exists()) {
            f.mkdirs();
        }

        recordAudioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/memos/memo_"+date+".3gp";

        mRecordingName.setText("file:\t" + recordAudioFilePath);
    }

    /** Record Start/Stop Button Presses **/
    public void onRecordButtonClick(View v) throws IOException {
        myRecorder = new MediaRecorder();
        try {
            myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch(Exception e) {
            mRecordingName.setText("Microphone not found, is this an emulator?\n" + e.getMessage());
            return;
        }

        //update with new name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
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
        if(mFileIndex == 0) mFileIndex = -1;
        mFileIndex++;
        checkAudioFiles();
        mRecordButton.setEnabled(true);
        mPlayButton.setEnabled(true);
    }

    /** Play Start/Stop Button Presses **/
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


        mStopPlayButton.setEnabled(false);
        mRecordButton.setEnabled(true);
        checkAudioFiles();

        //button was pressed, therefore a file exists
        mPlayButton.setEnabled(true);
    }

    /** Previous and Next Button Presses */
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
            playAudioFilePath = mFiles[--mFileIndex].getAbsolutePath();
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
            playAudioFilePath = mFiles[++mFileIndex].getAbsolutePath();
            mPlayingName.setText("file:\t"+playAudioFilePath);
        }
    }

    /** Audio file functions **/
    /* Check for available audio files in the folder and handle buttons/path appropriately */
    public void checkAudioFiles() {
        File folder = new File(Environment.getExternalStorageDirectory(), "memos");
        File[] files = folder.listFiles();
        if(files != null){ //are there files in the folder?
            if(files.length != 0) {
                if (files.length == 1) {
                    mPrevButton.setEnabled(false);
                    mNextButton.setEnabled(false);
                } else {
                    mPrevButton.setEnabled(true);
                    mNextButton.setEnabled(true);
                }
                mFiles = files;

                playAudioFilePath = mFiles[mFileIndex].getAbsolutePath();

                mPlayingName.setText("file:\t" + playAudioFilePath);
                mPlayButton.setEnabled(true);
            }
            else {
                mPrevButton.setEnabled(false);
                mNextButton.setEnabled(false);
                mPlayButton.setEnabled(false);
            }
        }
        else { //if no files, disable buttons
            mPrevButton.setEnabled(false);
            mNextButton.setEnabled(false);
            mPlayButton.setEnabled(false);
        }
    }
    /* List available audio files in the folder */
    public void listAudioFiles(View v) {
        String temp = "Files found:";
        if(mFiles == null) temp = "No files found.";
        else {
            for (File f : mFiles) {
                temp += "\n" + f.getAbsolutePath();
            }
        }
        Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
    }
}
