package com.example.soundguard;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Date;
import java.text.ParseException;
import java.util.Map;
import android.util.Patterns;

public class SignupTabFragment extends Fragment {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, dobEditText;
    private Button signupButton;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_tab, container, false);

        emailEditText = view.findViewById(R.id.signup_email);
        passwordEditText = view.findViewById(R.id.signup_password);
        confirmPasswordEditText = view.findViewById(R.id.signup_confirm);
        dobEditText = view.findViewById(R.id.dob_edit); // Modified: Referencing DOB EditText
        signupButton = view.findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener for DOB EditText to show DatePickerDialog
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void showDatePickerDialog() {
        // Get current date for DatePickerDialog initialization
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Initialize DatePickerDialog and set the listener to handle date selection
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Display the selected date in the DOB EditText
                dobEditText.setText((month + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        // Validate email format
        if (email.isEmpty() || !isValidEmail(email)) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password strength
        if (password.isEmpty() || !isValidPassword(password)) {
            Toast.makeText(getContext(), "Password must be at least 6 characters long and contain a number and a letter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if DOB is empty
        if (dob.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with Firebase signup
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Handle success
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(userId);

                            // Calculate age from DOB and save data to Firestore
                            saveUserInfo(email, dob, userRef);
                        } else {
                            // Handle failure
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to validate email format using regex
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to validate password strength
    private boolean isValidPassword(String password) {
        // Check if password has at least 6 characters, one digit, and one letter
        return password.length() >= 6 && password.matches(".*[A-Za-z].*") && password.matches(".*[0-9].*");
    }

    private void saveUserInfo(String email, String dob, DocumentReference userRef) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date birthDate;
        int age = 0;
        try {
            birthDate = sdf.parse(dob);
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(birthDate);
            Calendar today = Calendar.getInstance();
            age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create user info map and save to Firestore
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("dob", dob);
        userInfo.put("age", age);

        // Save data to Firestore
        userRef.set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
                        // Navigate to the next screen or perform other actions
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add user information to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
