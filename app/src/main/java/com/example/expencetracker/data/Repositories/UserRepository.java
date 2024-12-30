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
    // Authenticate user
    public boolean authenticateUser(String username, String password) {
        User user = userDAO.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    // Register user
    public void registerUser(String username,String email, String password) {
        User newUser = new User( username,email, password);
        insertUser(newUser);
    }

    // Find user by username
    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public int getUserIdByUsername(String username) {
       return userDAO.getUserIdByUsername(username);
    }
}
