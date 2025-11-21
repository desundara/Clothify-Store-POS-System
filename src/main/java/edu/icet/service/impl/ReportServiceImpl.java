package edu.icet.service.impl;

import edu.icet.service.interfaces.ReportService;
import edu.icet.model.TopProduct;
import edu.icet.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportServiceImpl implements ReportService {

    @Override
    public List<Map<String, Object>> getDailySalesReport(String date) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT o.order_id, o.total_amount, o.order_date, " +
                "COUNT(oi.order_item_id) as item_count " +
                "FROM `order` o " +
                "LEFT JOIN order_item oi ON o.order_id = oi.order_id " +
                "WHERE DATE(o.order_date) = ? " +
                "GROUP BY o.order_id, o.total_amount, o.order_date " +
                "ORDER BY o.order_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("orderId", rs.getString("order_id"));
                row.put("totalAmount", rs.getDouble("total_amount"));
                row.put("orderDate", rs.getTimestamp("order_date"));
                row.put("itemCount", rs.getInt("item_count"));
                report.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }

    @Override
    public List<Map<String, Object>> getMonthlySalesReport(String year, String month) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT DATE(order_date) as sale_date, " +
                "COUNT(*) as order_count, " +
                "SUM(total_amount) as daily_revenue " +
                "FROM `order` " +
                "WHERE YEAR(order_date) = ? AND MONTH(order_date) = ? " +
                "GROUP BY DATE(order_date) " +
                "ORDER BY sale_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, year);
            pstmt.setString(2, month);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("saleDate", rs.getDate("sale_date"));
                row.put("orderCount", rs.getInt("order_count"));
                row.put("dailyRevenue", rs.getDouble("daily_revenue"));
                report.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }

    @Override
    public List<TopProduct> getTopSellingProducts(int limit, String period) {
        List<TopProduct> topProducts = new ArrayList<>();

        String periodCondition = "";
        switch (period.toLowerCase()) {
            case "daily":
                periodCondition = " AND DATE(o.order_date) = CURDATE()";
                break;
            case "weekly":
                periodCondition = " AND o.order_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
                break;
            case "monthly":
                periodCondition = " AND o.order_date >= DATE_SUB(NOW(), INTERVAL 1 MONTH)";
                break;
            case "yearly":
                periodCondition = " AND YEAR(o.order_date) = YEAR(CURDATE())";
                break;
        }

        String sql = "SELECT p.product_id, p.name, p.code, " +
                "SUM(oi.quantity) as total_sold, " +
                "SUM(oi.quantity * oi.unit_price) as total_revenue " +
                "FROM order_item oi " +
                "JOIN product p ON oi.product_id = p.product_id " +
                "JOIN `order` o ON oi.order_id = o.order_id " +
                "WHERE 1=1" + "periodCondition" +
                " GROUP BY p.product_id, p.name, p.code " +
                "ORDER BY total_sold DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                TopProduct product = new TopProduct();
                product.setProductName(rs.getString("productName"));
                product.setUnitsSold(rs.getInt("unitsSold"));
                product.setRevenue(rs.getString("revenue"));
                topProducts.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topProducts;
    }

    @Override
    public List<Map<String, Object>> getLowStockProducts(int threshold) {
        List<Map<String, Object>> lowStockProducts = new ArrayList<>();
        String sql = "SELECT p.product_id, p.code, p.name, p.qty, p.price, c.name as category_name " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.category_id " +
                "WHERE p.qty <= ? " +
                "ORDER BY p.qty ASC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("product_id"));
                product.put("productCode", rs.getString("code"));
                product.put("productName", rs.getString("name"));
                product.put("quantity", rs.getInt("qty"));
                product.put("price", rs.getBigDecimal("price"));
                product.put("categoryName", rs.getString("category_name"));
                lowStockProducts.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lowStockProducts;
    }

    @Override
    public double getTotalRevenue(String period) {
        String sql = "";

        switch (period.toLowerCase()) {
            case "daily":
                sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM `order` WHERE DATE(order_date) = CURDATE()";
                break;
            case "weekly":
                sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM `order` WHERE order_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
                break;
            case "monthly":
                sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM `order` WHERE MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())";
                break;
            case "yearly":
                sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM `order` WHERE YEAR(order_date) = YEAR(CURDATE())";
                break;
            default:
                sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM `order`";
        }

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    @Override
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        String[] queries = {
                // Today's orders count
                "SELECT COUNT(*) as total_orders FROM `order` WHERE DATE(order_date) = CURDATE()",
                // Today's revenue
                "SELECT COALESCE(SUM(total_amount), 0) as daily_revenue FROM `order` WHERE DATE(order_date) = CURDATE()",
                // Low stock products (less than 10)
                "SELECT COUNT(*) as low_stock_products FROM product WHERE qty <= 10",
                // Total products count
                "SELECT COUNT(*) as total_products FROM product",
                // Total categories count
                "SELECT COUNT(*) as total_categories FROM category",
                // This month's revenue
                "SELECT COALESCE(SUM(total_amount), 0) as monthly_revenue FROM `order` WHERE MONTH(order_date) = MONTH(CURDATE()) AND YEAR(order_date) = YEAR(CURDATE())"
        };

        String[] keys = {"todayOrders", "todayRevenue", "lowStockCount", "totalProducts", "totalCategories", "monthlyRevenue"};

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            for (int i = 0; i < queries.length; i++) {
                ResultSet rs = stmt.executeQuery(queries[i]);
                if (rs.next()) {
                    summary.put(keys[i], rs.getObject(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }

    @Override
    public List<Map<String, Object>> getSalesReportByDateRange(String startDate, String endDate) {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT o.order_id, o.total_amount, o.order_date, " +
                "COUNT(oi.order_item_id) as item_count, " +
                "SUM(oi.quantity) as total_items " +
                "FROM `order` o " +
                "LEFT JOIN order_item oi ON o.order_id = oi.order_id " +
                "WHERE DATE(o.order_date) BETWEEN ? AND ? " +
                "GROUP BY o.order_id, o.total_amount, o.order_date " +
                "ORDER BY o.order_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("orderId", rs.getString("order_id"));
                row.put("totalAmount", rs.getDouble("total_amount"));
                row.put("orderDate", rs.getTimestamp("order_date"));
                row.put("itemCount", rs.getInt("item_count"));
                row.put("totalItems", rs.getInt("total_items"));
                report.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }

    @Override
    public List<Map<String, Object>> getProductPerformanceReport() {
        List<Map<String, Object>> performanceReport = new ArrayList<>();
        String sql = "SELECT p.product_id, p.code, p.name, p.price, p.qty, " +
                "COALESCE(SUM(oi.quantity), 0) as total_sold, " +
                "COALESCE(SUM(oi.quantity * oi.unit_price), 0) as total_revenue, " +
                "c.name as category_name " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.category_id " +
                "LEFT JOIN order_item oi ON p.product_id = oi.product_id " +
                "GROUP BY p.product_id, p.code, p.name, p.price, p.qty, c.name " +
                "ORDER BY total_revenue DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("product_id"));
                product.put("productCode", rs.getString("code"));
                product.put("productName", rs.getString("name"));
                product.put("price", rs.getBigDecimal("price"));
                product.put("stock", rs.getInt("qty"));
                product.put("totalSold", rs.getInt("total_sold"));
                product.put("totalRevenue", rs.getDouble("total_revenue"));
                product.put("categoryName", rs.getString("category_name"));
                performanceReport.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return performanceReport;
    }

    @Override
    public Map<String, Object> getSalesSummary(String startDate, String endDate) {
        Map<String, Object> summary = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as total_orders, " +
                "COALESCE(SUM(total_amount), 0) as total_revenue, " +
                "AVG(total_amount) as average_order_value, " +
                "MIN(total_amount) as min_order_value, " +
                "MAX(total_amount) as max_order_value " +
                "FROM `order` " +
                "WHERE DATE(order_date) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                summary.put("totalOrders", rs.getInt("total_orders"));
                summary.put("totalRevenue", rs.getDouble("total_revenue"));
                summary.put("averageOrderValue", rs.getDouble("average_order_value"));
                summary.put("minOrderValue", rs.getDouble("min_order_value"));
                summary.put("maxOrderValue", rs.getDouble("max_order_value"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summary;
    }

    @Override
    public List<Map<String, Object>> getInventoryValuationReport() {
        List<Map<String, Object>> valuationReport = new ArrayList<>();
        String sql = "SELECT p.product_id, p.code, p.name, p.qty, p.price, " +
                "(p.qty * p.price) as total_value, " +
                "c.name as category_name " +
                "FROM product p " +
                "LEFT JOIN category c ON p.category_id = c.category_id " +
                "ORDER BY total_value DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("product_id"));
                product.put("productCode", rs.getString("code"));
                product.put("productName", rs.getString("name"));
                product.put("quantity", rs.getInt("qty"));
                product.put("unitPrice", rs.getBigDecimal("price"));
                product.put("totalValue", rs.getBigDecimal("total_value"));
                product.put("categoryName", rs.getString("category_name"));
                valuationReport.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return valuationReport;
    }

    @Override
    public List<Map<String, Object>> getStockMovementReport(String productId) {
        List<Map<String, Object>> movementReport = new ArrayList<>();
        String sql = "SELECT il.log_id, il.product_id, il.movement_type, il.quantity, " +
                "il.reason, il.log_date, p.name as product_name " +
                "FROM inventory_log il " +
                "JOIN product p ON il.product_id = p.product_id " +
                "WHERE il.product_id = ? " +
                "ORDER BY il.log_date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(productId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("logId", rs.getInt("log_id"));
                log.put("productId", rs.getInt("product_id"));
                log.put("productName", rs.getString("product_name"));
                log.put("movementType", rs.getString("movement_type"));
                log.put("quantity", rs.getInt("quantity"));
                log.put("reason", rs.getString("reason"));
                log.put("logDate", rs.getTimestamp("log_date"));
                movementReport.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movementReport;
    }
}