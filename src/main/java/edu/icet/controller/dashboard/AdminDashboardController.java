package edu.icet.controller.dashboard;

import edu.icet.util.NavigationUtil;
import edu.icet.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    public void initialize() {
        String username = SessionManager.getCurrentUsername();
        lblWelcome.setText("Welcome, " + username + "!");
        System.out.println("Admin Dashboard loaded for: " + username);

        // Optional: Debug stage status
        NavigationUtil.debugStageStatus();
    }

    // With event parameter
    @FXML
    private void btnLogoutOnAction(ActionEvent event) {
        System.out.println("Logout with event");
        SessionManager.clearSession();
        NavigationUtil.logout(event);
    }

    @FXML
    private void btnDashboardOnAction() {
        System.out.println("Dashboard clicked");
    }

    @FXML
    private void btnProductOnAction() {
        System.out.println("Product Management clicked");
        NavigationUtil.loadProductManagement();
    }

    @FXML
    private void btnSalesOnAction() {
        System.out.println("Sales clicked");
        NavigationUtil.loadSalesManagement();
    }

    @FXML
    private void btnInventoryOnAction() {
        System.out.println("Inventory clicked");
        NavigationUtil.loadInventoryManagement();
    }

    @FXML
    private void btnPurchaseOrderOnAction() {
        System.out.println("Purchase Order clicked");
        NavigationUtil.loadPurchaseOrderManagement();
    }

    @FXML
    private void btnReportOnAction() {
        System.out.println("Reports clicked");
        NavigationUtil.loadReports();
    }

    @FXML
    private void btnStaffOnAction() {
        System.out.println("Staff Management clicked");
        NavigationUtil.loadStaffManagement();
    }
}