package com.example.expencetracker.data.DAOs;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expencetracker.data.Entities.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    @Query("SELECT * FROM users WHERE username = :username")
    User findByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

}