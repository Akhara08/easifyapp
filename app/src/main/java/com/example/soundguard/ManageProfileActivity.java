package com.example.soundguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManageProfileActivity extends AppCompatActivity {
    private TextView emailTextView;
    private EditText nameEditText;
    private EditText phoneEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        emailTextView = findViewById(R.id.emailTextView);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        // Set the email field as non-editable
        emailTextView.setText("aksharar79@gmail.com");

        // Handle save button click
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input values
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                // Check if the fields are valid
                if (name.isEmpty()) {
                    nameEditText.setError("Name is required");
                } else if (phone.isEmpty()) {
                    phoneEditText.setError("Phone number is required");
                } else {
                    // Save the data (this is just a placeholder, replace with actual saving logic)
                    // You can save the data to your database or update the profile in your backend
                    Toast.makeText(ManageProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // Move to ServiceListingActivity
                    Intent intent = new Intent(ManageProfileActivity.this, ServiceListingActivity.class);
                    startActivity(intent);
                    finish(); // Optionally, finish the current activity so the user can't go back to it
                }
            }
        });
    }
}
