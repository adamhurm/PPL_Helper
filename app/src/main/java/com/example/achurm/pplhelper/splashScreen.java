package com.example.achurm.pplhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

public class splashScreen extends AppCompatActivity {

    private TabHost tabHost;
    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tabSetup();
    }


    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void onGetStartedButtonClick(View v) {
        Intent mIntent = new Intent(this, startScreen.class);

        if(USE_FLAG)
            mIntent.addFlags(mFlag);

        startActivity(mIntent);
    }

    public void tabSetup() {
        /* Tab Setup */
        tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        //Push
        TabHost.TabSpec spec1 = tabHost.newTabSpec("PUSH");
        spec1.setContent(R.id.pushTab);
        spec1.setIndicator("PUSH");
        tabHost.addTab(spec1);

        //Pull
        TabHost.TabSpec spec2 = tabHost.newTabSpec("PULL");
        spec2.setContent(R.id.pullTab);
        spec2.setIndicator("PULL");
        tabHost.addTab(spec2);

        //Legs
        TabHost.TabSpec spec3 = tabHost.newTabSpec("LEGS");
        spec3.setContent(R.id.legsTab);
        spec3.setIndicator("LEGS");
        tabHost.addTab(spec3);
    }
}
