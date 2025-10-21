package com.example.mad_project;

public class Recipe {
    public String name;
    public String imageUrl;
    public String description;
    public boolean favorite;

    // Default constructor for Firebase
    public Recipe() {}

    // Parameterized constructor
    public Recipe(String name, String imageUrl, String description, boolean favorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.favorite = favorite;
    }
}
