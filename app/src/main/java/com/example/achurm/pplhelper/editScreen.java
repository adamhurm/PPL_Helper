package com.example.achurm.pplhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class editScreen extends AppCompatActivity {

    private TextView[][] pullExerciseTV;
    private TabHost tabHost;

    private static final boolean USE_FLAG = true;
    private static final int mFlag = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

    private static Exercise[][] pullExercises = {
        {new Exercise("Deadlift", 200, 3,5),
        new Exercise("Deadlift", 180, 3,12)
        }, {new Exercise("Barbell Row", 90, 3, 5),
        new Exercise("Barbell Row", 60, 3, 12)
        }, {new Exercise("Incline Bench Press", 70, 3, 5),
        new Exercise("Incline Bench Press", 55, 3, 12)
        }, {new Exercise("DB Side Lateral Raise", 10, 3, 12),
        new Exercise("Cardio replacement", 1, 1, 1)
        }, {new Exercise("Triceps Pushdown", 50, 3, 12),
        new Exercise("Cardio replacement", 1, 1, 1)
        }, {new Exercise("Overhead Triceps Extension", 50, 3, 12),
        new Exercise("Cardio replacement", 1, 1, 1)}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_screen);

        tabSetup();

        pullExerciseTV = new TextView[6][2]; //hardcoded for now

        /* Fill Array with TextViews */
        pullExerciseTV[0][0] = (TextView)findViewById(R.id.editEx1op1);
        pullExerciseTV[0][1] = (TextView)findViewById(R.id.editEx1op2);
        pullExerciseTV[1][0] = (TextView)findViewById(R.id.editEx2op1);
        pullExerciseTV[1][1] = (TextView)findViewById(R.id.editEx2op2);
        pullExerciseTV[2][0] = (TextView)findViewById(R.id.editEx3op1);
        pullExerciseTV[2][1] = (TextView)findViewById(R.id.editEx3op2);
        pullExerciseTV[3][0] = (TextView)findViewById(R.id.editEx4op1);
        pullExerciseTV[3][1] = (TextView)findViewById(R.id.editEx4op2);
        pullExerciseTV[4][0] = (TextView)findViewById(R.id.editEx5op1);
        pullExerciseTV[4][1] = (TextView)findViewById(R.id.editEx5op2);
        pullExerciseTV[5][0] = (TextView)findViewById(R.id.editEx6op1);
        pullExerciseTV[5][1] = (TextView)findViewById(R.id.editEx6op2);


        /* Fill TextViews */
        for(int i = 0; i<pullExercises.length; i++) {
            for(int j = 0; j<pullExercises[i].length; j++) {
                Exercise temp = pullExercises[i][j];
                pullExerciseTV[i][j].setText(String.format("%dx%d\t%s",
                        temp.getSets(), temp.getReps(), temp.getExercise()));
            }
        }

    }

    /* Switching with flags */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /* Switch to startScreen */
    public void onSaveButtonClick(View v) {
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
