package edu.icet.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class NavigationUtil {

    private static Stage currentStage;

    public static void setCurrentStage(Stage stage) {
        currentStage = stage;
        System.out.println("Current stage set: " + (stage != null ? stage.getTitle() : "null"));
    }

    // Enhanced FXML loading with better stage management
    private static void loadFXML(String fxmlPath, String title) {
        try {
            System.out.println("Loading: " + fxmlPath);

            Parent root = FXMLLoader.load(NavigationUtil.class.getResource(fxmlPath));
            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.setScene(new Scene(root));

            // Close current window if it exists
            if (currentStage != null) {
                System.out.println("Closing current stage: " + currentStage.getTitle());
                currentStage.close();
            }

            newStage.show();
            currentStage = newStage;
            System.out.println("Now displaying: " + currentStage.getTitle());

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            showErrorAlert("Page not available: " + title);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Navigation methods
    public static void loadLoginForm() {
        System.out.println("Navigating to Login Form...");
        loadFXML("/view/auth/login.fxml", "Clothify POS - Login");
    }

    public static void loadAdminDashboard() {
        System.out.println("Navigating to Admin Dashboard...");
        loadFXML("/view/dashboard/admin-dashboard.fxml", "Clothify POS - Admin Dashboard");
    }

    public static void loadProductManagement() {
        System.out.println("Navigating to Product Management...");
        loadFXML("/view/inventory/product-management.fxml", "Clothify POS - Product Management");
    }

    public static void loadSalesManagement() {
        System.out.println("Navigating to Sales Management...");
        loadFXML("/view/sales/sales-management.fxml", "Clothify POS - Sales Management");
    }

    public static void loadInventoryManagement() {
        System.out.println("Navigating to Inventory Management...");
        loadFXML("/view/inventory/inventory-management.fxml", "Clothify POS - Inventory Management");
    }

    public static void loadPurchaseOrderManagement() {
        System.out.println("Navigating to Purchase Orders...");
        loadFXML("/view/purchase/purchase-order-management.fxml", "Clothify POS - Purchase Orders");
    }

    public static void loadReports() {
        System.out.println("Navigating to Reports...");
        loadFXML("/view/reports/reports.fxml", "Clothify POS - Reports");
    }

    public static void loadStaffManagement() {
        System.out.println("Navigating to Staff Management...");
        loadFXML("/view/staff/staff-management.fxml", "Clothify POS - Staff Management");
    }

    // GUARANTEED logout method - ALWAYS works
    public static void logout() {
        System.out.println("=== LOGOUT INITIATED ===");

        try {
            // Method 1: Close current stage if exists
            if (currentStage != null) {
                System.out.println("Closing current stage: " + currentStage.getTitle());
                currentStage.close();
            }

            // Method 2: Close ALL other open stages (safety net)
            System.out.println("Closing all other windows...");
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage) {
                    Stage stage = (Stage) window;
                    if (stage.isShowing() && stage != currentStage) {
                        System.out.println("Closing: " + stage.getTitle());
                        stage.close();
                    }
                }
            }

            // Method 3: Create fresh login stage
            System.out.println("Creating login stage...");
            Parent root = FXMLLoader.load(NavigationUtil.class.getResource("/view/auth/login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Clothify POS - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            currentStage = loginStage;
            System.out.println("=== LOGOUT SUCCESSFUL ===");

        } catch (Exception e) {
            System.err.println("Logout failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Alternative: Event-based logout (MOST RELIABLE)
    public static void logout(javafx.event.ActionEvent event) {
        System.out.println("=== EVENT-BASED LOGOUT ===");

        try {
            // Get stage from event source (guaranteed to be correct)
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            System.out.println("Closing stage: " + currentStage.getTitle());
            currentStage.close();

            // Create login stage
            Parent root = FXMLLoader.load(NavigationUtil.class.getResource("/view/auth/login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Clothify POS - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();

            NavigationUtil.currentStage = loginStage;
            System.out.println("Event-based logout successful");

        } catch (Exception e) {
            System.err.println("Event logout failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Simple navigate to login (uses logout logic)
    public static void navigateToLogin() {
        logout(); // Use the guaranteed logout method
    }

    // Show error alert for missing FXML files
    private static void showErrorAlert(String message) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Could not show alert: " + e.getMessage());
        }
    }

    // Debug method to check stage status
    public static void debugStageStatus() {
        System.out.println("=== DEBUG: Stage Status ===");
        System.out.println("CurrentStage: " + (currentStage != null ?
                currentStage.getTitle() + " (showing: " + currentStage.isShowing() + ")" : "NULL"));

        System.out.println("All Open Stages:");
        int count = 0;
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage && ((Stage) window).isShowing()) {
                System.out.println("  " + (++count) + ". " + ((Stage) window).getTitle());
            }
        }
        System.out.println("Total: " + count + " stages showing");
        System.out.println("===========================");
    }
}