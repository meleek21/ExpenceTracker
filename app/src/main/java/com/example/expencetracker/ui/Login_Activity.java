package com.example.expencetracker.ui;
import android.annotation.SuppressLint;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.expencetracker.ui.Home_Activity;
import com.example.expencetracker.MainActivity;
import com.example.expencetracker.R;
import com.example.expencetracker.data.Repositories.UserRepository;
import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;

public class Login_Activity extends AppCompatActivity {
    private UserRepository repository;
    private EditText username, password;
    private ExecutorService executorService;

    @SuppressLint("MissingInflatedId")
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

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if (usernameText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(Login_Activity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;

                }

                // Perform authentication in background thread
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean isAuthenticated = repository.authenticateUser(usernameText, passwordText);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isAuthenticated) {
                                    // User authenticated, navigate to MainActivity
                                    Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish(); // Close login activity
                                } else {
                                    Toast.makeText(Login_Activity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                    password.setText(""); // Clear the password field
                                }
                            }
                        });
                    }
                });
            }
        });

        // Register text click listener
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this, Signup_Activity.class);
                startActivity(intent);
            }
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