package com.example.mad_project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText nameInput, descInput, imgUrlInput;
    private ImageView imgPreview;
    private Button btnPreview, btnUpload;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        nameInput = findViewById(R.id.nameInput);
        descInput = findViewById(R.id.descInput);
        imgUrlInput = findViewById(R.id.imgUrlInput);
        imgPreview = findViewById(R.id.imgPreview);
        btnPreview = findViewById(R.id.btnPreviewImage);
        btnUpload = findViewById(R.id.btnUpload);

        dbRef = FirebaseDatabase.getInstance(
                "https://mad-project-72034-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("recipes");

        btnPreview.setOnClickListener(v -> previewImage());
        btnUpload.setOnClickListener(v -> uploadRecipe());
    }

    private void previewImage() {
        String url = imgUrlInput.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "Enter image URL first!", Toast.LENGTH_SHORT).show();
        } else {
            Glide.with(this).load(url).into(imgPreview);
        }
    }

    private void uploadRecipe() {
        String name = nameInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String imgUrl = imgUrlInput.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Name and description are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Saving...");
        pd.show();

        Recipe recipe = new Recipe(name, imgUrl, desc, false);

        dbRef.child(recipe.getName()).setValue(recipe)
                .addOnSuccessListener(aVoid -> {
                    pd.dismiss();
                    Toast.makeText(this, "Recipe added!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
