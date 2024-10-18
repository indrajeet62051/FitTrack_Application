package com.example.gymesticapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.gymesticapp.LoginActivity;
import com.example.gymesticapp.Model.UserModel;
import com.example.gymesticapp.R;
import com.example.gymesticapp.SettingActivity;
import com.example.gymesticapp.UpdateProfileActivity;
import com.example.gymesticapp.UploadProfilePicActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    DatabaseReference dbReference;
    FirebaseAuth auth;
    TextView txtuname,txtEmail,txtMobileNo,txtGender,txtBirthDate,txtWeight,txtHeight;
    ImageView userImg;
    Button btnLogout;
    LinearLayout btnUpdateImg,btnSetting,feedback;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        txtuname=view.findViewById(R.id.userName);
        txtEmail=view.findViewById(R.id.txtEmail);
        txtMobileNo=view.findViewById(R.id.userMobileNo);
        txtGender=view.findViewById(R.id.userGender);
        txtBirthDate=view.findViewById(R.id.userBirtDate);
        txtWeight=view.findViewById(R.id.userWeight);
        txtHeight=view.findViewById(R.id.userHeight);

        btnUpdateImg=view.findViewById(R.id.imgEditProfile);
        //redirect to update profile activity
        btnUpdateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
            }
        });

        btnSetting=view.findViewById(R.id.settingBtn);
        //redirect to update profile activity
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        feedback=view.findViewById(R.id.Feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipientEmail = "vp348518@gmail.com";  // Replace with your support email
                String subject = "App Feedback";
                String body = "Please enter your feedback here...";

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822"); // Only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);

                // Explicitly specify the Gmail package to open
                emailIntent.setPackage("com.google.android.gm");

                try {
                    startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(requireActivity(), "Gmail app is not installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout=view.findViewById(R.id.btnLogout);

        userImg=view.findViewById(R.id.userImage);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), UploadProfilePicActivity.class);
                startActivity(i);
            }
        });

        //get the currently logged in user details
        auth=FirebaseAuth.getInstance();
        String currentUserId=auth.getCurrentUser().getUid();
        dbReference=FirebaseDatabase.getInstance().getReference("Users");

        //set user profile image
        FirebaseAuth authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        Uri uri=firebaseUser.getPhotoUrl();
        //image set
        Glide.with(ProfileFragment.this).load(uri).placeholder(R.drawable.userprofilepic).into(userImg);


        //retrive the user's details
        dbReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel=snapshot.getValue(UserModel.class);
                if (userModel!=null){
                    //Get user name
                    String userName=snapshot.child("fullName").getValue(String.class);
                    String userEmail=snapshot.child("email").getValue(String.class);
                    String userMobileNo=snapshot.child("mobileNo").getValue(String.class);
                    String userGender=snapshot.child("gender").getValue(String.class);
                    String userBirtDate=snapshot.child("birthDate").getValue(String.class);
                    String userWeight=snapshot.child("weight").getValue(String.class);
                    String userHeight=snapshot.child("height").getValue(String.class);

                    //Display the fetched user details
                    txtuname.setText(userName);
                    txtEmail.setText(userEmail);
                    txtMobileNo.setText(userMobileNo);
                    txtGender.setText(userGender);
                    txtBirthDate.setText(userBirtDate);
                    txtWeight.setText(userWeight);
                    txtHeight.setText(userHeight);


                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder progressBuilder=new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater=getLayoutInflater();
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
                        auth.signOut();
                        Toast.makeText(getActivity(), "Logout successfully", Toast.LENGTH_SHORT).show();
                        //redirect to login screen
                        Intent intent=new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                        progressDialog.dismiss();
                    }
                },2000);

            }
        });

        return view;
    }
}