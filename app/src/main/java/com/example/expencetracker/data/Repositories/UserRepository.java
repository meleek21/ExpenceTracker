package com.example.expencetracker.data.Repositories;

import com.example.expencetracker.data.DAOs.UserDAO;
import com.example.expencetracker.data.Entities.User;

import java.util.List;

public class UserRepository {
    private final UserDAO userDAO;

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public void insertUser(User user) {
        userDAO.insertUser(user);
    }

    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    // Authenticate user (plain-text password)
    public boolean authenticateUser(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            // Compare plain-text passwords
            return password.equals(user.getPassword());
        }
        return false;
    }

    // Register user (plain-text password)
    public void registerUser(String username, String email, String password) {
        User newUser = new User(username, email, password); // Store plain-text password
        insertUser(newUser);
    }

    // Find user by username
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    // Get user ID by username
    public int getUserIdByUsername(String username) {
        return userDAO.getUserIdByUsername(username);
    }
}