package edu.icet.controller.auth;

import edu.icet.db.DBConnection;
import edu.icet.util.PasswordUtil;
import edu.icet.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEnterKeyHandlers();
    }

    private void setupEnterKeyHandlers() {
        // Move focus to the password field when Enter is pressed in the username field
        txtUsername.setOnKeyPressed(this::handleEnterKey);

        // Trigger the Login button click when Enter is pressed in the password field
        txtPassword.setOnKeyPressed(this::handleEnterKey);

        // Perform login when Enter is pressed on the Login button
        btnLogin.setOnKeyPressed(this::handleEnterKey);
    }

    @FXML
    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == txtUsername) {
                // Move focus to the password field when Enter is pressed in the username field
                txtPassword.requestFocus();
            } else if (event.getSource() == txtPassword || event.getSource() == btnLogin) {
                // Perform login when Enter is pressed on the password field or the Login button
                performLogin();
            }
        }
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        performLogin();
    }

    private void performLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password!");
            if (username.isEmpty()) {
                txtUsername.requestFocus();
            } else {
                txtPassword.requestFocus();
            }
            return;
        }

        // Authenticate user
        if (!isValidUser(username, password)) {
            showError("Invalid username or password!");
            txtPassword.requestFocus();
            txtPassword.selectAll();
        }
        // If login is successful, the dashboard is loaded inside the isValidUser method itself
    }

    private boolean isValidUser(String username, String password) {
        String sql = "SELECT user_id, password, role FROM user WHERE username = ? AND is_active = 1";

        try (var connection = DBConnection.getInstance().getConnection();
             var pstm = connection.prepareStatement(sql)) {

            pstm.setString(1, username);
            var rs = pstm.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");

                if (PasswordUtil.checkPassword(password, storedPassword)) {

                    // ✅ store login session
                    SessionManager.setCurrentUser(username, role, userId);

                    openDashboard(role);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openDashboard(String role) {
        try {
            String fxmlPath;

            // Loading FXML files based on user role
            if ("ADMIN".equalsIgnoreCase(role)) {
                fxmlPath = "/view/dashboard/AdminDashboard.fxml";
            } else if ("STAFF".equalsIgnoreCase(role)) {
                fxmlPath = "/view/dashboard/StaffDashboard.fxml";
            } else {
                // Show admin dashboard as default
                fxmlPath = "/view/dashboard/AdminDashboard.fxml";
            }

            System.out.println("Loading dashboard: " + fxmlPath + " for role: " + role);

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("CLOTHIFY POS - " + role);
            stage.show();

            // Current login window close
            ((Stage) btnLogin.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.show();
    }

    public void linkForgotPasswordOnAction(ActionEvent actionEvent) {
        try {
            // Forgot Password window open
            Parent root = FXMLLoader.load(getClass().getResource("/view/ForgotPassword.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot open password reset page!");
        }
    }

    public void linkSignupOnAction(ActionEvent actionEvent) {
        try {
            // Registration window open
            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/Registration.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Create New Account");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot open registration page!");
        }
    }
}