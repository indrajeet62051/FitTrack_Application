package com.example.gymesticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gymesticapp.Adapter.ExerciseAdapter;
import com.example.gymesticapp.Model.Exercises;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    TextView workName,workTime;
    ImageView workoutImageView,back;
    RecyclerView exerciseRecyView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        workName=findViewById(R.id.workName);
        workTime=findViewById(R.id.workoutTime);
        workoutImageView=findViewById(R.id.imageWorkout);
        back=findViewById(R.id.backBtnImage);

        exerciseRecyView=findViewById(R.id.exerciseRecyView);

        //get workout title,time,image
        String title=getIntent().getStringExtra("title");
        String time=getIntent().getStringExtra("time");
        String imageUrl=getIntent().getStringExtra("imageWorkout");

        //set workoutName,workoutTime
        workName.setText(title);
        workTime.setText(time);
        //set workoutTime,Image
        if (imageUrl!=null){
            Glide.with(this).load(imageUrl).into(workoutImageView);
        }

        //back to home activity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set layoutmanager,initialize list and adapter
        List<Exercises> exercisesList=new ArrayList<>();
        exerciseRecyView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ExerciseAdapter exerciseAdapter=new ExerciseAdapter(exercisesList);
        exerciseRecyView.setAdapter(exerciseAdapter);

        //fetch data from firebase
        databaseReference=database.getReference("Exercises").child(title);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                exercisesList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Exercises exercises=snapshot1.getValue(Exercises.class);
                    exercisesList.add(exercises);
                }
                exerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExercisesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}