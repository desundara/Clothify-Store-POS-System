package edu.icet.dao;

import edu.icet.db.DBConnection;
import edu.icet.model.CategoryDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public static List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categories = new ArrayList<>();
        String sql = "SELECT * FROM category";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CategoryDTO category = new CategoryDTO();
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                categories.add(category);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading categories: " + e.getMessage());
        }
        return categories;
    }

    public static boolean addCategory(CategoryDTO category) {
        String sql = "INSERT INTO category (name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding category: " + e.getMessage());
            return false;
        }
    }

    public static boolean isCategoryNameExists(String name) {
        String sql = "SELECT COUNT(*) as count FROM category WHERE name = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking category name: " + e.getMessage());
        }
        return false;
    }
}