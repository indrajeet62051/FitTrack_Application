package com.example.gymesticapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText edFname,edMobileNo,edEmail,edPassword;
    TextView textLogin;
    FirebaseAuth auth;
    Button btnNext;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edFname=findViewById(R.id.edfname);
        edMobileNo=findViewById(R.id.edMobileNo);
        edEmail=findViewById(R.id.edemail);
        edPassword=findViewById(R.id.edpassword);
        btnNext=findViewById(R.id.btn_next);
        textLogin=findViewById(R.id.txtLogin);


        //User register
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        String fnameStr=edFname.getText().toString().trim();
                        String mobileNoStr=edMobileNo.getText().toString().trim();
                        String email=edEmail.getText().toString().trim();
                        String password=edPassword.getText().toString().trim();

                        if(validateInput(fnameStr,mobileNoStr,email,password)){
                            Intent intent=new Intent(RegisterActivity.this,RegistrationActivity.class);
                            intent.putExtra("fullName",fnameStr);
                            intent.putExtra("mobileNo",mobileNoStr);
                            intent.putExtra("email",email);
                            intent.putExtra("password",password);
                            startActivity(intent);
                            finish();
                        }

            }
        });
        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lintent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(lintent);
            }
        });
    }
    private boolean validateInput(String fullName,String mobile,String email,String password){
        if (fullName.isEmpty() || mobile.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "All fiels are reqired", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mobile.length()!=10){
            edMobileNo.setError("Enter a valid 10 digit mobile number");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edEmail.setError("Enter a valid email");
            return false;
        }
        if (password.length()<=5){
            edPassword.setError("Password must be at least 5 characters");
            return false;
        }
        return true;
    }

}