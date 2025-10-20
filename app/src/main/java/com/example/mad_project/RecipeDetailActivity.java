package com.example.mad_project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class RecipeDetailActivity extends AppCompatActivity {

    ImageView detailImage;
    TextView detailName, detailDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        detailImage = findViewById(R.id.detailImage);
        detailName = findViewById(R.id.detailName);
        detailDescription = findViewById(R.id.detailDescription);

        String name = getIntent().getStringExtra("name");
        String imageUrl = getIntent().getStringExtra("image");
        String desc = getIntent().getStringExtra("desc");

        detailName.setText(name != null ? name : "No Name");
        detailDescription.setText(desc != null ? desc : "No Description");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(detailImage);
        } else {
            detailImage.setImageResource(R.drawable.ic_launcher_background);
        }

    }
}
