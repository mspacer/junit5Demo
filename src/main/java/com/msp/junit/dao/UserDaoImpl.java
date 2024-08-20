package com.msp.junit.dao;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {

    @SneakyThrows
    public boolean delete(Integer id) {
        Connection connection = DriverManager.getConnection("", "login", "password");
        return true;
    }

    @Override
    public void anyMethod() {
    }

    @Override
    public boolean getConnection() throws SQLException {
        DriverManager.getConnection("", "login", "password");
        return true;
    }
}
