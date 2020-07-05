package gol.pocketmoney.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gol.pocketmoney.R;

public class signin extends AppCompatActivity {

    private static final String TAG = "ddlogesh1";
    private static int backButtonCount=0;

    private EditText e1,e2;
    private ImageView tv1;
    private ImageView b,b1;

    private ProgressDialog p;
    private DatabaseReference fri;
    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;

    private String aadhaar,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        e1=findViewById(R.id.aadhaar);
        e2=findViewById(R.id.pass);
        b=findViewById(R.id.login);
        b1=findViewById(R.id.back);
        tv1=findViewById(R.id.tv_msg1);
        p = new ProgressDialog(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        fri = ref.child("pocket").child("aadhaar");
        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getApplicationContext(), register.class);
                startActivity(i1);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();

                if (e1.getEditableText().toString().length() == 12) {
                    if (e2.getEditableText().toString().length() == 0) {
                        e2.setError("Password cannot be empty!");
                        e2.requestFocus();
                    }
                    else if (e2.getEditableText().toString().length() < 6) {
                        e2.setError("Password strength is weak!");
                        e2.requestFocus();
                    }
                    else if (e2.getEditableText().toString().length() >= 6) {
                        checkForLogin();
                    }
                }
                else{
                    e1.setError("Invalid Aadhaar Number!");
                    e1.requestFocus();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        p.dismiss();
                    }
                },500);
            }
        });
    }

    /************************************************************************************/

    private void checkForLogin(){
        aadhaar=e1.getEditableText().toString();
        password = e2.getEditableText().toString();
        fri.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag=false;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String number=snapshot.getKey();
                    if(number.equals(aadhaar)){
                        flag=true;
                        if(password.equals(snapshot.child("password").getValue(String.class))){
                            EStatus.putInt("status",100);           EStatus.apply();
                            EStatus.putString("aadhaar",aadhaar);   EStatus.apply();
                            EStatus.putInt("count",0);              EStatus.apply();
                            EStatus.putString("mobile",snapshot.child("mobile").getValue(String.class));     EStatus.apply();
                            EStatus.putString("username",snapshot.child("username").getValue(String.class)); EStatus.apply();
                            EStatus.putInt("credits",Integer.parseInt(snapshot.child("credits").getValue(String.class)));          EStatus.apply();

                            p.dismiss();
                            Intent i1 = new Intent(getApplicationContext(), mapView.class);
                            startActivity(i1);
                        }
                        else{
                            p.dismiss();
                            e2.setError("Invalid Password!");
                            e2.requestFocus();
                        }
                    }
                }
                if(!flag){
                    e1.setError("Invalid Aadhaar Number!");
                    e1.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /************************************************************************************/

    private void startProgress(){
        alerter();
        hideKeyboard();
        p.setTitle("Verification");
        p.setMessage("Processing...");
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCancelable(false);
        p.show();
    }

    private void hideKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /************************************************************************************/
    //Internet Connection Alert

    private boolean alerter(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }
        return false;
    }

    /************************************************************************************/

    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            backButtonCount = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Press Once Again to Exit", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }
}
