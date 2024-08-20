package com.msp.junit.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserDao {
    boolean delete(Integer id);
    void anyMethod();
    boolean getConnection() throws SQLException;
}
