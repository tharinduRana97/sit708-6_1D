package com.example.learnerapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.learnerapp.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("DELETE FROM User")
    void deleteAllUsers();

    @Query("SELECT * FROM User WHERE username = :username OR email = :email LIMIT 1")
    User findUserByUsernameOrEmail(String username, String email);

    @Query("SELECT * FROM User WHERE username = :username AND password = :password")
    User login(String username, String password);
}