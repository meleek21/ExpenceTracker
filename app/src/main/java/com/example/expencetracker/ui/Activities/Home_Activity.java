package com.example.expencetracker.ui.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.expencetracker.R;
import com.example.expencetracker.ui.Fragments.BudgetFragment;
import com.example.expencetracker.ui.Fragments.CategoryFragment;
import com.example.expencetracker.ui.Fragments.HomeFragment;
import com.example.expencetracker.ui.Fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home_Activity extends AppCompatActivity {
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user ID from intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            // Handle error - redirect to login
            finish();
            return;
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // Load the default fragment with user ID
        loadFragment(new HomeFragment(), currentUserId);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.navigation_expenses) {
                fragment = new CategoryFragment();
            } else if (itemId == R.id.navigation_budget) {
                fragment = new BudgetFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment, currentUserId);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment, int userId) {
        // Pass user ID to fragment using Bundle
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public int getCurrentUserId() {
        return currentUserId;
    }
}