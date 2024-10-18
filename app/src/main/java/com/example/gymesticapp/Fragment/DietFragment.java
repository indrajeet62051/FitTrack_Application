package com.example.gymesticapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymesticapp.Adapter.DietAdapter;
import com.example.gymesticapp.FavoriteDietActivity;
import com.example.gymesticapp.Model.Diets;
import com.example.gymesticapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DietFragment extends Fragment {

    RecyclerView rvBreakfast,rvLunch,rvDinner;
    List<Diets> breakfastDiets,lunchDiets,dinnerDiets;
    DietAdapter breakfastAdapter,lunchAdapter,dinnerAdapter;
    DatabaseReference brekfastRef,lunchRef,dinnerRef;
    ImageButton imageButton;

    public DietFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);
        imageButton=view.findViewById(R.id.userFavorite);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), FavoriteDietActivity.class);
                startActivity(intent);
            }
        });

        //Initialize RecyclerViews
        rvBreakfast=view.findViewById(R.id.breakfastRecy);
        rvLunch=view.findViewById(R.id.lunchRecy);
        rvDinner=view.findViewById(R.id.dinnerRecy);

        //set LayoutManager
        rvBreakfast.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvLunch.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvDinner.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        //Initialize Lists and Adapter
        breakfastDiets=new ArrayList<>();
        lunchDiets=new ArrayList<>();
        dinnerDiets=new ArrayList<>();

        breakfastAdapter=new DietAdapter(breakfastDiets);
        lunchAdapter=new DietAdapter(lunchDiets);
        dinnerAdapter=new DietAdapter(dinnerDiets);

        //set adapter
        rvBreakfast.setAdapter(breakfastAdapter);
        rvLunch.setAdapter(lunchAdapter);
        rvDinner.setAdapter(dinnerAdapter);

        //initialize firebase reference
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        brekfastRef=database.getReference("Diets").child("breakfast");
        lunchRef=database.getReference("Diets").child("lunch");
        dinnerRef=database.getReference("Diets").child("dinner");

        //fetch data from firebase
        fetchDiets(brekfastRef,breakfastDiets,breakfastAdapter);
        fetchDiets(lunchRef,lunchDiets,lunchAdapter);
        fetchDiets(dinnerRef,dinnerDiets,dinnerAdapter);

        return view;
    }
    private void fetchDiets(DatabaseReference databaseReference,List<Diets> dietsList,DietAdapter dietAdapter){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dietsList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    Diets diets=snapshot1.getValue(Diets.class);
                    if (diets != null) {
                        dietsList.add(diets);
                    }
                }
                dietAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}