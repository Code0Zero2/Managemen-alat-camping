/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package praktikum_oop.management_alat_camping.config;

/**
 *
 * @author morxidia
 */
import java.sql.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class DatabaseConfig {
    private static Connection connection = null;
    private static final String URL = "jdbc:mysql://localhost:3306/camping_rental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin"; // Change to your MySQL password
    
    static {
        loadMySQLDriver();
    }
    
    private static void loadMySQLDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            // Try to find JAR in lib folder
            try {
                File libDir = new File("lib");
                if (libDir.exists()) {
                    File[] jars = libDir.listFiles((dir, name) -> name.contains("mysql-connector"));
                    if (jars != null && jars.length > 0) {
                        URL url = jars[0].toURI().toURL();
                        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
                        Class.forName("com.mysql.cj.jdbc.Driver", true, classLoader);
                        Thread.currentThread().setContextClassLoader(classLoader);
                        System.out.println("Driver loaded from: " + jars[0].getName());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Failed to load driver automatically");
            }
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            connection.setAutoCommit(false);
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }
    
    public static void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }
    
    public static void rollback() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}