package com.example.expencetracker.data.Repositories;

import com.example.expencetracker.data.DAOs.ExpenseDAO;
import com.example.expencetracker.data.Entities.Expense;

import java.util.List;

public class ExpenseRepository {
    private final ExpenseDAO expenseDAO;

    public ExpenseRepository(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public List<Expense> getAllExpenses() {
        return expenseDAO.getAllExpenses();
    }

    public Expense getExpenseById(int id) {
        return expenseDAO.getExpenseById(id);
    }

    public void insertExpense(Expense expense) {
        expenseDAO.insertExpense(expense);
    }

    public void updateExpense(Expense expense) {
        expenseDAO.updateExpense(expense);
    }

    public void deleteExpense(Expense expense) {
        expenseDAO.deleteExpense(expense);
    }

    public List<Expense> getExpensesByUserId(int userId) {
        return expenseDAO.getExpensesByUserId(userId);
    }
}