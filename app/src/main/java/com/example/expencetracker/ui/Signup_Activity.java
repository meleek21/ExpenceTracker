package com.example.expencetracker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expencetracker.R;
import com.example.expencetracker.data.Repositories.UserRepository;
import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.Entities.User;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;

class SignupActivity extends AppCompatActivity {

    private UserRepository repository;
    private EditText email, username, password, confirmPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UserRepository
        ExpenseTrackerDataBase db = ExpenseTrackerDataBase.getDatabase(this);
        UserDAO userDao = db.userDAO();
        repository = new UserRepository(userDao);

        // UI elements
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        Button signUpButton = findViewById(R.id.sign_up_button);

        // Sign up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();

                if (passwordText.equals(confirmPasswordText)) {
                    repository.registerUser(emailText, usernameText, passwordText);
                    Toast.makeText(SignupActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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