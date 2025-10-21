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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecipeAdapter adapter;
    SearchView searchView;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar setup (for menu visibility)
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);

        adapter = new RecipeAdapter(this);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance(
                        "https://mad-project-72034-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("recipes");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.d("FirebaseDebug", "Recipes node empty. Adding sample recipes...");
                    addSampleRecipes();
                } else {
                    loadRecipes(snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDebug", "Database error: " + error.getMessage());
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filter(s);
                return false;
            }
        });
    }

    private void addSampleRecipes() {
        ArrayList<Recipe> samples = new ArrayList<>();
        samples.add(new Recipe("Pasta", "https://via.placeholder.com/150", "Delicious creamy pasta.", false));
        samples.add(new Recipe("Pizza", "https://via.placeholder.com/150", "Cheesy veggie pizza.", false));
        samples.add(new Recipe("Salad", "https://via.placeholder.com/150", "Healthy green salad.", false));

        for (Recipe recipe : samples) {
            String key = recipe.name.replace(".", "_");
            dbRef.child(key).setValue(recipe)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDebug", recipe.name + " added!"))
                    .addOnFailureListener(e -> Log.e("FirebaseDebug", "Failed to add " + recipe.name + ": " + e.getMessage()));
        }

        adapter.setData(samples);
    }

    private void loadRecipes(DataSnapshot snapshot) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (DataSnapshot ds : snapshot.getChildren()) {
            Recipe r = ds.getValue(Recipe.class);
            if (r != null) recipes.add(r);
        }
        adapter.setData(recipes);
    }

    // 🔸 Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // 🔸 Handle menu item clicks
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
