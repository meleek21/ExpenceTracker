package com.example.expencetracker.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expencetracker.MainActivity;
import com.example.expencetracker.R;
import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.Repositories.UserRepository;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;
import com.example.expencetracker.ui.Activities.Home_Activity;
import com.example.expencetracker.ui.Activities.Signup_Activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login_Activity extends AppCompatActivity {
    private UserRepository repository;
    private EditText username, password;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Initialize UserRepository
        ExpenseTrackerDataBase db = ExpenseTrackerDataBase.getDatabase(this);
        UserDAO userDao = db.userDAO();
        repository = new UserRepository(userDao);

        // UI elements
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerText = findViewById(R.id.register_text);
        View backButton = findViewById(R.id.back_button);  // Add back button view reference

        // Back button click listener
        backButton.setOnClickListener(view -> {
            // Go back to the MainActivity when the back button is pressed
            Intent intent = new Intent(Login_Activity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);  // Add sliding effect
            finish();  // Finish Login Activity
        });

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            String usernameText = username.getText().toString();
            String passwordText = password.getText().toString();

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(Login_Activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform authentication in background thread
            // In Login_Activity.java, modify the login button click listener
            executorService.execute(() -> {
                // Get the user ID along with authentication
                int userId = repository.getUserIdByUsername(usernameText); // You'll need to add this method to UserRepository
                runOnUiThread(() -> {
                    if (userId != -1) { // Assuming -1 means user not found
                        Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
                        intent.putExtra("USER_ID", userId); // Pass the user ID
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login_Activity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        password.setText("");
                    }
                });
            });
        });

        // Register text click listener
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(Login_Activity.this, Signup_Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // For sliding effect
        });

        // Edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
