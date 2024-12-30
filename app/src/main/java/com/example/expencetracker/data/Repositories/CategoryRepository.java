package com.example.expencetracker.data.Repositories;

import com.example.expencetracker.data.DAOs.CategoryDAO;
import com.example.expencetracker.data.Entities.Category;

import java.util.List;

public class CategoryRepository {
    private final CategoryDAO categoryDAO;

    public CategoryRepository(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryDAO.getCategoryById(id);
    }

    public void insertCategory(Category category) {
        categoryDAO.insertCategory(category);
    }

    public void updateCategory(Category category) {
        categoryDAO.updateCategory(category);
    }

    public void deleteCategory(Category category) {
        categoryDAO.deleteCategory(category);
    }

    // New method to set up initial categories
    public void setupInitialData() {
        new Thread(() -> {
            try {
                List<Category> categories = getAllCategories();
                if (categories.isEmpty()) {
                    // Create default categories
                    Category[] defaultCategories = {
                            new Category("General Expense", "expense"),
                            new Category("Food", "expense"),
                            new Category("Transportation", "expense"),
                            new Category("Salary", "income"),
                            new Category("Other Income", "income")
                    };

                    for (Category category : defaultCategories) {
                        insertCategory(category);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
