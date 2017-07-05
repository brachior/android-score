package net.brach.android.score.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.brach.android.score.ScoreView;

public class MainActivity extends AppCompatActivity {
    private final static int MAX_LEVEL = 1000;

    private ScoreView scoreView;

    private int points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreView = (ScoreView) findViewById(R.id.score);
    }

    /***********************************************/
    /** {@link android.view.View.OnClickListener} **/
    /***********************************************/

    public void add100(View view) {
        add(100);
    }

    public void add200(View view) {
        add(200);
    }

    public void add300(View view) {
        add(300);
    }

    /*************/
    /** private **/
    /*************/

    public void add(int value) {
        int level = scoreView.getLevel();
        points += value;

        while (points >= MAX_LEVEL) {
            level++;
            points -= MAX_LEVEL;
        }
        scoreView.update(level, points * 100 / MAX_LEVEL, true);
    }
}
