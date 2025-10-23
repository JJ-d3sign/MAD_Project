package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.api.MealApiService;
import com.example.mad_project.model.MealsResponse;
import com.example.mad_project.api.RetrofitClient;
import com.example.mad_project.model.Meal;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecipeAdapter adapter;
    SearchView searchView;
    private MealApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);

        adapter = new RecipeAdapter(this);
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();
        fetchInitialRecipes();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    searchRecipes(newText);
                } else {
                    fetchInitialRecipes();
                }
                return true;
            }
        });
    }

    private void fetchInitialRecipes() {
        searchRecipes("soup"); // Fetch a broader category of recipes on startup
    }

    private void searchRecipes(String query) {
        apiService.searchMealsByName(query).enqueue(new Callback<MealsResponse>() {
            @Override
            public void onResponse(Call<MealsResponse> call, Response<MealsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    List<Meal> meals = response.body().getMeals();
                    List<Recipe> recipes = new ArrayList<>();
                    for (Meal meal : meals) {
                        recipes.add(new Recipe(meal.getStrMeal(), meal.getStrMealThumb(), meal.getStrInstructions(), false));
                    }
                    adapter.setData(recipes);
                } else {
                    Log.e("API_ERROR", "Response not successful or body is empty");
                    adapter.setData(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealsResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_recipe) {
            startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
            return true;
        } else if (id == R.id.favorites) {
            // Placeholder for favorites feature
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
