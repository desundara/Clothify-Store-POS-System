package edu.icet.controller.dashboard;

import edu.icet.db.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StaffDashboardController implements Initializable {

    @FXML private Label lblWelcome;
    @FXML private Label lblTodaySales;
    @FXML private Label lblTransactions;
    @FXML private Label lblLowStock;
    @FXML private Button btnNewSale;
    @FXML private Button btnViewProducts;
    @FXML private Button btnLogout;

    @FXML private TableView<RecentSale> tblRecentSales;
    @FXML private TableColumn<RecentSale, String> colOrderId;
    @FXML private TableColumn<RecentSale, String> colCustomer;
    @FXML private TableColumn<RecentSale, String> colAmount;
    @FXML private TableColumn<RecentSale, String> colTime;

    private ObservableList<RecentSale> recentSalesList = FXCollections.observableArrayList();
    private String currentStaffName = "Gayani";
    private Integer currentStaffId = 1; // Default staff ID

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStaffName(currentStaffName);
        setupTableColumns();
        loadDashboardData();
        loadRecentSales();
    }

    private void setupTableColumns() {
        colOrderId.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        colCustomer.setCellValueFactory(cellData -> cellData.getValue().customerProperty());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        colTime.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
    }

    private void loadDashboardData() {
        // Load today's statistics from database
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            // Today's sales - Fixed query for database structure
            String salesQuery = "SELECT COALESCE(SUM(total), 0) as today_sales " +
                    "FROM `order` WHERE DATE(date) = CURDATE() AND employee_id = ? AND status = 'COMPLETED'";
            PreparedStatement salesStmt = connection.prepareStatement(salesQuery);
            salesStmt.setInt(1, getCurrentStaffId());
            ResultSet salesRs = salesStmt.executeQuery();

            if (salesRs.next()) {
                double todaySales = salesRs.getDouble("today_sales");
                lblTodaySales.setText(String.format("LKR %,.2f", todaySales));
            }

            // Today's transactions count
            String transQuery = "SELECT COUNT(*) as transaction_count " +
                    "FROM `order` WHERE DATE(date) = CURDATE() AND employee_id = ? AND status = 'COMPLETED'";
            PreparedStatement transStmt = connection.prepareStatement(transQuery);
            transStmt.setInt(1, getCurrentStaffId());
            ResultSet transRs = transStmt.executeQuery();

            if (transRs.next()) {
                int transactions = transRs.getInt("transaction_count");
                lblTransactions.setText(String.valueOf(transactions));
            }

            // Low stock items - Fixed query for database structure
            String stockQuery = "SELECT COUNT(*) as low_stock_count FROM product WHERE qty <= 10";
            PreparedStatement stockStmt = connection.prepareStatement(stockQuery);
            ResultSet stockRs = stockStmt.executeQuery();

            if (stockRs.next()) {
                int lowStock = stockRs.getInt("low_stock_count");
                lblLowStock.setText(String.valueOf(lowStock));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Set default values if database error
            lblTodaySales.setText("LKR 0.00");
            lblTransactions.setText("0");
            lblLowStock.setText("0");
        }
    }

    private void loadRecentSales() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String query = "SELECT order_id, total, date " +
                    "FROM `order` " +
                    "WHERE employee_id = ? AND DATE(date) = CURDATE() " +
                    "ORDER BY date DESC LIMIT 5";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, getCurrentStaffId());
            ResultSet rs = stmt.executeQuery();

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            while (rs.next()) {
                String orderId = "O" + String.format("%03d", rs.getInt("order_id"));
                String amount = String.format("LKR %,.2f", rs.getDouble("total"));

                LocalDateTime orderDate = rs.getTimestamp("date").toLocalDateTime();
                String time = orderDate.format(timeFormatter);

                recentSalesList.add(new RecentSale(orderId, "Walk-in Customer", amount, time));
            }

            // If no recent sales, add sample data
            if (recentSalesList.isEmpty()) {
                recentSalesList.add(new RecentSale("O001", "Customer 1", "LKR 2,500.00", "10:30 AM"));
                recentSalesList.add(new RecentSale("O002", "Customer 2", "LKR 1,800.00", "11:15 AM"));
            }

            tblRecentSales.setItems(recentSalesList);

        } catch (Exception e) {
            e.printStackTrace();
            // Add sample data if database error
            recentSalesList.add(new RecentSale("O001", "Customer 1", "LKR 2,500.00", "10:30 AM"));
            recentSalesList.add(new RecentSale("O002", "Customer 2", "LKR 1,800.00", "11:15 AM"));
            recentSalesList.add(new RecentSale("O003", "Customer 3", "LKR 3,200.00", "01:45 PM"));
            tblRecentSales.setItems(recentSalesList);
        }
    }

    private Integer getCurrentStaffId() {
        // Implement this method to get current logged-in staff ID
        // This should return the actual staff ID from session/auth system
        return currentStaffId; // Placeholder - replace with actual implementation
    }

    @FXML
    private void btnDashboardOnAction() {
        System.out.println("Dashboard clicked");
        // Refresh dashboard data
        loadDashboardData();
        loadRecentSales();
    }

    @FXML
    private void btnPOSOnAction() {
        System.out.println("POS clicked");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/POS.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("POS - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open POS interface!");
        }
    }

    @FXML
    private void btnProductsViewOnAction() {
        System.out.println("Products View clicked");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/ProductsView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Products - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open products view!");
        }
    }

    @FXML
    private void btnMySalesOnAction() {
        System.out.println("My Sales clicked");
        // Navigate to staff's personal sales
        showAlert(Alert.AlertType.INFORMATION, "My Sales", "My Sales feature coming soon!");
    }

    @FXML
    private void btnInventoryOnAction() {
        System.out.println("Inventory clicked");
        // Navigate to inventory view
        showAlert(Alert.AlertType.INFORMATION, "Inventory", "Inventory feature coming soon!");
    }

    @FXML
    private void btnViewStockOnAction() {
        System.out.println("View Stock clicked");
        // Navigate to stock view
        showAlert(Alert.AlertType.INFORMATION, "View Stock", "View Stock feature coming soon!");
    }

    @FXML
    private void btnMyProfileOnAction() {
        System.out.println("My Profile clicked");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MyProfile.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("My Profile - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot open profile!");
        }
    }

    @FXML
    private void btnLogoutOnAction() {
        System.out.println("Logout clicked");
        try {
            // Close current window
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();

            // Open login window
            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/LoginForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - CLOTHIFY POS");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error during logout!");
        }
    }

    @FXML
    private void btnNewSaleOnAction() {
        System.out.println("New Sale clicked");
        btnPOSOnAction(); // Redirect to POS
    }

    @FXML
    private void btnViewProductsOnAction() {
        System.out.println("View Products clicked");
        btnProductsViewOnAction(); // Redirect to products view
    }

    // Method to update staff name
    public void setStaffName(String staffName) {
        this.currentStaffName = staffName;
        lblWelcome.setText("Welcome, " + staffName + "!");
    }

    // Method to set staff ID (call this when loading the dashboard)
    public void setStaffId(Integer staffId) {
        this.currentStaffId = staffId;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    // Inner class for recent sales table
    public static class RecentSale {
        private final SimpleStringProperty orderId;
        private final SimpleStringProperty customer;
        private final SimpleStringProperty amount;
        private final SimpleStringProperty time;

        public RecentSale(String orderId, String customer, String amount, String time) {
            this.orderId = new SimpleStringProperty(orderId);
            this.customer = new SimpleStringProperty(customer);
            this.amount = new SimpleStringProperty(amount);
            this.time = new SimpleStringProperty(time);
        }

        // Property methods
        public StringProperty orderIdProperty() {
            return orderId;
        }

        public StringProperty customerProperty() {
            return customer;
        }

        public StringProperty amountProperty() {
            return amount;
        }

        public StringProperty timeProperty() {
            return time;
        }
    }
}