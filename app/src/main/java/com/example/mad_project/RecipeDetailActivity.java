package com.example.mad_project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout; // Import

public class RecipeDetailActivity extends AppCompatActivity {

    ImageView detailImage;
    TextView detailDescription; // detailName is now part of the toolbar
    CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // --- MODIFIED ---
        detailImage = findViewById(R.id.detailImage);
        detailDescription = findViewById(R.id.detailDescription);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);

        // Set up the toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // --- (End Modification) ---


        String name = getIntent().getStringExtra("name");
        String imageUrl = getIntent().getStringExtra("image");
        String desc = getIntent().getStringExtra("desc");

        // --- MODIFIED ---
        collapsingToolbar.setTitle(name != null ? name : "No Name"); // Set title on collapsing toolbar
        // detailName.setText(name != null ? name : "No Name"); // This TextView is gone, use the one in the CardView if you keep it
        detailDescription.setText(desc != null ? desc : "No Description");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(detailImage);
        } else {
            detailImage.setImageResource(R.drawable.ic_launcher_background);
        }
        // --- (End Modification) ---
    }

    // Handle the toolbar's back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}