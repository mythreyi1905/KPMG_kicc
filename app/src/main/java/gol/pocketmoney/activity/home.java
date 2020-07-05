package gol.pocketmoney.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import gol.pocketmoney.R;

public class home extends AppCompatActivity {

    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        final ImageView iv=findViewById(R.id.iv_logo);

        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                iv.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if(Status.getString("mobile",null) != null){
                            Intent i1 = new Intent(getApplicationContext(), mapView.class);
                            startActivity(i1);
                        }
                        else{
                            Intent i1 = new Intent(getApplicationContext(), register.class);
                            startActivity(i1);
                        }
                    }
                }, 2000);
            }
        }, 1000);
    }
}