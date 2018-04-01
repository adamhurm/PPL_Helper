package com.example.achurm.pplhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

public class splashScreen extends AppCompatActivity {

    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


    }


    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void pplButtonClick(View v) {
        Intent mIntent = new Intent(this, startScreen.class);
        if(USE_FLAG)
            mIntent.addFlags(mFlag);

        switch(v.getId()) {
            case R.id.pushButton:
                mIntent.putExtra("ppl", "PUSH");
                break;
            case R.id.pullButton:
                mIntent.putExtra("ppl", "PUSH");
                break;
            case R.id.legsButton:
                mIntent.putExtra("ppl", "LEGS");
                break;
            default:
                mIntent.putExtra("ppl", "PUSH");
        }
        startActivity(mIntent);
    }
}
