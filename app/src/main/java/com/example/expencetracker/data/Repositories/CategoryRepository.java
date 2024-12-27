package com.example.expencetracker.data.Repositories;

import com.example.expencetracker.data.DAOs.CategoryDAO;
import com.example.expencetracker.data.Entities.Category;

import java.util.List;

public class CategoryRepository {
    private CategoryDAO categoryDAO;

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
}
