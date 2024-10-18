package com.example.gymesticapp.Model;

public class Workouts {

    String title;
    String time;
    String exercise;
    String workoutImage;

    public Workouts() {
        //firebase
    }

    public Workouts(String title, String time, String exercise, String workoutImage) {
        this.title = title;
        this.time = time;
        this.exercise = exercise;
        this.workoutImage = workoutImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getWorkoutImage() {
        return workoutImage;
    }

    public void setWorkoutImage(String workoutImage) {
        this.workoutImage = workoutImage;
    }
}
