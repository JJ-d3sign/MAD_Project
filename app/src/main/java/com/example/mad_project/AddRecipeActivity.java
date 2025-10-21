package com.example.mad_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity {

    EditText nameInput, descInput;
    ImageView imgPreview;
    Button btnSelect, btnUpload;
    Uri imgUri;

    DatabaseReference dbRef;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        nameInput = findViewById(R.id.nameInput);
        descInput = findViewById(R.id.descInput);
        imgPreview = findViewById(R.id.imgPreview);
        btnSelect = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUpload);

        dbRef = FirebaseDatabase.getInstance(
                        "https://mad-project-72034-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("recipes");

        storageRef = FirebaseStorage.getInstance().getReference("images");

        btnSelect.setOnClickListener(v -> selectImage());
        btnUpload.setOnClickListener(v -> uploadRecipe());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            imgPreview.setImageURI(imgUri);
        }
    }

    private void uploadRecipe() {
        String name = nameInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Name and description are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();

        if (imgUri != null) {
            // If user selected an image, upload it
            String imgName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imgRef = storageRef.child(imgName);

            imgRef.putFile(imgUri)
                    .addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Recipe recipe = new Recipe(name, uri.toString(), desc, false);
                        saveRecipeToDB(recipe, pd);
                    }))
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // No image selected, save recipe with empty image URL
            Recipe recipe = new Recipe(name, "", desc, false);
            saveRecipeToDB(recipe, pd);
        }
    }

    // Helper method to save recipe to Firebase DB
    private void saveRecipeToDB(Recipe recipe, ProgressDialog pd) {
        dbRef.child(recipe.name).setValue(recipe)
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
