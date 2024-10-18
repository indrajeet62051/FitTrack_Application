package com.example.gymesticapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymesticapp.Adapter.WorkoutAdapter;
import com.example.gymesticapp.Model.Workouts;
import com.example.gymesticapp.R;
import com.example.gymesticapp.TwistActivity;
import com.example.gymesticapp.UploadProfilePicActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference= database.getReference();
    ShapeableImageView userimg;
    ShapeableImageView twist;
    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        TextView userText=view.findViewById(R.id.txtUsername);

        userimg=view.findViewById(R.id.userImgView);
        twist=view.findViewById(R.id.twist);
        twist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t=new Intent(getActivity(), TwistActivity.class);
                startActivity(t);
            }
        });
        //set user profile image
        FirebaseAuth authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        Uri uri=firebaseUser.getPhotoUrl();
        //image set
        Glide.with(HomeFragment.this).load(uri).placeholder(R.drawable.userprofilepic).into(userimg);

        userimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), UploadProfilePicActivity.class);
                startActivity(i);
            }
        });

        recyclerView=view.findViewById(R.id.workoutRecyView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        List<Workouts> workoutsList=new ArrayList<>();
        WorkoutAdapter workoutAdapter=new WorkoutAdapter(workoutsList);
        recyclerView.setAdapter(workoutAdapter);

        databaseReference.child("Workout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workoutsList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    workoutsList.add(dataSnapshot.getValue(Workouts.class));
                }
                workoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //get the currently logged in user name
        FirebaseAuth auth=FirebaseAuth.getInstance();
        String currentUser=auth.getCurrentUser().getUid();
        DatabaseReference dbReference=FirebaseDatabase.getInstance().getReference("Users");

        //retrive the user's name
        dbReference.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Get user name
                    String userName=snapshot.child("fullName").getValue(String.class);

                    //Display the fetched user name
                    userText.setText(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}