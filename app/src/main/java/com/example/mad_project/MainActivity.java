package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast; // Import for Toast message
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_project.api.MealApiService;
import com.example.mad_project.model.MealsResponse;
import com.example.mad_project.api.RetrofitClient;
import com.example.mad_project.model.Meal;

// --- Imports for Firebase (Database + Auth) ---
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// --- End Imports ---

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

    // --- Firebase Variables ---
    private DatabaseReference dbRef;
    private List<Recipe> firebaseRecipes = new ArrayList<>();
    private FirebaseAuth auth;
    // --- End Firebase Variables ---

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

        // --- Initialize Firebase Auth ---
        auth = FirebaseAuth.getInstance();

        // --- Initialize Firebase Database ---
        // Using the same URL from your AddRecipeActivity
        dbRef = FirebaseDatabase.getInstance(
                        "https://mad-project-72034-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("recipes");
        setupFirebaseListener();
        // --- End Firebase Init ---

        fetchInitialRecipes(); // Fetch initial API recipes

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

    /**
     * Sets up the real-time listener for Firebase Database.
     * This fetches all user-added recipes and updates the list on any change.
     */
    private void setupFirebaseListener() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firebaseRecipes.clear(); // Clear the old list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        firebaseRecipes.add(recipe);
                    }
                }

                // Refresh the list with the current query
                String currentQuery = searchView.getQuery().toString();
                if (currentQuery.isEmpty()) {
                    fetchInitialRecipes();
                } else {
                    searchRecipes(currentQuery);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase_Error", "Failed to read data: " + error.getMessage());
            }
        });
    }

    /**
     * Fetches the initial "soup" recipes from the API.
     */
    private void fetchInitialRecipes() {
        searchRecipes("soup");
    }

    /**
     * Searches for recipes from both the API and the Firebase Database.
     * @param query The search term entered by the user.
     */
    private void searchRecipes(String query) {
        // Normalize the query for case-insensitive matching
        String normalizedQuery = query.toLowerCase();

        apiService.searchMealsByName(query).enqueue(new Callback<MealsResponse>() {
            @Override
            public void onResponse(Call<MealsResponse> call, Response<MealsResponse> response) {
                List<Recipe> apiRecipes = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    List<Meal> meals = response.body().getMeals();
                    for (Meal meal : meals) {
                        apiRecipes.add(new Recipe(meal.getStrMeal(), meal.getStrMealThumb(), meal.getStrInstructions(), false));
                    }
                } else {
                    Log.e("API_ERROR", "Response not successful or body is empty");
                }

                // --- Combine lists ---
                List<Recipe> combinedList = new ArrayList<>();

                // 1. Add API recipes (they are already filtered by the API call)
                combinedList.addAll(apiRecipes);

                // 2. Filter and add Firebase recipes
                if (firebaseRecipes != null && !firebaseRecipes.isEmpty()) {
                    for (Recipe fbRecipe : firebaseRecipes) {
                        if (fbRecipe.getName() != null && fbRecipe.getName().toLowerCase().contains(normalizedQuery)) {
                            // Simple check to avoid adding duplicates
                            boolean alreadyAdded = false;
                            for(Recipe apiRecipe : apiRecipes){
                                if(apiRecipe.getName() != null && apiRecipe.getName().equalsIgnoreCase(fbRecipe.getName())){
                                    alreadyAdded = true;
                                    break;
                                }
                            }
                            if(!alreadyAdded){
                                combinedList.add(fbRecipe);
                            }
                        }
                    }
                }

                // 3. Set the combined data to the adapter
                adapter.setData(combinedList);
                // --- End combining lists ---
            }

            @Override
            public void onFailure(Call<MealsResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Failed to fetch data: " + t.getMessage());

                // If API fails, still show matching Firebase recipes
                List<Recipe> combinedList = new ArrayList<>();
                if (firebaseRecipes != null && !firebaseRecipes.isEmpty()) {
                    for (Recipe fbRecipe : firebaseRecipes) {
                        if (fbRecipe.getName() != null && fbRecipe.getName().toLowerCase().contains(normalizedQuery)) {
                            combinedList.add(fbRecipe);
                        }
                    }
                }
                adapter.setData(combinedList);
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
        }

        else if (id == R.id.favorites) {
            // Placeholder for favorites feature
            return true;
        }

        // --- Logout Button Handler ---
        else if (id == R.id.logout) {
            auth.signOut(); // Sign the user out
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

            // Send user back to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // Clear the activity stack to prevent user from pressing "back"
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish(); // Close MainActivity
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}