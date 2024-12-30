package com.example.expencetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.expencetracker.ui.Login_Activity; // Assuming Login_Activity exists
import com.example.expencetracker.ui.Signup_Activity; // Assuming Signup_Activity exists

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the Login and Sign Up buttons
        Button loginButton = findViewById(R.id.login_button); // Assuming you renamed the button to login_button
        Button signUpButton = findViewById(R.id.signup_button); // Assuming you renamed the button to signup_button

        // Set click listener for Login Button
        loginButton.setOnClickListener(view -> {
            // Start LoginActivity with a sliding effect
            Intent intent = new Intent(MainActivity.this, Login_Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Set slide effect for Login
        });

        // Set click listener for Sign Up Button
        signUpButton.setOnClickListener(view -> {
            // Start SignupActivity with a sliding effect
            Intent intent = new Intent(MainActivity.this, Signup_Activity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Set slide effect for Sign Up
        });
    }
}
