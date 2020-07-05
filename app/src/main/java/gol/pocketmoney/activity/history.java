package gol.pocketmoney.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import java.util.ArrayList;

import gol.pocketmoney.R;
import gol.pocketmoney.classes.ListViewAdapterHistory;
import gol.pocketmoney.classes.details;

public class history extends AppCompatActivity {

    private static final String TAG = "ddlogesh1";
    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.back_1));
        background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(background);

        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();

        ListView l1=(ListView)findViewById(R.id.list_item);
        ArrayList<details> arr = new ArrayList<details>();
        ListViewAdapterHistory adapter = new ListViewAdapterHistory(this, arr);
        l1.setAdapter(adapter);
        adapter.clear();

        Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        l1.setVisibility(View.VISIBLE);
        l1.startAnimation(animZoomIn);

        int count=Status.getInt("count",0);
        if(count==0)
            l1.setBackgroundResource(R.drawable.empty);

        for(int i=1;i<=count;i++) {
            Float distance = Status.getFloat("distance"+Integer.toString(i),0);
            Float speed = Status.getFloat("speed"+Integer.toString(i),0);
            final int calorie = Status.getInt("calorie"+Integer.toString(i),0);
            final int credit = Status.getInt("credit"+Integer.toString(i),0);
            adapter.add(new details(calorie,credit,distance,(distance/speed),speed));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i1 = new Intent(getApplicationContext(), mapView.class);
        startActivity(i1);
        return true;
    }
}
