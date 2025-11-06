package edu.icet.dao;

import edu.icet.db.DBConnection;
import edu.icet.model.ProductDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Add product to database
    public static boolean addProduct(ProductDTO product) {
        String sql = "INSERT INTO product (code, name, price, qty, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQty());
            pstmt.setInt(5, product.getCategoryId());

            int rowsAffected = pstmt.executeUpdate();

            // Get the auto-generated product_id
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    }
                }
            }

            System.out.println("✅ Product added to database: " + product.getName() + " (ID: " + product.getProductId() + ")");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding product to database: " + e.getMessage());
            return false;
        }
    }

    // Get all products from database - FIXED VERSION
    public static List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY product_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ProductDTO product = new ProductDTO(
                        rs.getInt("product_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("qty"),
                        rs.getInt("category_id")
                );
                products.add(product);
            }
            System.out.println("📦 Loaded " + products.size() + " products from database");

        } catch (SQLException e) {
            System.err.println("❌ Error getting products from database: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    // Delete product from database
    public static boolean deleteProduct(Integer productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ Product deleted from database, ID: " + productId);
                return true;
            } else {
                System.out.println("⚠️ No product found with ID: " + productId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting product from database: " + e.getMessage());
            return false;
        }
    }

    // Update product in database
    public static boolean updateProduct(ProductDTO product) {
        String sql = "UPDATE product SET code = ?, name = ?, price = ?, qty = ?, category_id = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQty());
            pstmt.setInt(5, product.getCategoryId());
            pstmt.setInt(6, product.getProductId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Product updated in database: " + product.getName() + " (ID: " + product.getProductId() + ")");
                return true;
            } else {
                System.out.println("⚠️ No product found with ID: " + product.getProductId());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating product in database: " + e.getMessage());
            return false;
        }
    }

    // Check if product code already exists in database
    public static boolean isCodeExists(String code) {
        String sql = "SELECT COUNT(*) as count FROM product WHERE code = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt("count") > 0;
                if (exists) {
                    System.out.println("⚠️ Product code already exists: " + code);
                }
                return exists;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking product code in database: " + e.getMessage());
        }

        return false;
    }

    // Get product by ID
    public static ProductDTO getProductById(Integer productId) {
        String sql = "SELECT * FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ProductDTO(
                        rs.getInt("product_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("qty"),
                        rs.getInt("category_id")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting product by ID: " + e.getMessage());
        }

        return null;
    }

    // Search products by name or code
    public static List<ProductDTO> searchProducts(String searchText) {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE name LIKE ? OR code LIKE ? ORDER BY product_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductDTO product = new ProductDTO(
                        rs.getInt("product_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("qty"),
                        rs.getInt("category_id")
                );
                products.add(product);
            }

            System.out.println("🔍 Found " + products.size() + " products matching: " + searchText);

        } catch (SQLException e) {
            System.err.println("❌ Error searching products: " + e.getMessage());
        }

        return products;
    }

    // Check if product exists by ID
    public static boolean isProductExists(Integer productId) {
        String sql = "SELECT COUNT(*) as count FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error checking product existence: " + e.getMessage());
        }

        return false;
    }

    // Get product count
    public static int getProductCount() {
        String sql = "SELECT COUNT(*) as count FROM product";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting product count: " + e.getMessage());
        }

        return 0;
    }
}