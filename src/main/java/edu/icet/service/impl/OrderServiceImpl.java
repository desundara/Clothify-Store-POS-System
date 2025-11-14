package edu.icet.service.impl;

import edu.icet.model.OrderDTO;
import edu.icet.model.OrderItemDTO;
import edu.icet.service.interfaces.OrderService;
import edu.icet.db.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    // Create a new order
    @Override
    public OrderDTO createOrder(OrderDTO order) {
        Connection conn = null;
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);

            // Insert order into `order` table
            String orderSql = "INSERT INTO `order` (date, total, employee_id, status) VALUES (?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);

            orderStmt.setTimestamp(1, Timestamp.valueOf(order.getDate()));
            orderStmt.setDouble(2, order.getTotal());
            orderStmt.setInt(3, order.getEmployeeId());
            orderStmt.setString(4, order.getStatus());
            orderStmt.executeUpdate();

            // Get generated order ID
            ResultSet rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
            }

            // Insert order items
            if (order.getItems() != null) {
                String itemSql = "INSERT INTO order_item (order_id, product_id, qty, price, status) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                for (OrderItemDTO item : order.getItems()) {
                    itemStmt.setInt(1, order.getOrderId());
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQty());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.setString(5, item.getStatus());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit();
            return order;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Get order by ID
    @Override
    public OrderDTO getOrderById(String orderId) {
        String sql = "SELECT order_id, date, total, employee_id, status FROM `order` WHERE order_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(orderId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setDate(rs.getTimestamp("date").toLocalDateTime());
                order.setTotal(rs.getDouble("total"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setStatus(rs.getString("status"));
                order.setItems(getOrderItems(orderId));
                return order;
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all orders
    @Override
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT order_id, date, total, employee_id, status FROM `order` ORDER BY date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setDate(rs.getTimestamp("date").toLocalDateTime());
                order.setTotal(rs.getDouble("total"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setStatus(rs.getString("status"));
                order.setItems(getOrderItems(String.valueOf(order.getOrderId())));
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Get orders by date range
    @Override
    public List<OrderDTO> getOrdersByDateRange(String startDate, String endDate) {
        List<OrderDTO> orders = new ArrayList<>();
        String sql = "SELECT order_id, date, total, employee_id, status FROM `order` " +
                "WHERE date BETWEEN ? AND ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startDate + " 00:00:00"));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate + " 23:59:59"));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setDate(rs.getTimestamp("date").toLocalDateTime());
                order.setTotal(rs.getDouble("total"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setStatus(rs.getString("status"));
                order.setItems(getOrderItems(String.valueOf(order.getOrderId())));
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Update order status
    @Override
    public boolean updateOrderStatus(String orderId, String status) {
        String sql = "UPDATE `order` SET status = ? WHERE order_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, Integer.parseInt(orderId));
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Calculate order total
    @Override
    public double calculateOrderTotal(String orderId) {
        String sql = "SELECT total FROM `order` WHERE order_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(orderId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Get order items
    @Override
    public List<OrderItemDTO> getOrderItems(String orderId) {
        List<OrderItemDTO> items = new ArrayList<>();
        String sql = "SELECT order_item_id, order_id, product_id, qty, price, status FROM order_item " +
                "WHERE order_id = ?";

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(orderId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItemDTO item = new OrderItemDTO();
                item.setOrderItemId(rs.getInt("order_item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQty(rs.getInt("qty"));
                item.setPrice(rs.getDouble("price"));
                item.setStatus(rs.getString("status"));
                items.add(item);
            }

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return items;
    }
}
