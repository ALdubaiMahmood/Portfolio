package com.coffee.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens a fresh JDBC connection to MySQL for each call, using credentials
 * from src/main/resources/db.properties. Simple by design (no connection
 * pooling) since this app only ever has one user at a time.
 */
public class DatabaseConnection {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                PROPS.load(in);
            } else {
                System.err.println("db.properties not found on classpath — using defaults (localhost, root, no password).");
            }
        } catch (IOException e) {
            System.err.println("Could not load db.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("db.url", "jdbc:mysql://localhost:3306/cafe_management_system");
        String user = PROPS.getProperty("db.user", "root");
        String password = PROPS.getProperty("db.password", "");
        return DriverManager.getConnection(url, user, password);
    }
}