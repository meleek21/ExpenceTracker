package com.example.expencetracker.ui.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expencetracker.R;
import com.example.expencetracker.data.Entities.Expense;
import com.example.expencetracker.data.Entities.Category;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;
import com.example.expencetracker.data.Repositories.ExpenseRepository;
import com.example.expencetracker.data.Repositories.CategoryRepository;
import com.example.expencetracker.data.Repositories.BudgetRepository;
import com.example.expencetracker.ui.Adapters.ExpenseAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private ExpenseRepository expenseRepository;
    private CategoryRepository categoryRepository;
    private BudgetRepository budgetRepository;

    private TextView textTotalBalance;
    private TextView textMonthlyExpenses;
    private TextView textMonthlyBudget;
    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private Button buttonAddExpense;
    private Button buttonAddIncome;

    private static final int DEFAULT_USER_ID = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize repositories
        ExpenseTrackerDataBase database = ExpenseTrackerDataBase.getDatabase(requireContext());
        expenseRepository = new ExpenseRepository(database.expenseDAO());
        categoryRepository = new CategoryRepository(database.categoryDAO());
        budgetRepository = new BudgetRepository(database.budgetDAO());

        // Setup initial data
        setupInitialData();

        // Initialize views
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();

        // Load data
        loadData();

        return view;
    }

    private void setupInitialData() {
        categoryRepository.setupInitialData();
    }

    private void initializeViews(View view) {
        textTotalBalance = view.findViewById(R.id.text_total_balance);
        textMonthlyExpenses = view.findViewById(R.id.text_monthly_expenses);
        textMonthlyBudget = view.findViewById(R.id.text_monthly_budget);
        recyclerView = view.findViewById(R.id.recycler_recent_transactions);
        buttonAddExpense = view.findViewById(R.id.button_add_expense);
        buttonAddIncome = view.findViewById(R.id.button_add_income);
    }

    private void setupRecyclerView() {
        expenseAdapter = new ExpenseAdapter();
        expenseAdapter.setOnExpenseClickListener(new ExpenseAdapter.OnExpenseClickListener() {
            @Override
            public void onEditClick(Expense expense) {
                editExpense(expense);
            }

            @Override
            public void onDeleteClick(Expense expense) {
                showDeleteConfirmationDialog(expense);
            }
        });
        recyclerView.setAdapter(expenseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupClickListeners() {
        buttonAddExpense.setOnClickListener(v -> showAddExpenseDialog());
        buttonAddIncome.setOnClickListener(v -> showAddIncomeDialog());
    }

    private void editExpense(Expense expense) {
        new Thread(() -> {
            try {
                List<Category> categories = categoryRepository.getAllCategories();
                categories.removeIf(category ->
                        expense.getAmount() < 0 ?
                                !"expense".equalsIgnoreCase(category.getType()) :
                                !"income".equalsIgnoreCase(category.getType())
                );

                if (categories.isEmpty()) {
                    Category defaultCategory = new Category(
                            expense.getAmount() < 0 ? "General Expense" : "General Income",
                            expense.getAmount() < 0 ? "expense" : "income"
                    );
                    categoryRepository.insertCategory(defaultCategory);
                    categories.add(defaultCategory);
                }

                List<Category> finalCategories = categories;

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        showTransactionDialog(finalCategories, expense.getAmount() < 0, expense);
                    });
                }
            } catch (Exception e) {
                Log.e("HomeFragment", "Error loading categories: " + e.getMessage());
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void showDeleteConfirmationDialog(Expense expense) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            expenseRepository.deleteExpense(expense);
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    loadData();
                                    Toast.makeText(getContext(), "Transaction deleted", Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (Exception e) {
                            Log.e("HomeFragment", "Error deleting expense: " + e.getMessage());
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Error deleting transaction", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddExpenseDialog() {
        if (!isAdded()) return;

        new Thread(() -> {
            try {
                List<Category> expenseCategories = categoryRepository.getAllCategories();
                expenseCategories.removeIf(category -> !"expense".equalsIgnoreCase(category.getType()));

                if (expenseCategories.isEmpty()) {
                    Category defaultCategory = new Category("General Expense", "expense");
                    categoryRepository.insertCategory(defaultCategory);
                    expenseCategories.add(defaultCategory);
                }

                List<Category> finalCategories = expenseCategories;

                requireActivity().runOnUiThread(() -> {
                    showTransactionDialog(finalCategories, true, null);
                });
            } catch (Exception e) {
                Log.e("HomeFragment", "Error loading categories: " + e.getMessage());
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void showAddIncomeDialog() {
        if (!isAdded()) return;

        new Thread(() -> {
            try {
                List<Category> incomeCategories = categoryRepository.getAllCategories();
                incomeCategories.removeIf(category -> !"income".equalsIgnoreCase(category.getType()));

                if (incomeCategories.isEmpty()) {
                    Category defaultCategory = new Category("General Income", "income");
                    categoryRepository.insertCategory(defaultCategory);
                    incomeCategories.add(defaultCategory);
                }

                List<Category> finalCategories = incomeCategories;

                requireActivity().runOnUiThread(() -> {
                    showTransactionDialog(finalCategories, false, null);
                });
            } catch (Exception e) {
                Log.e("HomeFragment", "Error loading categories: " + e.getMessage());
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void showTransactionDialog(List<Category> categories, boolean isExpense, Expense existingExpense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_expense, null);
        builder.setView(dialogView);

        EditText editAmount = dialogView.findViewById(R.id.edit_amount);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_category);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories.stream().map(Category::getName).toArray(String[]::new)
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // If editing, pre-fill the fields
        if (existingExpense != null) {
            editAmount.setText(String.valueOf(Math.abs(existingExpense.getAmount())));
            editDescription.setText(existingExpense.getDescription());
            // Set spinner selection based on existing category
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == existingExpense.getCategoryId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }

        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonSave = dialogView.findViewById(R.id.button_save);

        final AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            try {
                String amountStr = editAmount.getText().toString();
                String description = editDescription.getText().toString();

                if (amountStr.isEmpty()) {
                    editAmount.setError("Please enter an amount");
                    return;
                }

                double amount = Double.parseDouble(amountStr);
                if (isExpense) {
                    amount = -Math.abs(amount);
                } else {
                    amount = Math.abs(amount);
                }

                Category selectedCategory = categories.get(spinnerCategory.getSelectedItemPosition());

                final Expense expense;
                if (existingExpense != null) {
                    expense = new Expense(
                            existingExpense.getId(),
                            existingExpense.getDate(),
                            amount,
                            selectedCategory.getId(),
                            description,
                            DEFAULT_USER_ID
                    );
                } else {
                    expense = new Expense(
                            0,
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()),
                            amount,
                            selectedCategory.getId(),
                            description,
                            DEFAULT_USER_ID
                    );
                }

                new Thread(() -> {
                    try {
                        if (existingExpense != null) {
                            expenseRepository.updateExpense(expense);
                        } else {
                            expenseRepository.insertExpense(expense);
                        }

                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                dialog.dismiss();
                                loadData();
                                Toast.makeText(getContext(),
                                        existingExpense != null ? "Transaction updated" :
                                                (isExpense ? "Expense added successfully" : "Income added successfully"),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error saving transaction: " + e.getMessage());
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error saving transaction: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }).start();
            } catch (Exception e) {
                Log.e("HomeFragment", "Error processing input: " + e.getMessage());
                Toast.makeText(getContext(), "Error processing input: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<Expense> allExpenses = expenseRepository.getAllExpenses();

                // Get current month and year
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat yearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                String currentYearMonth = yearMonth.format(calendar.getTime());

                // Calculate totals
                double totalBalance = 0;
                double monthlyExpenses = 0;
                double monthlyIncome = 0;

                for (Expense expense : allExpenses) {
                    totalBalance += expense.getAmount();

                    // Check if expense is from the current month
                    if (expense.getDate().startsWith(currentYearMonth)) {
                        if (expense.getAmount() < 0) {
                            monthlyExpenses += Math.abs(expense.getAmount());
                        } else {
                            monthlyIncome += expense.getAmount();
                        }
                    }
                }

                // Set monthly budget to total income for the month
                double monthlyBudget = monthlyIncome;

                // Update UI on main thread
                double finalTotalBalance = totalBalance;
                double finalMonthlyExpenses = monthlyExpenses;
                double finalMonthlyBudget = monthlyBudget;

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        expenseAdapter.setExpenses(allExpenses);

                        // Format currency values
                        String currencyFormat = "$%.2f";
                        textTotalBalance.setText(String.format(currencyFormat, finalTotalBalance));
                        textMonthlyExpenses.setText(String.format(currencyFormat, finalMonthlyExpenses));
                        textMonthlyBudget.setText(String.format(currencyFormat, finalMonthlyBudget));
                    });
                }
            } catch (Exception e) {
                Log.e("HomeFragment", "Error loading data: " + e.getMessage());
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}