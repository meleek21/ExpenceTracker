package com.example.expencetracker.ui.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expencetracker.R;
import com.example.expencetracker.data.Entities.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenses = new ArrayList<>();

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.textDescription.setText(expense.getDescription());
        holder.textAmount.setText(String.format("$%.2f", expense.getAmount()));
        holder.textDate.setText(expense.getDate());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textDescription;
        TextView textAmount;
        TextView textDate;
        TextView textCategory;

        ExpenseViewHolder(View view) {
            super(view);
            textDescription = view.findViewById(R.id.text_description);
            textAmount = view.findViewById(R.id.text_amount);
            textDate = view.findViewById(R.id.text_date);
            textCategory = view.findViewById(R.id.text_category);
        }
    }
}