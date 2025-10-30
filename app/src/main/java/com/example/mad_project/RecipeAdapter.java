package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Import Toast
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference; // Import
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private List<Recipe> fullList;
    private DatabaseReference dbRef; // Add this

    // --- MODIFIED CONSTRUCTOR ---
    public RecipeAdapter(Context context, DatabaseReference dbRef) {
        this.context = context;
        this.dbRef = dbRef; // Initialize
        this.recipeList = new ArrayList<>();
        this.fullList = new ArrayList<>();
    }

    // --- SECOND CONSTRUCTOR (to avoid breaking MainActivity) ---
    // You can remove this if you update MainActivity right away
    public RecipeAdapter(Context context) {
        this.context = context;
        this.dbRef = null; // Or initialize it properly if you prefer
        this.recipeList = new ArrayList<>();
        this.fullList = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        String name = recipe.getName();
        holder.recipeName.setText(name != null ? name : "No Name");

        String imageUrl = recipe.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.favBtn.setImageResource(recipe.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("name", recipe.getName() != null ? recipe.getName() : "No Name");
            intent.putExtra("image", recipe.getImageUrl() != null ? recipe.getImageUrl() : "");
            intent.putExtra("desc", recipe.getDescription() != null ? recipe.getDescription() : "No Description");
            context.startActivity(intent);
        });

        // --- MODIFIED CLICK LISTENER ---
        holder.favBtn.setOnClickListener(v -> {
            if (dbRef == null) {
                Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show();
                return;
            }

            // This is a potential issue: using 'name' as a key
            // It will fail if the name has characters like '.', '#', '$', '[', or ']'
            // It also won't work for API-based recipes unless they are first saved to your DB.
            String recipeKey = recipe.getName();
            if (recipeKey == null || recipeKey.isEmpty()) {
                Toast.makeText(context, "Cannot favorite this item", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean newFav = !recipe.isFavorite();
            recipe.setFavorite(newFav); // Update local object
            holder.favBtn.setImageResource(newFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border); // Update UI

            // Save the new "favorite" value to Firebase
            dbRef.child(recipeKey).child("favorite").setValue(newFav)
                    .addOnSuccessListener(aVoid -> {
                        String msg = newFav ? "Added to favorites" : "Removed from favorites";
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // If save fails, revert the change
                        recipe.setFavorite(!newFav);
                        holder.favBtn.setImageResource(recipe.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        Toast.makeText(context, "Failed to update favorite", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // ... (getItemCount, setData, filter, ViewHolder class remain the same) ...

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setData(List<Recipe> data) {
        recipeList.clear();
        recipeList.addAll(data);

        fullList.clear();
        fullList.addAll(data);

        notifyDataSetChanged();
    }

    public void filter(String text) {
        recipeList.clear();
        if (text == null || text.isEmpty()) {
            recipeList.addAll(fullList);
        } else {
            String lower = text.toLowerCase();
            for (Recipe item : fullList) {
                String nm = item.getName();
                if (nm != null && nm.toLowerCase().contains(lower)) {
                    recipeList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName;
        ImageButton favBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            favBtn = itemView.findViewById(R.id.favBtn);
        }
    }
}