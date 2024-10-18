package com.example.gymesticapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gymesticapp.ExercisesActivity;
import com.example.gymesticapp.Model.Workouts;
import com.example.gymesticapp.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    List<Workouts> workoutsList;

    public WorkoutAdapter(List<Workouts> workoutsList) {
        this.workoutsList = workoutsList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_recyview,parent,false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        holder.setWorkoutData(workoutsList.get(position).getTitle(),
                workoutsList.get(position).getTime(),
                workoutsList.get(position).getExercise(),
                workoutsList.get(position).getWorkoutImage());
    }

    @Override
    public int getItemCount() {
        return workoutsList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder{
        TextView title,time,exercises;
        ShapeableImageView imageView;
        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);
            time=itemView.findViewById(R.id.time);
            exercises=itemView.findViewById(R.id.exercises);
            imageView=itemView.findViewById(R.id.workoutImage);
        }
        private void setWorkoutData(String title,String time,String exercises,String imageUrl){

            Glide.with(itemView.getContext()).load(imageUrl).into(imageView);
            this.title.setText(title);
            this.time.setText(time);
            this.exercises.setText(exercises);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(itemView.getContext(), ExercisesActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("time",time);
                    intent.putExtra("imageWorkout",imageUrl);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
