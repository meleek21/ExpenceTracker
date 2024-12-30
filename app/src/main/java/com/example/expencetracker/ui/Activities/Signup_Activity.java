package com.example.expencetracker.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expencetracker.MainActivity;
import com.example.expencetracker.R;
import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.Repositories.UserRepository;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;
import com.example.expencetracker.ui.Activities.Login_Activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Signup_Activity extends AppCompatActivity {
    private UserRepository repository;
    private EditText email, username, password, confirmPassword;
    private Button signUpButton;
    private TextView loginLink;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize ExecutorService for background operations
        executorService = Executors.newSingleThreadExecutor();

        // Initialize UserRepository
        ExpenseTrackerDataBase db = ExpenseTrackerDataBase.getDatabase(this);
        UserDAO userDao = db.userDAO();
        repository = new UserRepository(userDao);

        // Initialize views
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginLink = findViewById(R.id.login_link);

        // Initialize the back button and set click listener to go to MainActivity
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When back button is clicked, navigate to MainActivity
                navigateToMain();
            }
        });

        // Set click listeners
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void handleSignUp() {
        String emailText = email.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();

        // Validation
        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(usernameText) ||
                TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(confirmPasswordText)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usernameText.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        signUpButton.setEnabled(false);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // Check if username exists
                    if (repository.findByUsername(usernameText) != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Signup_Activity.this,
                                        "Username already exists", Toast.LENGTH_SHORT).show();
                                signUpButton.setEnabled(true);
                            }
                        });
                        return;
                    }

                    // Register user
                    repository.registerUser(usernameText, emailText, passwordText);

                    // Success
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Signup_Activity.this,
                                    "Registration successful!", Toast.LENGTH_SHORT).show();
                            navigateToLogin();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Signup_Activity.this,
                                    "Registration failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            signUpButton.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void navigateToMain() {
        // Navigate back to MainActivity
        Intent intent = new Intent(Signup_Activity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();  // Close current activity
    }

    private void navigateToLogin() {
        Intent intent = new Intent(Signup_Activity.this, Login_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
