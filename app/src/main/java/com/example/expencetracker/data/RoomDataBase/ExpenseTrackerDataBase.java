package com.example.expencetracker.data.RoomDataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expencetracker.data.DAOs.BudgetDAO;
import com.example.expencetracker.data.DAOs.CategoryDAO;
import com.example.expencetracker.data.DAOs.ExpenseDAO;
import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.Entities.Budget;
import com.example.expencetracker.data.Entities.Category;
import com.example.expencetracker.data.Entities.Expense;
import com.example.expencetracker.data.Entities.User;

@Database(entities = {Expense.class, Category.class, Budget.class, User.class}, version = 1)
public abstract class ExpenseTrackerDataBase extends RoomDatabase {

    private static ExpenseTrackerDataBase INSTANCE;

    public static ExpenseTrackerDataBase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ExpenseTrackerDataBase.class, "expense_tracker_db")
                    .build();
        }
        return INSTANCE;
    }

    public abstract ExpenseDAO expenseDAO();
    public abstract CategoryDAO categoryDAO();
    public abstract BudgetDAO budgetDAO();
    public abstract UserDAO userDAO();
}

