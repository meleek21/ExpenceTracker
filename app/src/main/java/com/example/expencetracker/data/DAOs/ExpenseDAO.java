package com.example.expencetracker.data.DAOs;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expencetracker.data.Entities.Expense;
import java.util.List;
@Dao
public interface ExpenseDAO {
    @Query("SELECT * FROM expenses")
    List<Expense> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE id = :id")
    Expense getExpenseById(int id);

    @Insert
    void insertExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Query("SELECT * FROM expenses WHERE user_Id = :userId ORDER BY date DESC")
    List<Expense> getExpensesByUserId(int userId);

    @Query("Select SUM (amount) from expenses where amount> 0")
    double getTotalIncome();

    @Query("Select SUM (amount) from expenses where amount< 0")
    double getTotalExpenses();
}
