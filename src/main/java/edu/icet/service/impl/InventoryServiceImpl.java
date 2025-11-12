package edu.icet.service.impl;

import edu.icet.model.InventoryLogDTO;
import edu.icet.model.ProductDTO;
import edu.icet.service.interfaces.InventoryService;
import edu.icet.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryServiceImpl implements InventoryService {

    // Update stock
    @Override
    public boolean updateStock(String productId, int quantity) {
        String sql = "UPDATE product SET qty = qty + ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, Integer.parseInt(productId));
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get current stock
    @Override
    public int getCurrentStock(String productId) {
        String sql = "SELECT qty FROM product WHERE product_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(productId));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("qty");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Add stock with logging
    @Override
    public boolean addStock(String productId, int quantity, String reason) {
        if (updateStock(productId, quantity)) {
            return logInventoryChange(Integer.parseInt(productId), quantity, "IN");
        }
        return false;
    }

    // Remove stock with logging
    @Override
    public boolean removeStock(String productId, int quantity, String reason) {
        int currentStock = getCurrentStock(productId);
        if (currentStock < quantity) return false;

        if (updateStock(productId, -quantity)) {
            return logInventoryChange(Integer.parseInt(productId), quantity, "OUT");
        }
        return false;
    }

    // Log inventory changes
    private boolean logInventoryChange(int productId, int quantity, String changeType) {
        String sql = "INSERT INTO inventory_log (product_id, change_type, qty_changed, date) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            pstmt.setString(2, changeType);
            pstmt.setInt(3, Math.abs(quantity));
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get inventory history
    @Override
    public List<InventoryLogDTO> getInventoryHistory(String productId) {
        List<InventoryLogDTO> logs = new ArrayList<>();
        String sql = "SELECT * FROM inventory_log WHERE product_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(productId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Integer supplierId = (Integer) rs.getObject("supplier_id"); // nullable
                InventoryLogDTO log = new InventoryLogDTO(
                        rs.getInt("log_id"),
                        rs.getInt("product_id"),
                        supplierId,
                        rs.getString("change_type"),
                        rs.getInt("qty_changed"),
                        rs.getTimestamp("date").toLocalDateTime()
                );
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    // Get low stock products
    @Override
    public List<ProductDTO> getLowStockProducts(int threshold) {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE qty <= ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProductDTO product = new ProductDTO();
                product.setProductId(rs.getInt("product_id"));
                product.setCode(rs.getString("code"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setQty(rs.getInt("qty"));
                product.setCategoryId(rs.getInt("category_id"));
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
