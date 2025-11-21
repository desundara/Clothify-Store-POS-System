package edu.icet.controller.sales;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderController implements Initializable {

    // Header Section
    @FXML
    private Button newOrderButton;

    // Filters Section
    @FXML
    private TextField searchField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> branchComboBox;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button searchButton;

    // Orders Table Section
    @FXML
    private Label ordersCountLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComboBoxes();
        setupEventHandlers();
    }

    private void setupComboBoxes() {
        // Initialize Status ComboBox
        statusComboBox.getItems().addAll(
                "All Status",
                "Pending",
                "Completed",
                "Cancelled"
        );
        statusComboBox.setValue("All Status");
    }

    private void setupEventHandlers() {
        // Set up any additional event handlers if needed
    }

    // Event Handlers

    @FXML
    private void handleNewOrder(ActionEvent event) {
        System.out.println("Creating new order...");
        // Add your new order creation logic here
        showAlert("New Order", "Create New Order functionality would open here.");

        // Example: Open a new order dialog or scene
        // openNewOrderDialog();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText();
        String branch = branchComboBox.getValue();
        String status = statusComboBox.getValue();

        System.out.println("Searching orders with criteria:");
        System.out.println("Search Text: " + searchText);
        System.out.println("Branch: " + branch);
        System.out.println("Status: " + status);
        System.out.println("Start Date: " + (startDatePicker.getValue() != null ? startDatePicker.getValue() : "Not set"));
        System.out.println("End Date: " + (endDatePicker.getValue() != null ? endDatePicker.getValue() : "Not set"));

        // Add your search logic here
        // Example: filterOrders(searchText, branch, status, startDatePicker.getValue(), endDatePicker.getValue());

        showAlert("Search", "Search functionality would filter orders based on criteria.");
    }

    @FXML
    private void handleViewOrder(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        HBox row = (HBox) sourceButton.getParent().getParent();
        Label orderIdLabel = (Label) row.getChildren().get(0);
        String orderId = orderIdLabel.getText();

        System.out.println("Viewing order: " + orderId);
        showAlert("View Order", "Viewing details for order: " + orderId);


    }

    @FXML
    private void handleEditOrder(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        HBox row = (HBox) sourceButton.getParent().getParent();
        Label orderIdLabel = (Label) row.getChildren().get(0);
        String orderId = orderIdLabel.getText();

        System.out.println("Editing order: " + orderId);
        showAlert("Edit Order", "Editing order: " + orderId);


    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        // Clear all filter fields
        searchField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        branchComboBox.setValue("All Branches");
        statusComboBox.setValue("All Status");

        System.out.println("Filters cleared");
        showAlert("Filters Cleared", "All search filters have been cleared.");

        // Reload all orders
        // loadAllOrders();
    }

    @FXML
    private void handleExportOrders(ActionEvent event) {
        System.out.println("Exporting orders...");
        showAlert("Export Orders", "Order export functionality would generate a report.");

        // Example: generateOrderReport();
    }

    @FXML
    private void handleRefreshOrders(ActionEvent event) {
        System.out.println("Refreshing orders...");
        showAlert("Refresh", "Orders list refreshed.");

        // Reload data from database
        // loadOrdersFromDatabase();
    }

    // Utility Methods

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data validation methods
    private boolean validateSearchDates() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
                showAlert("Date Error", "Start date cannot be after end date.");
                return false;
            }
        }
        return true;
    }

    // Business logic methods (placeholder implementations)

    public void filterOrders(String searchText, String branch, String status,
                             java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Implement your order filtering logic here
        // This would typically interact with your service layer

        System.out.println("Filtering orders with:");
        System.out.println("Search: " + searchText);
        System.out.println("Branch: " + branch);
        System.out.println("Status: " + status);
        System.out.println("Date Range: " + startDate + " to " + endDate);

        // Example implementation:
        // List<Order> filteredOrders = orderService.findOrders(searchText, branch, status, startDate, endDate);
        // updateOrdersTable(filteredOrders);
    }

    public void loadAllOrders() {
        // Load all orders from database
        // List<Order> allOrders = orderService.getAllOrders();
        // updateOrdersTable(allOrders);

        System.out.println("Loading all orders...");
    }

    public void updateOrdersTable(java.util.List<Object> orders) {
        // Update the UI table with the provided orders
        // This would typically clear the current table and add new rows

        System.out.println("Updating table with " + orders.size() + " orders");
    }

    // Method to add sample data rows programmatically (if needed)
    private void addSampleOrderRow(String orderId, String customerName, String status,
                                   String date, String totalAmount) {
        // This method can be used to dynamically add order rows
        // You would typically use this with a ListView or TableView

        System.out.println("Adding order: " + orderId + " - " + customerName);
    }

    // Navigation methods
    @FXML
    private void navigateToDashboard(ActionEvent event) {
        System.out.println("Navigating to Dashboard...");
        // Implement navigation logic
    }

    @FXML
    private void navigateToCustomers(ActionEvent event) {
        System.out.println("Navigating to Customers...");
        // Implement navigation logic
    }

    @FXML
    private void navigateToAnalytics(ActionEvent event) {
        System.out.println("Navigating to Analytics...");
        // Implement navigation logic
    }
}