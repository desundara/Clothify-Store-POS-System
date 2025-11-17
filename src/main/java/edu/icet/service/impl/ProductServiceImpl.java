package edu.icet.service.impl;

import edu.icet.model.CategoryDTO;
import edu.icet.model.ProductDTO;
import edu.icet.service.interfaces.ProductService;
import edu.icet.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    @Override
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT product.product_id, product.code, product.name, product.price, product.qty, " +
                "product.category_id, category.name AS category_name " +
                "FROM product JOIN category ON product.category_id = category.category_id";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ProductDTO product = mapProduct(rs);
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public ProductDTO getProductById(String productId) {
        String sql = "SELECT product.product_id, product.code, product.name, product.price, product.qty, " +
                "product.category_id, category.name AS category_name " +
                "FROM product JOIN category ON product.category_id = category.category_id " +
                "WHERE product.product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(productId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapProduct(rs);
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addProduct(ProductDTO product) {
        String sql = "INSERT INTO product (code, name, price, qty, category_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQty());
            pstmt.setInt(5, product.getCategoryId());

            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean updateProduct(ProductDTO product) {
        String sql = "UPDATE product SET code = ?, name = ?, price = ?, qty = ?, category_id = ? WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQty());
            pstmt.setInt(5, product.getCategoryId());
            pstmt.setInt(6, product.getProductId());

            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean deleteProduct(String productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setInt(1, Integer.parseInt(productId));
            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String categoryId) {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT product.product_id, product.code, product.name, product.price, product.qty, " +
                "product.category_id, category.name AS category_name " +
                "FROM product JOIN category ON product.category_id = category.category_id " +
                "WHERE product.category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(categoryId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(mapProduct(rs));
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return products;
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT product.product_id, product.code, product.name, product.price, product.qty, " +
                "product.category_id, category.name AS category_name " +
                "FROM product JOIN category ON product.category_id = category.category_id " +
                "WHERE product.name LIKE ? OR product.code LIKE ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(mapProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<CategoryDTO> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, description FROM category";

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
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public CategoryDTO getCategoryById(String categoryId) {
        String sql = "SELECT category_id, name, description FROM category WHERE category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(categoryId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CategoryDTO category = new CategoryDTO();
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));
                return category;
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addCategory(CategoryDTO category) {
        String sql = "INSERT INTO category (name, description) VALUES (?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());

            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean updateCategory(CategoryDTO category) {
        String sql = "UPDATE category SET description = ? WHERE name = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setString(1, category.getDescription());
            pstmt.setString(2, category.getName());

            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean deleteCategory(String categoryId) {
        String sql = "DELETE FROM category WHERE category_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            pstmt.setInt(1, Integer.parseInt(categoryId));
            int result = pstmt.executeUpdate();
            conn.commit();
            return result > 0;

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try (Connection conn = DBConnection.getInstance().getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to map ResultSet to ProductDTO
    private ProductDTO mapProduct(ResultSet rs) throws SQLException {
        ProductDTO product = new ProductDTO();
        product.setProductId(rs.getInt("product_id"));
        product.setCode(rs.getString("code"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setQty(rs.getInt("qty"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setCategoryName(rs.getString("category_name"));
        return product;
    }
}
