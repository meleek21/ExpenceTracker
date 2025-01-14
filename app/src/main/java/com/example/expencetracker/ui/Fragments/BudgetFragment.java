package com.example.expencetracker.ui.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.expencetracker.R;
import com.example.expencetracker.data.Repositories.ExpenseRepository;
import com.example.expencetracker.data.Repositories.BudgetRepository;
import com.example.expencetracker.data.Repositories.CategoryRepository;
import com.example.expencetracker.data.RoomDataBase.ExpenseTrackerDataBase;
import com.example.expencetracker.data.Entities.Expense;
import com.example.expencetracker.data.Entities.Budget;
import com.example.expencetracker.data.Entities.Category;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private ExpenseRepository expenseRepository;
    private BudgetRepository budgetRepository;
    private CategoryRepository categoryRepository;
    private int currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("USER_ID", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialize repositories
        ExpenseTrackerDataBase database = ExpenseTrackerDataBase.getDatabase(requireContext());
        expenseRepository = new ExpenseRepository(database.expenseDAO());
        budgetRepository = new BudgetRepository(database.budgetDAO());
        categoryRepository = new CategoryRepository(database.categoryDAO());

        // Initialize views
        initializeViews(view);
        setupCharts();

        // Load data
        fetchChartData();

        return view;
    }

    private void initializeViews(View view) {
        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);
    }

    private void setupCharts() {
        setupPieChart();
        setupBarCharts();
    }

    private void setupBarCharts() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.getLegend().setEnabled(true);
        barChart.animateY(1000);
    }

    private void fetchChartData() {
        new Thread(() -> {
            try {
                // Get all categories first
                Map<Integer, String> categoryMap = new HashMap<>();
                List<Category> categories = categoryRepository.getAllCategories();
                for (Category category : categories) {
                    categoryMap.put(category.getId(), category.getName());
                }

                // Get current user's expenses
                List<Expense> expenses = expenseRepository.getExpensesByUserId(currentUserId);

                // Calculate total incomes and expenses using repository methods
                double totalIncome = expenseRepository.getTotalIncome(currentUserId);
                double totalExpenses = expenseRepository.getTotalExpenses(currentUserId);

                // Process data with category names for PieChart
                Map<String, Double> spendingDistribution = calculateSpendingDistribution(expenses, categoryMap);

                // Update UI on main thread
                requireActivity().runOnUiThread(() -> {
                    loadPieChartData(spendingDistribution); // PieChart with percentages
                    loadBarChartData(totalIncome, totalExpenses); // BarChart with absolute values
                });
            } catch (Exception e) {
                Log.e("BudgetFragment", "Error fetching chart data: " + e.getMessage());
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error loading chart data", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private Map<String, Double> calculateSpendingDistribution(
            List<Expense> expenses,
            Map<Integer, String> categoryMap) {

        Map<String, Double> distribution = new HashMap<>();
        double total = 0.0;

        // Calculate totals per category (excluding Income)
        for (Expense expense : expenses) {
            String categoryName = categoryMap.get(expense.getCategoryId());
            if (categoryName != null && !categoryName.equalsIgnoreCase("Income")) {
                double amount = Math.abs(expense.getAmount());
                distribution.merge(categoryName, amount, Double::sum);
                total += amount;
            }
        }

        // Convert to percentages
        if (total > 0) {
            for (Map.Entry<String, Double> entry : distribution.entrySet()) {
                double percentage = (entry.getValue() / total) * 100;
                distribution.put(entry.getKey(), percentage);
            }
        }

        Log.d("BudgetFragment", "Calculated Distribution: " + distribution.toString() + " Total: " + total);
        return distribution;
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawEntryLabels(false); // Disable entry labels on slices
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(14f);
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);

        pieChart.setCenterText("Spending\nDistribution");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextColor(Color.BLACK);

        // Disable the default legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        pieChart.animateY(1000); // Animate the chart
    }

    private void loadPieChartData(Map<String, Double> spendingDistribution) {
        Log.d("BudgetFragment", "Spending Distribution Data: " + spendingDistribution.toString());
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        int[] colorList = new int[]{
                Color.parseColor("#FF6F61"), // Coral
                Color.parseColor("#6B5B95"), // Purple
                Color.parseColor("#88B04B"), // Green
                Color.parseColor("#F7CAC9"), // Pink
                Color.parseColor("#92A8D1"), // Blue
                Color.parseColor("#955251"), // Maroon
                Color.parseColor("#B565A7"), // Magenta
                Color.parseColor("#009B77"), // Teal
                Color.parseColor("#DD4124"), // Red
                Color.parseColor("#D65076"), // Rose
                Color.parseColor("#45B8AC"), // Aqua
                Color.parseColor("#EFC050"), // Gold
                Color.parseColor("#5B5EA6"), // Indigo
                Color.parseColor("#9B2335"), // Crimson
                Color.parseColor("#DFCFBE")  // Beige
        };

        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : spendingDistribution.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), String.format("%.1f%%", entry.getValue())));
            colors.add(colorList[colorIndex % colorList.length]);
            colorIndex++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Disable the default legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        // Load custom legend
        loadCustomLegend(spendingDistribution, colors);

        pieChart.invalidate(); // Refresh the chart
    }

    private void loadCustomLegend(Map<String, Double> spendingDistribution, List<Integer> colors) {
        LinearLayout customLegendLayout = requireView().findViewById(R.id.custom_legend);
        customLegendLayout.removeAllViews(); // Clear existing views

        List<String> categoryNames = new ArrayList<>(spendingDistribution.keySet());

        // Create rows for the legend
        LinearLayout rowLayout = null;
        for (int i = 0; i < categoryNames.size(); i++) {
            if (i % 3 == 0) {
                // Create a new row for every 3 entries
                rowLayout = new LinearLayout(requireContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                customLegendLayout.addView(rowLayout);
            }

            // Create a legend item (color + label)
            View legendItem = createLegendItem(categoryNames.get(i), colors.get(i));
            if (rowLayout != null) {
                rowLayout.addView(legendItem);
            }
        }
    }

    private View createLegendItem(String categoryName, int color) {
        // Inflate a custom layout for the legend item
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View legendItem = inflater.inflate(R.layout.legend_item, null);

        // Set the color and label
        View colorView = legendItem.findViewById(R.id.legend_color);
        TextView labelView = legendItem.findViewById(R.id.legend_label);

        colorView.setBackgroundColor(color);
        labelView.setText(categoryName);

        return legendItem;
    }

    private void loadBarChartData(double totalIncome, double totalExpenses) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) totalIncome)); // Income
        entries.add(new BarEntry(1, (float) totalExpenses*-1)); // Expenses

        BarDataSet dataSet = new BarDataSet(entries, "Income vs. Expenses");
        dataSet.setColors(new int[]{
                ContextCompat.getColor(requireContext(), R.color.blue_primary), // Income color
                ContextCompat.getColor(requireContext(), R.color.blue_secondary) // Expenses color
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);

        // Customize X-axis labels
        String[] labels = {"Income", "Expenses"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);

        barChart.invalidate(); // Refresh the chart
    }
}