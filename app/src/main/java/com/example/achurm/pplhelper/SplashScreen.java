package com.example.achurm.pplhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

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
        Intent mIntent = new Intent(this, StartScreen.class);
        if(USE_FLAG)
            mIntent.addFlags(mFlag);
        Bundle b = new Bundle();

        switch(v.getId()) {
            case R.id.pushButtonSplash:
                b.putString("ppl", "PUSH");
                break;
            case R.id.pullButtonSplash:
                b.putString("ppl", "PULL");
                break;
            case R.id.legsButtonSplash:
                b.putString("ppl", "LEGS");
                break;
            default:
                b.putString("ppl", "START");
                break;
        }
        mIntent.putExtras(b);
        startActivity(mIntent);
    }
}
