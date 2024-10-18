package com.example.gymesticapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView textForgot,txtRegistration;
    EditText lemail,lpassword;
    Button loginBtn;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textForgot=findViewById(R.id.txtForgotpwd);
        txtRegistration=findViewById(R.id.txtRegister);
        lemail=findViewById(R.id.edLoginEmail);
        lpassword=findViewById(R.id.edLoginPassword);
        loginBtn=findViewById(R.id.btnLogin);
        textForgot=findViewById(R.id.txtForgotpwd);
        //Forgot password? underline
        textForgot.setPaintFlags(textForgot.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=mAuth.getCurrentUser();

        if (currentUser!=null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder progressBuilder=new AlertDialog.Builder(LoginActivity.this);
                progressBuilder.setCancelable(false);
                progressBuilder.setView(R.layout.progress_layout);
                AlertDialog progressDialog= progressBuilder.create();
                if (progressDialog.getWindow()!=null){
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                progressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String email=lemail.getText().toString();
                        String password=lpassword.getText().toString();

                        if (!email.isEmpty()|| Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            if (!password.isEmpty()){
                                mAuth.signInWithEmailAndPassword(email,password)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                                Intent homeIntent=new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(homeIntent);
                                                finish();
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "Login Failed!Checked your credential", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }else{
                                lpassword.setError("Please filled password");
                            }
                        }else {
                            lemail.setError("Please enter email");
                        }
                        progressDialog.dismiss();
                    }
                },2500);
            }
        });

        //Forgot Password
        textForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText mail=new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog=new AlertDialog.Builder(LoginActivity.this);
                View dialogview=getLayoutInflater().inflate(R.layout.forgot_password_dialogbox,null);
                EditText mail=dialogview.findViewById(R.id.mailBox);

                passwordResetDialog.setView(dialogview);
                AlertDialog dialog=passwordResetDialog.create();

                dialogview.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String resetEmail=mail.getText().toString();

                        if (resetEmail.isEmpty() ){
                            Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                            mail.setError("Emial is required");
                            return;
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                            Toast.makeText(LoginActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                            mail.setError("Valid email is required");
                            mail.requestFocus();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialogview.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow()!=null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
                /*
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter Your Register Email Address");
                passwordResetDialog.setView(mail);


                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //send reset password link to user
                        String resetEmail=mail.getText().toString();
                        if (resetEmail.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()){
                            Toast.makeText(LoginActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(resetEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Link is not sent", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });


                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close the alert dialog box
                    }
                });
                passwordResetDialog.create().show();
                */
            }
        });

        txtRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rintent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(rintent);
            }
        });
    }
}