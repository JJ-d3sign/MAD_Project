package com.example.mad_project.api;

import com.example.mad_project.model.MealsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    @GET("random.php")
    Call<MealsResponse> getRandomMeal();

    @GET("search.php")
    Call<MealsResponse> searchMealsByFirstLetter(@Query("f") char firstLetter);

    @GET("search.php")
    Call<MealsResponse> searchMealsByName(@Query("s") String name);
}
