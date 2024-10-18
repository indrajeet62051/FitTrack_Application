package com.example.gymesticapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gymesticapp.Model.Exercises;
import com.example.gymesticapp.R;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    List<Exercises> exercisesList;

    public ExerciseAdapter(List<Exercises> exercisesList) {
        this.exercisesList = exercisesList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_list,parent,false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        holder.setExerciseData(exercisesList.get(position).getExerciseNo(),
                exercisesList.get(position).getExerciseName(),
                exercisesList.get(position).getDuration(),
                exercisesList.get(position).getExerciseImage());
        // Store the position as final to ensure it is correctly passed to the OnClickListener
        final int adapterPosition = holder.getAdapterPosition();

        // Handle click event for each item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExerciseDialog(holder.itemView, exercisesList.get(adapterPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercisesList.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder{

        LottieAnimationView exerciseAnim;
        TextView exerciseNo,exeriseName,duration;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseNo=itemView.findViewById(R.id.exerciseNo);
            exeriseName=itemView.findViewById(R.id.exerciseName);
            duration=itemView.findViewById(R.id.duration);
            exerciseAnim=itemView.findViewById(R.id.exerciseImage);

        }

        private void setExerciseData(String exerciseNo,String exerciseName,String duration,String exerciseUrl){
            //Glide.with(itemView.getContext()).load(exerciseUrl).into(exerciseImage);

            // Load Lottie animation from the URL
            exerciseAnim.setAnimationFromUrl(exerciseUrl);

            // Play the Lottie animation
            exerciseAnim.playAnimation();

            // Set text values for other fields
            this.exerciseNo.setText(exerciseNo);
            this.exeriseName.setText(exerciseName);
            this.duration.setText(duration);
        }
    }
    // Method to show a dialog with exercise details
    private void showExerciseDialog(View view, Exercises exercise) {
        // Create a dialog
        android.app.Dialog dialog = new android.app.Dialog(view.getContext());
        dialog.setContentView(R.layout.exerciseview);

        // Set exercise name in the dialog
        TextView exerciseName = dialog.findViewById(R.id.dialogExerciseName);
        exerciseName.setText(exercise.getExerciseName());


        // Set exercise name in the dialog
        TextView exerciseDuration = dialog.findViewById(R.id.dialogExerciseDuration);
        exerciseDuration.setText(exercise.getDuration());


        // Set Lottie animation in the dialog
        LottieAnimationView lottieAnimation = dialog.findViewById(R.id.exerciseAnimation);
        lottieAnimation.setAnimationFromUrl(exercise.getExerciseImage());
        lottieAnimation.playAnimation();

        // Show the dialog
        dialog.show();
    }
}
