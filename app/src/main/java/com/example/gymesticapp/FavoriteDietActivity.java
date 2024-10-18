package com.example.gymesticapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.example.gymesticapp.Adapter.DietAdapter;
import com.example.gymesticapp.Model.Diets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDietActivity extends AppCompatActivity {

    RecyclerView rvFavoriteDiets;
    List<Diets> favoriteDiets;
    DietAdapter favoriteAdapter;
    DatabaseReference favoriteRef;
    FirebaseAuth auth;
    ImageView backtoDiet;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_diet);

        backtoDiet=findViewById(R.id.backDietBtn);
        backtoDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView
        rvFavoriteDiets = findViewById(R.id.favoriteRecy);
        rvFavoriteDiets.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Initialize List and Adapter
        favoriteDiets = new ArrayList<>();
        favoriteAdapter = new DietAdapter(favoriteDiets);
        rvFavoriteDiets.setAdapter(favoriteAdapter);

        // Get reference to user's favorite diets in Firebase
        auth = FirebaseAuth.getInstance();
        favoriteRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(auth.getUid()).child("Favorites");

        // Fetch favorite diets
        fetchFavoriteDiets();
    }
    private void fetchFavoriteDiets() {
        favoriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteDiets.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Diets diet = snapshot1.getValue(Diets.class);
                    favoriteDiets.add(diet);
                }
                favoriteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}