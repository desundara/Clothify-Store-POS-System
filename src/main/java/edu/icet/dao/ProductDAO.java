package edu.icet.dao;

import edu.icet.db.DBConnection;
import edu.icet.model.ProductDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static boolean addProduct(ProductDTO product) {
        String sql = "INSERT INTO products (code, name, price, qty, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQty());
            pstmt.setInt(5, product.getCategoryId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
            return false;
        }
    }

    public static List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN category c ON p.category_id = c.category_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // FIXED: Use setter methods instead of constructor
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQty(rs.getInt("qty"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(rs.getString("category_name"));

                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading products: " + e.getMessage());
        }
        return products;
    }

    public static boolean updateProduct(ProductDTO product) {
        String sql = "UPDATE products SET code = ?, name = ?, price = ?, qty = ?, category_id = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQty());
            pstmt.setInt(5, product.getCategoryId());
            pstmt.setInt(6, product.getProductId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting product: " + e.getMessage());
            return false;
        }
    }

    public static boolean isCodeExists(String code) {
        String sql = "SELECT COUNT(*) as count FROM products WHERE code = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking product code: " + e.getMessage());
        }
        return false;
    }
}