package com.example.expencetracker.ui.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.expencetracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BudgetFragment extends Fragment {

    private BarChart barChart;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialize charts
        barChart = view.findViewById(R.id.bar_chart);
        pieChart = view.findViewById(R.id.pie_chart);

        // Set up and load data for both charts
        setupBarChart();
        loadBarChartData();

        setupPieChart();
        loadPieChartData();

        return view;
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.getLegend().setEnabled(true);

        // Add animation
        barChart.animateY(1000); // Animate the bar chart vertically over 1 second
    }

    private void loadBarChartData() {
        // Example data: Budget vs. Spending for 3 categories
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, new float[]{500f, 400f})); // Food: Budget = 500, Spending = 400
        entries.add(new BarEntry(1f, new float[]{300f, 350f})); // Transport: Budget = 300, Spending = 350
        entries.add(new BarEntry(2f, new float[]{200f, 150f})); // Entertainment: Budget = 200, Spending = 150

        BarDataSet dataSet = new BarDataSet(entries, "Budget vs. Spending");

        // Use your app's colors
        int budgetColor = ContextCompat.getColor(requireContext(), R.color.blue_primary); // Budget color
        int spendingColor = ContextCompat.getColor(requireContext(), R.color.blue_secondary); // Spending color
        dataSet.setColors(new int[]{budgetColor, spendingColor});

        dataSet.setStackLabels(new String[]{"Budget", "Spending"});

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.4f);
        barChart.setData(data);
        barChart.invalidate(); // Refresh chart
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(true);

        // Add animation
        pieChart.animateY(1000); // Animate the pie chart over 1 second
    }

    private void loadPieChartData() {
        // Example data: Spending distribution
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Food"));
        entries.add(new PieEntry(30f, "Transport"));
        entries.add(new PieEntry(20f, "Entertainment"));
        entries.add(new PieEntry(10f, "Utilities"));

        PieDataSet dataSet = new PieDataSet(entries, "Spending Distribution");

        // Use your app's colors
        int[] colors = new int[]{
                ContextCompat.getColor(requireContext(), R.color.blue_primary),
                ContextCompat.getColor(requireContext(), R.color.blue_secondary),
                ContextCompat.getColor(requireContext(), R.color.accent),
                ContextCompat.getColor(requireContext(), R.color.hint_color)
        };
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh chart
    }
}