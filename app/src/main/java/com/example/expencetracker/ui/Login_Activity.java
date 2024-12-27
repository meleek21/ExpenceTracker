package com.example.expencetracker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expencetracker.MainActivity;
import com.example.expencetracker.R;
import com.example.expencetracker.data.Repositories.UserRepository;
import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.Entities.User;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;

class LoginActivity extends AppCompatActivity {

    private UserRepository repository;
    private EditText username, password;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UserRepository
        ExpenseTrackerDataBase db = ExpenseTrackerDataBase.getDatabase(this);
        UserDAO userDao = db.userDAO();
        repository = new UserRepository(userDao);

        // UI elements
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if (repository.authenticateUser(usernameText, passwordText)) {
                    // User authenticated, navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}