package com.example.expencetracker.data.Repositories;

import com.example.expencetracker.data.DAOs.BudgetDAO;
import com.example.expencetracker.data.Entities.Budget;

import java.util.List;

public class BudgetRepository {
    private BudgetDAO budgetDAO;

    public BudgetRepository(BudgetDAO budgetDAO) {
        this.budgetDAO = budgetDAO;
    }

    public List<Budget> getAllBudgets() {
        return budgetDAO.getAllBudgets();
    }

    public Budget getBudgetById(int id) {
        return budgetDAO.getBudgetById(id);
    }

    public void insertBudget(Budget budget) {
        budgetDAO.insertBudget(budget);
    }

    public void updateBudget(Budget budget) {
        budgetDAO.updateBudget(budget);
    }

    public void deleteBudget(Budget budget) {
        budgetDAO.deleteBudget(budget);
    }
}
