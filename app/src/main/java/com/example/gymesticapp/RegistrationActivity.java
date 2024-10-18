package com.example.gymesticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.gymesticapp.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {

    EditText edHeight,edDate,edWeight;
    RadioGroup genderGroup;

    Calendar calendar;
    Button register;
    DatePickerDialog.OnDateSetListener setListener;
    FirebaseAuth mAuth;
    DatabaseReference mDatabse;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edHeight=findViewById(R.id.edHeight);
        edDate=findViewById(R.id.edDate);
        edWeight=findViewById(R.id.edWeight);
        genderGroup=findViewById(R.id.genderRadioGroup);
        register=findViewById(R.id.btnRegister);

        calendar=Calendar.getInstance();
        mAuth=FirebaseAuth.getInstance();
        mDatabse= FirebaseDatabase.getInstance().getReference();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder progressBuilder=new AlertDialog.Builder(RegistrationActivity.this);
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
                        String birthDate=edDate.getText().toString().trim();
                        int selectedGenderId=genderGroup.getCheckedRadioButtonId();
                        RadioButton selectGender=findViewById(selectedGenderId);
                        String gender=selectGender.getText().toString();
                        String weightstr=edWeight.getText().toString().trim();
                        String height=edHeight.getText().toString().trim();
                        if (birthDate.isEmpty() || selectedGenderId==-1 || weightstr.isEmpty() || height.isEmpty()){
                            Toast.makeText(RegistrationActivity.this, "All fields are reqired", Toast.LENGTH_SHORT).show();
                        }
                        float weightft=Float.parseFloat(weightstr);
                        if (weightft<20||weightft>300){
                            edWeight.setError("Please enter valid weight between 20 to 300");
                        }else {
                            String fullName = getIntent().getStringExtra("fullName");
                            String mobileNo = getIntent().getStringExtra("mobileNo");
                            String email = getIntent().getStringExtra("email");
                            String password = getIntent().getStringExtra("password");
                            //create user
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            String userId = firebaseUser.getUid();
                                            UserModel userModel = new UserModel(fullName, mobileNo, email, birthDate, gender, weightstr, height);
                                            //store user data
                                            mDatabse.child("Users").child(userId).setValue(userModel)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                        progressDialog.dismiss();
                    }
                },600);
            }
        });

        //show the date picker dialog box
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);

        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        RegistrationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month=month+1;
                        String date=day+"-"+month+"-"+year;
                        edDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }
}