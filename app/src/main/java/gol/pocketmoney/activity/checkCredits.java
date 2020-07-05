package gol.pocketmoney.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import gol.pocketmoney.R;

public class checkCredits extends AppCompatActivity {

    private static final String TAG = "ddlogesh1";
    private SharedPreferences Status;
    private DatabaseReference fri;

    private int credits=100;
    private double rate=1.0;

    private TextView tv_credits,tv_rate,tv_calc1,tv_calc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_credits);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        BitmapDrawable background = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.back_1));
        background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(background);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        fri = ref.child("pocket").child("credit");
        Status=getSharedPreferences("status", Context.MODE_PRIVATE);

        tv_credits=findViewById(R.id.tv_credits);
        tv_rate=findViewById(R.id.tv_rate);
        tv_calc1=findViewById(R.id.tv_calc1);
        tv_calc=findViewById(R.id.tv_calc);

        tv_calc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_calc.setVisibility(View.VISIBLE);
            }
        });

        try {
            credits = Status.getInt("credits",0);
            tv_credits.setText(Integer.toString(credits));
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }

        fri.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    rate=dataSnapshot.getValue(Double.class);
                    String val=Double.toString(rate);
                    tv_rate.setText("Rs. " + val);
                    tv_calc.setText("Rs. " + (new DecimalFormat("0.00").format(rate*credits)));
                }
                catch (Exception e){
                    Log.d(TAG,e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
