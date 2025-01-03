package com.example.expencetracker.ui.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.expencetracker.R;
import com.example.expencetracker.data.Entities.User;
import com.example.expencetracker.data.Repositories.UserRepository;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;
import com.example.expencetracker.ui.Activities.Login_Activity;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView userNameTextView, userEmailTextView;
    private MaterialButton logoutButton, editButton;
    private UserRepository userRepository;
    private int currentUserId;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("USER_ID", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize repositories
        ExpenseTrackerDataBase database = ExpenseTrackerDataBase.getDatabase(requireContext());
        userRepository = new UserRepository(database.userDAO());

        // Initialize views
        initializeViews(view);

        // Fetch the logged-in user's data
        fetchLoggedInUserData();

        // Set click listeners
        logoutButton.setOnClickListener(v -> logoutUser());
        editButton.setOnClickListener(v -> showEditProfileDialog());

        return view;
    }

    private void initializeViews(View view) {
        userNameTextView = view.findViewById(R.id.user_name);
        userEmailTextView = view.findViewById(R.id.user_email);
        logoutButton = view.findViewById(R.id.logout_button);
        editButton = view.findViewById(R.id.edit_button);
    }

    private void fetchLoggedInUserData() {
        new Thread(() -> {
            try {
                currentUser = userRepository.getUserById(currentUserId);
                if (currentUser != null) {
                    requireActivity().runOnUiThread(() -> {
                        userNameTextView.setText(currentUser.getUsername());
                        userEmailTextView.setText(currentUser.getEmail());
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e("ProfileFragment", "Error fetching user data: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        // Initialize dialog views
        EditText editUsername = dialogView.findViewById(R.id.edit_username);
        EditText editEmail = dialogView.findViewById(R.id.edit_email);
        EditText editPassword = dialogView.findViewById(R.id.edit_password);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonSave = dialogView.findViewById(R.id.button_save);

        // Pre-fill the fields with current user data
        if (currentUser != null) {
            editUsername.setText(currentUser.getUsername());
            editEmail.setText(currentUser.getEmail());
        }

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Username and email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the user object
            currentUser.setUsername(newUsername);
            currentUser.setEmail(newEmail);
            if (!newPassword.isEmpty()) {
                currentUser.setPassword(newPassword);
            }

            // Save the updated user to the database
            new Thread(() -> {
                try {
                    userRepository.updateUser(currentUser);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        fetchLoggedInUserData(); // Refresh the displayed data
                        dialog.dismiss();
                    });
                } catch (Exception e) {
                    Log.e("ProfileFragment", "Error updating user: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        dialog.show();
    }

    private void logoutUser() {
        // Navigate to the LoginActivity
        navigateToLoginActivity();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(requireContext(), Login_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}