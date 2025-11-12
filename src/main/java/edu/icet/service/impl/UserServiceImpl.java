package edu.icet.service.impl;

import edu.icet.model.UserDTO;
import edu.icet.service.interfaces.UserService;
import edu.icet.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    // Get all users
    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE deleted_at IS NULL";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserDTO user = mapResultSetToUser(rs);
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // Get user by ID
    @Override
    public UserDTO getUserById(String userId) {
        String sql = "SELECT * FROM user WHERE user_id = ? AND deleted_at IS NULL";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Update user
    @Override
    public boolean updateUser(UserDTO user) {
        String sql = "UPDATE user SET username = ?, password = ?, email = ?, role = ?, employee_id = ?, is_active = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            if (user.getEmployeeId() != null) {
                pstmt.setInt(5, user.getEmployeeId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setBoolean(6, user.isActive());
            pstmt.setInt(7, user.getUserId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Soft delete user
    @Override
    public boolean deleteUser(String userId) {
        String sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(userId));
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get users by role
    @Override
    public List<UserDTO> getUsersByRole(String role) {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT * FROM user WHERE role = ? AND deleted_at IS NULL";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // Helper method to map ResultSet to UserDTO
    private UserDTO mapResultSetToUser(ResultSet rs) throws SQLException {
        UserDTO user = new UserDTO();
        user.setUserId(rs.getInt("user_id"));
        user.setUserName(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));

        int empId = rs.getInt("employee_id");
        if (!rs.wasNull()) {
            user.setEmployeeId(empId);
        } else {
            user.setEmployeeId(null);
        }

        user.setActive(rs.getBoolean("is_active"));
        user.setDeletedAt(rs.getTimestamp("deleted_at"));

        return user;
    }
}
