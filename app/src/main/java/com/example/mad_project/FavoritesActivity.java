package com.example.mad_project;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private DatabaseReference dbRef;
    private List<Recipe> favoritesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favorite Recipes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back button

        recyclerView = findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // IMPORTANT: Use the same database URL and reference as in your other activities
        dbRef = FirebaseDatabase.getInstance(
                "https://mad-project-72034-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("recipes");

        // Pass the dbRef to the adapter so it can also handle favorite clicks
        adapter = new RecipeAdapter(this, dbRef);
        recyclerView.setAdapter(adapter);

        setupFavoritesListener();
    }

    private void setupFavoritesListener() {
        // Create a query to find recipes where "favorite" is true
        Query favoritesQuery = dbRef.orderByChild("favorite").equalTo(true);

        favoritesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        favoritesList.add(recipe);
                    }
                }
                adapter.setData(favoritesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavoritesActivity", "Failed to read data: " + error.getMessage());
            }
        });
    }

    // Handle the toolbar's back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}