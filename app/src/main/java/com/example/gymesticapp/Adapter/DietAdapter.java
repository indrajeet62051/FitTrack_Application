package com.example.gymesticapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymesticapp.Model.Diets;
import com.example.gymesticapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.FoodViewHolder> {

    List<Diets> dietsList;
    FirebaseAuth auth;
    DatabaseReference userFavoriteRef;


    public DietAdapter(List<Diets> dietsList) {

        this.dietsList = dietsList;
        auth=FirebaseAuth.getInstance();
        userFavoriteRef= FirebaseDatabase.getInstance().getReference("Users")
                .child(auth.getUid()).child("Favorites");
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_list,parent,false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Diets diets=dietsList.get(position);
        holder.setDietData(dietsList.get(position).getFoodName(), dietsList.get(position).getFtime(), dietsList.get(position).getCalories(), dietsList.get(position).getFoodImage());
        String dietName=diets.getFoodName();
        userFavoriteRef.child(dietName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.favoriteImgBtn.setImageResource(R.drawable.baseline_favorite_24);
                }else {
                    holder.favoriteImgBtn.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.favoriteImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(diets,holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dietsList.size();
    }

    private void toggleFavorite(Diets diets, FoodViewHolder viewHolder){
        String dietname= diets.getFoodName();
        userFavoriteRef.child(dietname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //if already favorite,remove it
                    userFavoriteRef.child(dietname).removeValue();
                    viewHolder.favoriteImgBtn.setImageResource(R.drawable.baseline_favorite_border_24);
                }else {
                    //if noy favorite,add it
                    userFavoriteRef.child(dietname).setValue(diets);
                    viewHolder.favoriteImgBtn.setImageResource(R.drawable.baseline_favorite_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder{

        ShapeableImageView imageView;
        ImageView favoriteImgBtn;
        TextView foodName,ftime,calories;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName=itemView.findViewById(R.id.foodName);
            ftime=itemView.findViewById(R.id.ftime);
            calories=itemView.findViewById(R.id.calories);
            imageView=itemView.findViewById(R.id.foodImage);
            favoriteImgBtn=itemView.findViewById(R.id.favoritImgBtn);

        }
        private void setDietData(String foodName,String ftime,String calories,String imageUrl){
            Glide.with(itemView.getContext()).load(imageUrl).into(imageView);
            this.foodName.setText(foodName);
            this.ftime.setText(ftime);
            this.calories.setText(calories);
        }

    }
}
