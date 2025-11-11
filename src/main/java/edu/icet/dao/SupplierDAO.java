package edu.icet.dao;

import edu.icet.db.DBConnection;
import edu.icet.model.SupplierDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public static List<SupplierDTO> getAllSuppliers() {
        List<SupplierDTO> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM supplier";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SupplierDTO supplier = new SupplierDTO();
                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setName(rs.getString("name"));
                // Add other fields as needed
                suppliers.add(supplier);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading suppliers: " + e.getMessage());
        }
        return suppliers;
    }
}