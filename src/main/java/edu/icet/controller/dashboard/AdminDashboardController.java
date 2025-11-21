package edu.icet.controller.dashboard;

import edu.icet.model.TopProduct;
import edu.icet.util.NavigationUtil;
import edu.icet.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label lblWelcome;
    @FXML private TableView<TopProduct> topProductsTable;
    @FXML private TableColumn<TopProduct, String> colProductName;
    @FXML private TableColumn<TopProduct, Integer> colUnitsSold;
    @FXML private TableColumn<TopProduct, String> colRevenue;

    private ObservableList<TopProduct> topProductsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminDashboardController initialized");

        setupWelcomeMessage();
        setupTableColumns();
        loadSampleData();

        System.out.println("Dashboard loaded successfully!");
    }

    private void setupWelcomeMessage() {
        String username = SessionManager.getCurrentUsername();
        if (username != null && !username.isEmpty()) {
            lblWelcome.setText("Welcome, " + username + "!");
        } else {
            lblWelcome.setText("Welcome, Admin!");
        }
    }

    private void setupTableColumns() {
        System.out.println("Setting up table columns...");

        // Check if columns are properly injected
        if (colProductName == null) {
            System.err.println("ERROR: colProductName is null!");
            return;
        }
        if (colUnitsSold == null) {
            System.err.println("ERROR: colUnitsSold is null!");
            return;
        }
        if (colRevenue == null) {
            System.err.println("ERROR: colRevenue is null!");
            return;
        }

        // Set up cell value factories
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colUnitsSold.setCellValueFactory(new PropertyValueFactory<>("unitsSold"));
        colRevenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        // Set table data
        topProductsTable.setItems(topProductsData);

        System.out.println("Table columns setup completed successfully");
    }

    private void loadSampleData() {
        System.out.println("Loading sample data...");

        // Sample data for top selling products
        topProductsData.addAll(
                new TopProduct("T-Shirt - V-Neck", 250, "LKR 75,000"),
                new TopProduct("Denim Jeans - Slim Fit", 180, "LKR 90,000"),
                new TopProduct("Denim Jeans - Slim - Brown", 120, "LKR 90,000"),
                new TopProduct("Cotton Shirt - White", 95, "LKR 47,500"),
                new TopProduct("Sports Shorts", 85, "LKR 25,500")
        );

        System.out.println("Sample data loaded: " + topProductsData.size() + " items");
    }

    // Navigation methods
    @FXML
    private void btnDashboardOnAction(ActionEvent event) {
        System.out.println("Dashboard clicked - Already on dashboard");
    }

    @FXML
    private void btnProductOnAction(ActionEvent event) {
        System.out.println("Product Management clicked");
        NavigationUtil.loadProductManagement();
    }

    @FXML
    private void btnInventoryOnAction(ActionEvent event) {
        System.out.println("Inventory clicked");
        NavigationUtil.loadInventoryManagement();
    }

    @FXML
    private void btnOrderOnAction(ActionEvent event) {
        System.out.println("Orders clicked");
        NavigationUtil.loadOrderManagement();
    }

    @FXML
    private void btnReportOnAction(ActionEvent event) {
        System.out.println("Reports clicked");
        NavigationUtil.loadReports();
    }

    @FXML
    private void btnStaffOnAction(ActionEvent event) {
        System.out.println("Staff Management clicked");
        NavigationUtil.loadStaffManagement();
    }

    @FXML
    private void btnLogoutOnAction(ActionEvent event) {
        System.out.println("Logout initiated from dashboard");
        SessionManager.clearSession();
        NavigationUtil.logout(event);
    }

    // Quick Action Methods
    @FXML
    private void btnAddNewProductOnAction(ActionEvent event) {
        System.out.println("Add New Product clicked");
        NavigationUtil.loadProductManagement();
    }

    @FXML
    private void btnNewSaleOnAction(ActionEvent event) {
        System.out.println("New Sale clicked");
        NavigationUtil.loadSalesManagement();
    }

    @FXML
    private void btnManageCategoriesOnAction(ActionEvent event) {
        System.out.println("Manage Categories clicked");
        NavigationUtil.loadCategoryManagement();
    }
}