package com.msp.junit.integration;

import com.msp.junit.util.ConnectionManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * просмотр базы из консоли
 * org.h2.tools.Server.startWebServer(com.msp.junit.integration.IntegrationTestBase.conn)
 */
public abstract class IntegrationTestBase {

    public static Connection conn;

    private static final String CLEAN_SQL = "DELETE FROM users;";
    private static final String CREATE_SQL = """
            CREATE TABLE IF NOT EXISTS users
            (
                id INT AUTO_INCREMENT PRIMARY KEY ,
                name VARCHAR(64) NOT NULL,
                birthday DATE NOT NULL ,
                email VARCHAR(64) NOT NULL UNIQUE ,
                password VARCHAR(64) NOT NULL ,
                role VARCHAR(32) NOT NULL ,
                gender VARCHAR(16) NOT NULL
            );
            """;

    @BeforeAll
    static void prepareDatabase() throws SQLException {
        try {
            conn = ConnectionManager.get();
            var statement = conn.createStatement();
            statement.execute(CREATE_SQL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void cleanData() throws SQLException {
        try (/*var connection = ConnectionManager.get();*/
             var statement = conn.createStatement()) {
            statement.execute(CLEAN_SQL);
        }
    }

}
