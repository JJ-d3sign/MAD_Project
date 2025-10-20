package com.example.mad_project;
public class Recipe {
    public String name;
    public String imageUrl;
    public String description;
    public boolean favorite;

    public Recipe() {} // Required for Firebase

    public Recipe(String name, String imageUrl, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.favorite = false;
    }
}



