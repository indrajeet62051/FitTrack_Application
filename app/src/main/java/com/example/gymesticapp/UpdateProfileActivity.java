package com.example.gymesticapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {

    ImageView imageView;
    Calendar calendar;
    EditText updateFullName,updateMobileno,updateBirthdate,updateWeight,updateHeight;
    RadioButton updateRadioMale,updateRadioFemale;
    RadioGroup updateGenderGroup;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    Button save;
    FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        updateFullName=findViewById(R.id.updateFname);
        updateMobileno=findViewById(R.id.updateMobileNo);
        updateBirthdate=findViewById(R.id.updateBirthDate);
        updateGenderGroup=findViewById(R.id.updateGenderRadioGroup);
        updateRadioMale=findViewById(R.id.updateRadioMale);
        updateRadioFemale=findViewById(R.id.updateRadioFemale);
        updateHeight=findViewById(R.id.updateHeight);
        updateWeight=findViewById(R.id.updateWeight);
        //save button
        save=findViewById(R.id.btnSave);

        calendar=Calendar.getInstance();

        updateBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //back to profile activity
        imageView=findViewById(R.id.backProfile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(UpdateProfileActivity.this);
                alertDialog.setTitle("Unsaved Changes");
                alertDialog.setMessage("Are you sure you want to discard unsaved changes?");
                alertDialog.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        });

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        //get user details
        getCurrentUserDetails();

        //save user edit profile details
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

    }
    //get currently login user details
    private void getCurrentUserDetails(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    updateFullName.setText(snapshot.child("fullName").getValue(String.class));
                    updateMobileno.setText(snapshot.child("mobileNo").getValue(String.class));
                    updateBirthdate.setText(snapshot.child("birthDate").getValue(String.class));
                    updateWeight.setText(snapshot.child("weight").getValue(String.class));
                    updateHeight.setText(snapshot.child("height").getValue(String.class));

                    String gender=snapshot.child("gender").getValue(String.class);
                    if (gender!=null){
                        if (gender.equals("Male")){
                            updateRadioMale.setChecked(true);
                        }else if (gender.equals("Female")){
                            updateRadioFemale.setChecked(true);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //date picker dialog
    private void showDatePickerDialog(){
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dtPickerDialog=new DatePickerDialog(UpdateProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String bdate=dayOfMonth+"-"+month+"-"+year;
                updateBirthdate.setText(bdate);
            }
        }, year,month,day);
        dtPickerDialog.show();
    }

    //save profile(if user update any details
    private void saveUserProfile(){

        AlertDialog.Builder progressBuilder=new AlertDialog.Builder(UpdateProfileActivity.this);
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

                String fullName=updateFullName.getText().toString().trim();
                String mobile=updateMobileno.getText().toString().trim();
                String birthDate=updateBirthdate.getText().toString().trim();
                int selectedGenderId=updateGenderGroup.getCheckedRadioButtonId();
                RadioButton selectGender=findViewById(selectedGenderId);
                String gender=selectGender.getText().toString();
                String weight=updateWeight.getText().toString().trim();
                String height=updateHeight.getText().toString().trim();

                //validation
                if (fullName.isEmpty() || mobile.isEmpty() || birthDate.isEmpty() || selectedGenderId==-1 || weight.isEmpty() || height.isEmpty()){
                    Toast.makeText(UpdateProfileActivity.this, "All fiels are reqired", Toast.LENGTH_SHORT).show();
                }else {
                    //check weight
                    float weightft=Float.parseFloat(weight);
                    if (weightft<20||weightft>300){
                        updateWeight.setError("Please enter valid weight between 20 to 300");
                    }else {
                        //update details in the database
                        databaseReference.child("fullName").setValue(fullName);
                        databaseReference.child("mobileNo").setValue(mobile);
                        databaseReference.child("birthDate").setValue(birthDate);
                        databaseReference.child("gender").setValue(gender);
                        databaseReference.child("weight").setValue(weight);
                        databaseReference.child("height").setValue(height);

                        Toast.makeText(UpdateProfileActivity.this, "Profile Updated successfully", Toast.LENGTH_SHORT).show();
                        //back to profile fragment
                        finish();
                        progressDialog.dismiss();
                    }
                }
                if (TextUtils.isEmpty(fullName)) {
                    updateFullName.setError("Full name is required");
                }
                if (mobile.length()!=10){
                    updateMobileno.setError("Enter a valid 10 digit mobile number");
                }
                progressDialog.dismiss();
            }
        },2000);

    }
}