package com.msp.junit.service;

import com.msp.junit.dao.UserDao;
import com.msp.junit.dao.UserDaoImpl;
import com.msp.junit.dto.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    /*public UserService() {
        this.userDao = null;
    }*/
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll() {
        return users;
    }

    public boolean add(User ... users) {
        return this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }

        return users.stream()
                .filter(user -> user.getPassword().equals(password) && user.getUsername().equals(username))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    public boolean deleteUserById(Integer id) {
        return userDao.delete(id);
    }

    public boolean deleteUserByAnotherId(Integer id) {
        id = 25;
        return userDao.delete(id);
    }

    public void anyMethod() {
        userDao.anyMethod();
    }

    public String processConnection() {
        try {
            userDao.getConnection();
            return "connection is ok";
        } catch (SQLException e) {
            return "connection is fail";
        }

    }
}
