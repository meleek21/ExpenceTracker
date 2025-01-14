package com.example.expencetracker.data.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expencetracker.data.Entities.Budget;

import java.util.List;
@Dao
public interface BudgetDAO {
    @Query("SELECT * FROM budgets")
    List<Budget> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE user_id = :userId")
    List<Budget> getBudgetsByUserId(int userId);

    @Query("SELECT * FROM budgets WHERE id = :id")
    Budget getBudgetById(int id);

    @Insert
    void insertBudget(Budget budget);

    @Update
    void updateBudget(Budget budget);

    @Delete
    void deleteBudget(Budget budget);
}
