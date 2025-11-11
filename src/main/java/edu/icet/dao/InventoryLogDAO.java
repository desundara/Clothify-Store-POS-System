package edu.icet.dao;

import edu.icet.db.DBConnection;
import edu.icet.model.InventoryLogDTO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryLogDAO {

    public static boolean addInventoryLog(InventoryLogDTO log) {
        String sql = "INSERT INTO inventory_log (product_id, supplier_id, change_type, qty_changed) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, log.getProductId());
            if (log.getSupplierId() != null) {
                pstmt.setInt(2, log.getSupplierId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, log.getChangeType());
            pstmt.setInt(4, log.getQtyChanged());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding inventory log: " + e.getMessage());
            return false;
        }
    }

    public static List<InventoryLogDTO> getAllInventoryLogs() {
        List<InventoryLogDTO> logs = new ArrayList<>();
        String sql = "SELECT * FROM inventory_log ORDER BY date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventoryLogDTO log = new InventoryLogDTO();
                log.setLogId(rs.getInt("log_id"));
                log.setProductId(rs.getInt("product_id"));

                int supplierId = rs.getInt("supplier_id");
                if (!rs.wasNull()) {
                    log.setSupplierId(supplierId);
                }

                log.setChangeType(rs.getString("change_type"));
                log.setQtyChanged(rs.getInt("qty_changed"));

                Timestamp timestamp = rs.getTimestamp("date");
                if (timestamp != null) {
                    log.setDate(timestamp.toLocalDateTime());
                }

                logs.add(log);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading inventory logs: " + e.getMessage());
        }
        return logs;
    }
}