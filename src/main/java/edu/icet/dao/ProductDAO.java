package edu.icet.dao;

import edu.icet.db.DBConnection;
import edu.icet.model.ProductDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static boolean addProduct(ProductDTO product) {
        String sql = "INSERT INTO product (code, name, price, qty, category_id) VALUES (?, ?, ?, ?, ?)";

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

        String sql = "SELECT * FROM product";

        System.out.println("🔍 Executing SQL: " + sql);

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int count = 0;
            while (rs.next()) {
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQty(rs.getInt("qty"));
                product.setCategoryId(rs.getInt("category_id"));

                // Set a default category name since we're not joining
                product.setCategoryName(getCategoryNameById(rs.getInt("category_id")));

                products.add(product);
                count++;
                System.out.println("   ✅ Loaded product: " + product.getName() + " - " + product.getCode());
            }

            System.out.println("🎯 Total products loaded from database: " + count);

        } catch (SQLException e) {
            System.err.println("❌ Error loading products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Helper method to get category name
    private static String getCategoryNameById(int categoryId) {
        switch (categoryId) {
            case 1: return "Denim Collection";
            case 2: return "Summer Tops";
            case 3: return "Shorts & Skirts";
            case 4: return "Casual Shirts";
            default: return "Unknown Category";
        }
    }

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
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";

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
        String sql = "SELECT COUNT(*) as count FROM product WHERE code = ?";

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

    // NEW METHODS FOR STOCK MANAGEMENT =========================================

    public static boolean updateProductStock(int productId, int newStock) {
        String sql = "UPDATE product SET qty = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newStock);
            pstmt.setInt(2, productId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Stock updated for product ID " + productId + " to " + newStock);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating product stock: " + e.getMessage());
            return false;
        }
    }

    public static ProductDTO getProductByCode(String code) {
        String sql = "SELECT * FROM product WHERE code = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQty(rs.getInt("qty"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(getCategoryNameById(rs.getInt("category_id")));
                return product;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting product by code: " + e.getMessage());
        }
        return null;
    }

    public static ProductDTO getProductByName(String name) {
        String sql = "SELECT * FROM product WHERE name = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQty(rs.getInt("qty"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(getCategoryNameById(rs.getInt("category_id")));
                return product;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting product by name: " + e.getMessage());
        }
        return null;
    }

    public static ProductDTO getProductById(int productId) {
        String sql = "SELECT * FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQty(rs.getInt("qty"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setCategoryName(getCategoryNameById(rs.getInt("category_id")));
                return product;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting product by ID: " + e.getMessage());
        }
        return null;
    }
}