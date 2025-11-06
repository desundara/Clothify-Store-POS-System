package edu.icet.util;

import edu.icet.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    private static String currentUserRole;
    private static String currentUsername;

    public static boolean login(String username, String password) {
        String sql = "SELECT password, role FROM user WHERE username = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql)) {

            pstm.setString(1, username);
            ResultSet resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                String role = resultSet.getString("role");

                if (PasswordUtil.checkPassword(password, storedPassword)) {
                    currentUserRole = role;
                    currentUsername = username;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }
    public static String getCurrentUsername() {
        return currentUsername;
    }
    public static boolean isAdmin() {
        return "ADMIN".equals(currentUserRole);
    }
    public static boolean isStaff() {
        return "STAFF".equals(currentUserRole);
    }

    public static void logout() {
        currentUserRole = null;
        currentUsername = null;
    }
}
