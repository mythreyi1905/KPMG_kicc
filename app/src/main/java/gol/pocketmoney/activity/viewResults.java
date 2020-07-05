package gol.pocketmoney.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;

import gol.pocketmoney.R;

public class viewResults extends AppCompatActivity {

    private static final String TAG = "ddlogesh1";
    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;

    private TextView tv_progress,tv_dist,tv_timer,tv_speed,tv_credits;
    private CircularProgressBar circularProgressBar;

    private int i=0, steps;
    private int inc=0, k=0, rem;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_results);

        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();

        int count=Status.getInt("count",0);

        Float distance = Status.getFloat("distance"+Integer.toString(count),0);
        Float speed = Status.getFloat("speed"+Integer.toString(count),0);
        final int calorie = Status.getInt("calorie"+Integer.toString(count),0);
        final int credit = Status.getInt("credit"+Integer.toString(count),0);

        tv_dist = (TextView) findViewById(R.id.tv_dist);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_credits = (TextView) findViewById(R.id.tv_credits);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        circularProgressBar = (CircularProgressBar)findViewById(R.id.progressBar);
        Button b=findViewById(R.id.start);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1=new Intent(getApplicationContext(),mapView.class);
                startActivity(i1);
            }
        });

        rem = calorie%100;
        steps=calorie/100;
        if(rem != 0) steps++;

        tv_dist.setText(Float.toString(distance));
        tv_speed.setText(Float.toString(speed));
        tv_timer.setText((new DecimalFormat("0.0").format(distance/speed)));
        tv_credits.setText(Integer.toString(credit));

        new thread1().execute();
    }

    /********************************************************************************/

    public class thread1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            for (i=0; i < steps;i++) {
                drawProgress(i);

                int x;
                if(i==steps-1 && rem!=0)
                    x=rem;
                else
                    x=100;
                inc+=x;

                while (k <= inc) {
                    writeCalorie();

                    try {
                        Thread.sleep(2500/x);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    private void drawProgress(final int i){
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (i % 2 == 0) {
                        circularProgressBar.setColor(Color.parseColor("#f4b006"));
                        circularProgressBar.setBackgroundColor(Color.parseColor("#ffedcd"));
                    }
                    else {
                        circularProgressBar.setColor(Color.parseColor("#F44336"));
                        circularProgressBar.setBackgroundColor(Color.parseColor("#FFCDD2"));
                    }
                    circularProgressBar.setProgress(0);
                    circularProgressBar.setProgressWithAnimation(100, 2500);

                    if(i==steps-1 && rem!=0)
                        circularProgressBar.setProgressWithAnimation(rem,2500);
                    else
                        circularProgressBar.setProgressWithAnimation(100,2500);
                }
                catch (Exception e){
                    Log.d(TAG,e.getMessage());
                }
            }
        });
    }

    private void writeCalorie(){
        handler2.post(new Runnable() {
            @Override
            public void run() {
                try {
                    tv_progress.setText(Integer.toString(k));
                    k++;
                }
                catch (Exception e){
                    Log.d(TAG,e.getMessage());
                }
            }
        });
    }

    /********************************************************************************/

    @Override
    public void onBackPressed() {
        Intent i1=new Intent(getApplicationContext(),mapView.class);
        startActivity(i1);
    }
}