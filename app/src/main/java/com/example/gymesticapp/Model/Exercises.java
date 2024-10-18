package com.example.gymesticapp.Model;

public class Exercises {

    String exerciseNo;
    String exerciseName;
    String duration;
    String exerciseImage;

    public Exercises() {
        //for firebase
    }

    public Exercises(String exerciseNo, String exerciseName, String duration, String exerciseImage) {
        this.exerciseNo = exerciseNo;
        this.exerciseName = exerciseName;
        this.duration = duration;
        this.exerciseImage = exerciseImage;
    }
    //getter and setter
    public String getExerciseNo() {
        return exerciseNo;
    }

    public void setExerciseNo(String exerciseNo) {
        this.exerciseNo = exerciseNo;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getExerciseImage() {
        return exerciseImage;
    }

    public void setExerciseImage(String exerciseImage) {
        this.exerciseImage = exerciseImage;
    }
}
