package gol.pocketmoney.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import gol.pocketmoney.R;

public class checkStores extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_stores);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.back_1));
        background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(background);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i1 = new Intent(getApplicationContext(), mapView.class);
        startActivity(i1);
        return true;
    }
}
