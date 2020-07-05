package gol.pocketmoney.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gol.pocketmoney.R;

public class myAccount extends AppCompatActivity {

    private TextView tv_name,tv_aadhaar,tv_mobile;
    private Button b;

    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.back_1));
        background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(background);

        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();

        tv_name=findViewById(R.id.tv_name);
        tv_mobile=findViewById(R.id.tv_mobile);
        tv_aadhaar=findViewById(R.id.tv_aadhaar);
        b=findViewById(R.id.signout);

        tv_name.setText(Status.getString("username",null));
        tv_mobile.setText(Status.getString("mobile",null));
        tv_aadhaar.setText(Status.getString("aadhaar",null));

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(myAccount.this);

                builder1.setTitle("Alert!").setMessage("Do you want to Signout?").setCancelable(true)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EStatus.clear();    EStatus.apply();
                            Intent i1=new Intent(myAccount.this,register.class);
                            startActivity(i1);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i1 = new Intent(getApplicationContext(), mapView.class);
        startActivity(i1);
        return true;
    }
}
