package com.example.mad_project;

public class Recipe {
    private String name;
    private String imageUrl;
    private String description;
    private boolean favorite;

    public Recipe() {}

    public Recipe(String name, String imageUrl, String description, boolean favorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
