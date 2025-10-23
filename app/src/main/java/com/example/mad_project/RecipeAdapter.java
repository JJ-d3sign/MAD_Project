package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipeList;
    private List<Recipe> fullList;

    public RecipeAdapter(Context context) {
        this.context = context;
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

        holder.recipeName.setText(recipe.name != null ? recipe.name : "No Name");

        if (recipe.imageUrl != null && !recipe.imageUrl.isEmpty()) {
            Glide.with(context).load(recipe.imageUrl).into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.favBtn.setImageResource(recipe.favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailActivity.class);
            intent.putExtra("name", recipe.name != null ? recipe.name : "No Name");
            intent.putExtra("image", recipe.imageUrl != null ? recipe.imageUrl : "");
            intent.putExtra("desc", recipe.description != null ? recipe.description : "No Description");
            context.startActivity(intent);
        });


        holder.favBtn.setOnClickListener(v -> {
            recipe.favorite = !recipe.favorite;
            holder.favBtn.setImageResource(recipe.favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        });
    }

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
        if (text.isEmpty()) {
            recipeList.addAll(fullList);
        } else {
            text = text.toLowerCase();
            for (Recipe item : fullList) {
                if (item.name != null && item.name.toLowerCase().contains(text)) {
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
