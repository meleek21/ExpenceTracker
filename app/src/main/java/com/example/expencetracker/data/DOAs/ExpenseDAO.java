package com.example.expencetracker.data.DOAs;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expencetracker.data.Entities.Expense;
import java.util.List;

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
}
