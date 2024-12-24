package com.example.expencetracker.data.Repositories;

import com.example.expencetracker.data.DOAs.UserDAO;
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
}
