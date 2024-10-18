package com.example.gymesticapp.Model;

public class Diets {
    String foodName;
    String ftime;
    String calories;
    String foodImage;

    public Diets() {
        //for firebase
    }

    public Diets(String foodName, String ftime, String calories, String foodImage) {
        this.foodName = foodName;
        this.ftime = ftime;
        this.calories = calories;
        this.foodImage = foodImage;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFtime() {
        return ftime;
    }

    public void setFtime(String ftime) {
        this.ftime = ftime;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
}
