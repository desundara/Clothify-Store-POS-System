package edu.icet.db;

import edu.icet.util.PasswordUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/clothify_pos_db", "", "");

        // Auto-create admin user on first connection
        initializeAdminUser();
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeAdminUser() {
        try {
            // Check if admin already exists
            var checkStmt = connection.createStatement();
            var resultSet = checkStmt.executeQuery("SELECT COUNT(*) as count FROM user WHERE username = 'admin'");

            if (resultSet.next() && resultSet.getInt("count") == 0) {
                // Hash the password before storing
                String hashedPassword = PasswordUtil.hashPassword("admin123");

                // Create admin user with HASHED password
                String sql = "INSERT INTO user (username, password, role, email, is_active) VALUES (" +
                        "'admin', '" + hashedPassword + "', 'ADMIN', 'admin@clothify.com', 1)";

                var stmt = connection.createStatement();
                stmt.executeUpdate(sql);
                System.out.println("Auto-created admin user with HASHED password: admin / admin123");
                System.out.println("Hashed password: " + hashedPassword);
            } else {
                System.out.println("Admin user already exists");
            }

        } catch (Exception e) {
            System.out.println("Could not create admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}