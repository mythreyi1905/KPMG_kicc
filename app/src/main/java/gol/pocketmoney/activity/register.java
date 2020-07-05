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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import gol.pocketmoney.R;

public class register extends AppCompatActivity {

    private static final String TAG = "ddlogesh555";
    public static int backButtonCount = 0;
    private FirebaseAuth fb;
    private DatabaseReference fri;

    private String codesent;
    private String mobile;
    private String aadhaar;
    private String username;
    private String password;
    private RelativeLayout layout1;
    private boolean flag[]=new boolean[4];

    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;

    private EditText e1,e2,e3,e4,e5,e6;
    private ImageView tv1,b1,b2,b3,layout2;
    private TextView change_mobile,resend_otp;
    private ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        e1=(EditText) findViewById(R.id.aadhaar);
        e2=(EditText) findViewById(R.id.name);
        e3=(EditText) findViewById(R.id.mobile);
        e4=(EditText) findViewById(R.id.otp);
        e5=(EditText) findViewById(R.id.pass);
        e6=(EditText) findViewById(R.id.cpass);

        tv1=(ImageView) findViewById(R.id.tv_msg1);
        layout2=(ImageView) findViewById(R.id.iv_mobile);

        change_mobile=(TextView) findViewById(R.id.change_mobile);
        resend_otp=(TextView) findViewById(R.id.resend_otp);
        layout1=(RelativeLayout)findViewById(R.id.layout1);

        b1=(ImageView)findViewById(R.id.next);
        b2=(ImageView)findViewById(R.id.get_otp);
        b3=(ImageView)findViewById(R.id.done);
        p = new ProgressDialog(this);

        fb=FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        fri = ref.child("pocket").child("aadhaar");
        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();

        change_mobile.setPaintFlags(change_mobile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        resend_otp.setPaintFlags(resend_otp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        change_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1.setVisibility(View.GONE);
                e3.setEnabled(true);
                e3.setText("");
                b2.setVisibility(View.VISIBLE);
                b3.setVisibility(View.GONE);
                layout2.setBackgroundResource(R.drawable.mobile_layout);
                e4.setVisibility(View.INVISIBLE);

                change_mobile.setVisibility(View.INVISIBLE);
                resend_otp.setVisibility(View.INVISIBLE);
            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                checkForMobile(mobile);
                e4.setText("");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        p.dismiss();
                    }
                },500);
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(getApplicationContext(), signin.class);
                startActivity(i1);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();

                if (e1.getEditableText().toString().length() == 12)
                    checkForAadhaar();
                else{
                    flag[0]=false;
                    e1.setError("Invalid Aadhaar Number!");
                    e1.requestFocus();
                }

                if (e2.getEditableText().toString().length() > 0) {
                    username = e2.getEditableText().toString();
                    flag[1]=true;
                }
                else{
                    flag[1]=false;
                    e2.setError("Username cannot be empty!");
                    e2.requestFocus();
                }

                if (e5.getEditableText().toString().length() == 0) {
                    flag[2]=false;
                    e5.setError("Password cannot be empty!");
                    e5.requestFocus();
                }
                else if (e5.getEditableText().toString().length() < 6) {
                    flag[2]=false;
                    e5.setError("Password strength is weak!");
                    e5.requestFocus();
                }
                else if (e5.getEditableText().toString().length() >= 6) {
                    flag[2]=true;
                    password = e5.getEditableText().toString();

                    if (e6.getEditableText().toString().length() == 0) {
                        flag[3]=false;
                        e6.setError("Password cannot be empty!");
                        e6.requestFocus();
                    }
                    else if ((e6.getEditableText().toString()).equals(e5.getEditableText().toString()))
                        flag[3]=true;
                    else{
                        flag[3]=false;
                        e6.setError("Password doesn't match!");
                        e6.requestFocus();
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int i;
                        for(i=0;i<4;i++){
                            if(!flag[i])
                                break;
                        }
                        if(i==4){
                            tv1.setVisibility(View.GONE);

                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.VISIBLE);
                            e3.setVisibility(View.VISIBLE);
                            e4.setVisibility(View.INVISIBLE);
                            b2.setVisibility(View.VISIBLE);
                        }

                        p.dismiss();
                    }
                },500);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();

                if (e3.getEditableText().toString().length() == 10) {
                    mobile=e3.getEditableText().toString();
                    checkForMobile(mobile);
                }
                else {
                    e3.setError("Invalid Mobile Number!");
                    e3.requestFocus();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        p.dismiss();
                    }
                },500);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();

                if(e4.getEditableText().toString().length() == 6) {
                    String otp = e4.getEditableText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codesent, otp);
                    checkForOtp(credential);
                }
                else{
                    e4.setError("Invalid Code!");
                    e4.requestFocus();
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

    private void checkForAadhaar(){
        String value=e1.getEditableText().toString();
        switch (value){
            case "123412341111": case "123412342222": case "123412343333": case "111111111111":
                //tv.setText("Sorry! You are not eligible to be part of the scheme, Please visit the nearest Taluk office to know more details");
                flag[0]=false;
                break;
            default:
                aadhaar=value;
                flag[0]=true;
        }
    }

    private void checkForMobile(String mobile){
        String mobile_num = "+91 " + mobile;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile_num, 60, TimeUnit.SECONDS, register.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                e3.setEnabled(false);
                e4.setVisibility(View.VISIBLE);
                e4.setText(phoneAuthCredential.getSmsCode());
                layout2.setBackgroundResource(R.drawable.otp_layout);
                b2.setVisibility(View.GONE);
                b3.setVisibility(View.VISIBLE);

                change_mobile.setVisibility(View.VISIBLE);
                resend_otp.setVisibility(View.VISIBLE);

                checkForOtp(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                codesent = s;
                e3.setEnabled(false);
                e4.setVisibility(View.VISIBLE);
                b2.setVisibility(View.GONE);
                b3.setVisibility(View.VISIBLE);
                layout2.setBackgroundResource(R.drawable.otp_layout);

                change_mobile.setVisibility(View.VISIBLE);
                resend_otp.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkForOtp(PhoneAuthCredential credential) {
        fb.signInWithCredential(credential).addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    EStatus.putInt("status",100);           EStatus.apply();
                    EStatus.putString("mobile",mobile);     EStatus.apply();
                    EStatus.putString("aadhaar",aadhaar);   EStatus.apply();
                    EStatus.putString("username",username); EStatus.apply();
                    EStatus.putInt("credits",100);          EStatus.apply();
                    EStatus.putInt("count",0);              EStatus.apply();

                    fri.child(aadhaar).child("username").setValue(username);
                    fri.child(aadhaar).child("mobile").setValue(mobile);
                    fri.child(aadhaar).child("password").setValue(password);
                    fri.child(aadhaar).child("credits").setValue("100");

                    p.dismiss();
                    Intent i1 = new Intent(getApplicationContext(), mapView.class);
                    startActivity(i1);
                }
                else {
                    p.dismiss();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        e4.setError("Invalid Code");
                        e4.requestFocus();
                    }
                }
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
