package com.ycce.kunal.conductorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private CheckBox cShowPassword;

    private ProgressDialog progressDialog;
    private LinearLayout lActivity,bActivity;
    private EditText email,password,busno;
    private Button login,confirm;

    private String mEMail,mPassword;
    private FirebaseAuth mAuth;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public  static  final  String MyPref = "MyPref";
    public  static  final  String emailk = "emailKey";
    public  static  final  String passwordk = "passwordKey";
    public  static  final  String busk = "buskey";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean flag;
    public  String busNo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Conductor Login");

        lActivity = (LinearLayout)findViewById(R.id.loginactivity);
        bActivity = (LinearLayout)findViewById(R.id.getbusnoactivity);

        cShowPassword = (CheckBox) findViewById(R.id.cShowPassword);
        //layout
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        busno = (EditText)findViewById(R.id.busno);
        //edit text
        login = (Button)findViewById(R.id.login);
        confirm = (Button)findViewById(R.id.confirm);
        progressDialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        flag = sharedPreferences.getBoolean("flag", false);
        if (checkAndRequestPermissions()){
        if (flag) {
                ///second time activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                busNo=sharedPreferences.getString(busk,busNo);
                intent.putExtra("BusNo",busNo);
                finish();
                startActivity(intent);
            }
        }


        cShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }else{
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
            }
        });

        if (!isNetworkAvailable()){
            Toast.makeText(LoginActivity.this, "Please check your internet connection!!!!!!!!!", Toast.LENGTH_SHORT).show();
        }else{

                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog.setMessage("Loading......");
                        progressDialog.show();

                        mEMail = email.getText().toString();
                        mPassword = password.getText().toString();

                        editor.putString(emailk, mEMail);
                        editor.putString(passwordk, mPassword);
                        editor.commit();

                        //Toast.makeText(getApplicationContext(), ""+mEMail+mPassword, Toast.LENGTH_LONG).show();
                        //FirebaseAuth instance
                        mAuth = FirebaseAuth.getInstance();

                        mAuth.signInWithEmailAndPassword(mEMail, mPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    //      Log.d("TAG", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (checkAndRequestPermissions()) {
                                        progressDialog.dismiss();
                                        busNumber();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Please enable the permissions !!!!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //                  Log.w("TAG", "signInWithEmail:failure", task.getException());

                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }


    }
    public void busNumber(){

        lActivity.setVisibility(View.GONE);
        bActivity.setVisibility(View.VISIBLE);

        if (!isNetworkAvailable()){
            Toast.makeText(LoginActivity.this, "Please check your internet connection!!!!!!!!!", Toast.LENGTH_SHORT).show();
        }else {
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setMessage("Loading......");
                    progressDialog.show();

                    busNo = busno.getText().toString();
                    if (busNo.length() == 4) {

                        editor.putString(busk, busNo);

                        editor.putBoolean("flag", true);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        intent.putExtra("BusNo", busNo);
                        finish();
                        //Toast.makeText(Login.this, ""+busNo, Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        startActivity(intent);
                    } else {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "please enter valid bus no.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

/* tforgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading......");
                progressDialog.show();
                finish();
             //   startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                progressDialog.dismiss();
            }
        });*/

    //runtime network state
    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
    }
    //runtime permission for
    private  boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
//        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int internet =  ContextCompat.checkSelfPermission(this,android.Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
     /*   if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
       /* if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }*/
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                    }

                }).create().show();
    }
}

