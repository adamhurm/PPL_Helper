package com.example.achurm.pplhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class startScreen extends AppCompatActivity {

    private TabHost tabHost;
    private Button setButton;

    private TextView setData;
    private TextView repData;

    private int setCurrent = 1;
    private static final int setTotal = 3;
    private static final int repTotal = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        tabSetup();
        updateSetRepData();
    }

    public void tabSetup() {
        /* Tab Setup */
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        //Push
        TabHost.TabSpec spec = tabHost.newTabSpec("PUSH");
        spec.setContent(R.id.pushTab);
        spec.setIndicator("PUSH");
        tabHost.addTab(spec);

        //Pull
        spec = tabHost.newTabSpec("PULL");
        spec.setContent(R.id.pullTab);
        spec.setIndicator("PULL");
        tabHost.addTab(spec);

        //Legs
        spec = tabHost.newTabSpec("LEGS");
        spec.setContent(R.id.legsTab);
        spec.setIndicator("LEGS");
        tabHost.addTab(spec);

        /* Set and Rep data */
        setData = (TextView)findViewById(R.id.setData);
        repData = (TextView)findViewById(R.id.repData);
    }

    public void updateSetRepData() {
        setData.setText(Integer.toString(setCurrent) + " / " + Integer.toString(setTotal));
        repData.setText(Integer.toString(repTotal));
    }
    public void doneSetButtonPressed(View v) {
        setCurrent++;
        if(setCurrent > setTotal) {
            Toast.makeText(this, "Finished this exercise."+Integer.toString(setCurrent), Toast.LENGTH_SHORT).show();
            setCurrent = 1;
        }
        updateSetRepData();
    }
}
