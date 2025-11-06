package edu.icet.util;

import edu.icet.db.DBConnection;

public class CreateAdminUser {
    public static void main(String[] args) {
        createAdminUser();
    }

    public static void createAdminUser() {
        try {
            var connection = DBConnection.getInstance().getConnection();

            // Check if admin already exists
            var checkStmt = connection.prepareStatement("SELECT COUNT(*) as count FROM user WHERE username = 'admin'");
            var checkResult = checkStmt.executeQuery();

            if (checkResult.next() && checkResult.getInt("count") > 0) {
                System.out.println("⚠Admin user already exists!");
                return;
            }

            // Hash the password
            String hashedPassword = PasswordUtil.hashPassword("admin123");
            System.out.println("Hashed password: " + hashedPassword);

            // Create admin user
            String sql = "INSERT INTO user (username, password, role, email, is_active) VALUES (?, ?, ?, ?, ?)";
            var pstm = connection.prepareStatement(sql);

            pstm.setString(1, "admin");
            pstm.setString(2, hashedPassword);
            pstm.setString(3, "ADMIN");
            pstm.setString(4, "admin@clothify.com");
            pstm.setBoolean(5, true);

            int rows = pstm.executeUpdate();
            System.out.println("✅ " + rows + " admin user created successfully!");

            // Verify
            var verifyStmt = connection.createStatement();
            var rs = verifyStmt.executeQuery("SELECT username, role, LENGTH(password) as pwd_length FROM user WHERE username = 'admin'");
            if (rs.next()) {
                System.out.println("Created User: " + rs.getString("username"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Password Length: " + rs.getInt("pwd_length"));
            }

        } catch (Exception e) {
            System.out.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}