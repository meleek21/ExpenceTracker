package com.example.expencetracker.ui.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expencetracker.R;
import com.example.expencetracker.data.Entities.Category;
import com.example.expencetracker.data.Repositories.CategoryRepository;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;
import com.example.expencetracker.ui.Adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private CategoryRepository categoryRepository;
    private RecyclerView recyclerViewExpense, recyclerViewIncome;
    private CategoryAdapter expenseAdapter, incomeAdapter;
    private Button buttonAddCategory;
    private TextView textExpenseCategories, textIncomeCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Initialize repositories
        ExpenseTrackerDataBase database = ExpenseTrackerDataBase.getDatabase(requireContext());
        categoryRepository = new CategoryRepository(database.categoryDAO());

        // Initialize views
        recyclerViewExpense = view.findViewById(R.id.recycler_expense_categories);
        recyclerViewIncome = view.findViewById(R.id.recycler_income_categories);
        buttonAddCategory = view.findViewById(R.id.button_add_category);
        textExpenseCategories = view.findViewById(R.id.text_expense_categories);
        textIncomeCategories = view.findViewById(R.id.text_income_categories);

        // Setup RecyclerViews
        setupRecyclerViews();

        // Load categories
        loadCategories();

        // Set click listeners
        buttonAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        return view;
    }

    private void setupRecyclerViews() {
        // Expense Categories RecyclerView
        expenseAdapter = new CategoryAdapter();
        expenseAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onEditClick(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                showDeleteConfirmationDialog(category);
            }
        });
        recyclerViewExpense.setAdapter(expenseAdapter);
        recyclerViewExpense.setLayoutManager(new LinearLayoutManager(getContext()));

        // Income Categories RecyclerView
        incomeAdapter = new CategoryAdapter();
        incomeAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onEditClick(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                showDeleteConfirmationDialog(category);
            }
        });
        recyclerViewIncome.setAdapter(incomeAdapter);
        recyclerViewIncome.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadCategories() {
        new Thread(() -> {
            try {
                List<Category> allCategories = categoryRepository.getAllCategories();
                List<Category> expenseCategories = new ArrayList<>();
                List<Category> incomeCategories = new ArrayList<>();

                // Separate categories by type
                for (Category category : allCategories) {
                    if ("expense".equalsIgnoreCase(category.getType())) {
                        expenseCategories.add(category);
                    } else if ("income".equalsIgnoreCase(category.getType())) {
                        incomeCategories.add(category);
                    }
                }

                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    expenseAdapter.setCategories(expenseCategories);
                    incomeAdapter.setCategories(incomeCategories);
                    textExpenseCategories.setText("Expense Categories (" + expenseCategories.size() + ")");
                    textIncomeCategories.setText("Income Categories (" + incomeCategories.size() + ")");
                });
            } catch (Exception e) {
                Log.e("CategoryFragment", "Error loading categories: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        EditText editCategoryName = dialogView.findViewById(R.id.edit_category_name);
        RadioGroup radioGroupCategoryType = dialogView.findViewById(R.id.radio_group_category_type);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonSave = dialogView.findViewById(R.id.button_save);

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            String categoryName = editCategoryName.getText().toString().trim();
            if (categoryName.isEmpty()) {
                editCategoryName.setError("Please enter a category name");
                return;
            }

            // Get the selected category type
            int selectedRadioId = radioGroupCategoryType.getCheckedRadioButtonId();
            String categoryType = (selectedRadioId == R.id.radio_expense) ? "expense" : "income";

            // Create a new category
            Category category = new Category(categoryName, categoryType);

            // Save the category to the database
            new Thread(() -> {
                try {
                    categoryRepository.insertCategory(category);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
                        loadCategories(); // Refresh the list
                        dialog.dismiss();
                    });
                } catch (Exception e) {
                    Log.e("CategoryFragment", "Error adding category: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error adding category", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showEditCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        EditText editCategoryName = dialogView.findViewById(R.id.edit_category_name);
        RadioGroup radioGroupCategoryType = dialogView.findViewById(R.id.radio_group_category_type);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonSave = dialogView.findViewById(R.id.button_save);

        // Pre-fill the category name and type
        editCategoryName.setText(category.getName());

        // Set the selected radio button based on the category type
        if ("expense".equalsIgnoreCase(category.getType())) {
            radioGroupCategoryType.check(R.id.radio_expense);
        } else {
            radioGroupCategoryType.check(R.id.radio_income);
        }

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            String categoryName = editCategoryName.getText().toString().trim();
            if (categoryName.isEmpty()) {
                editCategoryName.setError("Please enter a category name");
                return;
            }

            // Get the selected category type
            int selectedRadioId = radioGroupCategoryType.getCheckedRadioButtonId();
            String categoryType = (selectedRadioId == R.id.radio_expense) ? "expense" : "income";

            // Update the category
            category.setName(categoryName);
            category.setType(categoryType);

            // Save the updated category to the database
            new Thread(() -> {
                try {
                    categoryRepository.updateCategory(category);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Category updated successfully", Toast.LENGTH_SHORT).show();
                        loadCategories(); // Refresh the list
                        dialog.dismiss();
                    });
                } catch (Exception e) {
                    Log.e("CategoryFragment", "Error updating category: " + e.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error updating category", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteConfirmationDialog(Category category) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            categoryRepository.deleteCategory(category);
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show();
                                loadCategories(); // Refresh the list
                            });
                        } catch (Exception e) {
                            Log.e("CategoryFragment", "Error deleting category: " + e.getMessage());
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error deleting category", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}