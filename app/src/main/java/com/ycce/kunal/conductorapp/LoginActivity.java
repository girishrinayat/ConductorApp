package com.ycce.kunal.conductorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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

public class LoginActivity extends AppCompatActivity {

    private CheckBox cShowPassword;

    private ProgressDialog progressDialog;
    private LinearLayout lActivity,bActivity;
    private EditText email,password,busno;
    private Button login,confirm;

    private String mEMail,mPassword;
    private FirebaseAuth mAuth;

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
        if (flag) {
            ///second time activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            busNo=sharedPreferences.getString(busk,busNo);
            intent.putExtra("BusNo",busNo);
            finish();
            startActivity(intent);
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Loading......");
                progressDialog.show();

                mEMail = email.getText().toString();
                mPassword = password.getText().toString();

                editor.putString(emailk,mEMail);
                editor.putString(passwordk,mPassword);
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

                            progressDialog.dismiss();
                            busNumber();
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
    public void busNumber(){

        lActivity.setVisibility(View.GONE);
        bActivity.setVisibility(View.VISIBLE);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading......");
                progressDialog.show();

                busNo = busno.getText().toString();
                if (busNo.length()==4) {

                    editor.putString(busk,busNo);

                    editor.putBoolean("flag", true);
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    intent.putExtra("BusNo",busNo);
                    finish();
                    //Toast.makeText(Login.this, ""+busNo, Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                    startActivity(intent);
                }else{

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "please enter valid bus no.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
/*
        tforgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading......");
                progressDialog.show();
                finish();
             //   startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                progressDialog.dismiss();
            }
        });*/




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

